package com.abn.amro.recipe.service;

import com.abn.amro.recipe.dao.RecipeRepository;
import com.abn.amro.recipe.domain.Recipe;
import com.abn.amro.recipe.service.dto.IngredientDTO;
import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
class DefaultRecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @InjectMocks
    private DefaultRecipeService defaultRecipeService;

    private RecipeDTO getRecipeDTO(){
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setType("VEGAN");
        recipeDTO.setName("Recipe 1");
        recipeDTO.setNoOfServing(5);
        recipeDTO.setServingSize(32);
        recipeDTO.setInstructions("Cook in the oven");

        IngredientDTO ingredient = new IngredientDTO();
        ingredient.setName("Ingredient 1");
        ingredient.setVariation("Variation 1");
        ingredient.setUnit("Unit 1");
        ingredient.setQuantity(4);

        IngredientDTO ingredient2 = new IngredientDTO();
        ingredient2.setName("Ingredient 2");
        ingredient2.setVariation("Variation 2");
        ingredient2.setUnit("Unit 2");
        ingredient2.setQuantity(34);

        recipeDTO.setIngredient(List.of(ingredient2, ingredient));

        return recipeDTO;
    }

    @Test
    void checkPreconditions_shouldNotThrow_onValidRecipeDTO() {
       assertDoesNotThrow(() -> defaultRecipeService.checkPreconditions(getRecipeDTO()));
    }

    @Test
    void checkPreconditions_shouldThrow_onInvalidRecipeName() {
        RecipeDTO dto = getRecipeDTO();
        dto.setName("");
        assertThrows(IllegalArgumentException.class ,() -> defaultRecipeService.checkPreconditions(dto));

        dto.setName("an !nvalidDto");
        assertThrows(IllegalArgumentException.class ,() -> defaultRecipeService.checkPreconditions(dto));
    }

    @Test
    void checkPreconditions_shouldThrow_onInvalidRecipeSizeAndNoOfServing() {
        RecipeDTO dto = getRecipeDTO();
        dto.setNoOfServing(-3);
        assertThrows(IllegalArgumentException.class ,() -> defaultRecipeService.checkPreconditions(dto));

        dto.setNoOfServing(3);
        dto.setServingSize(-4);
        assertThrows(IllegalArgumentException.class ,() -> defaultRecipeService.checkPreconditions(dto));
    }

    @Test
    void checkPreconditions_shouldThrow_onInvalidIngredient() {
        RecipeDTO dto = getRecipeDTO();
        dto.getIngredient().get(0).setQuantity(-3);
        assertThrows(IllegalArgumentException.class ,() -> defaultRecipeService.checkPreconditions(dto));

        dto.getIngredient().get(0).setQuantity(3);
        dto.getIngredient().get(1).setName("*-*");
        assertThrows(IllegalArgumentException.class ,() -> defaultRecipeService.checkPreconditions(dto));
    }

    @Test
    void canCreateEntity_shouldCreateAValidRecipeEntityAndDTO() {
        RecipeDTO dto = getRecipeDTO();
        Recipe recipe = defaultRecipeService.createEntity(dto);

        assertEquals(dto.getName(), recipe.getName());
        assertEquals(dto.getNoOfServing(), recipe.getNoOfServing());
        assertEquals(dto.getServingSize(), recipe.getServingSize());
        assertEquals(dto.getInstructions(), recipe.getInstructions());
        assertEquals(dto.getType(), recipe.getType().name());
        assertEquals(dto.getIngredient().get(0).getName(), recipe.getIngredient().get(0).getName());
        assertEquals(dto.getIngredient().get(0).getUnit(), recipe.getIngredient().get(0).getUnit());
        assertEquals(dto.getIngredient().get(0).getQuantity(), recipe.getIngredient().get(0).getQuantity());
        assertEquals(dto.getIngredient().get(0).getVariation(), recipe.getIngredient().get(0).getVariation());

        RecipeDTO dto1 =  defaultRecipeService.toDTO(recipe);
        assertEquals(recipe.getName(), dto1.getName());
        assertEquals(recipe.getNoOfServing(), dto1.getNoOfServing());
        assertEquals(recipe.getServingSize(), dto1.getServingSize());
        assertEquals(recipe.getInstructions(), dto1.getInstructions());
        assertEquals(recipe.getType().name(), dto1.getType());
        assertEquals(recipe.getIngredient().get(0).getName(), dto1.getIngredient().get(0).getName());
        assertEquals(recipe.getIngredient().get(0).getUnit(), dto1.getIngredient().get(0).getUnit());
        assertEquals(recipe.getIngredient().get(0).getQuantity(), dto1.getIngredient().get(0).getQuantity());
        assertEquals(recipe.getIngredient().get(0).getVariation(), dto1.getIngredient().get(0).getVariation());
    }
}