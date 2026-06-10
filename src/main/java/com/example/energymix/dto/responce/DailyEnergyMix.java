package com.example.energymix.dto.responce;

import java.time.LocalDate;
import java.util.Map;

public record DailyEnergyMix (
        LocalDate date,
        Map<String, Double> fuelAverages, // eg. {"wind": 25.4, "solar": 12.1}
        Double totalCleanEnergyPercentage // sum: biomass + nuclear + hydro + wind + solar
){}
