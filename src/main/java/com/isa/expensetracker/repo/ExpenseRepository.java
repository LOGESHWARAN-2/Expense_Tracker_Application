package com.isa.expensetracker.repo;


import com.isa.expensetracker.db.Db;
import com.isa.expensetracker.model.Expense;


import java.math.BigDecimal;
import java.sql.*;
import java.sql.Date;
import java.time.LocalDate;
import java.util.*;


public class ExpenseRepository {
    public long insert(Expense e) {
        String sql = "INSERT INTO expenses(category_id, amount, currency, spent_at, merchant, note) VALUES(?,?,?,?,?,?) RETURNING id";
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            if (e.categoryId() == null) ps.setNull(1, Types.INTEGER); else ps.setInt(1, e.categoryId());
            ps.setBigDecimal(2, e.amount());
            ps.setString(3, e.currency());
            ps.setDate(4, Date.valueOf(e.spentAt()));
            ps.setString(5, e.merchant());
            ps.setString(6, e.note());
            try (var rs = ps.executeQuery()) { rs.next(); return rs.getLong(1); }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }


    public List<Expense> findByMonth(int year, int month) {
        String sql = "SELECT id, category_id, amount, currency, spent_at, merchant, note FROM expenses WHERE EXTRACT(YEAR FROM spent_at)=? AND EXTRACT(MONTH FROM spent_at)=? ORDER BY spent_at DESC";
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            ps.setInt(1, year); ps.setInt(2, month);
            try (var rs = ps.executeQuery()) {
                List<Expense> out = new ArrayList<>();
                while (rs.next()) {
                    out.add(new Expense(
                            rs.getLong(1), (Integer) rs.getObject(2), rs.getBigDecimal(3), rs.getString(4),
                            rs.getDate(5).toLocalDate(), rs.getString(6), rs.getString(7)));
                }
                return out;
            }
        } catch (SQLException ex) { throw new RuntimeException(ex); }
    }
}