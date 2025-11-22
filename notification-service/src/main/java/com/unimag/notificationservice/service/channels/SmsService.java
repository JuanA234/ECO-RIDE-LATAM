package com.unimag.notificationservice.service.channels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class SmsService {

    public void send(String to, String body){
        log.info("Sending SMS to: {}", to);

        log.info("SMS sent successfully to: {}", to);
    }
}
