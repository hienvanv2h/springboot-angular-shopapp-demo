import { ProductImage } from './product-image';

export interface Product {
  id: number;
  name: string;
  price: number;
  description: string;
  thumbnail_url: string;
  product_images: ProductImage[];
  category_id: number;
  create_at: string;
  update_at: string;
}
