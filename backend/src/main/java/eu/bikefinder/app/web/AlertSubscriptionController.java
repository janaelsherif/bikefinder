package eu.bikefinder.app.web;

import eu.bikefinder.app.service.PriceAlertSubscriptionService;
import eu.bikefinder.app.web.dto.PriceAlertSubscribeRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/alert-subscriptions")
public class AlertSubscriptionController {

    private final PriceAlertSubscriptionService subscriptionService;

    public AlertSubscriptionController(PriceAlertSubscriptionService subscriptionService) {
        this.subscriptionService = subscriptionService;
    }

    @PostMapping
    public ResponseEntity<Void> subscribe(@Valid @RequestBody PriceAlertSubscribeRequest body) {
        subscriptionService.subscribe(body.email(), body.filter(), body.locale());
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** One-click unsubscribe from alert digests (e-mail only; link included in digest messages). */
    @GetMapping(value = "/unsubscribe", produces = MediaType.TEXT_PLAIN_VALUE + ";charset=UTF-8")
    public ResponseEntity<String> unsubscribe(@RequestParam("token") UUID token) {
        boolean ok = subscriptionService.unsubscribe(token);
        if (!ok) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok("Unsubscribed. You will not receive further alert e-mails for this subscription.");
    }
}
