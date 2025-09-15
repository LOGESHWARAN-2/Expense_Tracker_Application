package com.isa.expensetracker.service;

import com.isa.expensetracker.db.Db;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Date;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Optional;

public class AnalyticsService {
    public Map<String, BigDecimal> sumByCategoryForMonth(YearMonth ym) {
        String sql = "SELECT c.name, COALESCE(SUM(e.amount),0) FROM categories c LEFT JOIN expenses e ON e.category_id=c.id AND date_trunc('month', e.spent_at)=?::date GROUP BY c.name ORDER BY c.name";
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ym.atDay(1)));
            try (var rs = ps.executeQuery()) {
                Map<String, BigDecimal> map = new LinkedHashMap<>();
                while (rs.next()) map.put(rs.getString(1), rs.getBigDecimal(2));
                return map;
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    /**
     * ✅ NEW: Calculates total spending for each day of a given month.
     * Uses a SQL query with generate_series to ensure all days are present, even if they have no expenses.
     */
    public Map<LocalDate, BigDecimal> sumByDayForMonth(YearMonth ym) {
        String sql = """
            WITH month_days AS (
              SELECT generate_series(
                ?::date,
                (?::date + '1 month'::interval - '1 day'::interval),
                '1 day'::interval
              )::date AS day
            )
            SELECT
              d.day,
              COALESCE(SUM(e.amount), 0) AS total
            FROM month_days d
            LEFT JOIN expenses e ON d.day = e.spent_at
            GROUP BY d.day
            ORDER BY d.day;
            """;
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            LocalDate startDate = ym.atDay(1);
            ps.setDate(1, Date.valueOf(startDate));
            ps.setDate(2, Date.valueOf(startDate));
            try (var rs = ps.executeQuery()) {
                Map<LocalDate, BigDecimal> map = new LinkedHashMap<>();
                while (rs.next()) {
                    map.put(rs.getDate(1).toLocalDate(), rs.getBigDecimal(2));
                }
                return map;
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }


    public BigDecimal getTotalSpentForMonth(YearMonth ym) {
        String sql = "SELECT COALESCE(SUM(amount), 0) FROM expenses WHERE date_trunc('month', spent_at) = ?::date";
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ym.atDay(1)));
            try (var rs = ps.executeQuery()) {
                return rs.next() ? rs.getBigDecimal(1) : BigDecimal.ZERO;
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public Optional<String> getTopCategoryForMonth(YearMonth ym) {
        String sql = """
            SELECT c.name FROM expenses e
            JOIN categories c ON e.category_id = c.id
            WHERE date_trunc('month', e.spent_at) = ?::date
            GROUP BY c.name ORDER BY SUM(e.amount) DESC LIMIT 1
            """;
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ym.atDay(1)));
            try (var rs = ps.executeQuery()) {
                return rs.next() ? Optional.of(rs.getString(1)) : Optional.empty();
            }
        } catch (Exception e) { throw new RuntimeException(e); }
    }

    public BigDecimal getDailyAverageForMonth(YearMonth ym) {
        BigDecimal totalSpent = getTotalSpentForMonth(ym);
        if (totalSpent.signum() == 0) {
            return BigDecimal.ZERO;
        }
        // Calculate average based on today's date if it's the current month, otherwise use the full month
        int daysInMonth = LocalDate.now().getMonth() == ym.getMonth() ?
                LocalDate.now().getDayOfMonth() : ym.lengthOfMonth();

        return totalSpent.divide(BigDecimal.valueOf(daysInMonth), 2, RoundingMode.HALF_UP);
    }
}
