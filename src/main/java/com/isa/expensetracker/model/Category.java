package com.isa.expensetracker.model;

/**
 * Matches repository usage: new Category(int, String, String)
 * Fields: id, name, icon (or color/emoji — whatever your third column is).
 */
public record Category(long id, String name, String icon) {
    @Override public String toString() { return name; }
}
