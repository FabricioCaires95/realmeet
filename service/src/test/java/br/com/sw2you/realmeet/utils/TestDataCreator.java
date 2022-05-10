package br.com.sw2you.realmeet.utils;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_EMPLOYEE_EMAIL;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_EMPLOYEE_NAME;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_END_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_ID;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_START_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_SUBJECT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_NAME;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_SEATS;

import br.com.sw2you.realmeet.api.model.CreateAllocationDTO;
import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.api.model.UpdateAllocationDTO;
import br.com.sw2you.realmeet.domain.entity.Allocation;
import br.com.sw2you.realmeet.domain.entity.Room;
import br.com.sw2you.realmeet.domain.model.Employee;

public final class TestDataCreator {

    private TestDataCreator() {}

    public static Room.Builder roomBuilder() {
        return Room.newBuilder().name(DEFAULT_ROOM_NAME).seats(DEFAULT_ROOM_SEATS);
    }

    public static Allocation.Builder allocationBuilder(Room room) {
        return Allocation
            .newBuilder()
            .id(DEFAULT_ALLOCATION_ID)
            .subject(DEFAULT_ALLOCATION_SUBJECT)
            .room(room)
            .employee(
                Employee
                    .newBuilder()
                    .name(DEFAULT_ALLOCATION_EMPLOYEE_NAME)
                    .email(DEFAULT_ALLOCATION_EMPLOYEE_EMAIL)
                    .build()
            )
            .startAt(DEFAULT_ALLOCATION_START_AT)
            .endAt(DEFAULT_ALLOCATION_END_AT);
    }

    public static CreateRoomDTO newCreateRoomDTO() {
        return (CreateRoomDTO) new CreateRoomDTO().name(DEFAULT_ROOM_NAME).seats(DEFAULT_ROOM_SEATS);
    }

    public static CreateAllocationDTO newCreateAllocationDTO() {
        return new CreateAllocationDTO()
            .roomId(DEFAULT_ROOM_ID)
            .subject(DEFAULT_ALLOCATION_SUBJECT)
            .employeeName(DEFAULT_ALLOCATION_EMPLOYEE_NAME)
            .employeeEmail(DEFAULT_ALLOCATION_EMPLOYEE_EMAIL)
            .startAt(DEFAULT_ALLOCATION_START_AT)
            .endAt(DEFAULT_ALLOCATION_END_AT);
    }

    public static UpdateAllocationDTO newUpdateAllocationDTO() {
        return new UpdateAllocationDTO()
            .subject(DEFAULT_ALLOCATION_SUBJECT)
            .startAt(DEFAULT_ALLOCATION_START_AT)
            .endAt(DEFAULT_ALLOCATION_END_AT);
    }
}
