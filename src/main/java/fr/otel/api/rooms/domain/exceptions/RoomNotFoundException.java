package fr.otel.api.rooms.domain.exceptions;

import java.util.UUID;

public class RoomNotFoundException extends RuntimeException {
    private final String uuid;

    public RoomNotFoundException(UUID uuid, Throwable cause) {
        super("Room with ID " + uuid + " not found.", cause);
        this.uuid = String.valueOf(uuid);
    }

    public RoomNotFoundException(String uuid, Throwable cause) {
        super("Room with ID " + uuid + " not found.", cause);
        this.uuid = uuid;
    }

    public RoomNotFoundException(UUID uuid) {
        super("Room with ID " + uuid + " not found.");
        this.uuid = String.valueOf(uuid);
    }

    public RoomNotFoundException(String uuid) {
        super("Room with ID " + uuid + " not found.");
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
} 