package com.mediai.service;

import com.mediai.entity.*;
import com.mediai.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepo;

    public Order save(Order order) { return orderRepo.save(order); }

    public List<Order> findByUserId(long userId) {
        return orderRepo.findByUserIdOrderByOrderDateDesc(userId);
    }

    public List<Order> findAll() { return orderRepo.findAll(); }

    public Optional<Order> findById(long id) { return orderRepo.findById(id); }

    public Order updateStatus(long id, String status) {
        Order o = orderRepo.findById(id).orElseThrow();
        o.setStatus(status);
        return orderRepo.save(o);
    }
}
