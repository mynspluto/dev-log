package com.example.demo.repository;

import com.example.demo.config.QuerydslConfig;
import com.example.demo.dto.ProductSearchDto;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@Import(QuerydslConfig.class)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @BeforeEach
    void setUp() {
        // 테스트 데이터 초기화
        productRepository.deleteAll();

        // 테스트 데이터 설정
        Product product1 = Product.builder()
                .name("노트북")
                .price(1000000)
                .stock(10)
                .status(ProductStatus.AVAILABLE)
                .build();

        Product product2 = Product.builder()
                .name("스마트폰")
                .price(800000)
                .stock(20)
                .status(ProductStatus.AVAILABLE)
                .build();

        Product product3 = Product.builder()
                .name("헤드폰")
                .price(200000)
                .stock(0)
                .status(ProductStatus.SOLD_OUT)
                .build();

        productRepository.save(product1);
        productRepository.save(product2);
        productRepository.save(product3);
    }

    @Test
    @DisplayName("이름으로 상품 검색 테스트")
    void findByNameContainingTest() {
        // given
        ProductSearchDto searchDto = ProductSearchDto.builder()
                .name("노트")
                .build();

        // when
        List<Product> products = productRepository.findBySearchCondition(searchDto);

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getName()).contains("노트");
    }

    @Test
    @DisplayName("가격 범위로 상품 검색 테스트")
    void findByPriceRangeTest() {
        // given
        ProductSearchDto searchDto = ProductSearchDto.builder()
                .minPrice(500000)
                .maxPrice(900000)
                .build();

        // when
        List<Product> products = productRepository.findBySearchCondition(searchDto);

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getPrice()).isBetween(500000, 900000);
    }

    @Test
    @DisplayName("상태로 상품 검색 테스트")
    void findByStatusTest() {
        // given
        ProductSearchDto searchDto = ProductSearchDto.builder()
                .status(ProductStatus.SOLD_OUT)
                .build();

        // when
        List<Product> products = productRepository.findBySearchCondition(searchDto);

        // then
        assertThat(products).hasSize(1);
        assertThat(products.get(0).getStatus()).isEqualTo(ProductStatus.SOLD_OUT);
    }

    @Test
    @DisplayName("검색 조건 및 페이징 테스트")
    void findBySearchConditionWithPagingTest() {
        // given
        ProductSearchDto searchDto = ProductSearchDto.builder()
                .status(ProductStatus.AVAILABLE)
                .build();
        PageRequest pageRequest = PageRequest.of(0, 1);

        // when
        Page<Product> productPage = productRepository.findBySearchCondition(searchDto, pageRequest);

        // then
        assertThat(productPage.getContent()).hasSize(1);
        assertThat(productPage.getTotalElements()).isEqualTo(2);
        assertThat(productPage.getTotalPages()).isEqualTo(2);
    }
}