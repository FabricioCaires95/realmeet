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
import static java.util.stream.Collectors.toList;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.sw2you.realmeet.api.facade.AllocationApi;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.entity.Allocation;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.service.AllocationService;
import br.com.sw2you.realmeet.utils.TestDataCreator;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.web.client.HttpClientErrorException;

class AllocationApiFilterIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AllocationService allocationService;

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

        var result = api.listAllocations(null, null, null, null, null, null, null);

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

        var result = api.listAllocations(null, roomA.getId(), null, null, null, null, null);

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

        var result = api.listAllocations(employee1.getEmail(), null, null, null, null, null, null);

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

        var result = api.listAllocations(
            null,
            null,
            baseStartAt.toLocalDate(),
            baseEndAt.toLocalDate(),
            null,
            null,
            null
        );

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
            DEFAULT_ALLOCATION_END_AT.toLocalDate(),
            null,
            null,
            null
        );

        assertFalse(result.isEmpty());
    }

    @Test
    void testFilterAllocationUsingPagination() {
        persistAllocations(15);
        ReflectionTestUtils.setField(allocationService, "maxLimit", 10);

        var page1Result = api.listAllocations(null, null, null, null, null, null, 0);
        var page2Result = api.listAllocations(null, null, null, null, null, null, 1);
        assertEquals(10, page1Result.size());
        assertEquals(5, page2Result.size());
    }

    @Test
    void testFilterAllocationUsingPaginationAndLimit() {
        persistAllocations(25);
        ReflectionTestUtils.setField(allocationService, "maxLimit", 50);

        var page1Result = api.listAllocations(null, null, null, null, null, 10, 0);
        var page2Result = api.listAllocations(null, null, null, null, null, 10, 1);
        var page3Result = api.listAllocations(null, null, null, null, null, 10, 2);
        assertEquals(10, page1Result.size());
        assertEquals(10, page2Result.size());
        assertEquals(5, page3Result.size());
    }

    @Test
    void testFilterAllocationOrderByStartAtDesc() {
        var allocations = persistAllocations(3);

        var result = api.listAllocations(null, null, null, null, "-startAt", null, null);

        assertEquals(3, result.size());
        assertEquals(allocations.get(0).getId(), result.get(2).getId());
        assertEquals(allocations.get(1).getId(), result.get(1).getId());
        assertEquals(allocations.get(2).getId(), result.get(0).getId());
    }

    @Test
    void testFilterAllocationOrderByInvalidField() {
        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> api.listAllocations(null, null, null, null, "invalid", null, null)
        );
    }

    private List<Allocation> persistAllocations(int numberOfAllocations) {
        var room = roomRepository.saveAndFlush(roomBuilder().build());

        return IntStream
            .range(0, numberOfAllocations)
            .mapToObj(
                i ->
                    allocationRepository.saveAndFlush(
                        allocationBuilder(room)
                            .subject(DEFAULT_ALLOCATION_SUBJECT + "_" + (i + 1))
                            .startAt(DEFAULT_ALLOCATION_START_AT.plusHours(i + 1))
                            .endAt(DEFAULT_ALLOCATION_END_AT.plusHours(i + 1))
                            .build()
                    )
            )
            .collect(toList());
    }
}
