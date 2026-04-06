package com.personalprojects.webhooks.API;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import tools.jackson.databind.ObjectMapper;

@RestController
@RequestMapping("/api/test")
public class TestAPI {
    private final ObjectMapper bodyJson = new ObjectMapper();

    @PostMapping("/webhook")
    public ResponseEntity<Void> onWebhook(
            @RequestHeader(value = "X-GitHub-Hook-ID") String githubId,
            @RequestHeader(value = "X-GitHub-Event") String githubEvent,
            @RequestBody String body) {
        System.out.println("X-GitHub-Hook-ID: " + githubId);
        System.out.println("X-GitHub-Event: " + githubEvent);
        try {
            var jsonNode = bodyJson.readTree(body);
            if (jsonNode.has("action")) {
                if(jsonNode.get("action").asString().equals("opened")) {
                    System.out.println("Commenting on GitHub.....");
                }else {
                    System.out.println(jsonNode.get("action").asString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error parsing JSON body:");
            e.printStackTrace();
            System.out.println("Raw Body: " + body);
        }
        return ResponseEntity.accepted().build(); // HTTP 202
    }

    @PostMapping
    public String test() {
        return "Hello";
    }
}