package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.MapperUtils.roomMapper;
import static br.com.sw2you.realmeet.utils.TestConstants.DEFAULT_ROOM_ID;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateRoomDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.mapper.RoomMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoomMapperUnitTest extends BaseUnitTest {
    private RoomMapper victim;

    @BeforeEach
    void setupEach() {
        victim = roomMapper();
    }

    @Test
    void testFromEntityToDto() {
        var entity = roomBuilder().id(DEFAULT_ROOM_ID).build();
        var dto = victim.fromEntityToDto(entity);

        assertEquals(entity.getId(), dto.getId());
        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getSeats(), dto.getSeats());
    }

    @Test
    void testCreateRoomDtoToEntity() {
        var dto = newCreateRoomDTO();
        var entity = victim.fromCreateDtoToEntity(dto);

        assertEquals(entity.getName(), dto.getName());
        assertEquals(entity.getSeats(), dto.getSeats());
    }
}
