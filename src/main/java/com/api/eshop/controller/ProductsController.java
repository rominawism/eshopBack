package com.api.eshop.controller;

import com.api.eshop.domain.Products;
import com.api.eshop.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductsController {


    @Autowired
    private ProductsService service;

    @GetMapping
    @CrossOrigin("*")
    public ResponseEntity getAll() {
        List<Products> result = service.getAll();
        for(Products product : result){
            product.getImages().size();
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("incredibleOffers")
    @CrossOrigin("*")
    public ResponseEntity getAllIncredibleOffers()
    {
        return new ResponseEntity(service.getAllIncredibleOffers(), HttpStatus.OK);
    }
}
