package com.food.ordering.system.order.service.domain.entity;

import com.food.ordering.system.domain.entity.AggregateRoot;
import com.food.ordering.system.domain.valueobject.*;
import com.food.ordering.system.order.service.domain.exception.OrderDomainException;
import com.food.ordering.system.order.service.domain.valueobject.OrderItemId;
import com.food.ordering.system.order.service.domain.valueobject.StreetAddress;
import com.food.ordering.system.order.service.domain.valueobject.TrackingId;

import java.util.List;
import java.util.UUID;

import static com.food.ordering.system.domain.valueobject.OrderStatus.*;
import static java.util.stream.Collectors.toList;

public class Order extends AggregateRoot<OrderId> {
    private final CustomerId customerId;
    private final RestaurentId restaurentId;
    private final StreetAddress streetAddress;
    private final Money price;
    private final List<OrderItem> items;

    private TrackingId trackingId;
    private OrderStatus orderStatus;
    private List<String> failureMessage;

    public void initializeOrder() {
        setId(new OrderId(UUID.randomUUID()));
        trackingId = new TrackingId(UUID.randomUUID());
        orderStatus = PENDING;
        initializeOrderItems();
    }

    private void initializeOrderItems() {
        long itemId = 1;
        for(OrderItem orderItem : items) {
            orderItem.initializeOrderItem(super.getId(), new OrderItemId(itemId++));
        }
    }

    public void validateOrder() {
        validateInitialOrder();
        validateTotalPrice();
        validateItemPrice();
    }

    private void validateItemPrice() {
        Money orderItemsTotal = items.stream().map(orderItem -> {
            validateItemPrice(orderItem);
            return orderItem.getSubtotal();
        }).reduce(Money.ZERO, Money::add);

        if(price.equals(orderItemsTotal)) {
            throw new OrderDomainException("Total price: " + price.getAmount() +
                    "is not equal to Order Items tota: " + orderItemsTotal.getAmount());
        }
    }

    private void validateItemPrice(OrderItem orderItem) {
        if (!orderItem.isPriceValid()) {
            throw new OrderDomainException("Order item price: " + price.getAmount()
                    + " is not equal for product " +  orderItem.getProduct().getId().getValue());
        }
    }

    private void validateTotalPrice() {
        if (price == null || !price.isGreaterThanZero()) {
            throw new OrderDomainException("Total price must be greater then zero!");
        }
    }

    private void validateInitialOrder() {
        if (orderStatus != null || getId() != null) {
            throw new OrderDomainException("Order is not in correct state for initialization!");
        }
    }


    /** state machine for order status */
    public void pay() {
        if(orderStatus != PENDING) {
            throw new OrderDomainException("Order is not in correct state to pay operation!");
        }
        orderStatus = PAID;
    }

    public void approve() {
        if(orderStatus != PAID) {
            throw new OrderDomainException("Order is not in correct state for apprive operation!");
        }
        orderStatus = APPROVED;
    }

    public void initCancel(List<String> failureMessage) {
        if(orderStatus != PAID) {
            throw new OrderDomainException("Order is not in correct state to pay operation!");
        }
        orderStatus = CANCELLING;
        updateFailureMessages(failureMessage);
    }

    public void cancel(List<String> failureMessage) {
        if(!(orderStatus != CANCELLING || orderStatus != PENDING)) {
            throw new OrderDomainException("Order is not in correct state to pay operation!");
        }
        orderStatus = CANCELLED;
        updateFailureMessages(failureMessage);
    }

    private void updateFailureMessages(List<String> failureMessage) {
        if(this.failureMessage != null && failureMessage != null) {
            this.failureMessage.addAll(failureMessage.stream().filter(message -> !message.isEmpty()).collect(toList()));
        }

        if(this.failureMessage == null) {
            this.failureMessage = failureMessage;
        }
    }



//    **********************************    builders and getters

    private Order(Builder builder) {
        super.setId(builder.orderId);
        customerId = builder.customerId;
        restaurentId = builder.restaurentId;
        streetAddress = builder.streetAddress;
        price = builder.money;
        items = builder.items;
        trackingId = builder.trackingId;
        orderStatus = builder.orderStatus;
        failureMessage = builder.failureMessage;
    }

    public static Builder builder() {
        return new Builder();
    }

    public static final class Builder {
        private OrderId orderId;
        private CustomerId customerId;
        private RestaurentId restaurentId;
        private StreetAddress streetAddress;
        private Money money;
        private List<OrderItem> items;
        private TrackingId trackingId;
        private OrderStatus orderStatus;
        private List<String> failureMessage;

        private Builder() {
        }

        public Builder id(OrderId val) {
            orderId = val;
            return this;
        }

        public Builder customerId(CustomerId val) {
            customerId = val;
            return this;
        }

        public Builder restaurentId(RestaurentId val) {
            restaurentId = val;
            return this;
        }

        public Builder streetAddress(StreetAddress val) {
            streetAddress = val;
            return this;
        }

        public Builder money(Money val) {
            money = val;
            return this;
        }

        public Builder items(List<OrderItem> val) {
            items = val;
            return this;
        }

        public Builder trackingId(TrackingId val) {
            trackingId = val;
            return this;
        }

        public Builder orderStatus(OrderStatus val) {
            orderStatus = val;
            return this;
        }

        public Builder failureMessage(List<String> val) {
            failureMessage = val;
            return this;
        }

        public Order build() {
            return new Order(this);
        }
    }

    public CustomerId getCustomerId() {
        return customerId;
    }

    public RestaurentId getRestaurentId() {
        return restaurentId;
    }

    public StreetAddress getStreetAddress() {
        return streetAddress;
    }

    public Money getPrice() {
        return price;
    }

    public List<OrderItem> getItems() {
        return items;
    }

    public TrackingId getTrackingId() {
        return trackingId;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }

    public List<String> getFailureMessage() {
        return failureMessage;
    }
}

