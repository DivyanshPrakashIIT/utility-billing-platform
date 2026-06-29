package com.billing.platform.service.allocation;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Tenant;
import com.billing.platform.entity.Unit;
import com.billing.platform.repository.TenantRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("OCCUPANCY")
public class OccupancyAllocationStrategy implements AllocationStrategy {

    private final TenantRepository tenantRepository;

    public OccupancyAllocationStrategy(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Map<Long, BigDecimal> allocate(BigDecimal totalBillAmount, List<Unit> occupiedUnits,
                                          AllocationRule rule, LocalDate periodStart, LocalDate periodEnd,
                                          Map<Long, BigDecimal> consumptionByUnitId) {
        Map<Long, BigDecimal> weights = new LinkedHashMap<>();

        for (Unit unit : occupiedUnits) {
            List<Tenant> tenants = tenantRepository.findByUnitId(unit.getId());
            long daysOccupied = 0;

            for (Tenant tenant : tenants) {
                LocalDate moveIn = tenant.getMoveInDate().isAfter(periodStart) ? tenant.getMoveInDate() : periodStart;
                LocalDate moveOut = (tenant.getMoveOutDate() != null && tenant.getMoveOutDate().isBefore(periodEnd))
                        ? tenant.getMoveOutDate() : periodEnd;
                if (!moveOut.isBefore(moveIn)) {
                    daysOccupied += ChronoUnit.DAYS.between(moveIn, moveOut) + 1;
                }
            }
            weights.put(unit.getId(), BigDecimal.valueOf(daysOccupied));
        }
        return AllocationMathUtil.distributeByWeights(totalBillAmount, weights);
    }
}