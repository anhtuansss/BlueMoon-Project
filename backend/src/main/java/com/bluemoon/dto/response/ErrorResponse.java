package com.bluemoon.dto.response;

import java.util.Collections;
import java.util.List;

public class ErrorResponse {
    private ErrorBody error;

    public ErrorResponse() {}

    public ErrorResponse(String code, String message, List<String> details) {
        this.error = new ErrorBody(code, message, details != null ? details : Collections.emptyList());
    }

    public ErrorResponse(String code, String message) {
        this(code, message, Collections.emptyList());
    }

    public ErrorBody getError() {
        return error;
    }

    public void setError(ErrorBody error) {
        this.error = error;
    }

    public static class ErrorBody {
        private String code;
        private String message;
        private List<String> details;

        public ErrorBody() {}

        public ErrorBody(String code, String message, List<String> details) {
            this.code = code;
            this.message = message;
            this.details = details;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMessage() {
            return message;
        }

        public void setMessage(String message) {
            this.message = message;
        }

        public List<String> getDetails() {
            return details;
        }

        public void setDetails(List<String> details) {
            this.details = details;
        }
    }
}
