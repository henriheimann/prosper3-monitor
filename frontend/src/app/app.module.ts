import { LOCALE_ID, NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { SharedModule } from './modules/shared/shared.module';
import { AdminModule } from './modules/admin/admin.module';
import { StoreModule } from '@ngrx/store';
import { StoreDevtoolsModule } from '@ngrx/store-devtools';
import { environment } from '../environments/environment';
import { AuthModule } from './modules/auth/auth.module';
import { EffectsModule } from '@ngrx/effects';
import { HTTP_INTERCEPTORS, HttpClientModule } from '@angular/common/http';
import { ModalModule } from 'ngx-bootstrap/modal';
import { TokenInterceptor } from './modules/auth/interceptors/token.interceptor';
import { LegalModule } from './modules/legal/legal.module';
import { SensorMapsModule } from './modules/sensor-maps/sensor-maps.module';
import { DashboardsModule } from './modules/dashboards/dashboards.module';
import { ApiDocModule } from './modules/api-doc/api-doc.module';
import { GrafanaModule } from './modules/grafana/grafana.module';
import localeDe from '@angular/common/locales/de';
import { DatePipe, registerLocaleData } from '@angular/common';

@NgModule({
  declarations: [AppComponent],
  imports: [
    BrowserModule,
    HttpClientModule,
    AppRoutingModule,
    AdminModule,
    ApiDocModule,
    GrafanaModule,
    AuthModule,
    LegalModule,
    SensorMapsModule,
    DashboardsModule,
    SharedModule,
    StoreModule.forRoot({}, {}),
    StoreDevtoolsModule.instrument({
      maxAge: 25,
      logOnly: environment.production
    }),
    EffectsModule.forRoot([]),
    ModalModule.forRoot()
  ],
  providers: [
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    },
    {
      provide: LOCALE_ID,
      useValue: "de-DE"
    },
    DatePipe
  ],
  bootstrap: [AppComponent]
})
export class AppModule {
  constructor() {
    registerLocaleData(localeDe);
  }
}
