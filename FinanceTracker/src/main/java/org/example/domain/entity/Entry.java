package org.example.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class Entry {
    public String type; // "income" or "expense"
    public String subCategory;
    public double amount;
    public LocalDate date;


    public String toCSV() {
        return type + "," + subCategory + "," + amount + "," + date;
    }
}