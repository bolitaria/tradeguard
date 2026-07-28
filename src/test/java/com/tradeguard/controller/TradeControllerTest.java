package com.tradeguard.controller;

import com.tradeguard.dto.TradeRequest;
import com.tradeguard.dto.TradeResponse;
import com.tradeguard.service.TradeValidationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc(addFilters = false)   // desactiva filtros para test
class TradeControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private TradeValidationService validationService;

    @Test
    void shouldReturnOkWhenValidRequest() throws Exception {
        TradeResponse response = new TradeResponse(1L, "AAPL", 100, 150.0, "John", "APPROVED", null);
        when(validationService.validateAndSave(any(TradeRequest.class))).thenReturn(response);

        mockMvc.perform(post("/api/trades")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"symbol\":\"AAPL\",\"quantity\":100,\"price\":150.0,\"traderName\":\"John\"}"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("APPROVED"));
    }

    @Test
    void shouldReturnBadRequestWhenSymbolMissing() throws Exception {
        mockMvc.perform(post("/api/trades")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON)
                .content("{\"quantity\":100,\"price\":150.0,\"traderName\":\"John\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error").value("Validation failed"));
    }
}
