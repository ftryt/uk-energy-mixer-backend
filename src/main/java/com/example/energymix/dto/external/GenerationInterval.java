package com.example.energymix.dto.external;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.util.List;

// Single half-hour interval
public record GenerationInterval(
        String from,
        String to,

        @JsonProperty("generationmix")
        List<FuelGeneration> generationMix
) {}