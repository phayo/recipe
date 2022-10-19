package com.abn.amro.recipe.service.dto;

import lombok.Data;

@Data
public class IngredientDTO {
    private Long id;
    private String name;
    private String variation;
    private double quantity;
    private String unit;
}
