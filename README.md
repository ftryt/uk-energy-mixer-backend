# UK Energy Mixer & EV Optimizer - Backend

This is a Spring Boot REST API that fetches the UK energy mix data and calculates the optimal charging window for Electric Vehicles based on the highest clean energy share.

## Tech Stack
- Java 17
- Spring Boot 4.1 (Spring Web)
- RestClient (for external API integration)
- JUnit 5 & Mockito (for testing)

## Endpoints
- `GET /api/energy-mix?days=3` - Returns daily averages of energy sources.
- `GET /api/optimal-charging?windowSize=3` - Calculates the cleanest charging window (1-6 hours).

## How to run locally
1. Clone the repository.
2. Run `./mvnw spring-boot:run`