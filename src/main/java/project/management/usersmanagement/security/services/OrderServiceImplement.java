package project.management.usersmanagement.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.social.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import project.management.usersmanagement.Config.CouponException;
import project.management.usersmanagement.Repository.*;
import project.management.usersmanagement.entities.*;

import java.util.Date;
import java.util.List;
import java.util.Optional;
@Service

public class OrderServiceImplement  implements IOrder {
    @Autowired
    private OrderRepo orderRepository;
    @Autowired
    private OrderItemRep orderItemRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private CouponRepo couponRepository;

    @Override
    public Order createOrder(User user, List<OrderItem> orderItems) {
        Order order = new Order();
        order.setUser(user);
        order.setOrderItems(orderItems);
        order.setStatus(EOrderStatus.CREATED);
        order.setCreatedDate(new Date());
        order.setUpdatedAt(new Date());
        order.setTotalPrice(calculateTotalPrice(orderItems));
        order = orderRepository.save(order);
        return orderRepository.findByUser(user).get(0);
    }

    @Override
    public List<Order> getOrdersByUser(User user) {
        return orderRepository.findByUser(user);
    }

    @Override
    public Order getOrderById(Long id) {
        return orderRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Order", "id"));
    }

    @Override
    public Double calculateTotalPrice(List<OrderItem> orderItems) {
        Double totalPrice = 0.0;
        for (OrderItem orderItem : orderItems) {
            totalPrice += orderItem.getProduct().getPrice() * orderItem.getQuantity();
        }
        return totalPrice;
    }

    @Override
    public List<ERole> getUserRoles(User user) {
        return userRepository.getRoles(user);
    }

    /*@Override
    public Purchase createPurchase(User user, Double amount) {
        Purchase purchase = new Purchase();
        purchase.setUser(user);
        purchase.setAmount(amount);
        purchase.setCreatedAt(new Date());
        purchase.setUpdatedAt(new Date());
        //purchase = purchaseRepository.save(purchase);
        return purchase;
        //return stripeService.chargeCreditCard(user, amount);

    }*/

    @Override
    public boolean isEligibleForCoupon(User user, Double amount) {
        if (amount >= 500.0 && userRepository.getPurchasesAmountByUser(user) >= 500.0) {
            return true;
        }
        return false;
    }

    @Override
    public Order applyCoupon(User user, Order order, String couponCode) throws CouponException {
        Optional<Coupon> coupon = couponRepository.findByCode(couponCode);
        if (coupon.isPresent()) {
            if (order.getCoupon() != null) {
                throw new CouponException("Order already has a coupon applied");
            }
            if (!coupon.get().getUsers().contains(user)) {
                throw new CouponException("Coupon code is not valid for this user");
            }
            if (!coupon.get().isValid()) {
                throw new CouponException("Coupon code is expired");
            }
            if (isEligibleForCoupon(user, calculateTotalPrice(order.getItems()))) {
                order.setCoupon(coupon.get());
                double discount = coupon.get().getDiscount().doubleValue();
                order.setTotalPrice(calculateTotalPrice(order.getItems()) * (1 - discount));
                orderRepository.save(order);
            } else {
                throw new CouponException("User is not eligible for this coupon");
            }
        } else {
            throw new CouponException("Coupon code is invalid");
        }

        return order;
    }
}
