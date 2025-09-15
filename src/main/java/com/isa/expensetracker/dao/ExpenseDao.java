package com.isa.expensetracker.dao;

import com.isa.expensetracker.db.Db;
import com.isa.expensetracker.model.Category;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ExpenseDao {

    public List<Category> listCategories() {
        // Works even if there's no 'icon' column. We just provide an empty string as the 3rd field.
        String sql = "SELECT id, name, '' AS icon FROM categories ORDER BY name";
        try (var con = Db.dataSource().getConnection();
             var ps = con.prepareStatement(sql);
             var rs = ps.executeQuery()) {
            List<Category> list = new ArrayList<>();
            while (rs.next()) {
                list.add(new Category(rs.getLong(1), rs.getString(2), rs.getString(3)));
            }
            return list;
        } catch (Exception e) {
            throw new RuntimeException("Failed to load categories", e);
        }
    }

    public void insertExpense(long categoryId,
                              BigDecimal amount,
                              String currencyCode,
                              LocalDate spentAt,
                              String merchant,
                              String note) {
        String sql = """
            INSERT INTO expenses (category_id, amount, currency_code, spent_at, merchant, note)
            VALUES (?,?,?,?,?,?)
            """;
        try (var con = Db.dataSource().getConnection();
             var ps = con.prepareStatement(sql)) {
            ps.setLong(1, categoryId);
            ps.setBigDecimal(2, amount);
            ps.setString(3, currencyCode);
            ps.setDate(4, Date.valueOf(spentAt));
            ps.setString(5, merchant);
            ps.setString(6, note);
            ps.executeUpdate();
        } catch (Exception e) {
            throw new RuntimeException("Failed to insert expense", e);
        }
    }
}
