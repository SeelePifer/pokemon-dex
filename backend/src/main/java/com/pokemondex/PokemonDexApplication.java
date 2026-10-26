package com.pokemondex;

import com.pokemondex.infrastructure.config.JwtProperties;
import com.pokemondex.infrastructure.config.PokeApiProperties;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.UserDetailsServiceAutoConfiguration;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
@EnableConfigurationProperties({PokeApiProperties.class, JwtProperties.class})
public class PokemonDexApplication {

    public static void main(String[] args) {
        SpringApplication.run(PokemonDexApplication.class, args);
    }
}
