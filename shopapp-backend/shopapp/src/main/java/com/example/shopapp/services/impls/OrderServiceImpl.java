package com.example.shopapp.services.impls;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.CartItemDTO;
import com.example.shopapp.dtos.OrderDTO;
import com.example.shopapp.exceptionhandling.DataNotFoundException;
import com.example.shopapp.exceptionhandling.InvalidParamException;
import com.example.shopapp.models.*;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.repositories.UserRepository;
import com.example.shopapp.responses.OrderResponse;
import com.example.shopapp.services.OrderService;
import com.example.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final UserRepository userRepository;

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ModelMapper modelMapper;

    private final LocalizationUtils localizationUtils;

    @Override
    public OrderResponse createOrder(OrderDTO orderDTO) {
        // login required
        User existingUser = userRepository
                .findById(orderDTO.getUserId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, orderDTO.getUserId())
                        )
                );

        // Using ModelMapper
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));

        Order order = new Order();
        modelMapper.map(orderDTO, order);
        // Check shipping date
        LocalDate shippingDate = orderDTO.getShippingDate() != null ? orderDTO.getShippingDate() : LocalDate.now();
        if(shippingDate.isBefore(LocalDate.now())) {
            throw new InvalidParamException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ORDER_SHIPPING_DATE_INVALID)
            );
        }
        order.setShippingDate(shippingDate);
        order.setUser(existingUser);
        order.setOrderDate(LocalDateTime.now());
        order.setStatus(OrderStatus.PENDING);
        order.setActive(true);

        // save
        orderRepository.save(order);

        // create list of OrderDetail from cartItems
        createOrderDetails(orderDTO.getCartItems(), order);

        // Reconfigure modelmapper: Order -> OrderResponse
        modelMapper.typeMap(Order.class, OrderResponse.class);
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public List<OrderResponse> getAllOrdersByUserId(Long userId) {
        List<Order> orders = orderRepository.findByUserId(userId);
        return orders.stream()
                .map(order -> modelMapper.map(order, OrderResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public OrderResponse getOrderById(Long id) {
        Order order = orderRepository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, id)
                        )
                );
        return modelMapper.map(order, OrderResponse.class);
    }

    @Override
    public OrderResponse updateOrder(Long id, OrderDTO orderDTO) {
        Order existingOrder = orderRepository.findById(id)
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, id)
                        )
                );
        User existingUser = userRepository.findById(orderDTO.getUserId())
                .orElseThrow(() ->
                        new DataNotFoundException(
                                localizationUtils.getLocalizedMessage(MessageKeys.USER_NOT_FOUND, orderDTO.getUserId())
                        )
                );
        modelMapper.typeMap(OrderDTO.class, Order.class)
                .addMappings(mapper -> mapper.skip(Order::setId));
        modelMapper.map(orderDTO, existingOrder);
        existingOrder.setUser(existingUser);
        orderRepository.save(existingOrder);

        modelMapper.typeMap(Order.class, OrderResponse.class)
                .addMapping(src -> src.getUser().getId(), OrderResponse::setUserId);
        return modelMapper.map(existingOrder, OrderResponse.class);
    }

    @Override
    public void deleteOrder(Long id) {
        Order existingOrder = orderRepository.findById(id).orElse(null);
        // soft delete
        if (existingOrder != null) {
            existingOrder.setActive(false);
            orderRepository.save(existingOrder);
        } else {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, id)
            );
        }
    }

    private void createOrderDetails(List<CartItemDTO> cartItems, Order order) {
        List<OrderDetail> orderDetails = new ArrayList<>();
        for(var item : cartItems) {
            Product existingProduct = productRepository.findById(item.getProductId())
                    .orElseThrow(() ->
                            new DataNotFoundException(
                                    localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, item.getProductId()))
                            );
            // TODO: check product quantity in storage
            // Set Order active to false if out of stocks

            OrderDetail orderDetail = OrderDetail.builder()
                    .order(order)
                    .product(existingProduct)
                    .price(existingProduct.getPrice())
                    .numberOfProducts(item.getQuantity())
                    .totalMoney(existingProduct.getPrice() * item.getQuantity())
                    .build();
            orderDetails.add(orderDetail);
        }
        orderDetailRepository.saveAll(orderDetails);
    }
}
