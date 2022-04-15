package br.com.sw2you.realmeet.mapper;

import br.com.sw2you.realmeet.api.model.AllocationDTO;
import br.com.sw2you.realmeet.api.model.CreateAllocationDTO;
import br.com.sw2you.realmeet.domain.entity.Allocation;
import br.com.sw2you.realmeet.domain.entity.Room;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public abstract class AllocationMapper {

    @Mapping(source = "room", target = "room")
    @Mapping(source = "allocationDTO.employeeName", target = "employee.name")
    @Mapping(source = "allocationDTO.employeeEmail", target = "employee.email")
    @Mapping(target = "id", ignore = true)
    public abstract Allocation fromCreateDtoToEntity(CreateAllocationDTO allocationDTO, Room room);

    @Mapping(source = "employee.name", target = "employeeName")
    @Mapping(source = "employee.email", target = "employeeEmail")
    @Mapping(source = "room.id", target = "roomId")
    public abstract AllocationDTO fromEntityToAllocationDTO(Allocation allocation);
}
