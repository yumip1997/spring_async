package com.example.async.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class MainContentsDto {

    private List<ProductDto> productList;
    private List<String> categoryList;
    private UserDto userInfo;

}
