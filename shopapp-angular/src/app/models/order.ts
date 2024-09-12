import { OrderDetail } from './order-detail';

export interface Order {
  id: number;
  user_id: number;
  fullname: string;
  email: string;
  phone_number: string;
  address: string;
  note: string;
  order_date: string;
  status: string;
  total_money: number;
  shipping_method: string;
  shipping_address: string;
  shipping_date: string;
  tracking_number: string;
  payment_method: string;
  active: boolean;
  order_details: OrderDetail[];
}
