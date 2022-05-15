package br.com.sw2you.realmeet.utils;

import static br.com.sw2you.realmeet.util.DateUtils.now;

import java.time.OffsetDateTime;

public final class TestConstants {
    public static final long DEFAULT_ROOM_ID = 1L;
    public static final String DEFAULT_ROOM_NAME = "Room C";
    public static final Integer DEFAULT_ROOM_SEATS = 6;
    public static final String DEFAULT_ALLOCATION_SUBJECT = "Some Subject";
    public static final String DEFAULT_ALLOCATION_EMPLOYEE_NAME = "John Fox";
    public static final String DEFAULT_ALLOCATION_EMPLOYEE_EMAIL = "johnfox@gmail.com";
    public static final OffsetDateTime DEFAULT_ALLOCATION_START_AT = now().plusDays(1);
    public static final OffsetDateTime DEFAULT_ALLOCATION_END_AT = DEFAULT_ALLOCATION_START_AT.plusHours(1);
    public static final long DEFAULT_ALLOCATION_ID = 2L;
    public static final String TEST_CLIENT_API_KEY = "test-api-key";
    public static final String TEST_CLIENT_DESCRIPTION = "Some test client";

    private TestConstants() {}
}
