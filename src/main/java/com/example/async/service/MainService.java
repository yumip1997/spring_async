package com.example.async.service;

import com.example.async.dto.MainContentsDto;
import com.example.async.dto.ProductDto;
import com.example.async.dto.UserDto;
import com.example.async.externals.GoodsApiService;
import com.example.async.externals.UserApiService;
import com.example.async.utils.ExceptionHandleUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.concurrent.CompletableFuture;

import static com.example.async.utils.ExceptionHandleUtils.logAndReturnEmptyList;
import static com.example.async.utils.ExceptionHandleUtils.logAndReturnEmptyObject;

@Service
@RequiredArgsConstructor
@Slf4j
public class MainService {

    private final GoodsApiService goodsApiService;
    private final UserApiService userApiService;

    public MainContentsDto getSyncMainContentsDto(){
        MainContentsDto mainContentsDto = new MainContentsDto();

        List<ProductDto> productList = goodsApiService.getProductList();
        mainContentsDto.setProductList(productList);

        List<String> categoryList = goodsApiService.getCategoryList();
        mainContentsDto.setCategoryList(categoryList);

        UserDto userInfo = userApiService.getUserInfo();
        mainContentsDto.setUserInfo(userInfo);

        return mainContentsDto;
    }

    public MainContentsDto getAsyncMainContentsDto(){
        MainContentsDto mainContentsDto = new MainContentsDto();

        CompletableFuture.allOf(
                CompletableFuture
                        .supplyAsync(goodsApiService::getProductList)
                        .exceptionally(throwable -> logAndReturnEmptyList(throwable, "call product list api error"))
                        .thenAccept(mainContentsDto::setProductList),
                CompletableFuture
                        .supplyAsync(goodsApiService::getCategoryList)
                        .exceptionally(throwable -> logAndReturnEmptyList(throwable, "call category list api error"))
                        .thenAccept(mainContentsDto::setCategoryList),
                CompletableFuture
                        .supplyAsync(userApiService::getUserInfo)
                        .exceptionally(throwable -> logAndReturnEmptyObject(throwable, "call user api error", UserDto::new))
                        .thenAccept(mainContentsDto::setUserInfo)
        ).join();

        return mainContentsDto;
    }
}
