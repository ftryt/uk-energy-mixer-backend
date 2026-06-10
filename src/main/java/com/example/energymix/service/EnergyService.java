package com.example.energymix.service;

import com.example.energymix.client.CarbonIntensityClient;
import com.example.energymix.dto.external.CarbonIntensityResponse;
import com.example.energymix.dto.external.FuelGeneration;
import com.example.energymix.dto.external.GenerationInterval;
import com.example.energymix.dto.responce.DailyEnergyMix;
import com.example.energymix.dto.responce.DailyEnergyMixResponse;
import com.example.energymix.dto.responce.OptimalChargingTimeResponse;
import org.springframework.stereotype.Service;

import java.time.*;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class EnergyService {

    private final CarbonIntensityClient intensityClient;
    private static final Set<String> CLEAN_ENERGY_FUELS = Set.of("biomass", "nuclear", "hydro", "wind", "solar");

    EnergyService(CarbonIntensityClient intensityClient){
        this.intensityClient = intensityClient;
    }

    public DailyEnergyMixResponse getEnergyMix(int days){
        // Call api and receive raw data
        ZonedDateTime now = ZonedDateTime.now();
        ZonedDateTime from = now.toLocalDate().atStartOfDay(ZoneOffset.UTC).plusMinutes(1);
        ZonedDateTime to = from.plusDays(days).minusMinutes(1);

        CarbonIntensityResponse rawData = intensityClient.getGenerationMix(from, to);

        // Check for errors
        if (rawData == null || rawData.data() == null) {
            return new DailyEnergyMixResponse(Collections.emptyList());
        }

        // Calculate average and convert to internal dto response
        // Firstly algorithm groups data by same day and then calculates avg
        Map<LocalDate, List<GenerationInterval>> intervalsByDay = rawData.data().stream()
                .collect(Collectors.groupingBy(interval ->
                        ZonedDateTime.parse(interval.from(), DateTimeFormatter.ISO_OFFSET_DATE_TIME).toLocalDate()
                ));

        List<DailyEnergyMix> dailyMixes = intervalsByDay.entrySet().stream()
                .map(entry -> calculateDailyAverage(entry.getKey(), entry.getValue()))
                .sorted(Comparator.comparing(DailyEnergyMix::date)) //Sort chronologically
                .toList();

        return new DailyEnergyMixResponse(dailyMixes);
    }

    private DailyEnergyMix calculateDailyAverage(LocalDate date, List<GenerationInterval> intervals) {
        // All intervals from a given day
        List<FuelGeneration> allFuelsForDay = intervals.stream()
                .flatMap(interval -> interval.generationMix().stream())
                .toList();

        // Grouping by fuel type (fuel) and calculating the average (perc)
        Map<String, Double> fuelAverages = allFuelsForDay.stream()
                .collect(Collectors.groupingBy(
                        FuelGeneration::fuel,
                        Collectors.averagingDouble(FuelGeneration::perc)
                ));

        // Round the results
        fuelAverages.replaceAll((fuel, avg) -> Math.round(avg * 100.0) / 100.0);

        // Calculate the sum of the averages for clean energy
        double totalCleanEnergy = fuelAverages.entrySet().stream()
                .filter(entry -> CLEAN_ENERGY_FUELS.contains(entry.getKey().toLowerCase()))
                .mapToDouble(Map.Entry::getValue)
                .sum();

        totalCleanEnergy = Math.round(totalCleanEnergy * 100.0) / 100.0;

        return new DailyEnergyMix(date, fuelAverages, totalCleanEnergy);
    }

    public OptimalChargingTimeResponse getOptimalChargingTime(int windowSizeInHours){
        // Half hour interval
        int k = windowSizeInHours * 2;

        // Call api and receive raw data (for next 2 days)
        ZonedDateTime now = ZonedDateTime.now(ZoneOffset.UTC);
        int minutes = now.getMinute() >= 30 ? 31 : 1;
        ZonedDateTime from = now.withMinute(minutes).withSecond(0).withNano(0);
        ZonedDateTime to = from.plusDays(2);

        CarbonIntensityResponse rawData = intensityClient.getGenerationMix(from, to);

        // Check for errors
        if (rawData == null || rawData.data() == null || rawData.data().size() < k) {
            return new OptimalChargingTimeResponse(ZonedDateTime.now(), ZonedDateTime.now(), 0.0,
                    ZonedDateTime.now(), Collections.emptyList());
        }

        List<GenerationInterval> sortedIntervals = rawData.data().stream()
                .sorted(Comparator.comparing(GenerationInterval::from))
                .toList();

        // The sum of pure energy for each interval (for readability and api response)
        List<Double> cleanEnergyPerInterval = sortedIntervals.stream()
                .map(interval -> interval.generationMix().stream()
                        .filter(mix -> CLEAN_ENERGY_FUELS.contains(mix.fuel().toLowerCase()))
                        .mapToDouble(FuelGeneration::perc)
                        .sum())
                .toList();

        // At first find first window sum
        double currentWindowSum = 0;
        for (int i = 0; i < k; i++) {
            currentWindowSum += cleanEnergyPerInterval.get(i);
        }

        double bestWindowSum = currentWindowSum;
        int bestStartIndex = 0;

        // Find an optimal time using sliding window approach
        for (int i = k; i < cleanEnergyPerInterval.size(); i++) {
            currentWindowSum += cleanEnergyPerInterval.get(i) - cleanEnergyPerInterval.get(i - k);

            if (currentWindowSum > bestWindowSum) {
                bestWindowSum = currentWindowSum;
                bestStartIndex = i - k + 1;
            }
        }

        ZonedDateTime bestL = ZonedDateTime.parse(sortedIntervals.get(bestStartIndex).from());
        ZonedDateTime bestR = ZonedDateTime.parse(sortedIntervals.get(bestStartIndex + k - 1).to());

        // The average is the sum of the percentages divided by the number of intervals
        double averageCleanEnergy = Math.round((bestWindowSum / k) * 100.0) / 100.0;

        return new OptimalChargingTimeResponse(bestL, bestR, averageCleanEnergy,
                ZonedDateTime.parse(sortedIntervals.get(0).from()), cleanEnergyPerInterval);
    }
}
