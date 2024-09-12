package com.example.shopapp.services;

import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.models.OrderDetail;

import java.util.List;

public interface OrderDetailService {

    OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO);

    List<OrderDetail> getOrderDetailsByOrderId(Long orderId);

    OrderDetail getOrderDetailById(Long id);

    OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO);

    void deleteOrderDetail(Long id);
}
