import {BrowserModule} from '@angular/platform-browser';
import {NgModule} from '@angular/core';

import {AppRoutingModule} from './app-routing.module';
import {AppComponent} from './app.component';
import {ComponentComponent} from './component/component.component';
import {CommonUiComponent} from './common-ui/common-ui.component';
import {BrowserAnimationsModule} from '@angular/platform-browser/animations';
import {MatAutocompleteModule} from '@angular/material/autocomplete';
import {MatCheckboxModule} from '@angular/material/checkbox';
import {MatDatepickerModule} from '@angular/material/datepicker';
import {MatFormFieldModule} from '@angular/material/form-field';
import {MatInputModule} from '@angular/material/input';
import {MatRadioModule} from '@angular/material/radio';
import {MatSelectModule} from '@angular/material/select';
import {MatSliderModule} from '@angular/material/slider';
import {MatSidenavModule} from '@angular/material/sidenav';
import {MatToolbarModule} from '@angular/material/toolbar';
import {MatIconModule} from '@angular/material/icon';
import {MatButtonModule} from '@angular/material/button';
import {MatListModule} from '@angular/material/list';
import {SignInComponent} from './sign-in/sign-in.component';
import {SignUpComponent} from './sign-up/sign-up.component';
import {MatDialogModule, MatDialogRef} from "@angular/material/dialog";
import {MatCardModule} from "@angular/material/card";
import {FormsModule, ReactiveFormsModule} from "@angular/forms";
import {HttpClientModule} from '@angular/common/http';
import {GraphComponent} from './graph/graph.component';
import {CodeComponent} from './code/code.component';
import {CodeEditorModule} from '@ngstack/code-editor';
import {NgxResizableModule} from '@3dgenomes/ngx-resizable';
import {UnionComponent} from './union/union.component';
import { HelpComponent } from './help/help.component';
import { HomeComponent } from './home/home.component';
import { DocumentsComponent } from './documents/documents.component';
import { DocumentComponent } from './documents/document/document.component';
import { AccountComponent } from './account/account.component';
import { ConfirmationComponent } from './account/confirmation/confirmation.component';

@NgModule({
  declarations: [
    AppComponent,
    ComponentComponent,
    CommonUiComponent,
    SignInComponent,
    SignUpComponent,
    GraphComponent,
    CodeComponent,
    UnionComponent,
    HelpComponent,
    HomeComponent,
    DocumentsComponent,
    DocumentComponent,
    AccountComponent,
    ConfirmationComponent,
  ],
    imports: [
        BrowserModule,
        AppRoutingModule,
        MatAutocompleteModule,
        MatCheckboxModule,
        MatDatepickerModule,
        MatFormFieldModule,
        MatInputModule,
        MatRadioModule,
        MatSelectModule,
        MatSliderModule,
        MatSidenavModule,
        MatToolbarModule,
        MatDialogModule,
        MatIconModule,
        MatButtonModule,
        MatListModule,
        BrowserAnimationsModule,
        MatCardModule,
        ReactiveFormsModule,
        HttpClientModule,
        NgxResizableModule,
        CodeEditorModule.forRoot(),
        FormsModule
    ],
  providers: [{provide: MatDialogRef, useValue: {}}],
  bootstrap: [AppComponent]
})
export class AppModule {
}
