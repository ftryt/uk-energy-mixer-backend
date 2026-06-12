package com.example.energymix.service;

import com.example.energymix.client.CarbonIntensityClient;
import com.example.energymix.dto.external.CarbonIntensityResponse;
import com.example.energymix.dto.external.FuelGeneration;
import com.example.energymix.dto.external.GenerationInterval;
import com.example.energymix.dto.responce.OptimalChargingTimeResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.time.ZonedDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class EnergyServiceTest {

    private CarbonIntensityClient clientMock;
    private EnergyService energyService;

    @BeforeEach
    void setUp() {
        // Arrange: Create a fake instance of the HTTP client
        clientMock = Mockito.mock(CarbonIntensityClient.class);
        energyService = new EnergyService(clientMock);
    }

    @Test
    void shouldFindTheMostOptimalChargingWindow() {
        // Prepare fake intervals. Let's make interval 2 and 3 highly ecological.
        String baseTime = "2026-06-12T12:00:00Z";
        String optimalTimeStart = "2026-06-12T12:30:00Z";

        List<GenerationInterval> fakeIntervals = List.of(
                new GenerationInterval(baseTime, "2026-06-12T12:30:00Z",
                        List.of(new FuelGeneration("wind", 10.0), new FuelGeneration("gas", 90.0))), // 10% clean

                new GenerationInterval(optimalTimeStart, "2026-06-12T13:00:00Z",
                        List.of(new FuelGeneration("wind", 80.0), new FuelGeneration("nuclear", 10.0))), // 90% clean [WINNER START]

                new GenerationInterval("2026-06-12T13:00:00Z", "2026-06-12T13:30:00Z",
                        List.of(new FuelGeneration("solar", 70.0))), // 70% clean [WINNER END]

                new GenerationInterval("2026-06-12T13:30:00Z", "2026-06-12T14:00:00Z",
                        List.of(new FuelGeneration("coal", 100.0))) // 0% clean
        );

        CarbonIntensityResponse fakeResponse = new CarbonIntensityResponse(fakeIntervals);

        // Instruct the mock to return our fake data whenever it's called
        when(clientMock.getGenerationMix(any(), any())).thenReturn(fakeResponse);

        // Run the algorithm asking for a 1-hour window (which means 2 consecutive intervals)
        OptimalChargingTimeResponse result = energyService.getOptimalChargingTime(1);

        // Verify if the algorithm picked the best combined 2 intervals (90% + 70% = 160 / 2 = 80% average)
        assertNotNull(result);
        assertEquals(80.0, result.avgCleanEnergy());
        assertEquals("2026-06-12T12:30Z", result.from().toString());
        assertEquals("2026-06-12T13:30Z", result.to().toString());
    }

    @Test
    void shouldReturnEmptyResponseWhenApiDataIsMissing() {
        // Simulate API failure returning null
        when(clientMock.getGenerationMix(any(), any())).thenReturn(null);

        String nowStr = ZonedDateTime.now().toInstant().toString();
        OptimalChargingTimeResponse result = energyService.getOptimalChargingTime(2);

        assertNotNull(result);
        assertEquals(0.0, result.avgCleanEnergy());
        assertTrue(result.totalAvgCleanIntervals().isEmpty());
    }
}