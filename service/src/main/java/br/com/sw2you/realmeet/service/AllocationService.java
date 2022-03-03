package br.com.sw2you.realmeet.service;

import br.com.sw2you.realmeet.api.model.AllocationDTO;
import br.com.sw2you.realmeet.api.model.CreateAllocationDTO;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.mapper.AllocationMapper;
import org.springframework.stereotype.Service;

@Service
public class AllocationService {
    private final RoomService roomService;
    private final AllocationRepository allocationRepository;
    private final AllocationMapper allocationMapper;

    public AllocationService(
        RoomService roomService,
        AllocationRepository allocationRepository,
        AllocationMapper allocationMapper
    ) {
        this.roomService = roomService;
        this.allocationRepository = allocationRepository;
        this.allocationMapper = allocationMapper;
    }

    public AllocationDTO createAllocation(CreateAllocationDTO allocationDTO) {
        return null;
    }
}
