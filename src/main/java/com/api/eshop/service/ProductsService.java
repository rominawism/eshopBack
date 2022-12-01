package com.api.eshop.service;

import com.api.eshop.domain.Products;
import com.api.eshop.repository.ProductsRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ProductsService {
    @Autowired
    private ProductsRepository repository;

    public List<Products> getAll() {
        return repository.findAll();
    }


    public List<Products> getAllIncredibleOffers() {
        return repository.findByIncredibleOffersIsTrue();
    }

    public List<Products> getAllDailySuggests() {
        return repository.findByDailySuggestIsTrue();
    }

    public Products getById(long id) {
        return repository.findById(id).get();
    }

    public List<Products> searchProducts(String textToSearch) {
        return repository.findByNameContains(textToSearch);
    }

    public  Products getByName(String name)
    {
        return repository.findByName(name);
    }
}
