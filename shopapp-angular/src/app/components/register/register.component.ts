import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { UserRegisterModel } from '../../models/user-register.model';
import { UserService } from '../../services/user.service';
import { Router } from '@angular/router';
import { RegisterDTO } from '../../dtos/user/register.dto';

@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrl: './register.component.scss',
})
export class RegisterComponent implements OnInit {
  @ViewChild('registerForm') registerForm!: NgForm;
  userRegisterModel!: UserRegisterModel;
  isSubmitted!: boolean;
  showPassword: boolean = false;

  constructor(private _userService: UserService, private _router: Router) {}

  ngOnInit(): void {
    this.userRegisterModel = new UserRegisterModel(
      '',
      '',
      '',
      '',
      new Date().toISOString().split('T')[0],
      '',
      false
    );
    this.isSubmitted = false;
  }

  togglePasswordVisibility() {
    this.showPassword = !this.showPassword;
  }

  submitForm(form: NgForm) {
    if (!this.validateForm(form)) {
      this.isSubmitted = true;
      return;
    }
    debugger;
    console.log(this.userRegisterModel);
    const registerDTO: RegisterDTO = {
      fullname: this.userRegisterModel.fullName,
      phone_number: this.userRegisterModel.phone,
      address: this.userRegisterModel.address,
      password: this.userRegisterModel.password,
      retype_password: this.userRegisterModel.retypePassword,
      date_of_birth: this.userRegisterModel.dateOfBirth,
      facebook_account_id: 0,
      google_account_id: 0,
      role_id: 1,
    };
    this._userService.registerUser(registerDTO).subscribe({
      next: (response: any) => {
        debugger;
        console.log(response);
        // TODO: Navigate to login and retrieve token to store in local storage / cookie ...
        this._router.navigate(['/login']);
      },
      error: (error) => {
        debugger;
        console.log(error);
        alert(`Lỗi - ${error?.error?.message}`);
      },
    });
  }

  // Check if password match
  checkPasswordMatch() {
    return (
      this.userRegisterModel.password === this.userRegisterModel.retypePassword
    );
  }

  // Check age
  checkAge() {
    const years =
      new Date().getFullYear() -
      new Date(this.userRegisterModel.dateOfBirth).getFullYear();
    return years >= 18 && years <= 120;
  }

  // Validate form (general)
  validateForm(form: NgForm) {
    return (
      !form.form.invalid &&
      this.checkPasswordMatch() &&
      this.checkAge() &&
      this.userRegisterModel.isAccepted
    );
  }
}
