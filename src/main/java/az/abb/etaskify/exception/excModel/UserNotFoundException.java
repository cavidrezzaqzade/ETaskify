package az.abb.etaskify.exception.excModel;

public class UserNotFoundException extends GeneralException {

    public UserNotFoundException() {
        super();
    }

    public UserNotFoundException(String message) {
        super(message);
    }
}