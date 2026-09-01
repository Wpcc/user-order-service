package com.wpcc.userorderservice.common;

import java.util.Map;

public record ApiErrorResponse(
    int status,
    String message,
    Map<String, String> fieldErrors) {

}
