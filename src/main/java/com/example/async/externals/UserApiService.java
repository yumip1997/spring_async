package com.example.async.externals;

import com.example.async.dto.UserDto;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

@Service
public class UserApiService {

    @Value("${api.fake}")
    private String fakeApi;

    public UserDto getUserInfo(){
        return WebClient
                .create()
                .get()
                .uri(fakeApi + "/users/1")
                .retrieve()
                .bodyToMono(UserDto.class)
                .block();
    }
}
