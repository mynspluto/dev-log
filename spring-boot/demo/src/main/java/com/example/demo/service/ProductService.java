package com.example.demo.service;

import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.dto.ProductSearchDto;
import com.example.demo.entity.Product;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ProductService {

    private final ProductRepository productRepository;

    @Transactional
    public ProductResponseDto createProduct(ProductRequestDto requestDto) {
        Product product = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .status(requestDto.getStatus())
                .build();

        Product savedProduct = productRepository.save(product);
        return ProductResponseDto.of(savedProduct);
    }

    public ProductResponseDto getProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다. ID: " + id));

        return ProductResponseDto.of(product);
    }

    @Transactional
    public ProductResponseDto updateProduct(Long id, ProductRequestDto requestDto) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다. ID: " + id));

        product.update(requestDto.getName(), requestDto.getPrice(), requestDto.getStock(), requestDto.getStatus());
        return ProductResponseDto.of(product);
    }

    @Transactional
    public void deleteProduct(Long id) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다. ID: " + id));

        productRepository.delete(product);
    }

    public List<ProductResponseDto> searchProducts(ProductSearchDto searchDto) {
        return productRepository.findBySearchCondition(searchDto)
                .stream()
                .map(ProductResponseDto::of)
                .collect(Collectors.toList());
    }

    public Page<ProductResponseDto> searchProductsWithPaging(ProductSearchDto searchDto, Pageable pageable) {
        return productRepository.findBySearchCondition(searchDto, pageable)
                .map(ProductResponseDto::of);
    }

    @Transactional
    public ProductResponseDto decreaseStock(Long id, int quantity) {
        Product product = productRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("상품을 찾을 수 없습니다. ID: " + id));

        product.decreaseStock(quantity);
        return ProductResponseDto.of(product);
    }
}