package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.domain.entity.Room.newBuilder;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_NAME;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDTO;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.BELLOW_MIN_VALUE;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.DUPLICATE;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.EXCEEDS_MAX_VALUE;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.MISSING;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_NAME;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_NAME_MAX_LENGTH;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS;
import static br.com.sw2you.realmeet.validator.ValidatorConstants.ROOM_SEATS_MAX_VALUE;
import static org.apache.commons.lang3.StringUtils.rightPad;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.InvalidRequestException;
import br.com.sw2you.realmeet.validator.RoomValidator;
import br.com.sw2you.realmeet.validator.ValidationError;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RoomValidatorUnitTest extends BaseUnitTest {
    private RoomValidator victim;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    void setupEach() {
        victim = new RoomValidator(roomRepository);
    }

    @Test
    void testValidateWhenRoomIsValid() {
        victim.validate(newCreateRoomDTO());
    }

    @Test
    void testValidateWhenRoomNameIsMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDTO().name(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(new ValidationError(ROOM_NAME, ROOM_NAME + MISSING), exception.getValidationErrors().getError(0));
    }

    @Test
    void testValidateWhenRoomNameIsGreaterThanMaxLength() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDTO().name(rightPad("Z", ROOM_NAME_MAX_LENGTH + 1, "Z")))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ROOM_NAME, ROOM_NAME + EXCEEDS_MAX_LENGTH),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomSeatsAreMissing() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDTO().seats(null))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ROOM_SEATS, ROOM_SEATS + MISSING),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomSeatsAreLessThanMinValue() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDTO().seats(-2))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ROOM_SEATS, ROOM_SEATS + BELLOW_MIN_VALUE),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomSeatsAreGreaterThanMaxValue() {
        var exception = assertThrows(
            InvalidRequestException.class,
            () -> victim.validate(newCreateRoomDTO().seats(ROOM_SEATS_MAX_VALUE + 1))
        );
        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ROOM_SEATS, ROOM_SEATS + EXCEEDS_MAX_VALUE),
            exception.getValidationErrors().getError(0)
        );
    }

    @Test
    void testValidateWhenRoomNameIsDuplicateName() {
        given(roomRepository.findByNameAndActive(DEFAULT_ROOM_NAME, true))
            .willReturn(Optional.of(newBuilder().build()));
        var exception = assertThrows(InvalidRequestException.class, () -> victim.validate(newCreateRoomDTO()));

        assertEquals(1, exception.getValidationErrors().getNumberErrors());
        assertEquals(
            new ValidationError(ROOM_NAME, ROOM_NAME + DUPLICATE),
            exception.getValidationErrors().getError(0)
        );
    }
}
