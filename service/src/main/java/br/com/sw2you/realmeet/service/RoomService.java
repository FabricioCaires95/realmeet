package br.com.sw2you.realmeet.service;

import static java.util.Objects.requireNonNull;

import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.api.model.RoomDTO;
import br.com.sw2you.realmeet.domain.entity.Room;
import br.com.sw2you.realmeet.domain.repository.RoomRepository;
import br.com.sw2you.realmeet.exception.RoomNotFoundException;
import br.com.sw2you.realmeet.mapper.RoomMapper;
import br.com.sw2you.realmeet.validator.RoomValidator;
import org.springframework.stereotype.Service;

@Service
public class RoomService {
    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;
    private final RoomValidator roomValidator;

    public RoomService(RoomRepository roomRepository, RoomMapper roomMapper, RoomValidator roomValidator) {
        this.roomRepository = roomRepository;
        this.roomMapper = roomMapper;
        this.roomValidator = roomValidator;
    }

    public RoomDTO getRoom(Long id) {
        requireNonNull(id);
        Room room = roomRepository
            .findByIdAndActive(id, true)
            .orElseThrow(() -> new RoomNotFoundException("Room not found"));
        return roomMapper.fromEntityToDto(room);
    }

    public RoomDTO createRoom(CreateRoomDTO createRoomDTO) {
        roomValidator.validate(createRoomDTO);
        var room = roomMapper.fromCreateDtoToEntity(createRoomDTO);
        roomRepository.save(room);
        return new RoomDTO().id(room.getId()).name(room.getName()).seats(room.getSeats());
    }
}
