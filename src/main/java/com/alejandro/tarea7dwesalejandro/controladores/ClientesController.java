package com.alejandro.tarea7dwesalejandro.controladores;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;

import com.alejandro.tarea7dwesalejandro.dto.RegistroClienteDTO;
import com.alejandro.tarea7dwesalejandro.servicios.ServiciosClientes;

import jakarta.validation.Valid;

@Controller
@RequestMapping("/clientes")
public class ClientesController {

    @Autowired
    private ServiciosClientes serviciosClientes;

    @GetMapping("/registro")
    public String mostrarFormularioRegistro(Model model) {
        model.addAttribute("cliente", new RegistroClienteDTO());
        return "clientes/registroClientes";
    }

    @PostMapping("/registro")
    public String procesarRegistro(@Valid @ModelAttribute("cliente") RegistroClienteDTO dto,
                                   BindingResult bindingResult,
                                   Model model) {

        // Validaciones personalizadas: campos únicos
        if (!bindingResult.hasFieldErrors("email") && serviciosClientes.emailYaExiste(dto.getEmail())) {
            bindingResult.rejectValue("email", "error.email", "Ya existe un usuario con ese email");
        }

        if (!bindingResult.hasFieldErrors("usuario") && serviciosClientes.usuarioYaExiste(dto.getUsuario())) {
            bindingResult.rejectValue("usuario", "error.usuario", "Ya existe un usuario con ese nombre");
        }

        if (!bindingResult.hasFieldErrors("nifNie") && serviciosClientes.nifYaExiste(dto.getNifNie())) {
            bindingResult.rejectValue("nifNie", "error.nifNie", "Ya existe un cliente con ese NIF/NIE");
        }

        if (bindingResult.hasErrors()) {
            return "clientes/registroClientes";
        }

        serviciosClientes.registrarCliente(dto);
        model.addAttribute("exito", "Cliente registrado correctamente. Ahora puede iniciar sesión.");
        return "clientes/registroClientes";
    }
}
