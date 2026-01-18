package com.jewelry.pos.web.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jewelry.pos.domain.repository.ProductRepository;
import com.jewelry.pos.service.CheckoutService;
import com.jewelry.pos.web.dto.SaleRequestDTO;
import com.jewelry.pos.web.mapper.ProductMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PosController.class)
class PosControllerTest {

    @Autowired private MockMvc mockMvc;
    @Autowired private ObjectMapper objectMapper;

    // CHANGED: @MockBean -> @MockitoBean
    @MockitoBean private CheckoutService checkoutService;
    @MockitoBean private ProductRepository productRepository;
    @MockitoBean private ProductMapper productMapper;

    @Test
    @WithMockUser(roles = "USER")
    void checkout_ShouldReturnCreated_WhenValid() throws Exception {
        SaleRequestDTO request = new SaleRequestDTO("12345", new BigDecimal("3500.00"));

        mockMvc.perform(post("/api/pos/checkout")
                        .with(csrf()) // Now available because of spring-security-test
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated());
    }

    @Test
    @WithMockUser(roles = "USER")
    void checkout_ShouldReturnBadRequest_WhenRateIsNegative() throws Exception {
        // Invalid Rate: -100
        SaleRequestDTO request = new SaleRequestDTO("12345", new BigDecimal("-100.00"));

        mockMvc.perform(post("/api/pos/checkout")
                        .with(csrf())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}