package com.dollartracker.dto;

public class ApiResponse {
    private boolean success;
    private Object data;
    private String message;
    private String error;
    private String timestamp;

    public ApiResponse() {}

    public ApiResponse(boolean success, Object data, String message, String error, String timestamp) {
        this.success = success;
        this.data = data;
        this.message = message;
        this.error = error;
        this.timestamp = timestamp;
    }

    public boolean isSuccess() { return success; }
    public void setSuccess(boolean success) { this.success = success; }

    public Object getData() { return data; }
    public void setData(Object data) { this.data = data; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getError() { return error; }
    public void setError(String error) { this.error = error; }

    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }

    public static ApiResponse.Builder builder() {
        return new Builder();
    }

    public static class Builder {
        private boolean success;
        private Object data;
        private String message;
        private String error;
        private String timestamp;

        public Builder success(boolean success) { this.success = success; return this; }
        public Builder data(Object data) { this.data = data; return this; }
        public Builder message(String message) { this.message = message; return this; }
        public Builder error(String error) { this.error = error; return this; }
        public Builder timestamp(String timestamp) { this.timestamp = timestamp; return this; }

        public ApiResponse build() {
            return new ApiResponse(success, data, message, error, timestamp);
        }
    }
}
