package com.shreyash.payledger.controller;

import java.util.Map;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class HomeController 
{

    @GetMapping("/")
    public Map<String, String> home()
    {
        return Map.of(
            "service", "PayLedger API",
            "status", "running",
            "docs", "https://github.com/Shreyash9330/payledger");
    }
}
