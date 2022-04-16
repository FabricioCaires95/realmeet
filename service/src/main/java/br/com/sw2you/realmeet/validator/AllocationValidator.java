package br.com.sw2you.realmeet.validator;

import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_EMAIL;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_EMAIL_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_NAME;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_NAME_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_END_AT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_START_AT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_SUBJECT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_SUBJECT_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.throwOnError;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.validateMaxLength;
import static br.com.sw2you.realmeet.validator.ValidatorUtils.validateRequired;

import br.com.sw2you.realmeet.api.model.CreateAllocationDTO;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import java.time.OffsetDateTime;
import org.springframework.stereotype.Component;

@Component
public class AllocationValidator {
    private final AllocationRepository allocationRepository;

    public AllocationValidator(AllocationRepository allocationRepository) {
        this.allocationRepository = allocationRepository;
    }

    public void validate(CreateAllocationDTO createAllocationDto) {
        var validationErrors = new ValidationErrors();
        validateSubject(createAllocationDto.getSubject(), validationErrors);
        validateEmployeeName(createAllocationDto.getEmployeeName(), validationErrors);
        validateEmployeeEmail(createAllocationDto.getEmployeeEmail(), validationErrors);
        validateDates(createAllocationDto.getStartAt(), createAllocationDto.getEndAt(), validationErrors);
        throwOnError(validationErrors);
    }

    private boolean validateSubject(String subject, ValidationErrors validationErrors) {
        return (
            validateRequired(subject, ALLOCATION_SUBJECT, validationErrors) &&
            validateMaxLength(subject, ALLOCATION_SUBJECT, ALLOCATION_SUBJECT_MAX_LENGTH, validationErrors)
        );
    }

    private boolean validateEmployeeName(String employeeName, ValidationErrors validationErrors) {
        return (
                validateRequired(employeeName, ALLOCATION_EMPLOYEE_NAME, validationErrors) &&
                validateMaxLength(employeeName, ALLOCATION_EMPLOYEE_NAME , ALLOCATION_EMPLOYEE_NAME_MAX_LENGTH, validationErrors)
        );
    }

    private boolean validateEmployeeEmail(String employeeEmail, ValidationErrors validationErrors) {
        return (
                validateRequired(employeeEmail, ALLOCATION_EMPLOYEE_EMAIL, validationErrors) &&
                validateMaxLength(employeeEmail, ALLOCATION_EMPLOYEE_EMAIL , ALLOCATION_EMPLOYEE_EMAIL_MAX_LENGTH, validationErrors)
        );
    }

    private void validateDates(OffsetDateTime startAt, OffsetDateTime endAt, ValidationErrors validationErrors) {
        validateDatesPresent(startAt, endAt, validationErrors);
    }

    private boolean validateDatesPresent(OffsetDateTime startAt, OffsetDateTime endAt, ValidationErrors validationErrors) {
        return (
                validateRequired(startAt, ALLOCATION_START_AT, validationErrors) &&
                validateRequired(endAt, ALLOCATION_END_AT, validationErrors)
        );
    }
}
