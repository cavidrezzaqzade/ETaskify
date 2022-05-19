package az.abb.etaskify.response;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class ResponseModelDTO<T> extends ResponseModel {
    private T data;
    private T errors;

    public ResponseModelDTO(String message, T data, T errors) {
        super(message);
        this.data = data;
        this.errors = errors;
    }
}
