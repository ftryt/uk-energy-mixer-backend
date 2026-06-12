package com.example.energymix.controller;

import com.example.energymix.service.EnergyService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(EnergyController.class) // Focuses only on the Web layer
public class EnergyControllerTest {

    @Autowired
    private MockMvc mockMvc; // Tool to perform fake HTTP requests

    @MockitoBean
    private EnergyService energyService; // Spring mock injected into the controller

    @Test
    void shouldReturn200ForEnergyMixEndpointWithValidParams() throws Exception {
        mockMvc.perform(get("/api/energy-mix")
                        .param("days", "3"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForEnergyMixEndpointWithInvalidParams() throws Exception {
        mockMvc.perform(get("/api/energy-mix")
                        .param("days", "99"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForEnergyMixEndpointWithNoParams() throws Exception {
        mockMvc.perform(get("/api/energy-mix"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForOptimalChargingEndpointWithValidParams() throws Exception {
        mockMvc.perform(get("/api/optimal-charging")
                        .param("windowSize", "4"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForOptimalChargingEndpointWithInvalidParams() throws Exception {
        mockMvc.perform(get("/api/optimal-charging")
                        .param("windowSize", "99"))
                .andExpect(status().isOk());
    }

    @Test
    void shouldReturn200ForOptimalChargingEndpointWithNoParams() throws Exception {
        mockMvc.perform(get("/api/optimal-charging"))
                .andExpect(status().isOk());
    }
}