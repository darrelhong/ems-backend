package com.is4103.backend.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.is4103.backend.dto.EventSearchCriteria;
import com.is4103.backend.model.Event;

import org.springframework.data.jpa.domain.Specification;

public class EventSpecification implements Specification<Event> {

    private static final long serialVersionUID = 1L;

    private EventSearchCriteria criteria;

    public EventSpecification(EventSearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Event> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (criteria.getKeyword() != null) {
            predicates.add(
                    builder.like(builder.lower(root.get("name")), "%" + criteria.getKeyword().toLowerCase() + "%"));
        }

        if (criteria.getCategory() != null) {
            predicates.add(builder.isMember(criteria.getCategory(), root.get("categories")));
        }

        if (predicates.size() > 0) {
            return builder.or(predicates.toArray(Predicate[]::new));
        } else {
            return null;
        }
    }
}
