package com.example.productapp.common.exception;

import java.util.Map;

public record ValidationErrorResponse(int status,
                                      String error,
                                      Map<String, String> messages) {

}
