package com.abn.amro.recipe.service.dto;

import lombok.Data;

import java.util.List;

@Data
public class RecipeDTO {
    private Long id;
    private String name;
    private List<IngredientDTO> ingredient;
    private int noOfServing;
    private int servingSize;
    private String type;
    private String instructions;
}
