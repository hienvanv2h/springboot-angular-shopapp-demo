import { Component, OnInit } from '@angular/core';
import { UserLoginModel } from '../../models/user-login.model';
import { NgForm } from '@angular/forms';
import { LoginDTO } from '../../dtos/user/login.dto';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { LoginResponse } from '../../custom-responses/users/login.response';
import { TokenService } from '../../services/token.service';
import { RoleService } from '../../services/role.service';
import { Role } from '../../models/role';
import { UserDetail } from '../../models/user-detail';

@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrl: './login.component.scss',
})
export class LoginComponent implements OnInit {
  userLoginModel!: UserLoginModel;
  roles: Role[] = [];
  rememberMe: boolean = false;
  selectedRole!: Role | null;
  userDetail?: UserDetail;

  showPassword: boolean = false;
  isSubmitted: boolean = false;

  constructor(
    private _userService: UserService,
    private _tokenService: TokenService,
    private _roleService: RoleService,
    private _router: Router
  ) {}

  ngOnInit(): void {
    debugger;
    // fetch roles
    this._roleService.getRoles().subscribe({
      next: (response) => {
        this.roles = response;
        this.selectedRole = this.roles ? this.roles[0] : null;
      },
      error: (error) => {
        console.log(error);
      },
    });

    this.userLoginModel = new UserLoginModel('', '');
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  fetchUserDetail(token: string) {
    this._userService.getUserDetails(token).subscribe({
      next: (response: any) => {
        debugger;
        this.userDetail = response;
        if (this.userDetail) {
          this._userService.saveUserDetailsToLocalStorage(this.userDetail);
        }
        this._router.navigate(['/home']);
      },
      error: (error) => {
        debugger;
        console.log(error);
      },
      complete: () => {
        debugger;
        console.log('done');
      },
    });
  }

  submitForm(form: NgForm) {
    if (!this.validateForm(form)) {
      this.isSubmitted = true;
      return;
    }
    debugger;
    console.log(this.userLoginModel);
    const loginDTO: LoginDTO = {
      phone_number: this.userLoginModel.phone,
      password: this.userLoginModel.password,
      role_id: this.selectedRole?.id || 1,
    };
    this._userService.loginUser(loginDTO).subscribe({
      next: (response: LoginResponse) => {
        debugger;
        console.log(response);
        const { token } = response;
        if (this.rememberMe) {
          this._tokenService.setToken(token);
          this.fetchUserDetail(token); // fetch user data then navigate to homepage
        }
      },
      error: (error) => {
        debugger;
        console.log(error);
        alert(`Lỗi: ${JSON.stringify(error)}`);
      },
    });
  }

  // Validate form (general)
  validateForm(form: NgForm) {
    return !form.form.invalid;
  }

  onRoleChange() {
    console.log(this.selectedRole);
  }
}
