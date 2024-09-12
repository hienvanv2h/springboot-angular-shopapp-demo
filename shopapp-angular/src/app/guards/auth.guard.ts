import { Injectable, inject } from '@angular/core';
import {
  ActivatedRouteSnapshot,
  CanActivateFn,
  Router,
  RouterStateSnapshot,
} from '@angular/router';
import { TokenService } from '../services/token.service';

@Injectable({
  providedIn: 'root',
})
export class AuthGuard {
  constructor(private router: Router, private tokenService: TokenService) {}

  canActivate(
    next: ActivatedRouteSnapshot,
    state: RouterStateSnapshot
  ): boolean {
    const isTokenExpired = this.tokenService.isTokenExpired();
    const isUserIdValid = this.tokenService.getUserId() > 0;
    debugger;
    if (isTokenExpired || !isUserIdValid) {
      this.router.navigate(['/login']);
      return false;
    } else {
      return true;
    }
  }
}

export const AuthGuardFn: CanActivateFn = (
  next: ActivatedRouteSnapshot,
  state: RouterStateSnapshot
): boolean => {
  return inject(AuthGuard).canActivate(next, state);
};

// Route Guards in Angular: https://medium.com/@hish.abdelshafouk/route-guards-in-angular-c9da0d815ef4
// CanActivate deprecate: https://stackoverflow.com/questions/75564717/angulars-canactivate-interface-is-deprecated-how-to-replace-it
