package fr.otel.api.rooms.infrastructure;

import fr.otel.api.reservations.infrastructure.ReservationEntity;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Subquery;

public class RoomSpecification {
    public static Specification<RoomEntity> buildSpec(Boolean available, LocalDate from, LocalDate to) {
        return (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (available != null && (from != null && to != null)) {
                if (available) {
                    // Room is available if there does NOT EXIST an overlapping reservation
                    Subquery<Long> subquery = query.subquery(Long.class);
                    var subRoot = subquery.from(ReservationEntity.class);
                    subquery.select(cb.literal(1L));
                    subquery.where(
                        cb.equal(subRoot.get("room"), root),
                        cb.lessThanOrEqualTo(subRoot.get("startDate"), to),
                        cb.greaterThanOrEqualTo(subRoot.get("endDate"), from)
                    );
                    predicates.add(cb.not(cb.exists(subquery)));
                } else {
                    // Room is unavailable if there is AT LEAST one overlapping reservation
                    Join<RoomEntity, ReservationEntity> reservationJoin = root.join("reservations", JoinType.INNER);
                    Predicate overlap = cb.and(
                        cb.lessThanOrEqualTo(reservationJoin.get("startDate"), to),
                        cb.greaterThanOrEqualTo(reservationJoin.get("endDate"), from)
                    );
                    predicates.add(overlap);
                }
            }
            return cb.and(predicates.toArray(new Predicate[0]));
        };
    }
} 