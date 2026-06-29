package com.billing.platform.service.allocation;

import org.springframework.stereotype.Service;

import java.util.Map;

@Service
public class AllocationStrategyFactory {

    private final Map<String, AllocationStrategy> strategies;

    public AllocationStrategyFactory(Map<String, AllocationStrategy> strategies) {
        this.strategies = strategies;
    }

    public AllocationStrategy get(String method) {
        AllocationStrategy strategy = strategies.get(method.toUpperCase());
        if (strategy == null) {
            throw new IllegalArgumentException("Unknown allocation method: " + method);
        }
        return strategy;
    }
}