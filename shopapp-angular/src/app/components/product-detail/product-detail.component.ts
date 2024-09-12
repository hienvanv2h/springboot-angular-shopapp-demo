import { Component, OnInit } from '@angular/core';
import { Product } from '../../models/product';
import { ProductService } from '../../services/product.service';
import { environment } from '../../environment/env';
import { CartService } from '../../services/cart.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-product-detail',
  templateUrl: './product-detail.component.html',
  styleUrl: './product-detail.component.scss',
})
export class ProductDetailComponent implements OnInit {
  product: Product = {} as Product;
  productId!: number;
  currentImageIndex: number = 0;

  quantity: number = 1;

  constructor(
    private _productService: ProductService,
    private _cartService: CartService,
    private _router: Router
  ) {}

  ngOnInit(): void {
    this.productId = Number(window.location.pathname.split('/').pop());

    this._productService.getProductById(this.productId).subscribe({
      next: (response: Product) => {
        debugger;
        console.log(response);
        if (response.product_images.length > 0) {
          response.product_images.forEach((productImage) => {
            productImage.image_url = `${environment.apiBaseUrl}/products/images/${productImage.image_url}`;
          });
        } else {
          response.product_images = [
            {
              id: 0,
              image_url: `${environment.apiBaseUrl}/products/images/not-found.jpg`,
            },
          ];
        }
        this.product = response;
        console.log('product', this.product);
        this.showImage(0);
      },
      error: (error) => {
        debugger;
        console.log(error);
      },
      complete: () => {
        console.log('Loaded product details!');
      },
    });
  }

  // image
  showImage(index: number) {
    if (this.product && this.product.product_images.length > 0) {
      if (index < 0) index = 0;
      else if (index >= this.product.product_images.length) {
        index = this.product.product_images.length - 1;
      }

      this.currentImageIndex = index;
    }
  }

  nextImage() {
    this.showImage(this.currentImageIndex + 1);
  }

  previousImage() {
    this.showImage(this.currentImageIndex - 1);
  }

  // cart
  addToCart() {
    debugger;
    if (this.product) {
      this._cartService.addToCart(this.product.id, this.quantity);
      alert('Thêm vào giở hàng thành công!');
    } else {
      alert('Product is NULL');
    }
  }

  increaseQuantity() {
    this.quantity++;
  }

  decreaseQuantity() {
    if (this.quantity > 1) {
      this.quantity--;
    }
  }

  buyNow() {
    // Add product info to local storage
    this.addToCart();

    // Navigate to order page
    this._router.navigate(['/orders']);
  }
}
