import { Component, OnInit } from '@angular/core';
import {
  AbstractControl,
  FormBuilder,
  FormGroup,
  ValidationErrors,
  ValidatorFn,
  Validators,
} from '@angular/forms';
import { UserService } from '../../services/user.service';
import { TokenService } from '../../services/token.service';
import { UserDetailDTO } from '../../dtos/user/user-detail.dto';
import { Router } from '@angular/router';

interface SocialAccount {
  provider: string;
  id: number;
}

@Component({
  selector: 'app-user-profile',
  templateUrl: './user-profile.component.html',
  styleUrl: './user-profile.component.scss',
})
export class UserProfileComponent implements OnInit {
  userDetailForm: FormGroup;

  linkedAccounts: SocialAccount[] = [];

  showPasswordFields: boolean = false;
  showPasswordContent: boolean = false;
  isSubmitted: boolean = false;

  constructor(
    private fb: FormBuilder,
    private tokenService: TokenService,
    private userService: UserService,
    private router: Router
  ) {
    this.userDetailForm = this.fb.group({
      id: [0],
      fullname: ['', Validators.required],
      email: ['', Validators.email], // NOTE: server not saving email
      phone_number: ['', Validators.pattern(/^\d{10}$/)],
      address: [''],
      password: ['', [Validators.minLength(3)]],
      retype_password: [
        '',
        [Validators.minLength(3), this.passwordMatchValidator()],
      ],
      date_of_birth: [''],
      facebook_account_id: [0],
      google_account_id: [0],
      role_id: [0],
    });
  }

  renderUserDetailsForm(response: any) {
    const date = new Date(response.date_of_birth);
    const isoDate = new Date(date.getTime() - date.getTimezoneOffset() * 60000);
    this.userDetailForm.patchValue({
      ...response,
      date_of_birth: isoDate.toISOString().split('T')[0],
      role_id: response.role.id,
    });

    this.linkedAccounts = [
      { provider: 'Facebook', id: response.facebook_account_id || 0 },
      { provider: 'Google', id: response.google_account_id || 0 },
    ];
  }

  ngOnInit(): void {
    // get token
    const token: string = this.tokenService.getToken() || '';

    // fetch user details
    this.userService.getUserDetails(token).subscribe({
      next: (response: any) => {
        debugger;
        this.renderUserDetailsForm(response);
        this.userService.saveUserDetailsToLocalStorage(response);
      },
      error: (error) => {
        debugger;
        alert(`Lỗi: ${JSON.stringify(error)}`);
      },
    });
  }

  saveUpdate(): void {
    if (this.userDetailForm.valid) {
      // TODO: update user
      const token = this.tokenService.getToken() || '';
      const userId = this.userDetailForm.get('id')?.value;
      const userDetailDto: UserDetailDTO = {
        ...this.userDetailForm.value,
      };
      this.userService
        .updateUserDetails(token, userId, userDetailDto)
        .subscribe({
          next: (response: any) => {
            debugger;
            this.renderUserDetailsForm(response);
            this.userService.saveUserDetailsToLocalStorage(response);

            if (this.password?.dirty || this.retype_password?.dirty) {
              // force user to logout if credentials changed
              this.tokenService.removeToken();
              this.userService.removeUserDetailsFromLocalStorage();
              this.router.navigate(['/login']);
            } else {
              // Mark form as pristine after successful update
              this.userDetailForm.markAsPristine();
            }
          },
          error: (error) => {
            debugger;
            console.log(error);
            alert(`Lỗi: ${JSON.stringify(error)}`);
          },
        });
    } else {
      alert('Form không hợp lệ! Vui lòng kiểm tra lại.');
    }
  }

  togglePasswordFields() {
    this.showPasswordFields = !this.showPasswordFields;
  }

  togglePasswordVisibility() {
    this.showPasswordContent = !this.showPasswordContent;
  }

  updateNewPassword(flag: boolean): void {
    if (!flag) {
      // cancel update, clear inputs
      this.password?.setValue('');
      this.retype_password?.setValue('');
    }
    this.togglePasswordFields();
  }

  // VALIDATOR FOR PASSWORDS
  passwordMatchValidator(): ValidatorFn {
    return (control: AbstractControl): ValidationErrors | null => {
      const password = control.parent?.get('password')?.value;
      const retypePassword = control.value;
      return password !== retypePassword ? { passwordMismatch: true } : null;
    };
  }

  get fullname() {
    return this.userDetailForm.get('fullname');
  }

  get email() {
    return this.userDetailForm.get('email');
  }

  get phone_number() {
    return this.userDetailForm.get('phone_number');
  }

  get address() {
    return this.userDetailForm.get('address');
  }

  get password() {
    return this.userDetailForm.get('password');
  }

  get retype_password() {
    return this.userDetailForm.get('retype_password');
  }

  get date_of_birth() {
    return this.userDetailForm.get('date_of_birth');
  }

  get facebook_account_id() {
    return this.userDetailForm.get('facebook_account_id');
  }

  get google_account_id() {
    return this.userDetailForm.get('google_account_id');
  }

  get role_id() {
    return this.userDetailForm.get('role_id');
  }
}
