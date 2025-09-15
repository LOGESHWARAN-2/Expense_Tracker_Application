package com.isa.expensetracker.service;

import com.isa.expensetracker.db.Db;
import com.opencsv.CSVWriter;

import java.io.FileWriter;
import java.nio.file.Path;
import java.sql.Date;

public class ExportService {
    public Path exportMonthToCsv(java.time.YearMonth ym, Path file) {
        // ✅ FIX: Changed e.currency to e.currency_code to match the database schema
        String sql = "SELECT e.id, c.name, e.amount, e.currency_code, e.spent_at, e.merchant, e.note FROM expenses e LEFT JOIN categories c ON c.id=e.category_id WHERE date_trunc('month', e.spent_at)=?::date ORDER BY e.spent_at";
        try (var con = Db.dataSource().getConnection(); var ps = con.prepareStatement(sql)) {
            ps.setDate(1, Date.valueOf(ym.atDay(1)));
            try (var rs = ps.executeQuery(); var w = new CSVWriter(new FileWriter(file.toFile()))) {
                w.writeNext(new String[]{"id","category","amount","currency","spent_at","merchant","note"});
                while (rs.next()) {
                    w.writeNext(new String[]{
                            String.valueOf(rs.getLong(1)),
                            rs.getString(2), rs.getBigDecimal(3).toPlainString(), rs.getString(4),
                            rs.getDate(5).toString(), rs.getString(6), rs.getString(7)
                    });
                }
            }
            return file;
        } catch (Exception e) { throw new RuntimeException(e); }
    }
}
