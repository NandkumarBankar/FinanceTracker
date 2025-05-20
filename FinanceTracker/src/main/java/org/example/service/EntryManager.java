package org.example.service;


import org.example.domain.entity.Entry;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.example.domain.constants.constants.PATH;

public class EntryManager {
    List<Entry> entries = new ArrayList<>();

    public EntryManager() {
        File file = new File(PATH);
        if (!file.exists()) {
            try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH))) {
                bw.write("Type,Sub Category,Amount,Date"); // add header
                bw.newLine();
            } catch (IOException e) {
                System.out.println("Error creating file: " + e.getMessage());
            }
        }
    }

    public void addEntry(Entry entry) {
        entries.add(entry);
    }

    public void loadFromFile() {
        entries.clear();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (BufferedReader br = new BufferedReader(new FileReader(PATH))) {
            String line = br.readLine(); // skip header
            while ((line = br.readLine()) != null) {
                String[] parts = line.split(",");
                if (parts.length != 4) continue;

                String type = parts[0];
                String subCat = parts[1];
                double amt = Double.parseDouble(parts[2]);
                LocalDate date = LocalDate.parse(parts[3], formatter);
                entries.add(new Entry(type, subCat, amt, date));
            }
        } catch (IOException e) {
            System.out.println("Error reading file: " + e.getMessage());
        }
    }

    public void saveEntryToFile(Entry entry) {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH, true))) {
            bw.write(entry.toCSV());
            bw.newLine();
        } catch (IOException e) {
            System.out.println("Error writing to file: " + e.getMessage());
        }
    }

    public void printMonthlySummary() {
        double totalIncome = 0, totalExpense = 0;
        Map<String, Double> incomeMap = new HashMap<>();
        Map<String, Double> expenseMap = new HashMap<>();

        for (Entry e : entries) {
            if (e.type.equalsIgnoreCase("income")) {
                totalIncome += e.amount;
                incomeMap.put(e.subCategory, incomeMap.getOrDefault(e.subCategory, 0.0) + e.amount);
            } else if (e.type.equalsIgnoreCase("expense")) {
                totalExpense += e.amount;
                expenseMap.put(e.subCategory, expenseMap.getOrDefault(e.subCategory, 0.0) + e.amount);
            }
        }

        System.out.println("\n--- Monthly Summary ---");
        System.out.println("Total Income: ₹" + totalIncome);
        incomeMap.forEach((k, v) -> System.out.println("  " + k + ": ₹ " + v));

        System.out.println("Total Expense: ₹" + totalExpense);
        expenseMap.forEach((k, v) -> System.out.println("  " + k + ": ₹ " + v));

        System.out.println("Net Savings: ₹" + (totalIncome - totalExpense));
    }

    public void clearDataFileButKeepHeader() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH))) {
            bw.write("type,subcategory,amount,date"); // write header only
            bw.newLine();
            System.out.println("File cleared successfully (header retained).");
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }
}