package az.abb.etaskify.exception.excModel;

public class GeneralJwtException extends RuntimeException{

    public GeneralJwtException() {
        super();
    }

    public GeneralJwtException(String message) {
        super(message);
    }

}
