package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_EMPLOYEE_EMAIL;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_END_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_START_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_SUBJECT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.allocationBuilder;
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
