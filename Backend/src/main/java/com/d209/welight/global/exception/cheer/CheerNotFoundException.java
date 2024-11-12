package com.d209.welight.global.exception.cheer;

import jakarta.persistence.EntityNotFoundException;

public class CheerNotFoundException extends EntityNotFoundException {
    public CheerNotFoundException(String message) {
        super(message);
    }
}