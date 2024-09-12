package com.example.shopapp.services;

import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.responses.OrderResponse;

import java.util.List;

public interface OrderService {

    OrderResponse createOrder(OrderDTO request);

    List<OrderResponse> getAllOrdersByUserId(Long userId);

    OrderResponse getOrderById(Long id);

    OrderResponse updateOrder(Long id, OrderDTO orderDTO);

    void deleteOrder(Long id);
}
