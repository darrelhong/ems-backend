package com.is4103.backend.controller;

import java.util.List;

import com.is4103.backend.model.Booth;
import com.is4103.backend.model.Product;
import com.is4103.backend.service.BoothService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/booth")
public class BoothController {

    @Autowired
    private BoothService boothService;

    @GetMapping(path = "/all")
    public List<Booth> getBooths() {
        return boothService.findAllBooths();
    }

    @GetMapping(path = "/{id}")
    public Booth getBoothById(@PathVariable Long id) {
        return boothService.findBoothById(id);
    }

    @GetMapping(path = "/sellerProfile/{id}")
    public List<Booth> getBoothsBySellerProfileId(@PathVariable Long id) {
        return boothService.findBoothsBySellerProfileId(id);
    }

    @GetMapping(path = "/products/{id}")
    public List<Product> getProductsByBoothId(@PathVariable Long id) {
        return boothService.findProductsByBoothId(id);
    }

    @PostMapping(path = "/add-product")
    public Booth addProductToBooth(
        @RequestParam(name = "pid", defaultValue = "1") Long pid,
        @RequestParam(name = "id", defaultValue = "10") Long id) {
        return boothService.addProductToBooth(pid, id);
    }

    @PostMapping(path = "/remove-product")
    public Booth removeProductFromBooth (
        @RequestParam(name = "pid", defaultValue = "1") Long pid,
        @RequestParam(name = "id", defaultValue = "10") Long id) {
        return boothService.removeProductFromBooth(pid, id);
    }

}
