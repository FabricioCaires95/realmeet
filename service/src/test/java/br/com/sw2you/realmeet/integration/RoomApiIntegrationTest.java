package br.com.sw2you.realmeet.integration;

import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import br.com.sw2you.realmeet.api.facade.RoomApi;
import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.api.model.UpdateRoomDTO;
import br.com.sw2you.realmeet.core.BaseIntegrationTest;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import org.junit.Ignore;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.client.HttpClientErrorException;

class RoomApiIntegrationTest extends BaseIntegrationTest {
    @Autowired
    private RoomApi api;

    @Autowired
    private RoomRepository roomRepository;

    @Override
    protected void setupEach() throws Exception {
        setLocalHostBasePath(api.getApiClient(), "/v1");
    }

    @Test
    void testGetRoomByIdSuccess() {
        var roomEntity = roomBuilder().build();
        roomRepository.saveAndFlush(roomEntity);

        assertNotNull(roomEntity.getId());
        assertTrue(roomEntity.getActive());

        var roomDTO = api.getRoom(roomEntity.getId());

        assertEquals(roomEntity.getId(), roomDTO.getId());
        assertEquals(roomEntity.getName(), roomDTO.getName());
        assertEquals(roomEntity.getSeats(), roomDTO.getSeats());
    }

    @Test
    void testGetRoomByIdInactive() {
        var roomEntity = roomBuilder().active(false).build();
        roomRepository.saveAndFlush(roomEntity);

        assertFalse(roomEntity.getActive());
        assertThrows(HttpClientErrorException.NotFound.class, () -> api.getRoom(roomEntity.getId()));
    }

    @Test
    void testGetRoomByIdWhenDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> api.getRoom(DEFAULT_ROOM_ID));
    }

    @Test
    void testCreateRoomSuccess() {
        var createRoomDto = newCreateRoomDTO();
        var roomDto = api.createRoom(createRoomDto);

        assertNotNull(roomDto.getId());
        assertEquals(createRoomDto.getName(), roomDto.getName());
        assertEquals(createRoomDto.getSeats(), roomDto.getSeats());

        var room = roomRepository.findById(roomDto.getId()).orElseThrow();
        assertEquals(roomDto.getName(), room.getName());
        assertEquals(roomDto.getSeats(), room.getSeats());
    }

    @Test
    void testRoomValidationError() {
        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> api.createRoom((CreateRoomDTO) newCreateRoomDTO().name(null))
        );
    }

    @Test
    void testDeleteRoomSuccess() {
        var roomId = roomRepository.saveAndFlush(roomBuilder().build()).getId();
        api.deleteRoom(roomId);
        assertFalse(roomRepository.findById(roomId).orElseThrow().getActive());
    }

    @Test
    void testDeleteRoomDoesNotExist() {
        assertThrows(HttpClientErrorException.NotFound.class, () -> api.deleteRoom(1L));
    }

    @Test
    void testUpdateRoomSuccess() {
        var roomEntity = roomRepository.saveAndFlush(roomBuilder().build());
        var updateRoomDTO = new UpdateRoomDTO()
            .name(roomEntity.getName().concat("teste"))
            .seats(roomEntity.getSeats() + 1);

        api.updateRoom(roomEntity.getId(), updateRoomDTO);

        var updatedRoom = roomRepository.findById(roomEntity.getId()).orElseThrow();

        assertEquals(updatedRoom.getName(), updateRoomDTO.getName());
        assertEquals(updatedRoom.getSeats(), updateRoomDTO.getSeats());
    }

    @Test
    void testUpdateRoomDoesNotExist() {
        assertThrows(
            HttpClientErrorException.NotFound.class,
            () -> api.updateRoom(1L, new UpdateRoomDTO().name("teste").seats(2))
        );
    }

    @Test
    void testUpdateRoomValidationError() {
        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> api.updateRoom(1L, new UpdateRoomDTO().name(null).seats(2))
        );
    }

    @Test
    void testUpdateRoomNameDuplicated() {
        roomRepository.saveAndFlush(roomBuilder().build());
        var roomEntity2 = roomRepository.saveAndFlush(roomBuilder().name("Room E").build());
        var updateRoomDTO = new UpdateRoomDTO().name("Room C").seats(roomEntity2.getSeats() + 1);

        assertThrows(
            HttpClientErrorException.UnprocessableEntity.class,
            () -> api.updateRoom(roomEntity2.getId(), updateRoomDTO)
        );
    }
}
