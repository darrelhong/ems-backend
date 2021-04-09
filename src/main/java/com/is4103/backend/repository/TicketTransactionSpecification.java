package com.is4103.backend.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.is4103.backend.dto.ticketing.TicketTransactionCriteria;
import com.is4103.backend.model.TicketTransaction;

import org.springframework.data.jpa.domain.Specification;

public class TicketTransactionSpecification implements Specification<TicketTransaction> {

    private static final long serialVersionUID = 1L;

    private TicketTransactionCriteria criteria;

    public TicketTransactionSpecification(TicketTransactionCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<TicketTransaction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        predicates.add(builder.equal(root.get("event").get("eid"), criteria.getEventId()));

        if (predicates.size() > 0) {
            return builder.and(predicates.toArray(Predicate[]::new));
        } else {
            return null;
        }
    }
}
