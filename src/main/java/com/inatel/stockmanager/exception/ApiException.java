package com.inatel.stockmanager.exception;

import java.time.ZonedDateTime;

public class ApiException {

    private final String message;
    private final String status;
    private final ZonedDateTime timestamp;

    public ApiException(ZonedDateTime timestamp, String status, String message) {
        this.timestamp = timestamp;
        this.status = status;
        this.message = message;
    }

    public String getMessage() {
        return message;
    }

    public String getStatus() {
        return status;
    }

    public ZonedDateTime getTimestamp() {
        return timestamp;
    }

}
