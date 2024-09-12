export class UserRegisterModel {
  constructor(
    public phone: string,
    public password: string,
    public retypePassword: string,
    public fullName: string,
    public dateOfBirth: string,
    public address: string,
    public isAccepted: boolean
  ) {}
}
