package com.alejandro.tarea7dwesalejandro.dto;

import jakarta.validation.constraints.*;
import java.time.LocalDate;

public class RegistroClienteDTO {

    @NotBlank(message = "El nombre completo no puede estar en blanco")
    private String nombre;

    @NotBlank(message = "El email no puede estar en blanco")
    @Email(message = "El formato del email no es válido")
    @Pattern(regexp = "^\\S+$", message = "El email no puede contener espacios")
    private String email;

    @NotBlank(message = "El nombre de usuario no puede estar en blanco")
    @Pattern(regexp = "^\\S+$", message = "El nombre de usuario no puede contener espacios")
    private String usuario;

    @NotBlank(message = "La contraseña no puede estar en blanco")
    @Pattern(regexp = "^\\S+$", message = "La contraseña no puede contener espacios")
    private String password;

    @NotNull(message = "Debe indicar la fecha de nacimiento")
    private LocalDate fechaNacimiento;

    @NotBlank(message = "El NIF/NIE no puede estar en blanco")
    @Pattern(regexp = "^\\S+$", message = "El NIF/NIE no puede contener espacios")
    private String nifNie;

    @NotBlank(message = "La dirección de envío no puede estar en blanco")
    private String direccionEnvio;

    @NotBlank(message = "El teléfono no puede estar en blanco")
    @Pattern(regexp = "^\\S+$", message = "El teléfono no puede contener espacios")
    private String telefono;

    // Getters y Setters

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public LocalDate getFechaNacimiento() {
        return fechaNacimiento;
    }

    public void setFechaNacimiento(LocalDate fechaNacimiento) {
        this.fechaNacimiento = fechaNacimiento;
    }

    public String getNifNie() {
        return nifNie;
    }

    public void setNifNie(String nifNie) {
        this.nifNie = nifNie;
    }

    public String getDireccionEnvio() {
        return direccionEnvio;
    }

    public void setDireccionEnvio(String direccionEnvio) {
        this.direccionEnvio = direccionEnvio;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }
    
    
}

