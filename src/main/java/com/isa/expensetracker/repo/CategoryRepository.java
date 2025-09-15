package com.isa.expensetracker.repo;


import com.isa.expensetracker.db.Db;
import com.isa.expensetracker.model.Category;


import java.sql.*;
import java.util.*;


public class CategoryRepository {
    public List<Category> findAll() {
        String sql = "SELECT id, name, color FROM categories ORDER BY name";
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql); var rs = ps.executeQuery()) {
            List<Category> out = new ArrayList<>();
            while (rs.next()) out.add(new Category(rs.getInt(1), rs.getString(2), rs.getString(3)));
            return out;
        } catch (SQLException e) { throw new RuntimeException(e); }
    }


    public Optional<Category> findByName(String name) {
        String sql = "SELECT id, name, color FROM categories WHERE name=?";
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            try (var rs = ps.executeQuery()) {
                if (rs.next()) return Optional.of(new Category(rs.getInt(1), rs.getString(2), rs.getString(3)));
                return Optional.empty();
            }
        } catch (SQLException e) { throw new RuntimeException(e); }
    }
}