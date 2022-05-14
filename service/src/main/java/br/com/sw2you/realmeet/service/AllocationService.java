package br.com.sw2you.realmeet.service;

import static br.com.sw2you.realmeet.domain.entity.Allocation.SORTABLE_FIELDS;
import static br.com.sw2you.realmeet.util.Constants.ALLOCATIONS_MAX_FILTER_LIMITS;
import static br.com.sw2you.realmeet.util.DateUtils.DEFAULT_TIMEZONE;
import static br.com.sw2you.realmeet.util.DateUtils.now;
import static br.com.sw2you.realmeet.util.PageUtils.newPageable;
import static java.util.Objects.isNull;
import static java.util.stream.Collectors.toList;

import br.com.sw2you.realmeet.api.model.AllocationDTO;
import br.com.sw2you.realmeet.api.model.CreateAllocationDTO;
import br.com.sw2you.realmeet.api.model.UpdateAllocationDTO;
import br.com.sw2you.realmeet.domain.entity.Allocation;
import br.com.sw2you.realmeet.domain.repository.AllocationRepository;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.AllocationCannotBeDeletedException;
import br.com.sw2you.realmeet.exception.AllocationCannotBeUpdatedException;
import br.com.sw2you.realmeet.exception.AllocationNotFoundException;
import br.com.sw2you.realmeet.exception.RoomNotFoundException;
import br.com.sw2you.realmeet.mapper.AllocationMapper;
import br.com.sw2you.realmeet.validator.AllocationValidator;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AllocationService {
    private final RoomRepository roomRepository;
    private final AllocationRepository allocationRepository;
    private final AllocationMapper allocationMapper;
    private final AllocationValidator allocationValidator;
    private final int maxLimit;

    public AllocationService(
        RoomRepository roomRepository,
        AllocationRepository allocationRepository,
        AllocationMapper allocationMapper,
        AllocationValidator allocationValidator,
        @Value(ALLOCATIONS_MAX_FILTER_LIMITS) int maxLimit
    ) {
        this.roomRepository = roomRepository;
        this.allocationRepository = allocationRepository;
        this.allocationMapper = allocationMapper;
        this.allocationValidator = allocationValidator;
        this.maxLimit = maxLimit;
    }

    public AllocationDTO createAllocation(CreateAllocationDTO allocationDTO) {
        var room = roomRepository
            .findById(allocationDTO.getRoomId())
            .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        allocationValidator.validate(allocationDTO);
        var allocation = allocationMapper.fromCreateDtoToEntity(allocationDTO, room);
        allocationRepository.save(allocation);
        return allocationMapper.fromEntityToAllocationDTO(allocation);
    }

    public void deleteAllocation(Long allocationId) {
        var allocation = getAllocationOrThrow(allocationId);

        if (isAllocationInThePast(allocation)) {
            throw new AllocationCannotBeDeletedException();
        }

        allocationRepository.delete(allocation);
    }

    @Transactional
    public void updateAllocation(Long allocationId, UpdateAllocationDTO updateAllocationDTO) {
        var allocation = getAllocationOrThrow(allocationId);
        allocationValidator.validate(allocationId, allocation.getRoom().getId(), updateAllocationDTO);

        if (isAllocationInThePast(allocation)) {
            throw new AllocationCannotBeUpdatedException();
        }

        allocationRepository.updateAllocation(
            allocationId,
            updateAllocationDTO.getSubject(),
            updateAllocationDTO.getStartAt(),
            updateAllocationDTO.getEndAt()
        );
    }

    public List<AllocationDTO> listAllocation(
        String employeeEmail,
        Long roomId,
        LocalDate startAt,
        LocalDate endAt,
        String orderBy,
        Integer limit,
        Integer page
    ) {
        Pageable pageable = newPageable(page, limit, maxLimit, orderBy, SORTABLE_FIELDS);
        return allocationRepository
            .findAllWithFilters(
                employeeEmail,
                roomId,
                isNull(startAt) ? null : startAt.atTime(LocalTime.MIN.atOffset(DEFAULT_TIMEZONE)),
                isNull(endAt) ? null : endAt.atTime(LocalTime.MAX.atOffset(DEFAULT_TIMEZONE)),
                pageable
            )
            .stream()
            .map(allocationMapper::fromEntityToAllocationDTO)
            .collect(toList());
    }

    private boolean isAllocationInThePast(Allocation allocation) {
        return allocation.getEndAt().isBefore(now());
    }

    private Allocation getAllocationOrThrow(Long allocationId) {
        return allocationRepository
            .findById(allocationId)
            .orElseThrow(() -> new AllocationNotFoundException("Allocation not found: " + allocationId));
    }
}
