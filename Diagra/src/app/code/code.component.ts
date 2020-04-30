import {Component, HostListener, Injectable, Input, OnInit} from '@angular/core';
import {EventMgr} from "../services/EventMgr";
import {Constant} from "../ constant";

export class Code {

  constructor(code, language) {
    this.code = code;
    this.language = language;
  }

  code: string
  language: string
}

@Component({
  selector: 'app-code',
  templateUrl: './code.component.html',
  styleUrls: ['./code.component.scss']
})
export class CodeComponent implements OnInit {

  constructor() {
  }

  ngOnInit(): void {
  }

  private codeValue;

  template = [
    `public class Main {`,
    ``,
    `    public static void main() {`,
    `        // Write main method here. `,
    `        // Example: `,
    `        // generate(); `,
    `        // Method name which located on this method will be used as root object for algorithm scheme.`,
    `        // But you can just paste your code here.`,
    `    }`,
    ``,
    `}`,
  ].join('\n');

  codeModel = {
    language: 'java',
    value: this.template,
    dependencies: ['@types/node', '@ngstack/translate', '@ngstack/code-editor']
  };

  options = {
    lineNumbers: true,
    contextmenu: true,
    minimap: {
      enabled: true
    }
  };

  public onCodeChanged(value): void {
    this.codeValue = value;
  }

  private code(): Code {
    return new Code(this.codeModel.value, this.codeValue);
  }

  @HostListener('window:keyup.alt.enter', ['$event']) sw() {
    Constant.EVENT_MGR.fireEvent("code_ready", this.code())
  }

}
