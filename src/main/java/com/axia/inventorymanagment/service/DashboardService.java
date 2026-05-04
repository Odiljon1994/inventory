package com.axia.inventorymanagment.service;

import com.axia.inventorymanagment.dto.DashboardResponse;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@Slf4j
public class DashboardService {

    @PersistenceContext
    private EntityManager entityManager;

    public DashboardResponse getDashboard(int year, int month) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("Invalid parameter: month must be 1-12");
        }

        int prevYear = month == 1 ? year - 1 : year;
        int prevMonth = month == 1 ? 12 : month - 1;

        log.info("Fetching dashboard stats for {}/{}, prev {}/{}", year, month, prevYear, prevMonth);

        return DashboardResponse.builder()
                .monthlySales(querySalesAmount(year, month))
                .monthlySalesPrev(querySalesAmount(prevYear, prevMonth))
                .totalSoldCount(querySoldCount(year, month))
                .totalSoldCountPrev(querySoldCount(prevYear, prevMonth))
                .lowStockSkuCount(queryLowStockSkuCount())
                .unknownInventoryCount(queryUnknownInventoryCount())
                .unknownInventoryStore(queryUnknownInventoryStore())
                .build();
    }

    private Long querySalesAmount(int year, int month) {
        String sql = """
                SELECT COALESCE(SUM(ABS(isl.delta) * l.avg_unit_price), 0)
                FROM inventory_stock_log isl
                JOIN inventory_stock is_ ON isl.stock_id = is_.stock_id
                JOIN lots l ON is_.lot_id = l.lot_id
                WHERE isl.change_type = 'SOLD'
                  AND EXTRACT(YEAR FROM isl.created_at) = :year
                  AND EXTRACT(MONTH FROM isl.created_at) = :month
                """;
        Object result = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .setParameter("month", month)
                .getSingleResult();
        return ((Number) result).longValue();
    }

    private Long querySoldCount(int year, int month) {
        String sql = """
                SELECT COALESCE(SUM(ABS(isl.delta)), 0)
                FROM inventory_stock_log isl
                WHERE isl.change_type = 'SOLD'
                  AND EXTRACT(YEAR FROM isl.created_at) = :year
                  AND EXTRACT(MONTH FROM isl.created_at) = :month
                """;
        Object result = entityManager.createNativeQuery(sql)
                .setParameter("year", year)
                .setParameter("month", month)
                .getSingleResult();
        return ((Number) result).longValue();
    }

    private Long queryLowStockSkuCount() {
        String sql = """
                SELECT COUNT(DISTINCT l.sku_id)
                FROM inventory_stock is_
                JOIN lots l ON is_.lot_id = l.lot_id
                WHERE is_.quantity <= 5
                """;
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    private Long queryUnknownInventoryCount() {
        String sql = """
                SELECT COUNT(*)
                FROM inventory_stock_log isl
                WHERE isl.change_type = 'DISPOSAL'
                  AND (isl.reason IS NULL OR isl.reason = '')
                  AND isl.created_at >= CURRENT_DATE - INTERVAL '7 days'
                """;
        Object result = entityManager.createNativeQuery(sql).getSingleResult();
        return ((Number) result).longValue();
    }

    private String queryUnknownInventoryStore() {
        String sql = """
                SELECT st.store_name
                FROM inventory_stock_log isl
                JOIN inventory_stock is_ ON isl.stock_id = is_.stock_id
                JOIN stores st ON is_.store_id = st.store_id
                WHERE isl.change_type = 'DISPOSAL'
                  AND (isl.reason IS NULL OR isl.reason = '')
                  AND isl.created_at >= CURRENT_DATE - INTERVAL '7 days'
                GROUP BY st.store_id, st.store_name
                ORDER BY COUNT(*) DESC
                LIMIT 1
                """;
        List<?> results = entityManager.createNativeQuery(sql).getResultList();
        return results.isEmpty() ? null : (String) results.get(0);
    }
}
