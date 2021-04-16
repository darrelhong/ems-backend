package com.is4103.backend.service;

import java.util.List;

import com.is4103.backend.model.Booth;
import com.is4103.backend.model.Product;
import com.is4103.backend.repository.BoothRepository;
import com.is4103.backend.repository.ProductRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private BoothRepository boothRepository;
    // @Autowired
    // private FileStorageProperties fileStorageProperties;

    @Autowired
    private FileStorageService fileStorageService;

    public Product createProduct(Product product, MultipartFile file) {
        String fileName = fileStorageService.storeFile(file, "profilepic", "");
        // String fileName = fileStorageService.storeFile(file, "productImage", "");

        String fileDownloadUri = ServletUriComponentsBuilder.fromCurrentContextPath().path("/downloadFile/")
                .path(fileName).toUriString();

        product.setImage(fileDownloadUri);
        // return product;
        return saveProduct(product);
    }

    public Product getProductById(Long id) {
        return productRepository.findById(id).get();
    }

    public Product saveProduct(Product p) {
        return productRepository.save(p);
    }

    public ResponseEntity<String> deleteProduct(Long id) {
        Product p = productRepository.findById(id).get();
        List<Booth> boothsWithProduct = p.getBooths();
        for (Booth b : boothsWithProduct) {
            b.getProducts().remove(p);
            boothRepository.save(b);
        }
        productRepository.delete(p);
        return ResponseEntity.ok("Success");
    }
}
