package com.isa.expensetracker.model;


import java.math.BigDecimal;
import java.time.LocalDate;


public record Expense(long id, Integer categoryId, BigDecimal amount, String currency,
                      LocalDate spentAt, String merchant, String note) {}