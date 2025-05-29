package com.example.webhookapp.service;

import com.example.webhookapp.model.SolutionRequest;
import com.example.webhookapp.model.WebhookRequest;
import com.example.webhookapp.model.WebhookResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class WebhookService {
    
    private static final Logger logger = LoggerFactory.getLogger(WebhookService.class);
    private static final String GENERATE_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/generateWebhook/JAVA";
    private static final String TEST_WEBHOOK_URL = "https://bfhldevapigw.healthrx.co.in/hiring/testWebhook/JAVA";
    
    private final WebClient webClient;
    private final SqlSolutionService sqlSolutionService;
    
    @Autowired
    public WebhookService(SqlSolutionService sqlSolutionService) {
        this.webClient = WebClient.builder().build();
        this.sqlSolutionService = sqlSolutionService;
    }
    
    public void processWebhookFlow() {
        try {
            // TODO: Update these values with your actual details
            WebhookRequest request = new WebhookRequest(
                "Your Name",           // Replace with your actual name
                "YOUR_REG_NO",         // Replace with your registration number
                "your.email@example.com" // Replace with your email
            );
            
            logger.info("Step 1: Generating webhook for regNo: {}", request.getRegNo());
            WebhookResponse response = generateWebhook(request);
            
            if (response != null && response.getWebhook() != null) {
                logger.info("Step 2: Webhook generated successfully. URL: {}", response.getWebhook());
                
                // Get SQL solution based on regNo
                String sqlSolution = sqlSolutionService.getSqlSolution(request.getRegNo());
                logger.info("Step 3: Generated SQL solution for regNo: {}", request.getRegNo());
                
                // Submit solution
                logger.info("Step 4: Submitting solution to webhook...");
                submitSolution(sqlSolution, response.getAccessToken());
            } else {
                logger.error("Failed to generate webhook. Response was null or invalid.");
            }
        } catch (Exception e) {
            logger.error("Error in webhook flow: ", e);
        }
    }
    
    private WebhookResponse generateWebhook(WebhookRequest request) {
        try {
            WebhookResponse response = webClient.post()
                    .uri(GENERATE_WEBHOOK_URL)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(request)
                    .retrieve()
                    .bodyToMono(WebhookResponse.class)
                    .block();
            
            logger.info("Webhook generation response received");
            return response;
        } catch (Exception e) {
            logger.error("Error generating webhook: ", e);
            return null;
        }
    }
    
    private void submitSolution(String finalQuery, String accessToken) {
        try {
            SolutionRequest solutionRequest = new SolutionRequest(finalQuery);
            
            String response = webClient.post()
                    .uri(TEST_WEBHOOK_URL)
                    .header(HttpHeaders.AUTHORIZATION, accessToken)
                    .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_JSON_VALUE)
                    .bodyValue(solutionRequest)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();
                    
            logger.info("Solution submitted successfully. Response: {}", response);
        } catch (Exception e) {
            logger.error("Error submitting solution: ", e);
        }
    }
}
