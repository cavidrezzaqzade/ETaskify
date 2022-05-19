package az.abb.etaskify.response;

public enum Reason {
    SUCCESS_ADD("success add"),
    SUCCESS_DELETE("success delete"),
    SUCCESS_GET("success get"),
    SUCCESS_UPDATE("success update"),
    VALIDATION_ERRORS("validation error(s)"),
    NOT_FOUND("data is not found"),
    ALREADY_EXIST("data already exist"),
    DUPLICATE_VALUE("duplicate item value "),
    NOTHING_IN_CHANGE("nothing in change"),
    FAILURE_GET("not founded."),
    UNKNOW("unknown error occured"),
    NAMENULL("fill in the blanks"),
    CHECK_VIOLATED("the information does not meet the required conditions"),
    CONSTRAINT_VIOLATED("constraint violated"),
    SUCCESSFUL_OPERATION("successful operation"),
    IS_EMPTY("is empty"),
    DATE_TIME_PARSE("the date format is incorrect"),
    NUMBER_FORMAT("only numbers can be added"),
    FORBIDDEN("access denied for token"),
    ISCORRET_PARAMETR("parameter is not right format");

    private final String value;

    Reason(String value) {
        this.value = value;
    }
    public String getValue() {
        return value;
    }
}
