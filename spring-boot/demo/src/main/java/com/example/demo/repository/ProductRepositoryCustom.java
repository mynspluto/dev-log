package com.example.demo.repository;

import com.example.demo.dto.ProductSearchDto;
import com.example.demo.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ProductRepositoryCustom {
    List<Product> findBySearchCondition(ProductSearchDto searchDto);
    Page<Product> findBySearchCondition(ProductSearchDto searchDto, Pageable pageable);
}