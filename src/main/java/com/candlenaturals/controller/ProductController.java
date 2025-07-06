package com.candlenaturals.controller;

import com.candlenaturals.dto.ProductRequest; // Importa el DTO que acabamos de crear
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // Si usas @PreAuthorize
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor; // Para inyección de dependencias con constructor

@RestController
@RequestMapping("/api/products") // Prefijo para todos los endpoints de este controlador
@RequiredArgsConstructor // Genera un constructor con los campos final inyectados
public class ProductController {

    // Si ya tienes un servicio para productos, inyéctalo aquí.
    // Ejemplo: private final ProductService productService;
    // Si no, puedes empezar directamente con un repositorio o implementar la lógica aquí
    // Aunque se recomienda usar una capa de servicio.

    @PostMapping("/register")
    // @PreAuthorize("hasRole('administrador')") // Opcional: para aplicar seguridad directamente en el método
    public ResponseEntity<String> registerProduct(@RequestBody ProductRequest productRequest) {
        // Aquí iría la lógica para guardar el producto en la base de datos
        // Por ahora, solo imprimiremos los datos para verificar que llegan
        System.out.println("Producto recibido:");
        System.out.println("Título: " + productRequest.getTitle());
        System.out.println("Descripción: " + productRequest.getDescription());
        System.out.println("Precio: " + productRequest.getPrice());
        System.out.println("Imagen: " + productRequest.getImage());

        // Aquí deberías llamar a tu servicio/repositorio para persistir el producto
        // Ejemplo: productService.saveProduct(productRequest);

        return new ResponseEntity<>("Producto registrado exitosamente: " + productRequest.getTitle(), HttpStatus.CREATED);
    }

    // Puedes añadir otros endpoints para productos aquí (GET, PUT, DELETE, etc.)
}