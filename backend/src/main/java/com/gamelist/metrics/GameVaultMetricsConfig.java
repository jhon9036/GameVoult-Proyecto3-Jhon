package com.gamelist.metrics;

import com.gamelist.repository.VideojuegoRepository;
import io.micrometer.core.instrument.Gauge;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameVaultMetricsConfig {

    public GameVaultMetricsConfig(MeterRegistry meterRegistry, VideojuegoRepository videojuegoRepository) {
        Gauge.builder("gamevault_videojuegos_total", videojuegoRepository, VideojuegoRepository::count)
                .description("Cantidad total de videojuegos registrados")
                .register(meterRegistry);
    }
}
