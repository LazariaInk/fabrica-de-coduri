package com.lazar.fabrica_de_coduri.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;


import java.nio.file.Path;
import java.nio.file.Paths;


@Configuration
public class WebMvcVideoConfig implements WebMvcConfigurer {


    @Value("${video.storage.root}")
    private String rootDir;


    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        Path rootPath = Paths.get(rootDir).toAbsolutePath().normalize();

        registry.addResourceHandler("/media/**")
                .addResourceLocations(rootPath.toUri().toString());
    }
}