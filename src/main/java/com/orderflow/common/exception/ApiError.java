package com.orderflow.common.exception;

import java.time.LocalDateTime;
import java.util.Map;

public record ApiError(
    LocalDateTime timestamp,
    int status,
    String error,
    String code,
    String message,
    String path,
    Map<String, String> fieldErrors
) {
}
