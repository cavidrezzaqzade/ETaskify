package az.abb.etaskify.exception;

import az.abb.etaskify.exception.excModel.*;
import az.abb.etaskify.exception.excModel.dto.ErrorDetails;
import az.abb.etaskify.response.MessageResponse;
import az.abb.etaskify.response.Reason;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Path;
import java.util.*;

@Slf4j
@RestController
@ControllerAdvice
public class CustomizedResponseEntityExceptionHandler extends ResponseEntityExceptionHandler {

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex,
                                                                  HttpHeaders headers, HttpStatus status, WebRequest request) {
            log.error("MethodArgumentNotValid handler exception -> " + ex.getMessage());
            Map<String, String> errors = new HashMap<>();
            ex.getBindingResult().getAllErrors().forEach((error) -> {
            String fieldName = ((FieldError) error).getField();
            String errorMessage = error.getDefaultMessage();
            errors.put(fieldName, errorMessage);
        });
        return (ResponseEntity<Object>) MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public final ResponseEntity<?> handleAuthException(AccessDeniedException ex) {
        log.error("AccessDeniedException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.FORBIDDEN.getValue(), null, errors, HttpStatus.FORBIDDEN);
    }

//    @ExceptionHandler(IOException.class)
//    public final ResponseEntity<?> handleIOException(AuthException ex, WebRequest request) {
//        log.error("IOException handler exception -> " + ex.getMessage());
//        Map<String, String> errors = new HashMap<>();
//        errors.put("message", ex.getMessage());
//        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
//    }
    @ExceptionHandler(AuthException.class)
    public final ResponseEntity<?> handleAuthException(AuthException ex, WebRequest request) {
        log.error("AuthException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(NoSuchElementException.class)
    public final ResponseEntity<?> handleNoSuchElementException(NoSuchElementException ex, WebRequest request) {
        log.error("NoSuchElementException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ExpiredJwtException.class)
    public final ResponseEntity<?> handleExpiredJwtException(ExpiredJwtException ex, WebRequest request) {
        log.error("ExpiredJwtException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UnsupportedJwtException.class)
    public final ResponseEntity<?> handleUnsupportedJwtException(UnsupportedJwtException ex, WebRequest request) {
        log.error("UnsupportedJwtException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(MalformedJwtException.class)
    public final ResponseEntity<?> handleMalformedJwtException(MalformedJwtException ex, WebRequest request) {
        log.error("MalformedJwtException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(SignatureException.class)
    public final ResponseEntity<?> handleSignatureException(SignatureException ex, WebRequest request) {
        log.error("SignatureException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(GeneralJwtException.class)
    public final ResponseEntity<?> handleGeneralJwtException(GeneralJwtException ex, WebRequest request) {
        log.error("GeneralJwtException handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.VALIDATION_ERRORS.getValue(), null, errors, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(Exception.class)
    public final ResponseEntity<?> handleGeneralException(Exception ex, WebRequest request) {
        log.error("General Exception handler exception -> " + ex.getMessage());
        Map<String, String> errors = new HashMap<>();
        errors.put("message", ex.getMessage());
        return MessageResponse.response(Reason.UNKNOW.getValue(), null, errors, HttpStatus.INTERNAL_SERVER_ERROR);
    }
    //others
    @ExceptionHandler(ConstraintViolationException.class)
    ResponseEntity<Set<String>> handleConstraintViolation(ConstraintViolationException e) {
        Set<ConstraintViolation<?>> constraintViolations = e.getConstraintViolations();
        Map<String, String> errors = new LinkedHashMap<>();
        constraintViolations.forEach((error) -> {
            String fieldName = "";
            for (Path.Node node : error.getPropertyPath()) {
                fieldName = ((node.getIndex() == null) ? "" : "[" + node.getIndex()+ "].") + node.getName();
            }
//            System.out.println(fieldName);
//            String fieldName = error.getPropertyPath().toString().substring(23);
            String errorMessage = error.getMessage();
            errors.put(fieldName, errorMessage);
        });
        ApiErrorDetails errorDetails = new ApiErrorDetails("Validation error(s)", errors);
        return new ResponseEntity(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(GeneralException.class)
    public final ResponseEntity<ErrorDetails> handleGeneralException(GeneralException ex, WebRequest request) {
        System.out.println("GeneralException");
        ErrorDetails errorDetails =
                new ErrorDetails(
                        Reason.UNKNOW.getValue());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(CanNotNullException.class)
    public final ResponseEntity<ErrorDetails> handleCanNotNullException(CanNotNullException ex, WebRequest request) {
        ErrorDetails errorDetails =
                new ErrorDetails(
                        Reason.NOT_FOUND.getValue());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(UserNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleUserNotFoundException(UserNotFoundException ex, WebRequest request) {
        ErrorDetails errorDetails =
                new ErrorDetails(
                        ex.getMessage() );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(ItemNotFoundException.class)
    public final ResponseEntity<ErrorDetails> handleItemNotFoundException(ItemNotFoundException ex, WebRequest request) {
        ErrorDetails  errorDetails =
                new ErrorDetails(
                        Reason.NOT_FOUND.getValue()
                );

        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(NameAlreadyExistException.class)
    public final ResponseEntity<ErrorDetails> handleNameAlreadyExistException(NameAlreadyExistException ex, WebRequest request) {
        ErrorDetails errorDetails =
                new ErrorDetails(
                        Reason.ALREADY_EXIST.getValue());
        return new ResponseEntity<>(errorDetails, HttpStatus.UNPROCESSABLE_ENTITY);
    }

    @ExceptionHandler(ForeignKeyCantBeDeletedException.class)
    public final ResponseEntity<ErrorDetails> handleForeignKeyCantBeDeletedException(ForeignKeyCantBeDeletedException ex, WebRequest request) {
        ErrorDetails errorDetails =
                new ErrorDetails(
                        Reason.CONSTRAINT_VIOLATED.getValue());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CustomValidationException.class)
    public final ResponseEntity<ErrorDetails> handleBadRequestException(CustomValidationException ex, WebRequest request) {
        ErrorDetails errorDetails =
                new ErrorDetails(
                        ex.getMessage());
        return new ResponseEntity<>(errorDetails, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InternalErrorException.class)
    public final ResponseEntity<ErrorDetails> handleGeneralException(InternalErrorException ex, WebRequest request) {
        ErrorDetails errorDetails = new ErrorDetails(Reason.UNKNOW.getValue());
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}