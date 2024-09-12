import { IsNotEmpty, IsNumber, IsPhoneNumber, IsString } from 'class-validator';

export class LoginDTO {
  @IsPhoneNumber()
  @IsNotEmpty()
  public phone_number!: string;

  @IsString()
  @IsNotEmpty()
  public password!: string;

  @IsNumber()
  @IsNotEmpty()
  public role_id!: number;

  constructor(data: any) {
    this.phone_number = data.phone_number;
    this.password = data.password;
    this.role_id = data.role_id;
  }
}
