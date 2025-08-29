package com.example.bfhl.webhook;

import com.example.bfhl.config.AppConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
@Component
@RequiredArgsConstructor
public class WebhookClient {

    private final AppConfig config;
    private final WebClient webClient = WebClient.builder().build();

    public AppConfig getConfig() {
        return config;
    }

    public int extractLastTwoDigits(String regNo) {
        // Corrected regex: needs double escaping in Java
        Matcher m = Pattern.compile("(\\d{2})(?!.*\\d)").matcher(regNo);
        if (m.find()) {
            return Integer.parseInt(m.group(1));
        }
        // Fallback: build number from digits modulo 100
        int acc = 0;
        for (char c : regNo.toCharArray()) {
            if (Character.isDigit(c)) {
                acc = (acc * 10 + (c - '0')) % 100;
            }
        }
        return acc;
    }

    public String questionUrlFor(boolean odd) {
        if (odd) {
            return "https://drive.google.com/file/d/1IeSI6l6KoSQAFfRihIT9tEDICtoz-G/view?usp=sharing";
        } else {
            return "https://drive.google.com/file/d/143MR5cLFrlNEuHzzWJ5RHnEWuijuM9X/view?usp=sharing";
        }
    }

    public GenerateWebhookResponse generateWebhook() {
        String url = config.getBaseUrl() + "/hiring/generateWebhook/JAVA";

        Map<String, Object> body = Map.of(
                "name", config.getName(),
                "regNo", config.getRegNo(),
                "email", config.getEmail()
        );

        return webClient.post()
                .uri(url)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .bodyToMono(GenerateWebhookResponse.class)
                .block();
    }

    public void submitFinalQuery(String accessToken, String finalQuery) {
        String url = config.getBaseUrl() + "/hiring/testWebhook/JAVA";
        Map<String, Object> body = Map.of("finalQuery", finalQuery);

        webClient.post()
                .uri(url)
                .header(HttpHeaders.AUTHORIZATION, accessToken) // NOTE: no "Bearer " prefix
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(body)
                .retrieve()
                .toBodilessEntity()
                .block();
    }
}
