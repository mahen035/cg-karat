package com.orbit.catalog.controller;

import com.orbit.catalog.entity.ProductLink;
import com.orbit.catalog.service.ProductLinkService;
import com.orbit.catalog.service.ProductService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController // @Controller + @ResponseBody
public class ProductLinkController {

    private final ProductLinkService productLinkService;
    private final ProductService productService;
    private final String productLinkBaseUrl;

    public ProductLinkController(ProductLinkService productLinkService,
                                 ProductService productService,
                                 @Value("${orbit.shortlink.base-url}")
                                 String productLinkBaseUrl){
        this.productLinkService = productLinkService;
        this.productService = productService;
        this.productLinkBaseUrl = productLinkBaseUrl;
    }

    @PostMapping("/api/products/{id}/share")
    public ResponseEntity<?> shareProduct(@PathVariable("id") Long id){

        if (productService.findById(id).isEmpty()){
            return ResponseEntity.notFound().build();
        }
        ProductLink link = productLinkService.createLink(id);
        return ResponseEntity.ok(Map.of(
                "shortCode", link.getShortCode(),
                "shortURL", productLinkBaseUrl+"/"+link.getShortCode(),
                "productId", link.getProductId()
        ));

    }

}
