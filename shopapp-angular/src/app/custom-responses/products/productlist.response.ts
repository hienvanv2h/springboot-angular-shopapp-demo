import { Product } from '../../models/product';

export interface ProductListResponse {
  products: Product[];
  total_pages: number;
}
