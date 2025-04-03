package com.example.demo.repository;

import com.example.demo.dto.ProductSearchDto;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import com.example.demo.entity.QProduct;
import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.util.StringUtils;

import java.util.List;

@RequiredArgsConstructor
public class ProductRepositoryImpl implements ProductRepositoryCustom {

    private final JPAQueryFactory queryFactory;

    @Override
    public List<Product> findBySearchCondition(ProductSearchDto searchDto) {
        QProduct product = QProduct.product;

        return queryFactory
                .selectFrom(product)
                .where(
                        nameContains(searchDto.getName()),
                        priceGoe(searchDto.getMinPrice()),
                        priceLoe(searchDto.getMaxPrice()),
                        statusEq(searchDto.getStatus())
                )
                .fetch();
    }

    @Override
    public Page<Product> findBySearchCondition(ProductSearchDto searchDto, Pageable pageable) {
        QProduct product = QProduct.product;

        QueryResults<Product> results = queryFactory
                .selectFrom(product)
                .where(
                        nameContains(searchDto.getName()),
                        priceGoe(searchDto.getMinPrice()),
                        priceLoe(searchDto.getMaxPrice()),
                        statusEq(searchDto.getStatus())
                )
                .offset(pageable.getOffset())
                .limit(pageable.getPageSize())
                .orderBy(product.id.desc())
                .fetchResults();

        return new PageImpl<>(results.getResults(), pageable, results.getTotal());
    }

    private BooleanExpression nameContains(String name) {
        return StringUtils.hasText(name) ? QProduct.product.name.contains(name) : null;
    }

    private BooleanExpression priceGoe(Integer minPrice) {
        return minPrice != null ? QProduct.product.price.goe(minPrice) : null;
    }

    private BooleanExpression priceLoe(Integer maxPrice) {
        return maxPrice != null ? QProduct.product.price.loe(maxPrice) : null;
    }

    private BooleanExpression statusEq(Enum<?> status) {
        return status != null ? QProduct.product.status.eq((ProductStatus) status) : null;
    }
}