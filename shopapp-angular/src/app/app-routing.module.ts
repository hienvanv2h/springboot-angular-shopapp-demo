import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';

import { HomeComponent } from './components/home/home.component';
import { LoginComponent } from './components/login/login.component';
import { RegisterComponent } from './components/register/register.component';
import { ProductDetailComponent } from './components/product-detail/product-detail.component';
import { OrderComponent } from './components/order/order.component';
import { OrderConfirmComponent } from './components/order-confirm/order-confirm.component';
import { UserProfileComponent } from './components/user-profile/user-profile.component';
import { PagenotfoundComponent } from './components/pagenotfound/pagenotfound.component';

import { AuthGuardFn } from './guards/auth.guard';

const routes: Routes = [
  { path: '', redirectTo: '/home', pathMatch: 'full' },
  { path: 'home', component: HomeComponent },
  { path: 'login', component: LoginComponent },
  { path: 'register', component: RegisterComponent },
  { path: 'products/:id', component: ProductDetailComponent },
  { path: 'orders', component: OrderComponent, canActivate: [AuthGuardFn] },
  {
    path: 'orders/:id',
    component: OrderConfirmComponent,
    canActivate: [AuthGuardFn],
  },
  {
    path: 'user-profile',
    component: UserProfileComponent,
    canActivate: [AuthGuardFn],
  },
  { path: '**', component: PagenotfoundComponent },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule],
})
export class AppRoutingModule {}

export const routingComponents = [
  HomeComponent,
  LoginComponent,
  RegisterComponent,
  ProductDetailComponent,
  OrderComponent,
  OrderConfirmComponent,
  UserProfileComponent,
  PagenotfoundComponent,
];
