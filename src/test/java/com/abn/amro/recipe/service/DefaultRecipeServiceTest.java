package com.abn.amro.recipe.service;

import com.abn.amro.recipe.dao.RecipeRepository;
import com.abn.amro.recipe.domain.Ingredient;
import com.abn.amro.recipe.domain.Recipe;
import com.abn.amro.recipe.domain.RecipeType;
import com.abn.amro.recipe.service.dto.IngredientDTO;
import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
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

    private Recipe getRecipe(){
        Recipe recipe = new Recipe();
        recipe.setType(RecipeType.VEGAN);
        recipe.setName("Recipe");
        recipe.setNoOfServing(5);
        recipe.setServingSize(32);
        recipe.setInstructions("Cook in the oven");

        Ingredient ingredient = new Ingredient();
        ingredient.setName("Ingredient 1");
        ingredient.setVariation("Variation 1");
        ingredient.setUnit("Unit 1");
        ingredient.setQuantity(4);

        Ingredient ingredient2 = new Ingredient();
        ingredient2.setName("Ingredient 2");
        ingredient2.setVariation("Variation 2");
        ingredient2.setUnit("Unit 2");
        ingredient2.setQuantity(34);

        ArrayList<Ingredient> ingredients = new ArrayList<>();
        ingredients.add(ingredient2);
        ingredients.add(ingredient);
        recipe.setIngredient(ingredients);

        return recipe;
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

    @Test
    void updateRecipeProps_shouldThrowError_onWrongInput(){
        RecipeDTO recipeDTO = getRecipeDTO();
        Recipe recipe = getRecipe();

        recipeDTO.setName("*****");
        assertThrows(IllegalArgumentException.class, () -> defaultRecipeService.updateRecipeProps(recipeDTO, recipe));

        recipeDTO.setName("A vid name");
        recipeDTO.setType("UNSUPPORTED");
        assertThrows(IllegalArgumentException.class, () -> defaultRecipeService.updateRecipeProps(recipeDTO, recipe));
    }

    @Test
    void updateRecipeProps_shouldUpdateRecipe_onCorrectInput(){
        RecipeDTO recipeDTO = getRecipeDTO();
        Recipe recipe = getRecipe();

        recipeDTO.setName("A valid name");
        recipeDTO.setType("VEGETARIAN");
        recipeDTO.setServingSize(67);
        recipeDTO.setNoOfServing(70);
        Recipe recipe1 = defaultRecipeService.updateRecipeProps(recipeDTO, recipe);

        assertEquals(recipeDTO.getNoOfServing(), recipe1.getNoOfServing());
        assertEquals(recipeDTO.getServingSize(), recipe1.getServingSize());
        assertEquals(recipeDTO.getName(), recipe1.getName());
        assertEquals(recipeDTO.getType(), recipe1.getType().name());

        int prevServingSize = recipe1.getServingSize();
        recipeDTO.setName("Another valid name");
        recipeDTO.setServingSize(0);
        recipeDTO.setNoOfServing(90);
        Recipe recipe2 = defaultRecipeService.updateRecipeProps(recipeDTO, recipe);

        assertEquals(recipeDTO.getNoOfServing(), recipe2.getNoOfServing());
        assertEquals(prevServingSize, recipe2.getServingSize());
        assertEquals(recipeDTO.getName(), recipe2.getName());

    }

    @Test
    void updateIngredientProps_shouldThrowError_onWrongInput(){
        IngredientDTO ingredientDTO = getRecipeDTO().getIngredient().get(0);
        Ingredient ingredient = getRecipe().getIngredient().get(0);

        ingredientDTO.setName("*****");
        assertThrows(IllegalArgumentException.class, () -> defaultRecipeService.updateIngredientProps(ingredientDTO, ingredient));
    }

    @Test
    void updateIngredientProps_shouldUpdateIngredient_onCorrectInput(){
        IngredientDTO ingredientDTO = getRecipeDTO().getIngredient().get(0);
        Ingredient ingredient = getRecipe().getIngredient().get(0);

        ingredientDTO.setName("A valid Ingredient name");
        ingredientDTO.setUnit("A valid Ingredient unit");
        ingredientDTO.setVariation("A valid Ingredient variation");
        ingredientDTO.setQuantity(300);
        Ingredient ingredient1 = defaultRecipeService.updateIngredientProps(ingredientDTO, ingredient);

        assertEquals(ingredientDTO.getVariation(), ingredient1.getVariation());
        assertEquals(ingredientDTO.getUnit(), ingredient1.getUnit());
        assertEquals(ingredientDTO.getVariation(), ingredient1.getVariation());
        assertEquals(ingredientDTO.getQuantity(), ingredient1.getQuantity());
    }

}