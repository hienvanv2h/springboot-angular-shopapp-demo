import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { environment } from '../environment/env';
import { ProductListResponse } from '../custom-responses/products/productlist.response';
import { Product } from '../models/product';

@Injectable({
  providedIn: 'root',
})
export class ProductService {
  private _url = `${environment.apiBaseUrl}/products`;

  constructor(private http: HttpClient) {}

  getProducts(
    keyword: string,
    categoryId: number,
    page: number,
    limit: number
  ): Observable<ProductListResponse> {
    const params = new HttpParams()
      .set('keyword', keyword)
      .set('category_id', categoryId.toString())
      .set('page', page.toString())
      .set('limit', limit.toString());
    return this.http.get<ProductListResponse>(this._url, { params });
  }

  getProductById(id: number): Observable<Product> {
    return this.http.get<Product>(`${this._url}/${id}`);
  }

  getProductsByIds(productIds: number[]): Observable<Product[]> {
    const params = new HttpParams().set('ids', productIds.join(','));
    return this.http.get<Product[]>(`${this._url}/by-ids`, { params });
  }

  // createProduct(product: any): Observable<any> {
  //   return this.http.post<any>(this._url, product);
  // }

  // updateProduct(id: number, product: any): Observable<any> {
  //   return this.http.put<any>(`${this._url}/${id}`, product);
  // }

  // deleteProduct(id: number): Observable<any> {
  //   return this.http.delete<any>(`${this._url}/${id}`);
  // }
}
