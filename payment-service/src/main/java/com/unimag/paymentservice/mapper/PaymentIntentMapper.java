package com.unimag.paymentservice.mapper;

import com.unimag.paymentservice.dto.payment.ResponsePaymentIntent;
import com.unimag.paymentservice.entity.PaymentIntent;
import com.unimag.paymentservice.entity.Refund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PaymentIntentMapper {

    ResponsePaymentIntent toResponsePaymentIntent(PaymentIntent paymentIntent);
}
