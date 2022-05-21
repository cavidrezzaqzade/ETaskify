package az.abb.etaskify.annotation;

import az.abb.etaskify.domain.auth.RoleDto;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class UniqueRolenameConstraint implements ConstraintValidator<UniqueRolename, RoleDto> {

    @Override
    public boolean isValid(RoleDto roleDto, ConstraintValidatorContext constraintValidatorContext) {
        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");
        System.out.println(roleDto.getRoleName());
        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");
        return false;
    }

    @Override
    public void initialize(UniqueRolename constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }
}
