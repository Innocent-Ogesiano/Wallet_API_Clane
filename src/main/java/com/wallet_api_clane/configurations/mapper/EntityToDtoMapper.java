package com.wallet_api_clane.configurations.mapper;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EntityToDtoMapper {

    @Bean
    public ModelMapper modelMapper () {
        return new ModelMapper();
    }
}
