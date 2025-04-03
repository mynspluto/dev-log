package com.example.demo.service;

import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.dto.ProductSearchDto;
import com.example.demo.entity.Product;
import com.example.demo.entity.ProductStatus;
import com.example.demo.exception.NotFoundException;
import com.example.demo.repository.ProductRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductService productService;

    @Test
    @DisplayName("상품 생성 테스트")
    void createProductTest() {
        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build();

        Product savedProduct = Product.builder()
                .name(requestDto.getName())
                .price(requestDto.getPrice())
                .stock(requestDto.getStock())
                .status(requestDto.getStatus())
                .build();

        // savedProduct에 ID를 설정하기 위한 리플렉션 사용 또는 MockitoAnnotations 사용
        // 여기서는 when 구문에서 직접 mock 값 설정

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        ProductResponseDto responseDto = productService.createProduct(requestDto);

        // then
        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
        assertThat(responseDto.getPrice()).isEqualTo(requestDto.getPrice());
        assertThat(responseDto.getStock()).isEqualTo(requestDto.getStock());
        assertThat(responseDto.getStatus()).isEqualTo(requestDto.getStatus());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    @DisplayName("ID로 상품 조회 테스트")
    void getProductTest() {
        // given
        Long productId = 1L;
        Product product = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        ProductResponseDto responseDto = productService.getProduct(productId);

        // then
        assertThat(responseDto.getName()).isEqualTo(product.getName());
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 예외 발생 테스트")
    void getProductNotFoundTest() {
        // given
        Long productId = 999L;
        when(productRepository.findById(productId)).thenReturn(Optional.empty());

        // when & then
        assertThatThrownBy(() -> productService.getProduct(productId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("상품을 찾을 수 없습니다");

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 업데이트 테스트")
    void updateProductTest() {
        // given
        Long productId = 1L;
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("업데이트 상품")
                .price(20000)
                .stock(50)
                .status(ProductStatus.AVAILABLE)
                .build();

        Product product = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        ProductResponseDto responseDto = productService.updateProduct(productId, requestDto);

        // then
        assertThat(responseDto.getName()).isEqualTo(requestDto.getName());
        assertThat(responseDto.getPrice()).isEqualTo(requestDto.getPrice());
        assertThat(responseDto.getStock()).isEqualTo(requestDto.getStock());

        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("상품 검색 테스트")
    void searchProductsTest() {
        // given
        ProductSearchDto searchDto = ProductSearchDto.builder()
                .name("테스트")
                .build();

        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
                .name("테스트 상품1")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build());
        products.add(Product.builder()
                .name("테스트 상품2")
                .price(20000)
                .stock(200)
                .status(ProductStatus.AVAILABLE)
                .build());

        when(productRepository.findBySearchCondition(searchDto)).thenReturn(products);

        // when
        List<ProductResponseDto> responseDtos = productService.searchProducts(searchDto);

        // then
        assertThat(responseDtos).hasSize(2);
        assertThat(responseDtos.get(0).getName()).contains("테스트");
        assertThat(responseDtos.get(1).getName()).contains("테스트");

        verify(productRepository, times(1)).findBySearchCondition(searchDto);
    }

    @Test
    @DisplayName("페이징을 이용한 상품 검색 테스트")
    void searchProductsWithPagingTest() {
        // given
        ProductSearchDto searchDto = ProductSearchDto.builder()
                .status(ProductStatus.AVAILABLE)
                .build();

        Pageable pageable = PageRequest.of(0, 10);
        List<Product> products = new ArrayList<>();
        products.add(Product.builder()
                .name("테스트 상품1")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build());

        Page<Product> productPage = new PageImpl<>(products, pageable, products.size());

        when(productRepository.findBySearchCondition(eq(searchDto), any(Pageable.class))).thenReturn(productPage);

        // when
        Page<ProductResponseDto> responseDtoPage = productService.searchProductsWithPaging(searchDto, pageable);

        // then
        assertThat(responseDtoPage.getContent()).hasSize(1);
        assertThat(responseDtoPage.getTotalElements()).isEqualTo(1);

        verify(productRepository, times(1)).findBySearchCondition(eq(searchDto), any(Pageable.class));
    }

    @Test
    @DisplayName("재고 감소 테스트")
    void decreaseStockTest() {
        // given
        Long productId = 1L;
        int quantity = 5;

        Product product = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(10)
                .status(ProductStatus.AVAILABLE)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when
        ProductResponseDto responseDto = productService.decreaseStock(productId, quantity);

        // then
        assertThat(responseDto.getStock()).isEqualTo(5);
        verify(productRepository, times(1)).findById(productId);
    }

    @Test
    @DisplayName("재고 이상의 수량 요청 시 예외 발생 테스트")
    void decreaseStockExceptionTest() {
        // given
        Long productId = 1L;
        int quantity = 20;

        Product product = Product.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(10)
                .status(ProductStatus.AVAILABLE)
                .build();

        when(productRepository.findById(productId)).thenReturn(Optional.of(product));

        // when & then
        assertThatThrownBy(() -> productService.decreaseStock(productId, quantity))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("재고가 부족합니다");

        verify(productRepository, times(1)).findById(productId);
    }
}