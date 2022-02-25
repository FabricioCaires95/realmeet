package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.MapperUtils.roomMapper;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyBoolean;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.RoomNotFoundException;
import br.com.sw2you.realmeet.service.RoomService;
import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;

class RoomServiceUnitTest extends BaseUnitTest {
    private RoomService victim;

    @Mock
    private RoomRepository roomRepository;

    @BeforeEach
    void setupEach() {
        victim = new RoomService(roomRepository, roomMapper());
    }

    @Test
    void testGetRoomByIdSuccess() {
        var roomEntity = roomBuilder().id(DEFAULT_ROOM_ID).build();
        when(roomRepository.findByIdAndActive(anyLong(), anyBoolean())).thenReturn(Optional.of(roomEntity));

        var roomDTO = victim.getRoom(DEFAULT_ROOM_ID);

        assertEquals(roomEntity.getId(), roomDTO.getId());
        assertEquals(roomEntity.getName(), roomDTO.getName());
        assertEquals(roomEntity.getSeats(), roomDTO.getSeats());
    }

    @Test
    void testGetRoomByIdNotFound() {
        when(roomRepository.findByIdAndActive(anyLong(), anyBoolean())).thenReturn(Optional.empty());
        assertThrows(RoomNotFoundException.class, () -> victim.getRoom(DEFAULT_ROOM_ID));
    }
}
