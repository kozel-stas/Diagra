import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SignInComponent} from "./sign-in/sign-in.component";
import {SignUpComponent} from "./sign-up/sign-up.component";
import {AppComponent} from "./app.component";
import {CommonUiComponent} from "./common-ui/common-ui.component";


const routes: Routes = [
  {
    path: "",
    component: CommonUiComponent,
    children: [
      {
        path: 'signUp',
        component: SignUpComponent
      },
      {
        path: 'signIn',
        component: SignInComponent
      }
    ]
  },
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule {
}
