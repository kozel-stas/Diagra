package com.diagra.controller;


import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @RequestMapping(value = "/products")
    public String getProductName() {
        return "Honey";
    }

}
