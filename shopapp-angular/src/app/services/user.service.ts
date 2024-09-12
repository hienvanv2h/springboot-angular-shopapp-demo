import { HttpClient, HttpHeaders } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { RegisterDTO } from '../dtos/user/register.dto';
import { LoginDTO } from '../dtos/user/login.dto';
import { environment } from '../environment/env';
import { UserDetail } from '../models/user-detail';
import { UserDetailDTO } from '../dtos/user/user-detail.dto';

@Injectable({
  providedIn: 'root',
})
export class UserService {
  private _registerUrl = `${environment.apiBaseUrl}/users/register`;
  private _loginUrl = `${environment.apiBaseUrl}/users/login`;
  private _userDetailsUrl = `${environment.apiBaseUrl}/users/details`;

  private apiConfig = {
    headers: new HttpHeaders({
      'Content-Type': 'application/json',
      // 'Accept-Language': 'vi',
    }),
  };
  constructor(private http: HttpClient) {}

  registerUser(registerDTO: RegisterDTO): Observable<any> {
    return this.http.post(this._registerUrl, registerDTO, this.apiConfig);
  }

  loginUser(loginDTO: LoginDTO): Observable<any> {
    return this.http.post(this._loginUrl, loginDTO, this.apiConfig);
  }

  getUserDetails(token: string): Observable<any> {
    return this.http.get(this._userDetailsUrl, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      }),
    });
  }

  updateUserDetails(
    token: string,
    userId: number,
    userDetailDto: UserDetailDTO
  ): Observable<any> {
    return this.http.put(`${this._userDetailsUrl}/${userId}`, userDetailDto, {
      headers: new HttpHeaders({
        'Content-Type': 'application/json',
        Authorization: `Bearer ${token}`,
      }),
    });
  }

  saveUserDetailsToLocalStorage(userDetail: UserDetail): void {
    try {
      const userDetailJSON = JSON.stringify(userDetail);
      // save user detail to local storage
      localStorage.setItem('user', userDetailJSON);
    } catch (error) {
      console.error('Error saving user details:', error);
    }
  }

  getUserDetailsFromLocalStorage(): UserDetail | null {
    try {
      const userDetailJSON = localStorage.getItem('user');
      if (userDetailJSON) {
        const userDetail: UserDetail = JSON.parse(userDetailJSON);
        return userDetail;
      }
      return null;
    } catch (error) {
      console.error('Error retrieving user details from localStorage:', error);
      return null;
    }
  }

  removeUserDetailsFromLocalStorage(): void {
    try {
      localStorage.removeItem('user');
      console.log('User details removed from localStorage');
    } catch (error) {
      console.error('Error removing user details from localStorage:', error);
    }
  }
}
