package concurrent;

import domain.GoodItem;
import java.util.List;

public class OrderRequest {

    private final int orderId;
    private final String customerName;
    private final List<GoodItem> items;

    public OrderRequest(int orderId, String customerName, List<GoodItem> items) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.items = items;
    }

    public int getOrderId() {
        return orderId;
    }

    public String getCustomerName() {
        return customerName;
    }

    public List<GoodItem> getItems() {
        return items;
    }

    @Override
    public String toString() {
        return "Заказ{id=" + orderId + ", покупатель='" + customerName + "', товаров=" + items.size() + "}";
    }
}
