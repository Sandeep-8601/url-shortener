package com.example.urlshortener.dto;

public class UrlRequest {

    private String url;
    private String customCode;

    public String getUrl() { return url; }
    public void setUrl(String url) { this.url = url; }

    public String getCustomCode() { return customCode; }
    public void setCustomCode(String customCode) {
        this.customCode = customCode;
    }
}