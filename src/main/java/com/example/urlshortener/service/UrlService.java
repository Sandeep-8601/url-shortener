package com.example.urlshortener.service;

import com.example.urlshortener.entity.Url;
import com.example.urlshortener.repository.UrlRepository;
import org.springframework.stereotype.Service;

import java.net.URL;
import java.util.Random;

@Service
public class UrlService {

    private final UrlRepository repository;
    private final String BASE_URL = "http://localhost:8080/";

    public UrlService(UrlRepository repository) {
        this.repository = repository;
    }

    public String shortenUrl(String originalUrl, String customCode) {

        if (!isValidUrl(originalUrl)) {
            throw new RuntimeException("Invalid URL");
        }

        String shortCode;

        // ✅ Case 1: Custom Code
        if (customCode != null && !customCode.trim().isEmpty()) {

            if (repository.existsByShortCode(customCode)) {
                throw new RuntimeException("Custom code already taken");
            }

            shortCode = customCode;
        }

        // ✅ Case 2: Random Code
        else {

            return repository.findByOriginalUrl(originalUrl)
                    .map(url -> BASE_URL + url.getShortCode())
                    .orElseGet(() -> {
                        String generatedCode = generateUniqueShortCode();
                        Url url = new Url(originalUrl, generatedCode);
                        repository.save(url);
                        return BASE_URL + generatedCode;
                    });
        }

        Url url = new Url(originalUrl, shortCode);
        repository.save(url);

        return BASE_URL + shortCode;
    }

    public String getOriginalUrl(String shortCode) {

        Url url = repository.findByShortCode(shortCode)
                .orElseThrow(() -> new RuntimeException("URL not found"));

        // increment click count
        url.setClickCount(url.getClickCount() + 1);
        repository.save(url);

        return url.getOriginalUrl();
    }

    public int getClicks(String shortCode) {
        return repository.findByShortCode(shortCode)
                .map(Url::getClickCount)
                .orElseThrow(() -> new RuntimeException("URL not found"));
    }

    private boolean isValidUrl(String url) {
        try {
            new URL(url).toURI();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private String generateUniqueShortCode() {
        String code;

        do {
            code = generateShortCode();
        } while (repository.existsByShortCode(code));

        return code;
    }

    private String generateShortCode() {
        String chars = "abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        Random random = new Random();
        StringBuilder code = new StringBuilder();

        for (int i = 0; i < 6; i++) {
            code.append(chars.charAt(random.nextInt(chars.length())));
        }

        return code.toString();
    }
}