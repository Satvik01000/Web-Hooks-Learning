package com.personalprojects.webhooks.API;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/test")
public class TestAPI {

    @GetMapping
    public String test(){
        return "hello";
    }
}