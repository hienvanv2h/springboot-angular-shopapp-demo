import { Component, OnInit, ViewEncapsulation } from '@angular/core';
import { UserService } from '../../services/user.service';
import { UserDetail } from '../../models/user-detail';
import { NgbPopoverConfig } from '@ng-bootstrap/ng-bootstrap';
import { TokenService } from '../../services/token.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-header',
  templateUrl: './header.component.html',
  styleUrl: './header.component.scss',
  encapsulation: ViewEncapsulation.None, // Cho phép styles của component này áp dụng global
})
export class HeaderComponent implements OnInit {
  userDetail?: UserDetail | null;

  constructor(
    private userService: UserService,
    private popoverConfig: NgbPopoverConfig,
    private tokenService: TokenService,
    private router: Router
  ) {}

  ngOnInit(): void {
    this.userDetail = this.userService.getUserDetailsFromLocalStorage();
  }

  handleItemClick(index: number): void {
    if (index === 0) {
      // Redirect to user profile
      this.router.navigate(['/user-profile']);
    } else if (index === 2) {
      // Log out
      this.userService.removeUserDetailsFromLocalStorage();
      this.tokenService.removeToken();
      this.userDetail = this.userService.getUserDetailsFromLocalStorage();
      // redirect to home page
      this.router.navigate(['/home']);
    }
  }
}

// Popover Guides: https://ng-bootstrap.github.io/#/components/popover/examples
