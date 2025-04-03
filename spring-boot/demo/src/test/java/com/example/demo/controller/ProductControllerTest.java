package com.example.demo.controller;

import com.example.demo.dto.ProductRequestDto;
import com.example.demo.dto.ProductResponseDto;
import com.example.demo.dto.ProductSearchDto;
import com.example.demo.entity.ProductStatus;
import com.example.demo.exception.ErrorCode;
import com.example.demo.exception.NotFoundException;
import com.example.demo.service.ProductService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private ProductService productService;

    @Test
    @DisplayName("상품 생성 테스트")
    void createProductTest() throws Exception {
        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build();

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(1L)
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productService.createProduct(any(ProductRequestDto.class))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()))
                .andExpect(jsonPath("$.price").value(responseDto.getPrice()))
                .andExpect(jsonPath("$.stock").value(responseDto.getStock()))
                .andExpect(jsonPath("$.status").value(responseDto.getStatus().toString()));
    }

    @Test
    @DisplayName("유효하지 않은 입력으로 상품 생성 시 400 응답 테스트")
    void createProductInvalidInputTest() throws Exception {
        // given
        ProductRequestDto requestDto = ProductRequestDto.builder()
                .name("") // 빈 이름 (유효하지 않음)
                .price(-1000) // 음수 가격 (유효하지 않음)
                .stock(-10) // 음수 재고 (유효하지 않음)
                .status(null) // null 상태 (유효하지 않음)
                .build();

        // when & then
        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(requestDto)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.INVALID_INPUT_VALUE.getCode()));
    }

    @Test
    @DisplayName("ID로 상품 조회 테스트")
    void getProductTest() throws Exception {
        // given
        Long productId = 1L;
        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(productId)
                .name("테스트 상품")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .build();

        when(productService.getProduct(eq(productId))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(get("/api/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(responseDto.getId()))
                .andExpect(jsonPath("$.name").value(responseDto.getName()));
    }

    @Test
    @DisplayName("존재하지 않는 상품 조회 시 404 응답 테스트")
    void getProductNotFoundTest() throws Exception {
        // given
        Long productId = 999L;
        when(productService.getProduct(productId)).thenThrow(new NotFoundException("상품을 찾을 수 없습니다. ID: " + productId));

        // when & then
        mockMvc.perform(get("/api/products/{id}", productId))
                .andDo(print())
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.code").value(ErrorCode.ENTITY_NOT_FOUND.getCode()));
    }

    @Test
    @DisplayName("상품 검색 테스트")
    void searchProductsTest() throws Exception {
        // given
        ProductSearchDto searchDto = ProductSearchDto.builder()
                .name("테스트")
                .build();

        List<ProductResponseDto> responseDtos = new ArrayList<>();
        responseDtos.add(ProductResponseDto.builder()
                .id(1L)
                .name("테스트 상품1")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build());
        responseDtos.add(ProductResponseDto.builder()
                .id(2L)
                .name("테스트 상품2")
                .price(20000)
                .stock(200)
                .status(ProductStatus.AVAILABLE)
                .build());

        when(productService.searchProducts(any(ProductSearchDto.class))).thenReturn(responseDtos);

        // when & then
        mockMvc.perform(get("/api/products/search")
                        .param("name", "테스트"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].name").value("테스트 상품1"))
                .andExpect(jsonPath("$[1].name").value("테스트 상품2"));
    }

    @Test
    @DisplayName("페이징을 이용한 상품 검색 테스트")
    void searchProductsWithPagingTest() throws Exception {
        // given
        List<ProductResponseDto> content = new ArrayList<>();
        content.add(ProductResponseDto.builder()
                .id(1L)
                .name("테스트 상품1")
                .price(10000)
                .stock(100)
                .status(ProductStatus.AVAILABLE)
                .build());

        Page<ProductResponseDto> page = new PageImpl<>(content, PageRequest.of(0, 10), 1);

        when(productService.searchProductsWithPaging(any(ProductSearchDto.class), any(PageRequest.class))).thenReturn(page);

        // when & then
        mockMvc.perform(get("/api/products/search/paging")
                        .param("status", "AVAILABLE")
                        .param("page", "0")
                        .param("size", "10"))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.content[0].name").value("테스트 상품1"))
                .andExpect(jsonPath("$.totalElements").value(1));
    }

    @Test
    @DisplayName("재고 감소 테스트")
    void decreaseStockTest() throws Exception {
        // given
        Long productId = 1L;
        int quantity = 5;

        ProductResponseDto responseDto = ProductResponseDto.builder()
                .id(productId)
                .name("테스트 상품")
                .price(10000)
                .stock(5) // 원래 10에서 5 감소
                .status(ProductStatus.AVAILABLE)
                .build();

        when(productService.decreaseStock(eq(productId), eq(quantity))).thenReturn(responseDto);

        // when & then
        mockMvc.perform(post("/api/products/{id}/decrease-stock", productId)
                        .param("quantity", String.valueOf(quantity)))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.stock").value(5));
    }

    @Test
    @DisplayName("재고 이상의 수량 요청 시 400 응답 테스트")
    void decreaseStockExceptionTest() throws Exception {
        // given
        Long productId = 1L;
        int quantity = 20;

        doThrow(new IllegalArgumentException("재고가 부족합니다. 현재 재고: 10"))
                .when(productService).decreaseStock(eq(productId), eq(quantity));

        // when & then
        mockMvc.perform(post("/api/products/{id}/decrease-stock", productId)
                        .param("quantity", String.valueOf(quantity)))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.code").value(ErrorCode.OUT_OF_STOCK.getCode()));
    }
}