package az.abb.etaskify.response;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import java.util.Map;

public class MessageResponse {
    public static ResponseEntity<?> response(String message, Object data, Object error, HttpStatus status) {
        return new ResponseEntity(new ResponseModelDTO(message, data, error), status);
    }

/*    public static ResponseEntity<?> failureDeleteConstraintViolationResponse(String field) {
        ResponseErrorMapModel responseErrorListModel =new ResponseErrorMapModel();
        responseErrorListModel.getErrors().put(field,Reason.CONSTRAINT_VIOLATED.getValue());
        return new ResponseEntity(responseErrorListModel,HttpStatus.UNPROCESSABLE_ENTITY);
    }

    public static ResponseEntity<?> errorResponse(Map<String, String> errors) {
        ResponseErrorMapModel responseErrorListModel =new ResponseErrorMapModel();
        responseErrorListModel.setErrors(errors);
        return new ResponseEntity(responseErrorListModel,HttpStatus.UNPROCESSABLE_ENTITY);
    }*/
}