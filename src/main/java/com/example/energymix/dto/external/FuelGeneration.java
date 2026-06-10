package com.example.energymix.dto.external;

// Specific energy source and its percentage share
public record FuelGeneration(
        String fuel,
        Double perc
) {}