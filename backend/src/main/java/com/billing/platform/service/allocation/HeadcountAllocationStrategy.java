package com.billing.platform.service.allocation;

import com.billing.platform.entity.AllocationRule;
import com.billing.platform.entity.Tenant;
import com.billing.platform.entity.Unit;
import com.billing.platform.repository.TenantRepository;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@Component("HEADCOUNT")
public class HeadcountAllocationStrategy implements AllocationStrategy {

    private final TenantRepository tenantRepository;

    public HeadcountAllocationStrategy(TenantRepository tenantRepository) {
        this.tenantRepository = tenantRepository;
    }

    @Override
    public Map<Long, BigDecimal> allocate(BigDecimal totalBillAmount, List<Unit> occupiedUnits,
                                          AllocationRule rule, LocalDate periodStart, LocalDate periodEnd,
                                          Map<Long, BigDecimal> consumptionByUnitId) {
        Map<Long, BigDecimal> weights = new LinkedHashMap<>();

        for (Unit unit : occupiedUnits) {
            List<Tenant> tenants = tenantRepository.findByUnitId(unit.getId());
            int totalResidents = tenants.stream()
                    .filter(t -> t.getMoveOutDate() == null || !t.getMoveOutDate().isBefore(periodStart))
                    .mapToInt(Tenant::getResidentCount)
                    .sum();
            weights.put(unit.getId(), BigDecimal.valueOf(totalResidents));
        }
        return AllocationMathUtil.distributeByWeights(totalBillAmount, weights);
    }
}