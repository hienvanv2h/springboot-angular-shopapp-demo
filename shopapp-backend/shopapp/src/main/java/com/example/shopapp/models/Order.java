package com.example.shopapp.models;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "orders")
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @Column(name = "fullname", length = 100, nullable = false)
    private String fullName;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone_number", length = 20, nullable = false)
    private String phoneNumber;

    @Column(name = "address", length = 200, nullable = false)
    private String address;

    @Column(name = "note", length = 100)
    private String note;

    @Column(name = "order_date")
    private LocalDateTime orderDate;

    @Column(name = "status")
//    @Enumerated(EnumType.STRING)
    @Convert(converter = OrderStatusConverter.class)
    private OrderStatus status;

    @Column(name = "total_money")
    @DecimalMin(value = "0.00", message = "Total money must be greater or equal to 0")
    private Float totalMoney;

    @Column(name = "shipping_method", length = 100)
    private String shippingMethod;

    @Column(name = "shipping_address", length = 200)
    private String shippingAddress;

    @Column(name = "shipping_date")
    private LocalDate shippingDate;

    @Column(name = "tracking_number", length = 100)
    private String trackingNumber;

    @Column(name = "payment_method", length = 100)
    private String paymentMethod;

    @Column(name = "active")
    private boolean active;

    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonManagedReference
    private List<OrderDetail> orderDetails;
}
