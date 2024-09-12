export interface CartItemDTO {
  product_id: number;
  quantity: number;
}

export interface OrderDTO {
  user_id: number;
  fullname: string;
  email: string;
  phone_number: string;
  address: string;
  note: string;
  total_money: number;
  shipping_method: string;
  shipping_address: string;
  shipping_date: string;
  payment_method: string;
  cart_items: CartItemDTO[];
}
