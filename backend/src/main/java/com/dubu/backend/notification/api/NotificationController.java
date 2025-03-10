package com.dubu.backend.notification.api;

import com.dubu.backend.notification.api.dto.FcmTokenDto;
import com.dubu.backend.notification.api.dto.PushSubscriptionDto;
import com.dubu.backend.notification.application.FcmService;
import com.dubu.backend.notification.application.WebPushService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/notification")
public class NotificationController implements NotificationApi {

    private final WebPushService webPushService;
    private final FcmService fcmService;

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/subscribe")
    public void subscribe(
            HttpServletRequest request,
            @RequestBody PushSubscriptionDto subscription
    ) {
        String bearerToken = request.getHeader("Authorization");
        String token = parseAccessToken(bearerToken);
        webPushService.saveSubscription(token, subscription);
    }

    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PostMapping("/fcm/token")
    public void registerFcmToken(
            HttpServletRequest request,
            @RequestBody FcmTokenDto fcmTokenDto
    ) {
        String bearerToken = request.getHeader("Authorization");
        String token = parseAccessToken(bearerToken);
        fcmService.saveToken(token, fcmTokenDto.deviceToken());
    }

    private String parseAccessToken(String bearerToken) {
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return bearerToken;
    }
}