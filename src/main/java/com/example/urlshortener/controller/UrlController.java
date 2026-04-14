package com.example.urlshortener.controller;

import com.example.urlshortener.dto.UrlRequest;
import com.example.urlshortener.dto.UrlResponse;
import com.example.urlshortener.service.UrlService;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;

@RestController
public class UrlController {

    private final UrlService service;

    public UrlController(UrlService service) {
        this.service = service;
    }

    @PostMapping("/api/shorten")
    public UrlResponse shorten(@RequestBody UrlRequest request) {
        return new UrlResponse(
                service.shortenUrl(request.getUrl(), request.getCustomCode())
        );
    }

    @GetMapping("/{shortCode}")
    public void redirect(@PathVariable String shortCode,
                         HttpServletResponse response) throws IOException {

        String originalUrl = service.getOriginalUrl(shortCode);
        response.sendRedirect(originalUrl);
    }

    @GetMapping("/api/stats/{shortCode}")
    public int getClicks(@PathVariable String shortCode) {
        return service.getClicks(shortCode);
    }
}