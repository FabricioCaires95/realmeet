package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.util.DateUtils.now;
import static br.com.sw2you.realmeet.utils.TestDataCreator.allocationBuilder;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateAllocationDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.sw2you.realmeet.api.facade.AllocationApi;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

class AllocationApiIntegrationTest extends BaseIntegrationTest {
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
    void testCreateAllocationSuccess() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var createAllocationDto = newCreateAllocationDTO().roomId(room.getId());
        var allocationDto = api.createAllocation(createAllocationDto);

        assertNotNull(allocationDto.getId());
        assertEquals(room.getId(), allocationDto.getRoomId());
        assertEquals(createAllocationDto.getSubject(), allocationDto.getSubject());
        assertEquals(createAllocationDto.getEmployeeName(), allocationDto.getEmployeeName());
        assertEquals(createAllocationDto.getEmployeeEmail(), allocationDto.getEmployeeEmail());
        assertTrue(createAllocationDto.getStartAt().isEqual(allocationDto.getStartAt()));
        assertTrue(createAllocationDto.getEndAt().isEqual(allocationDto.getEndAt()));
    }

    @Test
    void testCreateAllocationValidationError() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var createAllocation = newCreateAllocationDTO().roomId(room.getId()).subject(null);
        assertThrows(HttpClientErrorException.UnprocessableEntity.class, () -> api.createAllocation(createAllocation));
    }

    @Test
    void testCreateAllocationWhenRoomDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> api.createAllocation(newCreateAllocationDTO()));
    }

    @Test
    void testDeleteAllocationSuccess() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var allocationId = allocationRepository.saveAndFlush(allocationBuilder(room).build()).getId();

        api.deleteAllocation(allocationId);
        assertFalse(allocationRepository.findById(allocationId).isPresent());
    }

    @Test
    void testDeleteAllocationInThePast() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var allocationId = allocationRepository
            .saveAndFlush(
                allocationBuilder(room).startAt(now().minusDays(1)).endAt(now().minusDays(1).plusHours(1)).build()
            )
            .getId();

        assertThrows(HttpClientErrorException.UnprocessableEntity.class, () -> api.deleteAllocation(allocationId));
    }

    @Test
    void testDeleteAllocationDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> api.deleteAllocation(1L));
    }
}