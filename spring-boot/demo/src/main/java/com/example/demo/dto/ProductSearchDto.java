package com.example.demo.dto;

import com.example.demo.entity.ProductStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSearchDto {
    private String name;
    private Integer minPrice;
    private Integer maxPrice;
    private ProductStatus status;
}