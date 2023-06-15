package com.smuraha.service.myExceptions;

import com.fasterxml.jackson.core.JsonProcessingException;

public class MyJsonProcessingException extends JsonProcessingException {
    public MyJsonProcessingException(String msg) {
        super(msg);
    }
}
