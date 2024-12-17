package com.example.async.service;

import com.example.async.dto.MainContentsDto;
import com.example.async.dto.ProductDto;
import com.example.async.dto.UserDto;
import com.example.async.externals.GoodsApiService;
import com.example.async.externals.UserApiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.concurrent.DelegatingSecurityContextCallable;
import org.springframework.security.concurrent.DelegatingSecurityContextRunnable;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletableFuture;
import java.util.function.Supplier;

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

    public MainContentsDto getSecureCallableAsyncMainContentsDto(){
        MainContentsDto mainContentsDto = new MainContentsDto();

        CompletableFuture.allOf(
                CompletableFuture.supplyAsync(secureCallable(goodsApiService::getProductList, () -> Collections.EMPTY_LIST))
                        .exceptionally(throwable -> logAndReturnEmptyList(throwable, "call product list api error"))
                        .thenAccept(mainContentsDto::setProductList),
                CompletableFuture
                        .supplyAsync(secureCallable(goodsApiService::getCategoryList, () -> Collections.EMPTY_LIST))
                        .exceptionally(throwable -> logAndReturnEmptyList(throwable, "call category list api error"))
                        .thenAccept(mainContentsDto::setCategoryList),
                CompletableFuture
                        .supplyAsync(secureCallable(userApiService::getUserInfo, UserDto::new))
                        .exceptionally(throwable -> logAndReturnEmptyObject(throwable, "call user api error", UserDto::new))
                        .thenAccept(mainContentsDto::setUserInfo)
        ).join();


        return mainContentsDto;
    }

    public <T> Supplier<T> secureCallable(Callable<T> callable, Supplier<T> defaultSupplier) {
        SecurityContext securityContext = SecurityContextHolder.getContext();

        return () -> {
            try {
                return DelegatingSecurityContextCallable.create(callable, securityContext).call();
            } catch (Exception e) {
                return defaultSupplier.get();
            }
        };
    }

    public MainContentsDto getSecureRunnableAsyncMainContentsDto(){
        MainContentsDto mainContentsDto = new MainContentsDto();

        CompletableFuture.allOf(
                CompletableFuture
                        .runAsync(DelegatingSecurityContextRunnable.create(() -> mainContentsDto.setProductList(goodsApiService.getProductList()), SecurityContextHolder.getContext()))
                        .exceptionally(throwable -> log(throwable, "call product list api error")),
                CompletableFuture
                        .runAsync(DelegatingSecurityContextRunnable.create(() -> mainContentsDto.setCategoryList(goodsApiService.getCategoryList()), SecurityContextHolder.getContext()))
                        .exceptionally(throwable -> log(throwable, "call category list api error")),
                CompletableFuture
                        .runAsync(DelegatingSecurityContextRunnable.create(() -> mainContentsDto.setUserInfo(userApiService.getUserInfo()), SecurityContextHolder.getContext()))
                        .exceptionally(throwable -> log(throwable, "call user api error"))
        ).join();


        return mainContentsDto;
    }

    public Void log(Throwable throwable, String message) {
        log.error(message, throwable);
        return null;
    }

}
