import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { HeaderComponent } from './components/header/header.component';
import { FooterComponent } from './components/footer/footer.component';

// import { HomeComponent } from './components/home/home.component';
// import { ProductDetailComponent } from './components/product-detail/product-detail.component';
// import { OrderComponent } from './components/order/order.component';
// import { OrderConfirmComponent } from './components/order-confirm/order-confirm.component';

// import { LoginComponent } from './components/login/login.component';
// import { RegisterComponent } from './components/register/register.component';
// import { UserProfileComponent } from './components/user-profile/user-profile.component';
// import { PagenotfoundComponent } from './components/pagenotfound/pagenotfound.component';

import { routingComponents } from './app-routing.module';

import { FormsModule, ReactiveFormsModule } from '@angular/forms';
import {
  HTTP_INTERCEPTORS,
  provideHttpClient,
  withInterceptorsFromDi,
} from '@angular/common/http';
import { TokenInterceptor } from './interceptors/token.interceptor';
import { NgbModule, NgbPopoverModule } from '@ng-bootstrap/ng-bootstrap';

@NgModule({
  declarations: [
    AppComponent,
    HeaderComponent,
    FooterComponent,
    ...routingComponents,
  ],
  bootstrap: [AppComponent],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    ReactiveFormsModule,
    NgbModule,
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true,
    },
    provideHttpClient(withInterceptorsFromDi()),
  ],
})
export class AppModule {}
