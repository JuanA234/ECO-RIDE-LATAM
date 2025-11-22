package com.unimag.notificationservice.service.channels;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class PushService {

    public void send(String deviceId, String title, String body) {
        log.info("Sending push notification to device: {} ",  deviceId);

        log.info("Push notification sent successfully to device: {}", deviceId);
    }
}
