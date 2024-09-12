import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { environment } from '../../environment/env';
import { Order } from '../../models/order';
import { OrderService } from '../../services/order.service';

@Component({
  selector: 'app-order-confirm',
  templateUrl: './order-confirm.component.html',
  styleUrl: './order-confirm.component.scss',
})
export class OrderConfirmComponent implements OnInit {
  // This component will show the user list of order details of an order

  // test
  orderId = 4;

  orderResponse: Order = {
    id: this.orderId,
    user_id: 1,
    fullname: '',
    email: '',
    phone_number: '',
    address: '',
    note: '',
    order_date: '',
    status: '',
    total_money: 0,
    shipping_method: '',
    shipping_address: '',
    shipping_date: '',
    tracking_number: '',
    payment_method: '',
    active: false,
    order_details: [],
  };

  constructor(private _orderService: OrderService) {}

  ngOnInit(): void {
    this.fetchOrder(this.orderId);
  }

  fetchOrder(id: number): void {
    this._orderService.getOrderById(id).subscribe({
      next: (response: Order) => {
        debugger;
        if (response && response.order_details) {
          response.order_details.forEach((orderDetail) => {
            orderDetail.product.thumbnail_url = `${
              environment.apiBaseUrl
            }/products/images/${
              orderDetail.product.thumbnail_url || 'not-found.jpg'
            }`;
          });
        }
        this.orderResponse = response;
        console.log(this.orderResponse);
      },
      error: (error) => {
        debugger;
        console.log(error);
      },
      complete: () => {
        debugger;
        console.log('done');
      },
    });
  }

  confirmOrder() {
    // TODO: Call service to confirm order
    console.log('confirm order');
  }
}
