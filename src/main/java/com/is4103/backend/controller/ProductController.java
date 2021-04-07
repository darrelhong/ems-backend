package com.is4103.backend.controller;

import com.is4103.backend.model.BusinessPartner;
import com.is4103.backend.model.Product;
import com.is4103.backend.service.BusinessPartnerService;
import com.is4103.backend.service.ProductService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.lang.Nullable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping(path = "/product")
public class ProductController {
    
    @Autowired
    private ProductService productService;

    @Autowired
    private BusinessPartnerService bpService;

    @PostMapping("/create")
    public Product createProduct(
    @RequestParam("file") MultipartFile file,
    @RequestParam("id") Long id,
    @RequestParam("name") String name,
    @RequestParam("description") String description
    ) {
        BusinessPartner bp = bpService.getBusinessPartnerById(id);
        Product p = new Product();
        p.setName(name);
        p.setDescription(description);    
        p.setBusinessPartner(bp);
        return productService.createProduct(p, file);
    }

    @PostMapping("/update")
    public Product updateProduct(
    @RequestParam("file") @Nullable MultipartFile file,
    @RequestParam("name") @Nullable String name,
    @RequestParam("description") @Nullable String description,
    @RequestParam("pid") Long pid
    ) {
        // BusinessPartner bp = bpService.getBusinessPartnerById(id);
        Product p = productService.getProductById(pid);
        p.setName(name);
        p.setDescription(description);    
        // p.setBusinessPartner(bp);
        if (file != null) {
            Product productWithImage = productService.createProduct(p, file);
            return productService.saveProduct(productWithImage);
        } else return productService.saveProduct(p);
    }

    @PostMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id) {
        return productService.deleteProduct(id);
    }

    @GetMapping("/{id}")
    public Product getProduct(@PathVariable Long id) {
        return productService.getProductById(id);
    }
}
