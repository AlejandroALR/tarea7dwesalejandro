package com.alejandro.tarea7dwesalejandro.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;

import com.alejandro.tarea7dwesalejandro.servicios.ServiciosDetallesUsuarios;
import com.alejandro.tarea7dwesalejandro.servicios.ServiciosUsuariosCombinados;

import java.util.List;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Value("${app.admin.username}")
    private String adminUsername;

    @Value("${app.admin.password}")
    private String adminPassword;

    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    // COMBINAR ADMIN + BBDD
    @Bean
    public UserDetailsService combinedUserDetailsService(ServiciosDetallesUsuarios serviciosDetallesUsuarios) {
        UserDetails admin = User.builder()
                .username(adminUsername)
                .password(adminPassword)
                .authorities(List.of(new SimpleGrantedAuthority("ROLE_ADMIN")))
                .build();

        InMemoryUserDetailsManager memory = new InMemoryUserDetailsManager(admin);
        return new ServiciosUsuariosCombinados(memory, serviciosDetallesUsuarios);
    }

    @Bean
    public DaoAuthenticationProvider authenticationProvider(UserDetailsService combinedUserDetailsService,
                                                            BCryptPasswordEncoder encoder) {
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setUserDetailsService(combinedUserDetailsService);
        provider.setPasswordEncoder(encoder);
        return provider;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/", "/login", "/css/**", "/invitado", "/perfiles/**", "/plantas/verPlantasInvi").permitAll()
                .requestMatchers("/admin/**").hasRole("ADMIN")
                .requestMatchers("/personal/**").hasRole("PERSONAL")
                .anyRequest().authenticated()
            )
            .formLogin(form -> form
                .loginPage("/login")
                .defaultSuccessUrl("/home", true)
                .permitAll()
            )
            .logout(logout -> logout
                .logoutSuccessUrl("/login?logout")
                .permitAll()
            );

        return http.build();
    }
}


