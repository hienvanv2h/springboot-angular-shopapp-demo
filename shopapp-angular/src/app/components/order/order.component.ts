import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product';
import { ProductService } from '../../services/product.service';
import { CartService } from '../../services/cart.service';
import { environment } from '../../environment/env';
import { FormBuilder, FormGroup, Validators } from '@angular/forms';
import { OrderDTO } from '../../dtos/order/order.dto';
import { OrderService } from '../../services/order.service';
import { Order } from '../../models/order';
import { Router } from '@angular/router';
import { TokenService } from '../../services/token.service';

export interface CartItem {
  product: Product;
  quantity: number | undefined;
}

@Component({
  selector: 'app-order',
  templateUrl: './order.component.html',
  styleUrl: './order.component.scss',
})
export class OrderComponent implements OnInit {
  cartItems: CartItem[] = [];
  totalMoney: number = 0;
  couponCode: string = '';

  orderForm!: FormGroup;

  constructor(
    private _productService: ProductService,
    private _cartService: CartService,
    private _orderService: OrderService,
    private _tokenService: TokenService,
    private _router: Router,
    private fb: FormBuilder
  ) {}

  ngOnInit(): void {
    const userId = this._tokenService.getUserId();
    // build form
    this.orderForm = this.fb.nonNullable.group({
      user_id: [userId, Validators.required],
      fullname: ['', Validators.required],
      email: ['', [Validators.email]],
      phone_number: ['', [Validators.required, Validators.pattern(/^\d{10}$/)]],
      address: ['', [Validators.minLength(5)]],
      note: [''],
      total_money: [0, Validators.min(0)],
      shipping_method: ['EXPRESS'],
      shipping_address: ['', [Validators.required, Validators.minLength(5)]],
      shipping_date: [new Date().toISOString().substring(0, 10)],
      payment_method: ['CARD'],
      cart_items: this.fb.control([]),
    });

    const cart = this._cartService.getCart();
    const productIds = Array.from(cart.keys());
    if (productIds.length === 0) return;

    this._productService.getProductsByIds(productIds).subscribe({
      next: (response: Product[]) => {
        debugger;
        console.log(response);
        this.cartItems = response.map((product) => {
          product.thumbnail_url = `${environment.apiBaseUrl}/products/images/${
            product.thumbnail_url || 'not-found.jpg'
          }`;
          return {
            product: product,
            quantity: cart.get(product.id),
          };
        });

        // map to cart items in form
        this.orderForm.patchValue({
          cart_items: this.cartItems.map((item) => {
            return {
              product_id: item.product.id,
              quantity: item.quantity,
            };
          }),
        });
        this.calculateTotalMoney();
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

  calculateTotalMoney() {
    this.totalMoney = this.cartItems.reduce(
      (acc, current) => acc + current.product.price * (current.quantity || 0),
      0
    );
    this.orderForm.patchValue({
      total_money: this.totalMoney,
    });
  }

  applyCoupon() {
    // TODO: apply coupon
  }

  // address changes reflect on shipping address
  onAddressChange() {
    this.orderForm
      .get('shipping_address')
      ?.setValue(this.orderForm.get('address')?.value || '');
  }

  placeOrder() {
    if (this.orderForm.valid) {
      if (this.orderForm.get('cart_items')?.value.length === 0) {
        alert('Giỏ hàng ràng buộc phải có ít nhất 1 sản phẩm');
        return;
      }
      const orderData: OrderDTO = {
        ...this.orderForm.value,
      };
      this._orderService.createOrder(orderData).subscribe({
        next: (response: Order) => {
          debugger;
          console.log(response);
          // clear cart
          this._cartService.clearCart();
          // Navigate to order confirm page
          this._router.navigate(['/orders', response.id]);
        },
        error: (error) => {
          debugger;
          alert(`Lỗi khi đặt hàng: ${error}`);
        },
        complete: () => {
          debugger;
          console.log('done');
        },
      });
    } else {
      alert('Dữ liệu không hợp lệ. Vui lòng kiểm tra lại.');
    }
  }

  // form control getters
  get user_id() {
    return this.orderForm.get('user_id');
  }

  get fullname() {
    return this.orderForm.get('fullname');
  }

  get email() {
    return this.orderForm.get('email');
  }

  get phone_number() {
    return this.orderForm.get('phone_number');
  }

  get address() {
    return this.orderForm.get('address');
  }

  get note() {
    return this.orderForm.get('note');
  }

  get total_money() {
    return this.orderForm.get('total_money');
  }

  get shipping_address() {
    return this.orderForm.get('shipping_address');
  }

  get shipping_method() {
    return this.orderForm.get('shipping_method');
  }

  get shipping_date() {
    return this.orderForm.get('shipping_date');
  }

  get payment_method() {
    return this.orderForm.get('payment_method');
  }

  get cart_items() {
    return this.orderForm.get('cart_items');
  }
}
