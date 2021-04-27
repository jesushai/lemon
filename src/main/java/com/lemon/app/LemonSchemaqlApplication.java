package com.lemon.app;

import com.lemon.boot.autoconfigure.commons.EnableAsyncExecutor;
import com.lemon.boot.autoconfigure.commons.EnableLocaleMessage;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
@EnableAsyncExecutor
@EnableLocaleMessage
public class LemonSchemaqlApplication {

    public static void main(String[] args) {
        SpringApplication.run(LemonSchemaqlApplication.class, args);
    }

}
