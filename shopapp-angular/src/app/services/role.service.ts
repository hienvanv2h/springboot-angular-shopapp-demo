import { Injectable } from '@angular/core';
import { environment } from '../environment/env';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root',
})
export class RoleService {
  private _url = `${environment.apiBaseUrl}/roles`;

  constructor(private http: HttpClient) {}

  getRoles(): Observable<any> {
    return this.http.get<any[]>(this._url);
  }
}
