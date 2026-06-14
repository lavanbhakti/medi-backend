package com.mediai.controller;

import com.mediai.entity.*;
import com.mediai.service.*;
import com.mediai.security.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.*;

@RestController
@RequestMapping("/api/orders")
public class OrderController {

    @Autowired private OrderService orderService;
    @Autowired private MedicineService medicineService;
    @Autowired private UserService userService;
    @Autowired private JwtUtil jwtUtil;

    /**
     * POST /api/orders/place
     * Body: {
     *   "deliveryAddress": "...",
     *   "phone": "...",
     *   "items": [ { "medicineId": 1, "quantity": 2 }, ... ]
     * }
     */
    @PostMapping("/place")
    public ResponseEntity<?> placeOrder(
            @RequestBody Map<String, Object> body,
            @RequestHeader("Authorization") String authHeader) {

        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userService.findByEmail(email).orElseThrow();

        Order order = new Order();
        order.setUser(user);
        order.setDeliveryAddress((String) body.get("deliveryAddress"));
        order.setPhone((String) body.get("phone"));

        @SuppressWarnings("unchecked")
        List<Map<String, Object>> itemsData = (List<Map<String, Object>>) body.get("items");

        List<OrderItem> items = new ArrayList<>();
        double total = 0;

        for (Map<String, Object> itemData : itemsData) {
            long medId = Long.parseLong(itemData.get("medicineId").toString());
            int qty = Integer.parseInt(itemData.get("quantity").toString());

            Medicine med = medicineService.findById(medId).orElseThrow();

            OrderItem item = new OrderItem();
            item.setOrder(order);
            item.setMedicine(med);
            item.setQuantity(qty);
            item.setPrice(med.getPrice());
            items.add(item);
            total += med.getPrice() * qty;
        }

        order.setItems(items);
        order.setTotalAmount(total);
        Order saved = orderService.save(order);

        return ResponseEntity.ok(Map.of(
            "success", true,
            "orderId", saved.getId(),
            "message", "Order placed successfully!",
            "total", total
        ));
    }

    /** GET /api/orders/my — user's own orders */
    @GetMapping("/my")
    public ResponseEntity<?> myOrders(@RequestHeader("Authorization") String authHeader) {
        String token = authHeader.substring(7);
        String email = jwtUtil.extractEmail(token);
        User user = userService.findByEmail(email).orElseThrow();
        return ResponseEntity.ok(orderService.findByUserId(user.getId()));
    }

    /** GET /api/orders/all — admin sees all orders */
    @GetMapping("/all")
    public ResponseEntity<?> allOrders() {
        return ResponseEntity.ok(orderService.findAll());
    }

    /** PUT /api/orders/{id}/status — admin updates order status */
    @PutMapping("/{id}/status")
    public ResponseEntity<?> updateStatus(@PathVariable long id,
                                          @RequestBody Map<String, String> body) {
        Order updated = orderService.updateStatus(id, body.get("status"));
        return ResponseEntity.ok(Map.of("success", true, "status", updated.getStatus()));
    }
}
