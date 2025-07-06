package com.candlenaturals.dto;

import lombok.Data; // Necesitarás Lombok en tu pom.xml

@Data // Genera getters, setters, toString, equals y hashCode automáticamente
public class ProductRequest {
    private String title;
    private String description;
    private double price; // O String si manejas el formato "COP $" en el backend
    private String image; // URL de la imagen

    // Puedes añadir un constructor, pero @Data ya se encarga
    // Si no usas Lombok, tendrías que escribir los getters y setters manualmente
    // public String getTitle() { return title; }
    // public void setTitle(String title) { this.title = title; }
    // ... y así sucesivamente para los demás campos
}