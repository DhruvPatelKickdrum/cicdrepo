package com.caching.exceptions;

public class DataFetchingFailException extends RuntimeException {
    public DataFetchingFailException(String message) {
        super(message);
    }
}
