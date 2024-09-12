import { Injectable } from '@angular/core';
import { environment } from '../environment/env';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class CategoryService {
  private _url = `${environment.apiBaseUrl}/categories`;

  constructor(private http: HttpClient) {}

  getAllCategories(): Observable<any> {
    return this.http.get<any[]>(this._url);
  }
}
