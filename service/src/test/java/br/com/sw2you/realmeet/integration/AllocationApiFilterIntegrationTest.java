package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.util.DateUtils.now;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_EMPLOYEE_EMAIL;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_END_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_START_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_SUBJECT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_NAME;
import static br.com.sw2you.realmeet.utils.TestDataCreator.allocationBuilder;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newEmployee;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

import br.com.sw2you.realmeet.api.facade.AllocationApi;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

class AllocationApiFilterIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AllocationApi api;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    @Override
    protected void setupEach() throws Exception {
        setLocalHostBasePath(api.getApiClient(), "/v1");
    }

    @Test
    void testGetAllocationsWithoutAnyFilter() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var allocation1 = allocationRepository.saveAndFlush(
            allocationBuilder(room).subject(DEFAULT_ALLOCATION_SUBJECT + 1).build()
        );
        var allocation2 = allocationRepository.saveAndFlush(
            allocationBuilder(room).subject(DEFAULT_ALLOCATION_SUBJECT + 2).build()
        );
        var allocation3 = allocationRepository.saveAndFlush(
            allocationBuilder(room).subject(DEFAULT_ALLOCATION_SUBJECT + 3).build()
        );

        var result = api.listAllocations(null, null, null, null);

        assertEquals(3, result.size());
        assertEquals(allocation1.getSubject(), result.get(0).getSubject());
        assertEquals(allocation2.getSubject(), result.get(1).getSubject());
        assertEquals(allocation3.getSubject(), result.get(2).getSubject());
    }

    @Test
    void testFilterAllocationsByRoomId() {
        var roomA = roomRepository.saveAndFlush(roomBuilder().name(DEFAULT_ROOM_NAME + "A").build());
        var roomB = roomRepository.saveAndFlush(roomBuilder().name(DEFAULT_ROOM_NAME + "B").build());

        var allocation1 = allocationRepository.saveAndFlush(allocationBuilder(roomA).build());
        var allocation2 = allocationRepository.saveAndFlush(allocationBuilder(roomA).build());
        allocationRepository.saveAndFlush(allocationBuilder(roomB).build());

        var result = api.listAllocations(null, roomA.getId(), null, null);

        assertEquals(2, result.size());
        assertEquals(allocation1.getId(), result.get(0).getId());
        assertEquals(allocation2.getId(), result.get(1).getId());
    }

    @Test
    void testFilterAllocationsByEmployeeEmail() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var employee1 = newEmployee().email(DEFAULT_ALLOCATION_EMPLOYEE_EMAIL + 1).build();
        var employee2 = newEmployee().email(DEFAULT_ALLOCATION_EMPLOYEE_EMAIL + 2).build();

        var allocation1 = allocationRepository.saveAndFlush(allocationBuilder(room).employee(employee1).build());
        var allocation2 = allocationRepository.saveAndFlush(allocationBuilder(room).employee(employee1).build());
        allocationRepository.saveAndFlush(allocationBuilder(room).employee(employee2).build());

        var result = api.listAllocations(employee1.getEmail(), null, null, null);

        assertEquals(2, result.size());
        assertEquals(allocation1.getEmployee().getEmail(), result.get(0).getEmployeeEmail());
        assertEquals(allocation2.getEmployee().getEmail(), result.get(1).getEmployeeEmail());
    }

    @Test
    void testFilterAllocationsByDateRange() {
        var baseStartAt = now().plusDays(2).withHour(14).withMinute(0);
        var baseEndAt = now().plusDays(4).withHour(20).withMinute(0);

        var room = roomRepository.saveAndFlush(roomBuilder().build());

        var allocation1 = allocationRepository.saveAndFlush(
            allocationBuilder(room).startAt(baseStartAt.plusHours(1)).endAt(baseStartAt.plusHours(2)).build()
        );
        var allocation2 = allocationRepository.saveAndFlush(
            allocationBuilder(room).startAt(baseStartAt.plusHours(4)).endAt(baseStartAt.plusHours(5)).build()
        );
        allocationRepository.saveAndFlush(
            allocationBuilder(room).startAt(baseStartAt.plusDays(1)).endAt(baseEndAt.plusDays(3).plusHours(1)).build()
        );

        var result = api.listAllocations(null, null, baseStartAt.toLocalDate(), baseEndAt.toLocalDate());

        assertEquals(2, result.size());
        assertEquals(allocation1.getId(), result.get(0).getId());
        assertEquals(allocation2.getId(), result.get(1).getId());
    }

    @Test
    void testGetAllocationsWithAllFilters() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var allocation = allocationRepository.saveAndFlush(allocationBuilder(room).build());

        var result = api.listAllocations(
            DEFAULT_ALLOCATION_EMPLOYEE_EMAIL,
            DEFAULT_ROOM_ID,
            DEFAULT_ALLOCATION_START_AT.toLocalDate(),
            DEFAULT_ALLOCATION_END_AT.toLocalDate()
        );

        assertFalse(result.isEmpty());
    }
}
