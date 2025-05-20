package org.example;

import org.example.domain.entity.Entry;
import org.example.service.EntryManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;

public class FinanceTracker {
    private static final Scanner scanner = new Scanner(System.in);
    private static final EntryManager manager = new EntryManager();

    public static void main(String[] args) {
        // Load existing entries from file
        manager.loadFromFile();

        while (true) {
            System.out.println("\nChoose an option:\n1. Add Entry\n2. Load from Custom File\n3. Show Monthly Summary\n4. Clear File (Keep Header)\n5. Exit");
            int choice = scanner.nextInt();
            scanner.nextLine(); // clear buffer

            switch (choice) {
                case 1 -> addEntryInteractive();
                case 2 -> loadFromCustomFile();
                case 3 -> manager.printMonthlySummary();
                case 4 -> manager.clearDataFileButKeepHeader(); // Extra OPTION
                case 5 -> {
                    System.out.println("Exiting...");
                    return;
                }
                default -> System.out.println("Invalid choice.");
            }
        }
    }

    private static void addEntryInteractive() {
        System.out.print("Enter type (income/expense): ");
        String type = scanner.nextLine().trim().toLowerCase();

        List<String> incomeCats = Arrays.asList("salary", "business");
        List<String> expenseCats = Arrays.asList("food", "rent", "travel");

        String subCategory;
        if (type.equals("income")) {
            System.out.print("Enter subcategory (salary/business): ");
            subCategory = scanner.nextLine().trim().toLowerCase();
            if (!incomeCats.contains(subCategory)) {
                System.out.println("Invalid subcategory.");
                return;
            }
        } else if (type.equals("expense")) {
            System.out.print("Enter subcategory (food/rent/travel): ");
            subCategory = scanner.nextLine().trim().toLowerCase();
            if (!expenseCats.contains(subCategory)) {
                System.out.println("Invalid subcategory.");
                return;
            }
        } else {
            System.out.println("Invalid type.");
            return;
        }

        System.out.print("Enter amount : ");
        double amount = scanner.nextDouble();
        scanner.nextLine(); // clear buffer

        System.out.print("Enter date (yyyy-MM-dd): ");
        String dateStr = scanner.nextLine();
        LocalDate date = LocalDate.parse(dateStr);

        Entry entry = new Entry(type, subCategory, amount, date);
        manager.addEntry(entry);
        manager.saveEntryToFile(entry);
        System.out.println("Entry added and saved to file!");

    }

    private static void loadFromCustomFile() {
        manager.loadFromFile();
        System.out.println("Custom file loaded successfully!");
    }
}