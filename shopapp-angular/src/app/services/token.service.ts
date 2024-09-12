import { Injectable } from '@angular/core';
import { JwtHelperService } from '@auth0/angular-jwt';

@Injectable({
  providedIn: 'root',
})
export class TokenService {
  private readonly TOKEN_KEY = 'access_token';
  private jwtHelperService = new JwtHelperService();

  constructor() {}

  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  setToken(token: string): void {
    localStorage.setItem(this.TOKEN_KEY, token);
  }

  removeToken(): void {
    localStorage.removeItem(this.TOKEN_KEY);
  }

  getUserId(): number {
    const token = this.getToken() ?? '';
    let userObject = this.jwtHelperService.decodeToken(token);
    return 'userId' in userObject ? parseInt(userObject['userId']) : 0;
  }

  isTokenExpired(): boolean {
    const token = this.getToken();
    if (token) {
      return this.jwtHelperService.isTokenExpired(token);
    }
    return true;
  }
}
