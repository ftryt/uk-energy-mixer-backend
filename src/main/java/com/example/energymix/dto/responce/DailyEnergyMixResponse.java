package com.example.energymix.dto.responce;


import java.util.List;

public record DailyEnergyMixResponse (
    List<DailyEnergyMix> dailyMixes
){}
