package com.alejandro.tarea7dwesalejandro.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class CustomSuccessHandler implements AuthenticationSuccessHandler {

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {

		for (GrantedAuthority auth : authentication.getAuthorities()) {
			String rol = auth.getAuthority();
			switch (rol) {
			case "ROLE_ADMIN":
				response.sendRedirect("/admin");
				return;
			case "ROLE_PERSONAL":
				response.sendRedirect("/personal");
				return;
			case "ROLE_CLIENTE":
				response.sendRedirect("/cliente");
				return;
			}
		}

		response.sendRedirect("/invitado");
	}
}
