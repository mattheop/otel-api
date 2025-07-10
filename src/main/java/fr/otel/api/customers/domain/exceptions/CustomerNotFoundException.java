package fr.otel.api.customers.domain.exceptions;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    private final String uuid;

    public CustomerNotFoundException(UUID uuid, Throwable cause) {
        super("User with ID " + uuid + " not found.", cause);
        this.uuid = String.valueOf(uuid);
    }

    public CustomerNotFoundException(String uuid, Throwable cause) {
        super("User with ID " + uuid + " not found.", cause);
        this.uuid = uuid;
    }

    public CustomerNotFoundException(UUID uuid) {
        super("User with ID " + uuid + " not found.");
        this.uuid = String.valueOf(uuid);
    }

    public CustomerNotFoundException(String uuid) {
        super("User with ID " + uuid + " not found.");
        this.uuid = uuid;
    }

    public String getUuid() {
        return uuid;
    }
}
