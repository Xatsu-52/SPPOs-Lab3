package concurrent;

import domain.Good;
import domain.GoodItem;
import domain.StockItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Customer implements Runnable {

    private final String name;
    private final Store store;
    private final Map<Integer, StockItem> catalog;
    private final int orderId;
    private final Random random = new Random();

    public Customer(String name, int orderId, Store store, Map<Integer, StockItem> catalog) {
        this.name = name;
        this.orderId = orderId;
        this.store = store;
        this.catalog = catalog;
    }

    @Override
    public void run() {
        System.out.println("[" + name + "] Начинает выбирать товары.");

        List<GoodItem> selectedItems = pickRandomItems();

        if (selectedItems.isEmpty()) {
            System.out.println("[" + name + "] Ничего не выбрал и уходит.");
            return;
        }
        try {
            Thread.sleep(random.nextInt(1500) + 500);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            return;
        }

        OrderRequest order = new OrderRequest(orderId, name, selectedItems);
        System.out.println("[" + name + "] Идёт на кассу с " + selectedItems.size() + " позицией(ями).");
        store.submitOrder(order);
    }

    private List<GoodItem> pickRandomItems() {
        List<StockItem> available = new ArrayList<>(catalog.values());
        List<GoodItem> picked = new ArrayList<>();

        int count = random.nextInt(3) + 1;
        for (int i = 0; i < count; i++) {
            StockItem stock = available.get(random.nextInt(available.size()));
            Good good = stock.getGood();

            synchronized (stock) {
                int qty = random.nextInt(2) + 1;
                if (stock.getQuantity() >= qty) {
                    stock.removeQuantity(qty);
                    picked.add(new GoodItem(good, qty));
                    System.out.println("[" + name + "] Берёт: " + good.getName() + " x" + qty);
                } else {
                    System.out.println("[" + name + "] Товар недоступен: " + good.getName());
                }
            }

            try {
                Thread.sleep(random.nextInt(500) + 300);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return picked;
            }
        }

        return picked;
    }
}
