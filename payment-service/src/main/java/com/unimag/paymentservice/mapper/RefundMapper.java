package com.unimag.paymentservice.mapper;

import com.unimag.paymentservice.dto.refund.ResponseRefund;
import com.unimag.paymentservice.entity.Refund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface RefundMapper {

    ResponseRefund toResponseRefund(Refund refund);
}
