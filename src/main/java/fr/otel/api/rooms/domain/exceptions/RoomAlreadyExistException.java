package fr.otel.api.rooms.domain.exceptions;

public class RoomAlreadyExistException extends RuntimeException {
    private final String roomNumber;

    public RoomAlreadyExistException(String roomNumber, Throwable cause) {
        super("Room with number " + roomNumber + " already exists.", cause);
        this.roomNumber = roomNumber;
    }

    public RoomAlreadyExistException(String roomNumber) {
        super("Room with number " + roomNumber + " already exists.");
        this.roomNumber = roomNumber;
    }

    public String getRoomNumber() {
        return roomNumber;
    }
} 