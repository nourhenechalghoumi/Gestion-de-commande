package project.management.usersmanagement.entities;

public enum EOrderStatus {

        CREATED("Order created"),

        WAITING("Order placed, awaiting payment confirmation"),

        PAID("Your order has been paid"),

        SHIPPED("Your order has been sent"),

        ON_THE_WAY("Your order is on the way"),

        CANCELED("Your order has been canceled"),

        DELIVERED("Your order has been delivered");

        private final String description;

        EOrderStatus(String description) {
            this.description = description;
        }

        public String getDescription() {
            return description;
        }
    }


