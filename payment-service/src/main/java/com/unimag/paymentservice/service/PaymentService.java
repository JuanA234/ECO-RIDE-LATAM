package com.unimag.paymentservice.service;

import com.unimag.paymentservice.dto.charge.ResponseCharge;
import com.unimag.paymentservice.dto.payment.ResponsePaymentIntent;
import com.unimag.paymentservice.dto.refund.ResponseRefund;
import com.unimag.paymentservice.entity.PaymentIntent;
import reactor.core.publisher.Mono;

import java.math.BigDecimal;

public interface PaymentService {

    Mono<ResponsePaymentIntent> authorizePayment(Long reservationId, BigDecimal amount);

    Mono<ResponsePaymentIntent> capturePayment(Long reservationId);

    Mono<ResponseRefund> refundPayment(Long reservationId, String reason);

    Mono<ResponsePaymentIntent> cancelPayment(Long reservationId);

}
