package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class HelloController {

    private final String finnApi = "https://finnhub.io";
    private final String apiKey = "d6osuqpr01qk3chi1rbgd6osuqpr01qk3chi1rc0";
    private final RestTemplate restTemplate = new RestTemplate();
    
    @GetMapping("/hello")
    public Map<String, Object> hello() {
        return Map.of(
            "name", "lewis",
            "age", 15
        );
    }

    @GetMapping("/search")
    public String searchSymbol(@RequestParam String symbol) {
        String url = "https://finnhub.io/api/v1/search?q=" + symbol + "&exchange=US&token=" + apiKey;
        return restTemplate.getForObject(url, String.class);
    }
    
}   