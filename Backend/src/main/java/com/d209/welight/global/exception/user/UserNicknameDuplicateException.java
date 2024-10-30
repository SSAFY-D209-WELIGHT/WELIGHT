package com.d209.welight.global.exception.user;

public class UserNicknameDuplicateException extends RuntimeException{

    public UserNicknameDuplicateException(String message) {
        super(message);
    }

    public UserNicknameDuplicateException(String message, Throwable cause) {
        super(message, cause);
    }
}
