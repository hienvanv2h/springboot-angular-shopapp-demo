import { Injectable } from '@angular/core';
import { environment } from '../environment/env';
import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Observable } from 'rxjs';
import { OrderDTO } from '../dtos/order/order.dto';

@Injectable({
  providedIn: 'root',
})
export class OrderService {
  private _url = `${environment.apiBaseUrl}/orders`;
  private apiConfig = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      'Accept-Language': 'vi',
    }),
  };

  constructor(private http: HttpClient) {}

  createOrder(orderData: OrderDTO): Observable<any> {
    return this.http.post<any>(this._url, orderData, this.apiConfig);
  }

  getOrderById(id: number): Observable<any> {
    return this.http.get<any>(`${this._url}/${id}`, this.apiConfig);
  }
}
