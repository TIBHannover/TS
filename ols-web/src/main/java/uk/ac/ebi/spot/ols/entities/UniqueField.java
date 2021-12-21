package uk.ac.ebi.spot.ols.entities;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Constraint(validatedBy = UniqueFieldValidator.class)
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface UniqueField {
    String message() default "uniquefield";
    Class<?>[] groups() default { };
    Class<? extends Payload>[] payload() default { };
}
