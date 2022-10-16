package com.abn.amro.recipe.service;

import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface RecipeService {
    List<RecipeDTO> searchRecipe(String searchString);
    void createRecipe(RecipeDTO recipeDTO);
    List<RecipeDTO> findAllRecipe(Pageable pageable);
}
