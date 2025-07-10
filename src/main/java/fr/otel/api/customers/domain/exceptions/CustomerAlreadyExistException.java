package fr.otel.api.customers.domain.exceptions;

public class CustomerAlreadyExistException extends RuntimeException {

    private final String email;

    public CustomerAlreadyExistException(String email, Throwable cause) {
        super("User with email " + email + " already exist.", cause);
        this.email = email;
    }

    public CustomerAlreadyExistException(String email) {
        super("User with email " + email + " already exist.");
        this.email = email;
    }

    public String getEmail() {
        return email;
    }
}
