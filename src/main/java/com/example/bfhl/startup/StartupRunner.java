package com.example.bfhl.startup;

import com.example.bfhl.webhook.WebhookClient;
import com.example.bfhl.sql.SolutionService;
import com.example.bfhl.sql.SolutionEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StartupRunner {

    private final WebhookClient webhookClient;
    private final SolutionService solutionService;

    @EventListener(ApplicationReadyEvent.class)
    public void onReady() {
        try {
            log.info("Starting BFHL Qualifier flow...");

            var resp = webhookClient.generateWebhook();
            log.info("Generated webhook URL: {}", resp.webhook());
            log.info("Received accessToken (redacted length={}): {}", 
                     resp.accessToken() != null ? resp.accessToken().length() : 0, "***");

            // Decide question by regNo parity of last two digits
            var regNo = webhookClient.getConfig().getRegNo();
            int lastTwoDigits = webhookClient.extractLastTwoDigits(regNo);
            boolean isOdd = lastTwoDigits % 2 == 1;
            String questionType = isOdd ? "ODD_Q1" : "EVEN_Q2";
            String questionUrl = webhookClient.questionUrlFor(isOdd);

            log.info("Detected lastTwoDigits={} -> {} ({}).", 
                     lastTwoDigits, isOdd ? "Odd" : "Even", questionUrl);

            // Compute / obtain final SQL query
            String finalQuery = solutionService.solve(questionType);

            // Store result locally
            SolutionEntity saved = solutionService.storeSolution(regNo, questionType, finalQuery);
            log.info("Stored solution id={}, createdAt={}", saved.getId(), saved.getCreatedAt());

            // Submit to webhook using JWT in Authorization header
            webhookClient.submitFinalQuery(resp.accessToken(), finalQuery);
            log.info("Final query submitted successfully.");

        } catch (Exception e) {
            log.error("Startup flow failed: {}", e.getMessage(), e);
        }
    }
}
