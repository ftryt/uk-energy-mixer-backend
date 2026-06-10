package com.example.energymix.dto.external;

import java.util.List;

// The main root object from JSON
public record CarbonIntensityResponse(
        List<GenerationInterval> data
) {}