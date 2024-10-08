package com.abde.aji_n9raw_kitab_api.book;

import org.springframework.data.jpa.domain.Specification;

public class BookSpecification {

    public static Specification<Book> withOwnerId(Integer userId){
        return ((root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("owner").get("id"), userId));
    }
}
