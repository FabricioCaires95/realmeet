package br.com.sw2you.realmeet.unit;

import static br.com.sw2you.realmeet.utils.MapperUtils.allocationMapper;
import static br.com.sw2you.realmeet.utils.TestDataCreator.allocationBuilder;
import static br.com.sw2you.realmeet.utils.TestDataCreator.newCreateAllocationDTO;
import static br.com.sw2you.realmeet.utils.TestDataCreator.roomBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import br.com.sw2you.realmeet.core.BaseUnitTest;
import br.com.sw2you.realmeet.mapper.AllocationMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class AllocationMapperUnitTest extends BaseUnitTest {
    private AllocationMapper victim;

    @BeforeEach
    void setupEach() {
        victim = allocationMapper();
    }

    @Test
    void testFromCreateAllocationDtoToEntity() {
        var dto = newCreateAllocationDTO();
        var allocation = victim.fromCreateDtoToEntity(dto, roomBuilder().build());

        assertNull(allocation.getRoom().getId());
        assertEquals(allocation.getSubject(), dto.getSubject());
        assertEquals(allocation.getEmployee().getName(), dto.getEmployeeName());
        assertEquals(allocation.getEmployee().getEmail(), dto.getEmployeeEmail());
        assertEquals(allocation.getStartAt(), dto.getStartAt());
        assertEquals(allocation.getEndAt(), dto.getEndAt());
    }

    @Test
    void testEntityToAllocationDto() {
        var allocation = allocationBuilder(roomBuilder().build()).build();
        var allocationDTO = victim.fromEntityToAllocationDTO(allocation);

        assertEquals(allocation.getRoom().getId(), allocationDTO.getRoomId());
        assertEquals(allocation.getSubject(), allocationDTO.getSubject());
        assertEquals(allocation.getEmployee().getName(), allocationDTO.getEmployeeName());
        assertEquals(allocation.getEmployee().getEmail(), allocationDTO.getEmployeeEmail());
        assertEquals(allocation.getStartAt(), allocationDTO.getStartAt());
        assertEquals(allocation.getEndAt(), allocationDTO.getEndAt());
    }
}
