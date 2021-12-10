package uk.ac.ebi.spot.ols.entities;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = URLValidator.class)
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface ValidURL {
    String message() default "validurl";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
