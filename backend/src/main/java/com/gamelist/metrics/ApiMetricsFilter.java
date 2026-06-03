package com.gamelist.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

@Component
public class ApiMetricsFilter extends OncePerRequestFilter {

    private final MeterRegistry meterRegistry;

    public ApiMetricsFilter(MeterRegistry meterRegistry) {
        this.meterRegistry = meterRegistry;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return !request.getRequestURI().startsWith("/api/");
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {
        long start = System.nanoTime();
        try {
            filterChain.doFilter(request, response);
        } finally {
            long duration = System.nanoTime() - start;
            String path = normalizePath(request.getRequestURI());
            String method = request.getMethod();
            String status = String.valueOf(response.getStatus());

            Counter.builder("gamevault_api_requests_total")
                    .description("Total de requests recibidos por la API de GameVault")
                    .tag("method", method)
                    .tag("path", path)
                    .tag("status", status)
                    .register(meterRegistry)
                    .increment();

            Timer.builder("gamevault_api_request_latency")
                    .description("Latencia de requests de la API de GameVault")
                    .tag("method", method)
                    .tag("path", path)
                    .tag("status", status)
                    .publishPercentileHistogram()
                    .register(meterRegistry)
                    .record(duration, TimeUnit.NANOSECONDS);
        }
    }

    private String normalizePath(String uri) {
        return uri.replaceAll("/\\d+", "/{id}");
    }
}
