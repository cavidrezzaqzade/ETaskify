package az.abb.etaskify.exception.excModel;

public class InternalErrorException extends RuntimeException {

    public InternalErrorException() {
        super();
    }

    public InternalErrorException(String message) {
        super(message);
    }
}