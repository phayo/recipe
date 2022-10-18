package com.abn.amro.recipe.service;

import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

@Service
public interface RecipeService {
    Set<RecipeDTO> searchRecipe(String searchString);
    RecipeDTO createRecipe(RecipeDTO recipeDTO);
    List<RecipeDTO> findAllRecipe(Pageable pageable);
    RecipeDTO fineOneRecipe(Long id);
}
