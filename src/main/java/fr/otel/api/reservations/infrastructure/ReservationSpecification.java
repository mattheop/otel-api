package fr.otel.api.reservations.infrastructure;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class ReservationSpecification {

    public static Specification<ReservationEntity> buildSpec(UUID reservationUUID, UUID customerId, UUID roomId) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (reservationUUID != null) {
                predicates.add(cb.equal(root.get("id"), reservationUUID));
            }
            if (customerId != null) {
                predicates.add(cb.equal(root.join("customer").get("id"), customerId));
            }
            if (roomId != null) {
                predicates.add(cb.equal(root.join("room").get("id"), roomId));
            }

            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
}
