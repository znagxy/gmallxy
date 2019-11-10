package com.yin.gmall.cart.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;

import java.math.BigDecimal;

@Controller
public class CartController {

    @RequestMapping("addToCart")
    public String addToCart(String skuId , BigDecimal quantity){

        return "redirect:/success.html";
    }
}
