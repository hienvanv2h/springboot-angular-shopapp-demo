package com.example.shopapp.controllers;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.responses.GenericResponse;
import com.example.shopapp.responses.OrderDetailResponse;
import com.example.shopapp.services.OrderDetailService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("${api.prefix}/order_details")
@RequiredArgsConstructor
public class OrderDetailController {

    private final OrderDetailService orderDetailService;

    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createOrderDetail(
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            var errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    GenericResponse.getErrorMessages(errorMessages)
            );
        }
        OrderDetail newOrderDetail = orderDetailService.createOrderDetail(orderDetailDTO);
        return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(newOrderDetail));
    }

    @GetMapping("/{id}")
    public ResponseEntity<OrderDetailResponse> getOrderDetailById(@PathVariable Long id) {
        OrderDetail orderDetail = orderDetailService.getOrderDetailById(id);
        return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
    }

    // Get all order details of an order
    @GetMapping("/order/{orderId}")
    public ResponseEntity<List<OrderDetailResponse>> getOrderDetailsByOrderId(@PathVariable Long orderId) {
        List<OrderDetail> orderDetails = orderDetailService.getOrderDetailsByOrderId(orderId);
        List<OrderDetailResponse> orderDetailResponses = orderDetails.stream()
                .map(OrderDetailResponse::fromOrderDetail)
                .toList();
        return ResponseEntity.ok(orderDetailResponses);
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateOrderDetail(
            @PathVariable Long id,
            @Valid @RequestBody OrderDetailDTO orderDetailDTO,
            BindingResult result
    ) {
        if (result.hasErrors()) {
            var errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    GenericResponse.getErrorMessages(errorMessages)
            );
        }
        OrderDetail orderDetail = orderDetailService.updateOrderDetail(id, orderDetailDTO);
        return ResponseEntity.ok(OrderDetailResponse.fromOrderDetail(orderDetail));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrderDetail(
            @PathVariable Long id
    ) {
        orderDetailService.deleteOrderDetail(id);
        return ResponseEntity.ok(
                localizationUtils.getLocalizedMessage(MessageKeys.ORDER_DETAIL_DELETE_SUCCESS, id)
        );
    }
}
