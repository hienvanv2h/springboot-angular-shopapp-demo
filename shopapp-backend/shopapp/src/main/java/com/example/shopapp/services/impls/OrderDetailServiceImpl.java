package com.example.shopapp.services.impls;

import com.example.shopapp.components.LocalizationUtils;
import com.example.shopapp.dtos.OrderDetailDTO;
import com.example.shopapp.exceptionhandling.DataNotFoundException;
import com.example.shopapp.models.Order;
import com.example.shopapp.models.OrderDetail;
import com.example.shopapp.models.Product;
import com.example.shopapp.repositories.OrderDetailRepository;
import com.example.shopapp.repositories.OrderRepository;
import com.example.shopapp.repositories.ProductRepository;
import com.example.shopapp.services.OrderDetailService;
import com.example.shopapp.utils.MessageKeys;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderDetailServiceImpl implements OrderDetailService {

    private final OrderRepository orderRepository;

    private final ProductRepository productRepository;

    private final OrderDetailRepository orderDetailRepository;

    private final ModelMapper modelMapper;

    private final LocalizationUtils localizationUtils;

    @Override
    @Transactional
    public OrderDetail createOrderDetail(OrderDetailDTO orderDetailDTO) {
        Order order = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderDetailDTO.getOrderId())));
        Product product = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, orderDetailDTO.getProductId())));
        OrderDetail newOrderDetail = OrderDetail.builder()
                .order(order)
                .product(product)
                .price(orderDetailDTO.getPrice())
                .numberOfProducts(orderDetailDTO.getNumberOfProducts())
                .totalMoney(orderDetailDTO.getTotalMoney())
                .color(orderDetailDTO.getColor())
                .build();
        return orderDetailRepository.save(newOrderDetail);
    }

    @Override
    public List<OrderDetail> getOrderDetailsByOrderId(Long orderId) {
        return orderDetailRepository.findByOrderId(orderId);
    }

    @Override
    public OrderDetail getOrderDetailById(Long id) {
        return orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_DETAIL_NOT_FOUND, id)
                    )
                );
    }

    @Override
    @Transactional
    public OrderDetail updateOrderDetail(Long id, OrderDetailDTO orderDetailDTO) {
        OrderDetail existingOrderDetail = orderDetailRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_DETAIL_NOT_FOUND, id)));
        Order existingOrder = orderRepository.findById(orderDetailDTO.getOrderId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.ORDER_NOT_FOUND, orderDetailDTO.getOrderId())));
        Product existingProduct = productRepository.findById(orderDetailDTO.getProductId())
                .orElseThrow(() -> new DataNotFoundException(
                        localizationUtils.getLocalizedMessage(MessageKeys.PRODUCT_NOT_FOUND, orderDetailDTO.getProductId())));

        modelMapper.typeMap(OrderDetailDTO.class, OrderDetail.class)
                .addMappings(mapper -> mapper.skip(OrderDetail::setId));
        modelMapper.map(orderDetailDTO, existingOrderDetail);
        existingOrderDetail.setOrder(existingOrder);
        existingOrderDetail.setProduct(existingProduct);

        return orderDetailRepository.save(existingOrderDetail);
    }

    @Override
    @Transactional
    public void deleteOrderDetail(Long id) {
        if(orderDetailRepository.existsById(id)) {
            // hard delete
            orderDetailRepository.deleteById(id);
        } else {
            throw new DataNotFoundException(
                    localizationUtils.getLocalizedMessage(MessageKeys.ORDER_DETAIL_NOT_FOUND, id)
            );
        }
    }
}
