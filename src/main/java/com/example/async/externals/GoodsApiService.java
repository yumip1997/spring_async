package com.example.async.externals;

import com.example.async.dto.ProductDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.util.List;

@Service
public class GoodsApiService {

    @Value("${api.fake}")
    private String fakeApi;

    public List<ProductDto> getProductList(){
        return WebClient
                .create()
                .get()
                .uri(fakeApi + "/products")
                .retrieve()
                .bodyToFlux(ProductDto.class)
                .collectList()
                .block();
    }

    public List<String> getCategoryList(){
        return WebClient
                .create()
                .get()
                .uri(fakeApi + "/products/categories")
                .retrieve()
                .bodyToFlux(String.class)
                .collectList()
                .block();
    }
}
