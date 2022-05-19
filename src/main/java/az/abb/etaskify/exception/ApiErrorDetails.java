package az.abb.etaskify.exception;

import java.util.Map;

public class ApiErrorDetails {
//    private Date timestamp;
    private String message;
    private Map<String, String> errors;

    public ApiErrorDetails(String message, Map<String, String> details) {
        super();
//        this.timestamp = timestamp;
        this.message = message;
        this.errors = details;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Map<String, String> getErrors() {
        return errors;
    }

    public void setErrors(Map<String, String> errors) {
        this.errors = errors;
    }
}