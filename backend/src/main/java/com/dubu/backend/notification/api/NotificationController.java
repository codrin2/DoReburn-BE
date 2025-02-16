package com.dubu.backend.notification.api;

import com.dubu.backend.notification.application.NotificationService;
import com.dubu.backend.notification.dto.PushMessageDto;
import com.dubu.backend.notification.dto.PushSubscriptionDto;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController {

    private final NotificationService notificationService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/subscribe")
    public void subscribe(
            @RequestAttribute("memberId") Long memberId,
            @RequestBody PushSubscriptionDto subscription
    ) {
        notificationService.saveSubscription(memberId, subscription);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/")
    public void sendNotification(@RequestBody PushMessageDto message) {
        notificationService.sendPushNotification(message);
    }
}