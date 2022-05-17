package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.util.DateUtils.now;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_END_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_START_AT;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ALLOCATION_SUBJECT;
import static br.com.sw2you.realmeet.utils.TestConstants.TEST_CLIENT_API_KEY;
import static br.com.sw2you.realmeet.utils.TestDataCreator.allocationBuilder;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateAllocationDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newUpdateAllocationDTO;
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
import br.com.sw2you.realmeet.email.EmailSender;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.web.client.HttpClientErrorException;

class AllocationApiIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private AllocationApi api;

    @Autowired
    private RoomRepository roomRepository;

    @Autowired
    private AllocationRepository allocationRepository;

    @MockBean
    private EmailSender emailSender;

    @Override
    protected void setupEach() throws Exception {
        setLocalHostBasePath(api.getApiClient(), "/v1");
    }

    @Test
    void testCreateAllocationSuccess() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var createAllocationDto = newCreateAllocationDTO().roomId(room.getId());
        var allocationDto = api.createAllocation(TEST_CLIENT_API_KEY, createAllocationDto);

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
        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> api.createAllocation(TEST_CLIENT_API_KEY, createAllocation)
        );
    }

    @Test
    void testCreateAllocationWhenRoomDoesNotExist() {
        assertThrows(
            HttpClientErrorException.NotFound.class,
            () -> api.createAllocation(TEST_CLIENT_API_KEY, newCreateAllocationDTO())
        );
    }

    @Test
    void testDeleteAllocationSuccess() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var allocationId = allocationRepository.saveAndFlush(allocationBuilder(room).build()).getId();

        api.deleteAllocation(TEST_CLIENT_API_KEY, allocationId);
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

        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> api.deleteAllocation(TEST_CLIENT_API_KEY, allocationId)
        );
    }

    @Test
    void testDeleteAllocationDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> api.deleteAllocation(TEST_CLIENT_API_KEY, 1L));
    }

    @Test
    void testUpdateAllocationSuccess() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var createAllocation = newCreateAllocationDTO().roomId(room.getId());
        var allocationDto = api.createAllocation(TEST_CLIENT_API_KEY, createAllocation);

        var updateAllocationDto = newUpdateAllocationDTO()
            .subject(DEFAULT_ALLOCATION_SUBJECT + "__")
            .startAt(DEFAULT_ALLOCATION_START_AT.plusDays(1))
            .endAt(DEFAULT_ALLOCATION_END_AT.plusDays(1));

        api.updateAllocation(TEST_CLIENT_API_KEY, allocationDto.getId(), updateAllocationDto);

        var allocation = allocationRepository.findById(allocationDto.getId()).orElseThrow();

        assertEquals(updateAllocationDto.getSubject(), allocation.getSubject());
        assertTrue(updateAllocationDto.getStartAt().isEqual(allocation.getStartAt()));
        assertTrue(updateAllocationDto.getEndAt().isEqual(allocation.getEndAt()));
    }

    @Test
    void testUpdateAllocationDoesNotExist() {
        assertThrows(
            HttpClientErrorException.NotFound.class,
            () -> api.updateAllocation(TEST_CLIENT_API_KEY, 1L, newUpdateAllocationDTO())
        );
    }

    @Test
    void testUpdateAllocationValidationError() {
        var room = roomRepository.saveAndFlush(roomBuilder().build());
        var allocationId = allocationRepository.saveAndFlush(allocationBuilder(room).build()).getId();
        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> api.updateAllocation(TEST_CLIENT_API_KEY, allocationId, newUpdateAllocationDTO().subject(null))
        );
    }
}
