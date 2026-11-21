package llc.devlab.app.audit;

import jakarta.persistence.criteria.Predicate;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class AuditSpecification {

    private AuditSpecification() {
    }

    public static Specification<AuditEvent> fromFilter(AuditFilterRequest filter) {
        return (root, query, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (hasText(filter.actorId())) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("actorId")), filter.actorId().toLowerCase()));
            }

            if (hasText(filter.action())) {
                predicates.add(criteriaBuilder.equal(root.get("action"), filter.action()));
            }

            if (hasText(filter.targetType())) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("targetType")), filter.targetType().toLowerCase()));
            }

            if (hasText(filter.targetId())) {
                predicates.add(criteriaBuilder.equal(criteriaBuilder.lower(root.get("targetId")), filter.targetId().toLowerCase()));
            }

            if (filter.dateFrom() != null) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("occurredAt"), filter.dateFrom()));
            }
            if (filter.dateTo() != null) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("occurredAt"), filter.dateTo()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private static boolean hasText(String value) {
        return value != null && !value.isBlank();
    }
}
