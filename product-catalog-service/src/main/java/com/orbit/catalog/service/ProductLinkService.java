package com.orbit.catalog.service;

import com.orbit.catalog.entity.ProductLink;
import com.orbit.catalog.repository.ProductLinkRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ProductLinkService {

   // @Autowired
  //  ProductLinkRepository productLinkRepository;

    private final  ProductLinkRepository productLinkRepository;

    @Autowired
    public ProductLinkService(ProductLinkRepository productLinkRepository){
        this.productLinkRepository = productLinkRepository;
    }
    public ProductLink createLink(Long productId){

        return productLinkRepository.findByProductId(productId)
                .orElseGet(() -> {
                    String shortCode = Base62Encoder.encode(productId);
                    ProductLink link = new ProductLink(shortCode, productId);
                    return productLinkRepository.save(link);
                });

    }
}
