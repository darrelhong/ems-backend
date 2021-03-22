package com.is4103.backend.service;
import org.springframework.stereotype.Service;

import java.util.List;

import com.is4103.backend.model.Booth;
import com.is4103.backend.model.Product;
import com.is4103.backend.repository.BoothRepository;
import com.is4103.backend.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;

@Service
public class BoothService {
    
    @Autowired
    BoothRepository boothRepository;

    @Autowired
    ProductRepository productRepository;

    public List<Booth> findAllBooths() {
        return boothRepository.findAll();
    }

    public Booth findBoothById(Long id) {
        return boothRepository.findById(id).get();
    }

    public List<Product> findProductsByBoothId(Long id) {
        Booth booth = findBoothById(id);
        return booth.getProducts();
    }

    public List<Booth> findBoothsBySellerProfileId(Long id) {
        return boothRepository.findBoothsBySellerProfile(id);
    }

    public Booth addProductToBooth(Long pid, Long id) {
        Booth booth = boothRepository.findById(id).get();
        booth.getProducts().add(productRepository.findById(pid).get());
        return boothRepository.save(booth);
    }

    public Booth removeProductFromBooth(Long pid, Long id) {
        Booth booth = boothRepository.findById(id).get();
        booth.getProducts().remove(productRepository.findById(pid).get());
        return boothRepository.save(booth);
    }
}
