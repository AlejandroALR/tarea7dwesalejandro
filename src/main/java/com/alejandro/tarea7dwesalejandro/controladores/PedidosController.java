package com.alejandro.tarea7dwesalejandro.controladores;

import com.alejandro.tarea7dwesalejandro.dto.FiltroPedidoFechaDTO;
import com.alejandro.tarea7dwesalejandro.modelo.*;
import com.alejandro.tarea7dwesalejandro.repositorios.EjemplaresRepository;
import com.alejandro.tarea7dwesalejandro.servicios.ServiciosPedidos;
import com.alejandro.tarea7dwesalejandro.servicios.ServiciosPersonas;
import com.alejandro.tarea7dwesalejandro.servicios.ServiciosPlantas;

import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/clientes")
public class PedidosController {

    @Autowired
    private ServiciosPlantas serviciosPlantas;

    @Autowired
    private ServiciosPedidos serviciosPedidos;

    @Autowired
    private ServiciosPersonas serviciosPersonas;

    @Autowired
    private EjemplaresRepository ejemplaresRepository;

    @GetMapping("/realizarPedido")
    public String mostrarFormularioPedido(Model model, HttpSession session) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        List<Plantas> todasPlantas = serviciosPlantas.listarTodas();

        Map<String, Long> stockDisponible = new HashMap<>();

        for (Plantas planta : todasPlantas) {
            String codPlanta = planta.getCodigo();
            long disponiblesEnBD = ejemplaresRepository.countByPlanta_CodigoAndDisponibleTrue(codPlanta);

            int enCarrito = 0;
            if (carrito != null) {
                enCarrito = carrito.getItems().stream()
                        .filter(item -> item.getCodigo().equals(codPlanta))
                        .mapToInt(ItemCarrito::getCantidad)
                        .sum();
            }

            long disponiblesFinales = disponiblesEnBD - enCarrito;
            if (disponiblesFinales < 0) disponiblesFinales = 0;

            stockDisponible.put(codPlanta, disponiblesFinales);
        }

        model.addAttribute("plantas", todasPlantas);
        model.addAttribute("stockDisponible", stockDisponible);
        return "clientes/realizarPedido";
    }

    @PostMapping("/realizarPedido")
    public String procesarPedido(@RequestParam("codPlanta") List<String> codPlantas,
                                 @RequestParam("nombrePlanta") List<String> nombresPlantas,
                                 @RequestParam("cantidad") List<Integer> cantidades,
                                 HttpSession session) {

        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null) {
            carrito = new Carrito();
        }

        for (int i = 0; i < codPlantas.size(); i++) {
            int cantidad = cantidades.get(i);
            if (cantidad > 0) {
                ItemCarrito item = new ItemCarrito(codPlantas.get(i), nombresPlantas.get(i), cantidad);
                carrito.agregarItem(item);
            }
        }

        session.setAttribute("carrito", carrito);
        return "redirect:/clientes/carrito";
    }

    @GetMapping("/carrito")
    public String verCarrito(HttpSession session, Model model) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null || carrito.getItems().isEmpty()) {
            carrito = new Carrito();
        }
        model.addAttribute("carrito", carrito);
        return "clientes/verCarrito";
    }

    @PostMapping("/carrito/eliminar")
    public String eliminarDelCarrito(@RequestParam("codigo") String codigo, HttpSession session) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito != null) {
            carrito.eliminarItem(codigo);
        }
        return "redirect:/clientes/carrito";
    }

    @GetMapping("/pedido/confirmar")
    public String mostrarConfirmacionPedido(HttpSession session, Model model) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        if (carrito == null || carrito.getItems().isEmpty()) {
            return "redirect:/clientes/carrito";
        }
        model.addAttribute("carrito", carrito);
        return "clientes/confirmarPedido";
    }

    @PostMapping("/pedido/confirmar")
    public String procesarConfirmacionPedido(HttpSession session, RedirectAttributes redirectAttrs) {
        Carrito carrito = (Carrito) session.getAttribute("carrito");
        Personas cliente = (Personas) session.getAttribute("persona");

        if (carrito == null || cliente == null) {
            redirectAttrs.addFlashAttribute("error", "No se puede confirmar el pedido. Sesión inválida.");
            return "redirect:/clientes/carrito";
        }

        serviciosPedidos.registrarPedido(carrito, cliente);
        session.setAttribute("carrito", new Carrito());

        redirectAttrs.addFlashAttribute("exito", "¡Pedido confirmado con éxito!");
        return "redirect:/clientes/misPedidos";
    }

    @GetMapping("/misPedidos")
    public String verMisPedidos(HttpSession session, Model model) {
        Personas cliente = (Personas) session.getAttribute("persona");
        if (cliente == null) {
            return "redirect:/login";
        }

        List<Pedido> pedidos = serviciosPedidos.obtenerPedidosDePersona(cliente);
        model.addAttribute("pedidos", pedidos);
        return "clientes/misPedidos";
    }

    @GetMapping("/pedido/{id}")
    public String verDetallePedido(@PathVariable Long id, Model model, HttpSession session, RedirectAttributes redirectAttrs) {
        Personas cliente = (Personas) session.getAttribute("persona");
        if (cliente == null) {
            redirectAttrs.addFlashAttribute("error", "Debes iniciar sesión para ver el detalle del pedido.");
            return "redirect:/login";
        }

        Pedido pedido = serviciosPedidos.obtenerPedidoPorId(id);
        if (!pedido.getPersona().getId().equals(cliente.getId())) {
            redirectAttrs.addFlashAttribute("error", "No tienes permiso para ver ese pedido.");
            return "redirect:/clientes/misPedidos";
        }

        model.addAttribute("pedido", pedido);
        return "clientes/detallePedido";
    }

    @GetMapping("/filtrarPedidos")
    public String mostrarFormularioFiltroPedidos(Model model) {
        model.addAttribute("filtro", new FiltroPedidoFechaDTO());
        return "clientes/filtrarPedidos";
    }

    @PostMapping("/filtrarPedidos")
    public String procesarFiltroPedidos(@ModelAttribute("filtro") FiltroPedidoFechaDTO filtro, Model model) {
        List<Pedido> pedidos = serviciosPedidos.filtrarPedidosPorFecha(filtro.getDesde(), filtro.getHasta());
        model.addAttribute("filtro", filtro);
        model.addAttribute("resultados", pedidos);
        return "clientes/filtrarPedidos";
    }
}



