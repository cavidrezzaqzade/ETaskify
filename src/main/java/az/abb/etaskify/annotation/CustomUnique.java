package az.abb.etaskify.annotation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import static java.lang.annotation.ElementType.*;

@Documented
@Target( { TYPE })
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = CustomUniqueValidator.class)
public @interface CustomUnique {
    //error mesajı
    String message() default "unique value violation";
    //constraints qrupları
    Class<?>[] groups() default {};
    //annotasiya ucun elave informasiya(metadata)
    Class<? extends Payload>[] payload() default {};
}
