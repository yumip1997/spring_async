package com.example.async.controller;

import com.example.async.dto.MainContentsDto;
import com.example.async.service.MainService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequiredArgsConstructor
@Slf4j
public class MainController {

    private final MainService mainService;

    @GetMapping("/syncMain")
    public MainContentsDto getSyncMainContentsDto(){
        log.info("sysncMain");
        return mainService.getSyncMainContentsDto();
    }

    @GetMapping("/asyncMain")
    public MainContentsDto getASyncMainContentsDto(){
        log.info("asyncMain");
        return mainService.getAsyncMainContentsDto();
    }
}
