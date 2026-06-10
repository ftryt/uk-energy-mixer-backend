package com.example.energymix.dto.responce;

import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.List;

public record OptimalChargingTimeResponse(
        ZonedDateTime from,
        ZonedDateTime to,
        Double avgCleanEnergy,
        ZonedDateTime totalCleanIntervalsStartTime,
        List<Double> totalAvgCleanIntervals
){}
