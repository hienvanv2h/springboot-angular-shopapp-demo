import {
  IsString,
  isPhoneNumber,
  IsNotEmpty,
  IsDateString,
  IsPhoneNumber,
} from 'class-validator';

export class RegisterDTO {
  @IsString()
  public fullname!: string;

  @IsPhoneNumber()
  @IsNotEmpty()
  public phone_number!: string;

  @IsString()
  public address!: string;

  @IsString()
  @IsNotEmpty()
  public password!: string;

  @IsString()
  @IsNotEmpty()
  public retype_password!: string;

  @IsDateString()
  public date_of_birth!: string;

  public facebook_account_id!: number;
  public google_account_id!: number;
  public role_id!: number;

  constructor(data: any) {
    this.fullname = data.fullname;
    this.phone_number = data.phone_number;
    this.address = data.address;
    this.password = data.password;
    this.retype_password = data.retype_password;
    this.date_of_birth = data.date_of_birth;
    this.facebook_account_id = data.facebook_account_id || 0;
    this.google_account_id = data.google_account_id || 0;
    this.role_id = data.role_id || 1;
  }
}
