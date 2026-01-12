package com.lazar.fabrica_de_coduri;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@EnableAsync
public class FabricaDeCoduriApplication {
    public static void main(String[] args) {
        SpringApplication.run(FabricaDeCoduriApplication.class, args);
    }
}
