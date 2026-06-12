package com.example.energymix.controller;

import com.example.energymix.dto.responce.DailyEnergyMixResponse;
import com.example.energymix.dto.responce.OptimalChargingTimeResponse;
import com.example.energymix.service.EnergyService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
@CrossOrigin(origins = "*")
public class EnergyController {

    private final EnergyService energyService;

    EnergyController(EnergyService energyService){
        this.energyService = energyService;
    }

    @GetMapping("/energy-mix")
    public DailyEnergyMixResponse energyMix(@RequestParam(required = false, defaultValue = "3") int days){
        if (days <= 0) days = 1;

        // More than 3 days UK API won't return
        return energyService.getEnergyMix(days);
    }

    @GetMapping("/optimal-charging")
    public OptimalChargingTimeResponse optimalCharging(@RequestParam(required = false, defaultValue = "0") int windowSize){
        if (windowSize <= 0) windowSize = 1;
        if (windowSize > 6) windowSize = 6;

        return energyService.getOptimalChargingTime(windowSize);
    }
}
