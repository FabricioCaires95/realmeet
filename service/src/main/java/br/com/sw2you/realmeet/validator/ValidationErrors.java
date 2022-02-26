package br.com.sw2you.realmeet.validator;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.springframework.data.util.Streamable;

public class ValidationErrors implements Streamable<ValidationError> {
    private final List<ValidationError> validationErrorList;

    public ValidationErrors() {
        this.validationErrorList = new ArrayList<>();
    }

    public ValidationErrors add(String field, String errorCode) {
        return add(new ValidationError(field, errorCode));
    }

    public ValidationErrors add(ValidationError error) {
        validationErrorList.add(error);
        return this;
    }

    public ValidationError getError(int index) {
        return validationErrorList.get(index);
    }

    public int getNumberErrors() {
        return validationErrorList.size();
    }

    public boolean hasErrors() {
        return !validationErrorList.isEmpty();
    }

    @Override
    public Iterator<ValidationError> iterator() {
        return validationErrorList.iterator();
    }

    @Override
    public String toString() {
        return "ValidationErrors{validationErrorList=" + validationErrorList + '}';
    }
}
