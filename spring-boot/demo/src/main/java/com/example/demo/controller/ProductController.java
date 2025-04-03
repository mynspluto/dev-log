package com.example.demo.controller;

import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.dto.ProductSearchDto;
import com.example.demo.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/products")
@RequiredArgsConstructor
public class ProductController {

    private final ProductService productService;

    @PostMapping
    public ResponseEntity<ProductResponseDto> createProduct(@Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto responseDto = productService.createProduct(requestDto);
        return new ResponseEntity<>(responseDto, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductResponseDto> getProduct(@PathVariable Long id) {
        ProductResponseDto responseDto = productService.getProduct(id);
        return ResponseEntity.ok(responseDto);
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductResponseDto> updateProduct(
            @PathVariable Long id,
            @Valid @RequestBody ProductRequestDto requestDto) {
        ProductResponseDto responseDto = productService.updateProduct(id, requestDto);
        return ResponseEntity.ok(responseDto);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/search")
    public ResponseEntity<List<ProductResponseDto>> searchProducts(ProductSearchDto searchDto) {
        List<ProductResponseDto> responseDtos = productService.searchProducts(searchDto);
        return ResponseEntity.ok(responseDtos);
    }

    @GetMapping("/search/paging")
    public ResponseEntity<Page<ProductResponseDto>> searchProductsWithPaging(
            ProductSearchDto searchDto,
            @PageableDefault(size = 10) Pageable pageable) {
        Page<ProductResponseDto> responseDtos = productService.searchProductsWithPaging(searchDto, pageable);
        return ResponseEntity.ok(responseDtos);
    }

    @PostMapping("/{id}/decrease-stock")
    public ResponseEntity<ProductResponseDto> decreaseStock(
            @PathVariable Long id,
            @RequestParam int quantity) {
        ProductResponseDto responseDto = productService.decreaseStock(id, quantity);
        return ResponseEntity.ok(responseDto);
    }
}