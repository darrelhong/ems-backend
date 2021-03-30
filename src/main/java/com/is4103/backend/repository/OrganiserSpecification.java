package com.is4103.backend.repository;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

import com.is4103.backend.dto.OrganiserSearchCriteria;
import com.is4103.backend.dto.PartnerSearchCriteria;
import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.EventOrganiser;

import org.springframework.data.jpa.domain.Specification;

public class OrganiserSpecification implements Specification<EventOrganiser> {

    private static final long serialVersionUID = 1L;

    private OrganiserSearchCriteria criteria;

    public OrganiserSpecification(OrganiserSearchCriteria criteria) {
        super();
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<EventOrganiser> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        List<Predicate> predicates = new ArrayList<Predicate>();

        if (criteria.getKeyword() != null) {
            predicates.add(
                    builder.like(builder.lower(root.get("name")), "%" + criteria.getKeyword().toLowerCase() + "%"));
        }

        if (predicates.size() > 0) {
            return builder.or(predicates.toArray(Predicate[]::new));
        } else {
            return null;
        }
    }

}
