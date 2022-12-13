package mustodo.backend.todo.ui.dto.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Documented
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Constraint(validatedBy = HexColorValidator.class)
public @interface HexColor {
    String message() default "올바르지 않은 색상 코드입니다.";
    Class<? extends Payload>[] payload() default {};
    Class<?>[] groups() default {};
}
