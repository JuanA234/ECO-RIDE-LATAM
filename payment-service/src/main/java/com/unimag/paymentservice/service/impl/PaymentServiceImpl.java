package com.unimag.paymentservice.service.impl;

import com.unimag.paymentservice.dto.charge.ResponseCharge;
import com.unimag.paymentservice.dto.payment.ResponsePaymentIntent;
import com.unimag.paymentservice.dto.refund.ResponseRefund;
import com.unimag.paymentservice.entity.Charge;
import com.unimag.paymentservice.entity.PaymentIntent;
import com.unimag.paymentservice.entity.Refund;
import com.unimag.paymentservice.enums.PaymentStatus;
import com.unimag.paymentservice.exception.CannotCancelPayment;
import com.unimag.paymentservice.exception.PaymentNotAuthorized;
import com.unimag.paymentservice.exception.notFound.ChargeNotFound;
import com.unimag.paymentservice.exception.notFound.PaymentIntentNotFound;
import com.unimag.paymentservice.mapper.ChargeMapper;
import com.unimag.paymentservice.mapper.PaymentIntentMapper;
import com.unimag.paymentservice.mapper.RefundMapper;
import com.unimag.paymentservice.repository.ChargeRepository;
import com.unimag.paymentservice.repository.PaymentIntentRepository;
import com.unimag.paymentservice.repository.RefundRepository;
import com.unimag.paymentservice.service.PaymentService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Random;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class PaymentServiceImpl implements PaymentService {

    private final PaymentIntentRepository paymentIntentRepository;
    private final ChargeRepository chargeRepository;
    private final RefundRepository refundRepository;

    private final PaymentIntentMapper paymentIntentMapper;
    private final ChargeMapper chargeMapper;
    private final RefundMapper refundMapper;

    private final Random random = new Random();


    /**
     // Step 1: Create payment intent and authorize payment
     */
    @Override
    @Transactional
    public Mono<ResponsePaymentIntent> authorizePayment(Long reservationId, BigDecimal amount) {
        log.info("Authorizing payment for reservation: {}, amount: {}", reservationId, amount);

        PaymentIntent paymentIntent = PaymentIntent.builder()
                .reservationId(reservationId)
                .amount(amount)
                .currency("USD")
                .status(PaymentStatus.REQUIRES_ACTION)
                .build();
        return paymentIntentRepository.save(paymentIntent)
                .flatMap(this::processAuthorization)
                .map(paymentIntentMapper::toResponsePaymentIntent);
    }

    /**
     * Simulate payment authorization (can fail randomly for testing)
     */
    private Mono<PaymentIntent> processAuthorization(PaymentIntent paymentIntent) {
        return Mono.just(paymentIntent)
                .delayElement(java.time.Duration.ofMillis(500))
                .flatMap(intent -> {
                    // Simulate 10% failure rate for testing saga compensation
                    boolean shouldFail = random.nextInt(10) == 0;


                    if(shouldFail){
                        intent.setStatus(PaymentStatus.FAILED);

                        log.warn("Payment authorization failed for reservation: {}", intent.getReservationId());
                        return paymentIntentRepository.save(intent);
                    }

                    intent.setStatus(PaymentStatus.AUTHORIZED);

                    return paymentIntentRepository.save(intent)
                            .flatMap(savedIntent -> createCharge(savedIntent)
                                    .thenReturn(savedIntent));
                });
    }



    /**
     * Step 2: Create charge after authorization
     */
    private Mono<Charge> createCharge(PaymentIntent paymentIntent) {

        Charge charge = Charge.builder()
                .paymentIntentId(paymentIntent.getId())
                .provider("STRIPE")
                .providerRef(UUID.randomUUID().toString())
                .createdAt(LocalDateTime.now())
                .build();

        return chargeRepository.save(charge)
                .doOnSuccess(c -> log.info("Charge created: {} for payment intent: {}",
                        c.getId(), paymentIntent.getId()));
    }

    /**
     * Step 3 (Happy Path): Capture payment when reservation confirmed
     */
    @Override
    @Transactional
    public Mono<ResponsePaymentIntent> capturePayment(Long reservationId) {
        return paymentIntentRepository.findByReservationId(reservationId)
                .switchIfEmpty(Mono.error(new PaymentIntentNotFound("Payment intent not found")))
                .flatMap(paymentIntent -> {
                    if(paymentIntent.getStatus() != PaymentStatus.AUTHORIZED){
                        return Mono.error(new PaymentNotAuthorized("Payment not authorized"));
                    }

                    paymentIntent.setStatus(PaymentStatus.CAPTURED);

                    return paymentIntentRepository.save(paymentIntent)
                            .doOnSuccess(pi -> log.info("Payment captured for reservation: {}", reservationId));
                })
                .map(paymentIntentMapper::toResponsePaymentIntent);
    }

    /**
     * Compensation: Refund payment if reservation cancelled
     */
    @Override
    @Transactional
    public Mono<ResponseRefund> refundPayment(Long reservationId, String reason) {
        return paymentIntentRepository.findByReservationId(reservationId)
                .switchIfEmpty(Mono.error(new PaymentIntentNotFound("Payment intent not found")))
                .flatMap(paymentIntent -> {
                    if(paymentIntent.getStatus() != PaymentStatus.CAPTURED){
                        log.warn("Payment not captured, cannot refund for reservation: {}", reservationId);
                        return Mono.empty();
                    }

                    return chargeRepository.findByPaymentIntentId(paymentIntent.getId())
                            .switchIfEmpty(Mono.error(new ChargeNotFound("Charge not found")))
                            .flatMap(charge -> {
                                Refund refund = Refund.builder()
                                        .chargeId(charge.getId())
                                        .amount(paymentIntent.getAmount())
                                        .reason(reason)
                                        .createdAt(LocalDateTime.now())
                                        .build();

                                return refundRepository.save(refund)
                                        .flatMap(savedRefund ->{
                                            paymentIntent.setStatus(PaymentStatus.REFUNDED);

                                            return paymentIntentRepository.save(paymentIntent)
                                                    .thenReturn(savedRefund);

                                        })
                                        .doOnSuccess(r -> log.info("Refund created: {} for reservation: {}",
                                                r.getId(), reservationId));
                            });
                })
                .map(refundMapper::toResponseRefund);
    }

    /**
     * Cancel payment if not yet captured (compensation)
     */
    @Override
    @Transactional
    public Mono<ResponsePaymentIntent> cancelPayment(Long reservationId) {
        return paymentIntentRepository.findByReservationId(reservationId)
                .switchIfEmpty(Mono.error(new PaymentIntentNotFound("Payment not found")))
                .flatMap(paymentIntent -> {
                    if (paymentIntent.getStatus() == PaymentStatus.CAPTURED){
                        return Mono .error(new CannotCancelPayment("Cannot cancel captured payment, use refund"));
                    }
                    paymentIntent.setStatus(PaymentStatus.FAILED);

                    return paymentIntentRepository.save(paymentIntent)
                            .doOnSuccess(pi -> log.info("Payment cancelled for reservation: {}", reservationId));

                })
                .map(paymentIntentMapper::toResponsePaymentIntent);
    }
}
