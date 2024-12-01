## 스프링 비동기 프로그래밍

### CompletableFuture 활용
``` java
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
        );
        return mainContentsDto;
    }
```
- ```<U> CompletableFuture<U> supplyAsync(Supplier<U> supplier)``` : 비동기적으로 작업을 실행하고 결과를 반환합니다.
- ```CompletableFuture<T> exceptionally(Function<Throwable, ? extends T> fn)``` : 예외가 발생한 경우 이를 처리하고 기본 값을 반환합니다.
- ```CompletableFuture<Void> thenAccept(Consumer<? super T> action)``` : 비동기 작업이 완료된 후 결과를 처리합니다.
- ```CompletableFuture<Void> allOf(CompletableFuture<?>... cfs)```: 여러 개의 CompletableFuture를 하나로 묶어주는 메서드입니다. join이 호출되어야 모든 CompletableFuture들이 완료될때 까지 기다릴 수 있습니다.


동기적 방식 코드는 다음과 같습니다.

```java
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
```
단순히 상품목록 API가 호출되면 카테고리 목록 API를 호출하고, 그 다음 회원 정보 API가 호출됩니다.

### CompletableFuture 활용 VS 단순 동기 방식
Apache JMeter을 이용하여 간단한 성능 테스트를 해보았습니다.

사용자 수 : 10 / 루프 카운트 2

| 라벨       | 표본 수 | 평균  | 최소값 | 최대값 | 표준편차   | 오류 %  | 처리량  | 수신 KB/초 | 전송 KB/초 | 평균 바이트 수 |
|------------|--------|-------|--------|--------|------------|---------|---------|------------|------------|----------------|
| 동기 요청  | 20     | 3712  | 1009   | 7027   | 2695.17    | 0.000%  | 2.15008 | 21.70      | 0.26       | 10334.0        |
| 비동기 요청 | 20     | 702   | 398    | 1488   | 284.00     | 0.000%  | 5.85823 | 59.12      | 0.72       | 10334.0        |


결론 :  속도 개선의 한 가지 방법 중 CompletableFuture 활용법이 있다!


