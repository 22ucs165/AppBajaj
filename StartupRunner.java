package com.example.webhookapp.config;

import com.example.webhookapp.service.WebhookService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class StartupRunner implements CommandLineRunner {
    
    @Autowired
    private WebhookService webhookService;
    
    @Override
    public void run(String... args) throws Exception {
        // This will run on application startup
        System.out.println("=== Starting Webhook Flow ===");
        webhookService.processWebhookFlow();
        System.out.println("=== Webhook Flow Completed ===");
    }
}
