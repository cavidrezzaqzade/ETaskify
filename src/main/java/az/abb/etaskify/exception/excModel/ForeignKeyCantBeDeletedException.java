package az.abb.etaskify.exception.excModel;

public class ForeignKeyCantBeDeletedException extends GeneralException {

    public ForeignKeyCantBeDeletedException() {
        super();
    }

    public ForeignKeyCantBeDeletedException(String message) {
        super(message);
    }
}
