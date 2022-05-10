package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.util.DateUtils.now;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateAllocationDTO;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_EMAIL;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_EMAIL_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_NAME;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_EMPLOYEE_NAME_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_END_AT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_MAX_DURATION_SECONDS;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_START_AT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_SUBJECT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ALLOCATION_SUBJECT_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_DURATION;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.INCONSISTENT;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.IN_THE_PAST;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.MISSING;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.validator.AllocationValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class AllocationCreateValidatorUnitTest extends BaseUnitTest {
    private AllocationValidator victim;

    @Mock
    private AllocationRepository allocationRepository;

    @BeforeEach
    void setupEach() {
        victim = new AllocationValidator(allocationRepository);
    }

    @Test
    void testValidateWhenSubjectIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().subject(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_SUBJECT, ALLOCATION_SUBJECT + MISSING),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenSubjectMaxLengthIsViolated() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(newCreateAllocationDTO().subject(rightPad("F", ALLOCATION_SUBJECT_MAX_LENGTH + 1, "A")))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_SUBJECT, ALLOCATION_SUBJECT + EXCEEDS_MAX_LENGTH),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeNameIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().employeeName(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_NAME, ALLOCATION_EMPLOYEE_NAME + MISSING),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeNameMaxLengthIsViolated() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO().employeeName(rightPad("F", ALLOCATION_EMPLOYEE_NAME_MAX_LENGTH + 1, "F"))
                )
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_NAME, ALLOCATION_EMPLOYEE_NAME + EXCEEDS_MAX_LENGTH),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeEmailIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().employeeEmail(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_EMAIL, ALLOCATION_EMPLOYEE_EMAIL + MISSING),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenEmployeeEmailMaxLengthIsViolated() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO().employeeEmail(rightPad("F", ALLOCATION_EMPLOYEE_EMAIL_MAX_LENGTH + 1, "F"))
                )
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_EMPLOYEE_EMAIL, ALLOCATION_EMPLOYEE_EMAIL + EXCEEDS_MAX_LENGTH),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateIfStartDateIsPresent() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().startAt(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + MISSING),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateIfEndDateIsPresent() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().startAt(now()).endAt(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_END_AT, ALLOCATION_END_AT + MISSING),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenDateOrderingIsInvalid() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().startAt(now().plusDays(1)).endAt(now()))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + INCONSISTENT),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenStartAtIsInThePast() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateAllocationDTO().startAt(now().minusMinutes(20)).endAt(now()))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_START_AT, ALLOCATION_START_AT + IN_THE_PAST),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenDateIntervalExceedsMAx() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () ->
                victim.validate(
                    newCreateAllocationDTO().endAt(now().plusDays(1).plusSeconds(ALLOCATION_MAX_DURATION_SECONDS + 1))
                )
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ALLOCATION_END_AT, ALLOCATION_END_AT + EXCEEDS_DURATION),
            exception.getValidationErrors().getError(0)
        );
    }
}
