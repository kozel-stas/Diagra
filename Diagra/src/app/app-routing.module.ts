import {NgModule} from '@angular/core';
import {Routes, RouterModule} from '@angular/router';
import {SignInComponent} from "./sign-in/sign-in.component";
import {SignUpComponent} from "./sign-up/sign-up.component";
import {CommonUiComponent} from "./common-ui/common-ui.component";
import {GraphComponent} from "./graph/graph.component";
import {CodeComponent} from "./code/code.component";
import {UnionComponent} from "./union/union.component";


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
        path: 'graph',
        component: GraphComponent
      },
      {
        path: 'union',
        component: UnionComponent
      },
      {
        path: 'code',
        component: CodeComponent
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
