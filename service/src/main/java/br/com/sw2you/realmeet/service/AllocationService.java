package br.com.sw2you.realmeet.service;

import br.com.sw2you.realmeet.api.model.AllocationDTO;
import br.com.sw2you.realmeet.api.model.CreateAllocationDTO;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.RoomNotFoundException;
import br.com.sw2you.realmeet.mapper.AllocationMapper;
import br.com.sw2you.realmeet.validator.AllocationValidator;
import org.springframework.stereotype.Service;

@Service
public class AllocationService {
    private final RoomRepository roomRepository;
    private final AllocationRepository allocationRepository;
    private final AllocationMapper allocationMapper;
    private final AllocationValidator allocationValidator;

    public AllocationService(
        RoomRepository roomRepository,
        AllocationRepository allocationRepository,
        AllocationMapper allocationMapper,
        AllocationValidator allocationValidator
    ) {
        this.roomRepository = roomRepository;
        this.allocationRepository = allocationRepository;
        this.allocationMapper = allocationMapper;
        this.allocationValidator = allocationValidator;
    }

    public AllocationDTO createAllocation(CreateAllocationDTO allocationDTO) {
        var room = roomRepository.findById(allocationDTO.getRoomId()).orElseThrow(() -> new RoomNotFoundException("Room not found: "));
        allocationValidator.validate(allocationDTO);
        var allocation = allocationMapper.fromCreateDtoToEntity(allocationDTO, room);
        allocationRepository.save(allocation);
        return allocationMapper.fromEntityToAllocationDTO(allocation);
    }
}
