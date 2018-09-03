package com.maksimov.accountManager;

import org.modelmapper.ModelMapper;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class AccountManager {

    public static void main(String[] args) {
        SpringApplication.run(AccountManager.class, args);
    }

    @Bean
    public ModelMapper modelMap() {
        ModelMapper modelMapper = new ModelMapper();
//        modelMapper.getConfiguration()
//                .setMatchingStrategy(MatchingStrategies.STRICT);

        return modelMapper;
    }
}
