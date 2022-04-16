package br.com.sw2you.realmeet.validator;

public final class ValidatorConstants {
    public static final String ROOM_ID = "id";
    public static final String ROOM_NAME = "name";
    public static final String ROOM_SEATS = "seats";

    public static final String DUPLICATE = ".nameDuplicated";
    public static final int ROOM_NAME_MAX_LENGTH = 20;
    public static final int ROOM_SEATS_MIN_VALUE = 1;
    public static final int ROOM_SEATS_MAX_VALUE = 20;

    public static final String MISSING = ".missing";
    public static final String EXCEEDS_MAX_LENGTH = ".exceedsMaxLength";
    public static final String EXCEEDS_MAX_VALUE = ".exceedsMaxValue";
    public static final String BELLOW_MIN_VALUE = ".bellowMinValue";

    public static final String ALLOCATION_SUBJECT = "subject";
    public static final int ALLOCATION_SUBJECT_MAX_LENGTH = 60;
    public static final String ALLOCATION_EMPLOYEE_NAME = "employeeName";
    public static final int ALLOCATION_EMPLOYEE_NAME_MAX_LENGTH = 20;
    public static final String ALLOCATION_EMPLOYEE_EMAIL = "employeeEmail";
    public static final int ALLOCATION_EMPLOYEE_EMAIL_MAX_LENGTH = 30;
    public static final String ALLOCATION_START_AT = "startAt";
    public static final String ALLOCATION_END_AT = "endAt";

    private ValidatorConstants() {}
}
