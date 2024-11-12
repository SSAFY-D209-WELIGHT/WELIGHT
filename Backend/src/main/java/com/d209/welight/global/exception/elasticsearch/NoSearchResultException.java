package com.d209.welight.global.exception.elasticsearch;

public class NoSearchResultException extends RuntimeException {
    public NoSearchResultException(String message) {
        super(message);
    }
} 