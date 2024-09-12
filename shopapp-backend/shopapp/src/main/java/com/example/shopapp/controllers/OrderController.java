package com.example.shopapp.controllers;

import com.example.shopapp.advice.AppControllerAdvice;
import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.exceptionhandling.ErrorInfo;
import com.example.shopapp.responses.GenericResponse;
import com.example.shopapp.responses.OrderResponse;
import com.example.shopapp.services.OrderService;
import com.example.shopapp.utils.MessageKeys;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Min;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Validated
@RestController
@RequestMapping("${api.prefix}/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    private final LocalizationUtils localizationUtils;

    @PostMapping("")
    public ResponseEntity<?> createOrder(
            @Valid @RequestBody OrderDTO orderDTO,
            BindingResult result
    ) {
        if(result.hasErrors()) {
            var errorMessages = result.getFieldErrors().stream()
                    .map(fieldError -> fieldError.getDefaultMessage())
                    .toList();
            return ResponseEntity.badRequest().body(
                    GenericResponse.getErrorMessages(errorMessages)
            );
        }
        var orderResponse = orderService.createOrder(orderDTO);
        return ResponseEntity.ok(orderResponse);
    }

    @GetMapping("/user/{user_id}")
    // GET http://localhost:8088/api/v1/orders/user/{user_id}
    public ResponseEntity<?> getOrderByUserId(
            @PathVariable("user_id") @Min(value = 1, message = "User ID must be greater than 0") Long userId) {
        List<OrderResponse> orderResponses = orderService.getAllOrdersByUserId(userId);
        return ResponseEntity.ok(orderResponses);
    }

    @GetMapping("/{id}")
    // GET http://localhost:8088/api/v1/orders/{id}
    public ResponseEntity<?> getOrderById(@PathVariable Long id) {
        OrderResponse orderResponse = orderService.getOrderById(id);
        return ResponseEntity.ok(orderResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<OrderResponse> updateOrder(
            @Valid @PathVariable Long id,
            @Valid @RequestBody OrderDTO orderDTO
    ) {
        OrderResponse orderResponse = orderService.updateOrder(id, orderDTO);
        return ResponseEntity.ok(orderResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<String> deleteOrder(
            @Valid @PathVariable Long id
    ) {
        orderService.deleteOrder(id);
        return ResponseEntity.ok(
                localizationUtils.getLocalizedMessage(MessageKeys.ORDER_DELETE_SUCCESS, id)
        );
    }

    @ExceptionHandler(value = {ConstraintViolationException.class})
    public ResponseEntity<ErrorInfo> handleValidationError(Exception exc) {
        return AppControllerAdvice.handleException(exc, HttpStatus.BAD_REQUEST);
    }
}
