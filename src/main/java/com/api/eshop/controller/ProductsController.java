package com.api.eshop.controller;

import com.api.eshop.domain.Products;
import com.api.eshop.domain.ProductsCategories;
import com.api.eshop.service.ProductsCategoriesService;
import com.api.eshop.service.ProductsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("products")
public class ProductsController {


    @Autowired
    private ProductsService service;

    @Autowired
    private ProductsCategoriesService productsCategoriesService;

    @GetMapping
    @CrossOrigin("*")
    public ResponseEntity getAll() {
        List<Products> result = service.getAll();
        for (Products product : result) {
            product.getImages().size();
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("{id}")
    @CrossOrigin("*")
    public ResponseEntity getById(@PathVariable long id) {
        Products result = service.getById(id);
        if(result!=null) {
            result.getImages().size();
            return new ResponseEntity(result, HttpStatus.OK);
        }
        else
            return new ResponseEntity(result , HttpStatus.BAD_REQUEST);
    }

    @GetMapping("incredibleOffers")
    @CrossOrigin("*")
    public ResponseEntity getAllIncredibleOffers() {
        return new ResponseEntity(service.getAllIncredibleOffers(), HttpStatus.OK);
    }

    @GetMapping("categories")
    @CrossOrigin("*")
    public ResponseEntity getAllCategories() {
        List<ProductsCategories> result = productsCategoriesService.getAll();
        for (ProductsCategories item : result) {
            item.getProducts().size();
        }
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("dailySuggests")
    @CrossOrigin("*")
    public ResponseEntity getAllDailySuggests() {
        List<Products> result = service.getAllDailySuggests();
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("search/{textToSearch}")
    @CrossOrigin("*")
    public ResponseEntity getAllDailySuggests(@PathVariable String textToSearch) {
        List<Products> result = service.searchProducts(textToSearch);
        return new ResponseEntity(result, HttpStatus.OK);
    }

    @GetMapping("category/{categoryName}")
    @CrossOrigin("*")
    public ResponseEntity getProductsByCategoryName(@PathVariable String categoryName) {

         ProductsCategories result = productsCategoriesService.getByName(categoryName);
         result.getProducts().size();
        return new ResponseEntity(result, HttpStatus.OK);
    }
}
