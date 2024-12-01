package com.example.async.utils;

import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.List;
import java.util.function.Supplier;

@Slf4j
public class ExceptionHandleUtils {

    private ExceptionHandleUtils(){}

    public static <T> List<T> logAndReturnEmptyList(Throwable throwable, String loggingMessage) {
        log.error(loggingMessage, throwable);
        return Collections.emptyList();
    }

    public static <T> T logAndReturnEmptyObject(Throwable throwable, String loggingMessage, Supplier<T> supplier) {
        log.error(loggingMessage, throwable);
        return supplier.get();
    }
}
