import { Injectable } from '@angular/core';

@Injectable({
  providedIn: 'root',
})
export class CartService {
  private cart: Map<number, number> = new Map(); // Map<productId, quantity>

  constructor() {
    debugger;
    const storedCart = localStorage.getItem('cart');
    // "cart": {"1": 2, "2": 1, "3": 3}
    if (storedCart) {
      this.cart = new Map(JSON.parse(storedCart)); // deserialize
    }
  }

  saveCartToLocalStorage(): void {
    debugger;
    localStorage.setItem('cart', JSON.stringify([...this.cart])); // serialize
  }

  getCart(): Map<number, number> {
    return this.cart;
  }

  addToCart(productId: number, quantity: number): void {
    debugger;
    if (this.cart.has(productId)) {
      this.cart.set(productId, this.cart.get(productId)! + quantity);
    } else {
      this.cart.set(productId, quantity);
    }
    this.saveCartToLocalStorage();
  }

  clearCart(): void {
    this.cart.clear();
    this.saveCartToLocalStorage();
  }
}
