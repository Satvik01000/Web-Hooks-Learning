package com.personalprojects.webhooks.API;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import tools.jackson.databind.ObjectMapper; // Note: Usually this is com.fasterxml.jackson...

import java.util.Map;

@RestController
@RequestMapping("/api/test")
public class TestAPI {
    private final ObjectMapper bodyJson = new ObjectMapper();
    private final WebClient.Builder webClientBuilder;

    @Value("${GITHUB_PAT}")
    private String personalAccessToken;

    public TestAPI(WebClient.Builder webClientBuilder) {
        this.webClientBuilder = webClientBuilder;
    }

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

                    String issueNumber = jsonNode.get("number").asString();

                    Map<String, String> requestBody = Map.of(
                            "body", "Comment from web hook receiving server! WebClient is working"
                    );

                    System.out.println("Commenting on GitHub PR #" + issueNumber + ".....");

                    webClientBuilder.build()
                            .post()
                            .uri("https://api.github.com/repos/Satvik01000/Web-Hooks-Learning/issues/" + issueNumber + "/comments")
                            .headers(httpHeaders -> {
                                httpHeaders.set("Accept", "application/vnd.github+json");
                                httpHeaders.set("Authorization", "Bearer " + personalAccessToken);
                                httpHeaders.set("X-GitHub-Api-Version", "2022-11-28");
                            })
                            .bodyValue(requestBody)
                            .retrieve()
                            .bodyToMono(String.class)
                            .block();

                    System.out.println("Comment posted successfully!");
                } else {
                    System.out.println("Action was: " + jsonNode.get("action").asString());
                }
            }
        } catch (Exception e) {
            System.out.println("Error processing webhook:");
            e.printStackTrace();
        }
        return ResponseEntity.accepted().build(); // HTTP 202
    }

    @PostMapping
    public String test() {
        return "Hello";
    }
}