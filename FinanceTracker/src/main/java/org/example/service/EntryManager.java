package org.example.service;


import org.example.domain.entity.Entry;

import java.io.*;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static org.example.domain.constants.constants.PATH;
import static org.example.utility.StringFormatters.capitalize;

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

    public void printDateWiseMonthlySummary() {
        if (entries.isEmpty()) {
            System.out.println("No data available.");
            return;
        }

        DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        DateTimeFormatter monthFormatter = DateTimeFormatter.ofPattern("MMMM yyyy");

        Map<YearMonth, Map<LocalDate, List<Entry>>> grouped = new TreeMap<>();

        for (Entry entry : entries) {
            YearMonth ym = YearMonth.from(entry.getDate());
            grouped
                    .computeIfAbsent(ym, k -> new TreeMap<>())
                    .computeIfAbsent(entry.getDate(), k -> new ArrayList<>())
                    .add(entry);
        }

        for (Map.Entry<YearMonth, Map<LocalDate, List<Entry>>> monthEntry : grouped.entrySet()) {
            System.out.println("\n📅 Summary for " + monthEntry.getKey().format(monthFormatter));
            double monthlyIncome = 0;
            double monthlyExpense = 0;

            for (Map.Entry<LocalDate, List<Entry>> dateEntry : monthEntry.getValue().entrySet()) {
                System.out.println("\n" + dateEntry.getKey().format(dateFormatter) + ":");

                for (Entry e : dateEntry.getValue()) {
                    System.out.printf("  %-7s - %-12s: ₹%.2f%n",
                            capitalize(e.getType()), e.getSubCategory(), e.getAmount());

                    if (e.getType().equalsIgnoreCase("income")) {
                        monthlyIncome += e.getAmount();
                    } else {
                        monthlyExpense += e.getAmount();
                    }
                }
            }

            System.out.printf("\nTotal Income  : ₹%.2f%n", monthlyIncome);
            System.out.printf("Total Expense : ₹%.2f%n", monthlyExpense);
            System.out.printf("Balance       : ₹%.2f%n", (monthlyIncome - monthlyExpense));
        }
    }


    public void clearDataFileButKeepHeader() {
        try (BufferedWriter bw = new BufferedWriter(new FileWriter(PATH))) {
            bw.write("type,subcategory,amount,date"); // write header only
            bw.newLine();
            entries.clear();
            System.out.println("File cleared successfully (header retained).");
        } catch (IOException e) {
            System.out.println("Error clearing file: " + e.getMessage());
        }
    }
}