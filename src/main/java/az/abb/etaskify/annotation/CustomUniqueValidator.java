package az.abb.etaskify.annotation;

import az.abb.etaskify.domain.AdminUserDto;
import az.abb.etaskify.entity.OrganizationEntity;
import az.abb.etaskify.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;

import javax.validation.ConstraintValidator;
import javax.validation.ConstraintValidatorContext;

public class CustomUniqueValidator implements ConstraintValidator<CustomUnique, OrganizationEntity> {

    @Autowired
    private UserRepository userRepository;

    @Override
    public boolean isValid(OrganizationEntity adminUserDto, ConstraintValidatorContext constraintValidatorContext) {

//        if(adminUserDto != null && ){
//
//        }

        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");
        System.out.println(adminUserDto.getOrgName());
        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");
        System.out.println("+++++++++++++++++++");

        System.out.println(userRepository.existsByEmailIgnoreCase(adminUserDto.getUser().getEmail()));;

        return false;
    }

    @Override
    public void initialize(CustomUnique constraintAnnotation) {
        ConstraintValidator.super.initialize(constraintAnnotation);
    }

}
