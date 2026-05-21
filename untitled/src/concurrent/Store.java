package concurrent;

import behavioral.BudgetWatcher;
import behavioral.LoyaltyPricing;
import behavioral.ReceiptVisitor;
import domain.GoodItem;
import domain.Purchase;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Store implements Runnable {

    private final BlockingQueue<OrderRequest> orderQueue;
    private final int totalOrdersExpected;

    public Store(int totalOrdersExpected) {
        this.orderQueue = new LinkedBlockingQueue<>();
        this.totalOrdersExpected = totalOrdersExpected;
    }

    public void submitOrder(OrderRequest order) {
        try {
            orderQueue.put(order);
            System.out.println("[Касса] Принят " + order);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            System.out.println("[Касса] Прервана при приеме заказа.");
        }
    }

    @Override
    public void run() {
        System.out.println("[Касса] Открыта и готова к обработке заказов.\n");
        int processed = 0;

        while (processed < totalOrdersExpected) {
            try {
                OrderRequest order = orderQueue.take();
                processOrder(order);
                processed++;
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                System.out.println("[Касса] Прервана.");
                break;
            }
        }

        System.out.println("\n[Касса] Все заказы обработаны. Магазин закрывается.");
    }

    private void processOrder(OrderRequest order) {
        System.out.println("\n[Касса] Обрабатывает " + order + "...");

        Purchase cart = new Purchase("Заказ #" + order.getOrderId());
        cart.addObserver(new BudgetWatcher(100000));

        for (GoodItem item : order.getItems()) {
            cart.add(item);
            System.out.println("[Касса] Пробивает: " + item.getName());
            try {
                Thread.sleep(600);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }

        cart.setStrategy(new LoyaltyPricing(10000, 0.15));
        double loyaltyPrice = cart.getCalculatedCost();

        ReceiptVisitor visitor = new ReceiptVisitor();
        cart.accept(visitor);

        System.out.println("--- ЧЕК [" + order.getCustomerName() + "] ---");
        System.out.println(visitor.print());
        System.out.printf("Базовая стоимость : %.2f руб.%n", cart.getTotalCost());
        System.out.printf("По программе лояльности: %.2f руб.%n", loyaltyPrice);
        System.out.printf("Итого (лояльность): %.2f руб.%n", loyaltyPrice);
        System.out.println("[Касса] Завершила обработку " + order + "\n");
    }
}
