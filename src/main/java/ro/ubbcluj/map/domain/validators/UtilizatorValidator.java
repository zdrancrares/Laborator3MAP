package ro.ubbcluj.map.domain.validators;

import ro.ubbcluj.map.domain.Utilizator;
import ro.ubbcluj.map.domain.validators.ValidationException;
import ro.ubbcluj.map.domain.validators.Validator;

import java.util.ArrayList;
import java.util.List;

public class UtilizatorValidator implements Validator<Utilizator> {
    @Override
    public void validate(Utilizator entity) throws ValidationException {
        //TODO: implement method validate
        validateFirstName(entity.getFirstName());
        validateLastName(entity.getLastName());
    }

    private static void validateFirstName(String firstName) throws ValidationException{
        String errors = "";
        if (firstName.isEmpty()){
            errors += "Lungimea prenumelui nu poate sa fie nula.\n";
        }
        if (firstName.length() > 50){
            errors += "Lungimea prenumelui nu poate sa depaseasca 50 de caractere.\n";
        }
        if (!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }

    private static void validateLastName(String lastName) throws ValidationException{
        String errors = "";
        if (lastName.isEmpty()){
            errors += "Lungimea numelui nu poate sa fie nula.\n";
        }
        if (lastName.length() > 50){
            errors += "Lungimea numelui nu poate sa depaseasca 50 de caractere.\n";
        }
        if (!errors.isEmpty()){
            throw new ValidationException(errors);
        }
    }
}

