package br.com.sw2you.realmeet.controller;

import static java.util.concurrent.CompletableFuture.runAsync;
import static java.util.concurrent.CompletableFuture.supplyAsync;

import br.com.sw2you.realmeet.api.facade.RoomsApi;
import br.com.sw2you.realmeet.api.model.CreateRoomDTO;
import br.com.sw2you.realmeet.api.model.RoomDTO;
import br.com.sw2you.realmeet.api.model.UpdateRoomDTO;
import br.com.sw2you.realmeet.service.RoomService;
import br.com.sw2you.realmeet.util.ResponseEntityUtils;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RoomController implements RoomsApi {
    private final Executor controllerExecutor;
    private final RoomService roomService;

    public RoomController(Executor controllerExecutor, RoomService roomService) {
        this.controllerExecutor = controllerExecutor;
        this.roomService = roomService;
    }

    @Override
    public CompletableFuture<ResponseEntity<RoomDTO>> getRoom(Long id) {
        return supplyAsync(() -> roomService.getRoom(id), controllerExecutor).thenApply(ResponseEntityUtils::ok);
    }

    @Override
    public CompletableFuture<ResponseEntity<RoomDTO>> createRoom(CreateRoomDTO createRoomDTO) {
        return supplyAsync(() -> roomService.createRoom(createRoomDTO), controllerExecutor)
            .thenApply(ResponseEntityUtils::created);
    }

    @Override
    public CompletableFuture<ResponseEntity<Void>> deleteRoom(Long id) {
        return runAsync(() -> roomService.deleteRoom(id), controllerExecutor).thenApply(ResponseEntityUtils::noContent);
    }

    @Override
    public CompletableFuture<ResponseEntity<Void>> updateRoom(Long id, UpdateRoomDTO updateRoomDTO) {
        return runAsync(() -> roomService.updateRoom(id, updateRoomDTO), controllerExecutor)
            .thenApply(ResponseEntityUtils::noContent);
    }
}
