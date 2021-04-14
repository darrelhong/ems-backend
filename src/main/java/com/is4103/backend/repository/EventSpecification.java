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

        if (criteria.getIsPublished() != null) {
            predicates.add(builder.equal(root.get("isPublished"), criteria.getIsPublished()));
        }

        if (criteria.getEventStartAfter() != null) {
            predicates.add(builder.greaterThan(root.get("eventStartDate"), criteria.getEventStartAfter()));
        }

        if (criteria.getKeyword() != null) {
            predicates.add(
                    builder.like(builder.lower(root.get("name")), "%" + criteria.getKeyword().toLowerCase() + "%"));
        }

        if (criteria.getCategory() != null) {
            predicates.add(builder.equal(root.get("category"), criteria.getCategory()));
        }

        if (predicates.size() > 0) {
            return builder.and(predicates.toArray(Predicate[]::new));
        } else {
            return null;
        }
    }
}
