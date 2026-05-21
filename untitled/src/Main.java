import concurrent.Customer;
import concurrent.Store;
import creational.*;
import domain.*;

import java.util.HashMap;
import java.util.Map;

public class Main {

    public static void main(String[] args) throws InterruptedException {

        Map<Integer, StockItem> catalog = new HashMap<>();

        Good milk = new GoodBuilder("Молоко от Нины", 1000.0, 89.99).ID(1).type("Напиток").build();
        Good bread = new GoodBuilder("Хлеб бородинский", 500.0, 49.50).ID(2).type("Еда").build();
        Good cheese = new GoodBuilder("Сыр Пармезан", 300.0, 450.00).ID(3).type("Еда").build();
        Good tea = new GoodBuilder("Чай Greenfield", 100.0, 130.00).ID(4).type("Напиток").build();
        Good phone = new GoodBuilder("Чехол для телефона", 50.0, 299.00).ID(5).type("Электроника").build();

        catalog.put(1, new StockItem(milk,  30));
        catalog.put(2, new StockItem(bread, 20));
        catalog.put(3, new StockItem(cheese, 15));
        catalog.put(4, new StockItem(tea,   25));
        catalog.put(5, new StockItem(phone,  10));

        System.out.println("Открытие магазина\n");
        printCatalog(catalog);


        int numberOfCustomers = 5;

        Store store = new Store(numberOfCustomers);
        Thread storeThread = new Thread(store, "Касса");
        storeThread.start();

        Thread[] customerThreads = new Thread[numberOfCustomers];
        for (int i = 1; i <= numberOfCustomers; i++) {
            Customer customer = new Customer("Покупатель-" + i, i, store, catalog);
            Thread t = new Thread(customer, "Покупатель-" + i);
            customerThreads[i - 1] = t;
            t.start();
        }

        for (Thread t : customerThreads) {
            t.join();
        }

        storeThread.join();

        System.out.println("\nЗакрытие магазина");
    }

    private static void printCatalog(Map<Integer, StockItem> catalog) {
        System.out.println("КАТАЛОГ ТОВАРОВ:");
        for (StockItem item : catalog.values()) {
            Good g = item.getGood();
            System.out.printf("  [%d] %-25s | %-12s | %7.2f руб | %.0f г | Остаток: %d%n",
                    g.getID(), g.getName(), g.getType(),
                    g.getCost(), g.getWeight(), item.getQuantity());
        }
        System.out.println();
    }
}
