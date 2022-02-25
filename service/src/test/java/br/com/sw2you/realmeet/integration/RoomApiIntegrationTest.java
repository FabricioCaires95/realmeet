package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.sw2you.realmeet.api.facade.RoomApi;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

import org.junit.jupiter.api.Test;

class RoomApiIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private RoomApi roomApi;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    protected void setupEach() throws Exception {
        setLocalHostBasePath(roomApi.getApiClient(), "/v1");
    }

    @Test
    void testGetRoomByIdSuccess() {
        var roomEntity = roomBuilder().build();
        roomRepository.saveAndFlush(roomEntity);

        assertNotNull(roomEntity.getId());
        assertTrue(roomEntity.getActive());

        var roomDTO = roomApi.getRoom(roomEntity.getId());

        assertEquals(roomEntity.getId(), roomDTO.getId());
        assertEquals(roomEntity.getName(), roomDTO.getName());
        assertEquals(roomEntity.getSeats(), roomDTO.getSeats());
    }

    @Test
    void testGetRoomByIdInactive() {
        var roomEntity = roomBuilder().active(false).build();
        roomRepository.saveAndFlush(roomEntity);

        assertFalse(roomEntity.getActive());
        assertThrows(HttpClientErrorException.NotFound.class, () -> roomApi.getRoom(roomEntity.getId()));
    }

    @Test
    void testGetRoomByIdWhenDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> roomApi.getRoom(DEFAULT_ROOM_ID));
    }
}
