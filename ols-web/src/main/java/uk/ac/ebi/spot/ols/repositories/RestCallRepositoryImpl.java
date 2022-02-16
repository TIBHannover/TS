package uk.ac.ebi.spot.ols.repositories;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.query.QueryUtils;
import org.springframework.stereotype.Repository;
import org.springframework.web.util.UriUtils;
import uk.ac.ebi.spot.ols.controller.dto.RestCallDto;
import uk.ac.ebi.spot.ols.controller.dto.RestCallRequest;
import uk.ac.ebi.spot.ols.entities.RestCall;

import javax.persistence.EntityManager;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.io.UnsupportedEncodingException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class RestCallRepositoryImpl implements RestCallRepositoryCustom {
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public static final String CREATED_AT_COLUMN = "createdAt";

    private final EntityManager em;

    @Autowired
    public RestCallRepositoryImpl(EntityManager em) {
        this.em = em;
    }

    @Override
    public Page<RestCallDto> query(RestCallRequest request, Pageable pageable) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<RestCall> query = builder.createQuery(RestCall.class);

        Root<RestCall> root = query.from(RestCall.class);
        List<Predicate> predicates = getPredicates(request, builder, root);

        query.where(builder.and(predicates.toArray(new Predicate[0])));
        query.orderBy(QueryUtils.toOrders(pageable.getSort(), root, builder));

        List<RestCall> list = em.createQuery(query)
            .setFirstResult(pageable.getOffset())
            .setMaxResults(pageable.getPageSize())
            .getResultList();

        Long count = getCount(builder, predicates);

        List<RestCallDto> dtos = list.stream()
            .map(RestCallDto::of)
            .collect(Collectors.toList());

        return new PageImpl<>(dtos, pageable, count);
    }

    @Override
    public Long count(RestCallRequest request) {
        CriteriaBuilder builder = em.getCriteriaBuilder();
        CriteriaQuery<RestCall> query = builder.createQuery(RestCall.class);

        Root<RestCall> root = query.from(RestCall.class);
        List<Predicate> predicates = getPredicates(request, builder, root);

        query.where(builder.and(predicates.toArray(new Predicate[0])));

        return getCount(builder, predicates);
    }

    private List<Predicate> getPredicates(RestCallRequest request, CriteriaBuilder builder, Root<RestCall> root) {
        List<Predicate> predicates = new ArrayList<>();

        String url = getDecodedUrl(request);

        if (url != null) {
            predicates.add(builder.equal(root.get("url"), request.getUrl()));
        }

        if (request.getDateTimeFrom() != null) {
            predicates.add(builder.greaterThanOrEqualTo(root.get(CREATED_AT_COLUMN), request.getDateTimeFrom()));
        }

        if (request.getDateTimeTo() != null) {
            predicates.add(builder.lessThanOrEqualTo(root.get(CREATED_AT_COLUMN), request.getDateTimeTo()));
        }
        return predicates;
    }

    private String getDecodedUrl(RestCallRequest request) {
        if (request.getUrl() == null) {
            return null;
        }

        String decodedUrl = null;
        try {
            decodedUrl = UriUtils.decode(request.getUrl(), StandardCharsets.UTF_8.toString());
        } catch (UnsupportedEncodingException e) {
            logger.error("Could not get query parameters: {}", e.getLocalizedMessage());
        }

        return decodedUrl;
    }

    private Long getCount(CriteriaBuilder builder, List<Predicate> predicates) {
        CriteriaQuery<Long> countQuery = builder.createQuery(Long.class);
        Root<RestCall> booksRootCount = countQuery.from(RestCall.class);
        countQuery
            .select(builder.count(booksRootCount))
            .where(builder.and(predicates.toArray(new Predicate[0])));

        return em.createQuery(countQuery).getSingleResult();
    }
}
