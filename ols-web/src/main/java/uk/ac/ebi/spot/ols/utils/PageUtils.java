package uk.ac.ebi.spot.ols.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PagedResourcesAssembler;
import org.springframework.hateoas.PagedResources;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.hateoas.server.ResourceSupportAssembler;

import java.util.List;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.NONE)
public class PageUtils {

    public static <T> PageImpl<T> toPage(List<T> list, Pageable pageable) {
        List<T> slice = list.stream()
                .skip((long) pageable.getPageNumber() * pageable.getPageSize())
                .limit(pageable.getPageSize())
                .collect(Collectors.toList());

        return new PageImpl<>(slice, pageable, list.size());
    }

    public static <T, M extends ResourceSupport<?>> PagedResources <M> toPagedResources(
            Page<T> page,
            Class<M> resourceType,
            PagedResourcesAssembler<T> pagedResourcesAssembler,
            ResourceSupportAssembler<T, M> modelAssembler
    ) {

        return page.isEmpty()
                ? (PagedResources <M>) pagedResourcesAssembler.toEmptyModel(page, resourceType)
                : pagedResourcesAssembler.toModel(page, modelAssembler);



    }
}
