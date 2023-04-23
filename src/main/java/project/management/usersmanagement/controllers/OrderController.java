package project.management.usersmanagement.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import project.management.usersmanagement.Config.CouponException;
import project.management.usersmanagement.Repository.OrderItemsRepository;
import project.management.usersmanagement.Repository.OrderRepo;
import project.management.usersmanagement.Repository.UserRepository;
import project.management.usersmanagement.entities.Order;
import project.management.usersmanagement.entities.OrderItem;
import project.management.usersmanagement.entities.User;
import project.management.usersmanagement.security.services.OrderServiceImplement;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {
    @Autowired
        private OrderServiceImplement orderService;
        @Autowired
        private UserRepository userRepository;
        @Autowired
        private OrderRepo orderRepository;
        private OrderItemsRepository orderItemsRepository;
        @PostMapping("/{userId}")
        public Order createOrder(User user, List<OrderItem> orderItems) {
            // calculate the total price of the items
            Double totalPrice = orderItems.stream()
                    .mapToDouble(item -> item.getPrice())
                    .sum();

            // create a new order object and set the total price
            Order order = new Order();
            order.setUser(user);
            order.setOrderItems(orderItems);
            order.setTotalPrice(totalPrice);

            // save the order to the database
            orderRepository.save(order);

            return order;
        }


    @GetMapping("/{id}")
        public ResponseEntity<Order> getOrderById(@PathVariable("id") Long id) {
            Order order = orderService.getOrderById(id);
            return ResponseEntity.ok().body(order);
        }

        @GetMapping("/user/{userId}")
        public ResponseEntity<List<Order>> getOrdersByUser(@PathVariable("userId") Long userId) {
            User user = userRepository.getUserById(userId);
            List<Order> orders = orderService.getOrdersByUser(user);
            return ResponseEntity.ok().body(orders);
        }

      /*  @PostMapping("/{userId}/purchase")
        public ResponseEntity<Purchase> createPurchase(@PathVariable("userId") Long userId,
                                                       @RequestParam("amount") Double amount) {
            User user = userRepository.getUserById(userId);
            Purchase purchase = orderService.createPurchase(user, amount);
            return ResponseEntity.ok().body(purchase);
        }*/

        @PostMapping("/{userId}/coupon")
        public ResponseEntity<Order> applyCoupon(@PathVariable("userId") Long userId,
                                                 @RequestParam("orderId") Long orderId,
                                                 @RequestParam("couponCode") String couponCode) {
            User user = userRepository.getUserById(userId);
            Order order = orderService.getOrderById(orderId);
            try {
                order = orderService.applyCoupon(user, order, couponCode);
            } catch (CouponException e) {
                return ResponseEntity.badRequest().body(null);
            }
            return ResponseEntity.ok().body(order);
        }
    }

