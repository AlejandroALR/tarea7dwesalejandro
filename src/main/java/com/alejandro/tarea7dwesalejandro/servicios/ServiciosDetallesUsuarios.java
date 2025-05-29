package com.alejandro.tarea7dwesalejandro.servicios;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.*;
import org.springframework.stereotype.Service;

import com.alejandro.tarea7dwesalejandro.modelo.Credenciales;
import com.alejandro.tarea7dwesalejandro.repositorios.CredencialesRepository;

import java.util.Collections;

@Service
public class ServiciosDetallesUsuarios implements UserDetailsService {

    @Autowired
    private CredencialesRepository credencialesRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Credenciales cred = credencialesRepository.findByUsuario(username)
            .orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + username));

        String rol = cred.getPersona().getRol();
        return new User(
                cred.getUsuario(),
                cred.getPassword(),
                Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + rol))
        );
    }

}
