import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SignInComponent} from "./sign-in/sign-in.component";
import {SignUpComponent} from "./sign-up/sign-up.component";
import {CommonUiComponent} from "./common-ui/common-ui.component";
import {GraphComponent} from "./graph/graph.component";
import {UnionComponent} from "./union/union.component";
import {HelpComponent} from "./help/help.component";
import {HomeComponent} from "./home/home.component";


const routes: Routes = [
  {
    path: "",
    component: CommonUiComponent,
    children: [
      {
        path: '',
        component: HomeComponent
      },
      {
        path: 'help',
        component: HelpComponent
      },
      {
        path: 'home',
        component: HomeComponent
      },
      {
        path: 'signUp',
        component: SignUpComponent
      },
      {
        path: 'graph',
        component: GraphComponent
      },
      {
        path: 'union',
        component: UnionComponent
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
