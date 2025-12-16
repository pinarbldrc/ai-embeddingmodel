package liva.SpringSearchAI.dto;

import jdk.jfr.Description;

public record ProductSearchParameters(
        @Description("Ürünün ana kategorisi veya tipi (örn: sandalet, bot).")
        String productType,

        @Description("Aranan ürünün rengi.")
        String color,

        @Description("Aranan ayakkabı numarası. Eğer yoksa 'null' olmalı.")
        String size,
        @Description("Aranan ayakkabı markası")
        String brand,
        @Description("Aranan ayakkabı linki")
        String link


) {}
