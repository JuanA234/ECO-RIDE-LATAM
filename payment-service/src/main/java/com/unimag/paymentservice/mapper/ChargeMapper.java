package com.unimag.paymentservice.mapper;

import com.unimag.paymentservice.dto.charge.ResponseCharge;
import com.unimag.paymentservice.entity.Charge;
import com.unimag.paymentservice.entity.PaymentIntent;
import com.unimag.paymentservice.entity.Refund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ChargeMapper {

    ResponseCharge toResponseCharge(Charge charge);
}
