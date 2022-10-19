package com.abn.amro.recipe.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import com.abn.amro.recipe.RecipeApplication;
import com.abn.amro.recipe.dao.RecipeRepository;
import com.abn.amro.recipe.domain.Ingredient;
import com.abn.amro.recipe.domain.Recipe;
import com.abn.amro.recipe.domain.RecipeType;
import com.abn.amro.recipe.service.dto.IngredientDTO;
import com.abn.amro.recipe.service.dto.RecipeDTO;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {RecipeApplication.class})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeResourceIT {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    RecipeRepository recipeRepository;

    @Autowired
    EntityManager em;

    ObjectMapper mapper = createObjectMapper();

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

    private static ObjectMapper createObjectMapper() {
        ObjectMapper mapper = new ObjectMapper();
        mapper.setSerializationInclusion(JsonInclude.Include.NON_EMPTY);
        return mapper;
    }

    @Test
    @Transactional
    void createRecipeEndpoint_shouldCreateRecipe_andReturnCorrectStatusCodeAndResponse() throws Exception {
        int sizeBeforeCreate = recipeRepository.findAll().size();
        mockMvc.perform(post("/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(getRecipeDTO())))
                .andExpect(status().isCreated())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("id").isNotEmpty())
                .andExpect(jsonPath("name").value("Recipe 1"));

        List<Recipe> recipes = recipeRepository.findAll();
        assertEquals(sizeBeforeCreate + 1, recipes.size());
    }

    @Test
    @Transactional
    void createRecipeEndpoint_shouldReturnBadRequest_forWrongRequestBody() throws Exception {
        RecipeDTO dto = getRecipeDTO();
        dto.setName("N@me");
        dto.setNoOfServing(-2);
        int sizeBeforeCreate = recipeRepository.findAll().size();
        mockMvc.perform(post("/recipe")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(dto)))
                .andExpect(status().isBadRequest())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON_VALUE))
                .andExpect(jsonPath("message").isNotEmpty());

        List<Recipe> recipes = recipeRepository.findAll();
        assertEquals(sizeBeforeCreate, recipes.size());
    }

    @Test
    @Transactional
    void searchRecipeEndpoint_shouldReturnBadRequest_forWrongSearchTerm() throws Exception {
        String searchTermKey = "wrongsearchterm:Recipe";
        mockMvc.perform(get("/recipe/search?search_term="+ searchTermKey)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isNotEmpty());

        String searchTermOperation = "wrongsearchterm?Recipe";
        mockMvc.perform(get("/recipe/search?search_term="+ searchTermOperation)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isNotEmpty());

    }

    @Test
    @Transactional
    void searchRecipeEndpoint_shouldReturn200OkAndCorrectResults_forCorrectSearchTermThatExists() throws Exception {
        String searchTerm = "name:Myownrecipe";
        mockMvc.perform(get("/recipe/search?search_term="+ searchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").isNotEmpty());
        Recipe recipe = getRecipe();
        recipe.setName("Myownrecipe");
        recipeRepository.saveAndFlush(recipe);

        mockMvc.perform(get("/recipe/search?search_term="+ searchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*].noOfServing").value(recipe.getNoOfServing()))
                .andExpect(jsonPath("$.[*].servingSize").value(recipe.getServingSize()))
                .andExpect(jsonPath("$.[*].instructions").value(recipe.getInstructions()))
                .andExpect(jsonPath("$.[*].type").value(recipe.getType().name()))
                .andExpect(jsonPath("$.[*].name").value(recipe.getName()));

        Recipe newrecipe = getRecipe();
        newrecipe.setName("anotherrecipe");
        newrecipe.getIngredient().get(0).setName("awesomeIngredient");
        recipeRepository.saveAndFlush(newrecipe);

        searchTerm = "ingredient_name:awesomeIngredient";
        mockMvc.perform(get("/recipe/search?search_term="+ searchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[0].ingredient.[0].name").value(newrecipe.getIngredient().get(0).getName()))
                .andExpect(jsonPath("$.[0].servingSize").value(newrecipe.getServingSize()))
                .andExpect(jsonPath("$.[0].instructions").value(newrecipe.getInstructions()))
                .andExpect(jsonPath("$.[0].type").value(newrecipe.getType().name()))
                .andExpect(jsonPath("$.[0].name").value(newrecipe.getName()));

        Recipe newrecipe1 = getRecipe();
        newrecipe1.setName("anotherrecipe");
        newrecipe1.getIngredient().get(0).setName("awesomeIngredient");
        newrecipe1.getIngredient().get(0).setVariation("anothervariation");
        recipeRepository.saveAndFlush(newrecipe1);
        searchTerm = "ingredient_name:awesomeIngredient,ingredient_variation:anothervariation";
        mockMvc.perform(get("/recipe/search?search_term="+ searchTerm)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.[*]").isArray())
                .andExpect(jsonPath("$.[0].ingredient.[0].name").value(newrecipe1.getIngredient().get(0).getName()))
                .andExpect(jsonPath("$.[0].ingredient.[0].quantity").value(newrecipe1.getIngredient().get(0).getQuantity()))
                .andExpect(jsonPath("$.[0].ingredient.[0].variation").value(newrecipe1.getIngredient().get(0).getVariation()))
                .andExpect(jsonPath("$.[0].servingSize").value(newrecipe1.getServingSize()))
                .andExpect(jsonPath("$.[0].instructions").value(newrecipe1.getInstructions()))
                .andExpect(jsonPath("$.[0].type").value(newrecipe1.getType().name()))
                .andExpect(jsonPath("$.[0].name").value(newrecipe1.getName()));

    }

    @Test
    @Transactional
    void getOneRecipeEndpoint_shouldReturn404NotFound_forRecipeThatDoesNotExist() throws Exception {
        mockMvc.perform(get("/recipe/2300000")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("message").isNotEmpty());

        mockMvc.perform(get("/recipe/qas")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("message").isNotEmpty());

    }

    @Test
    @Transactional
    void getOneRecipeEndpoint_shouldReturn200ok_forRecipeThatExists() throws Exception {
        Recipe recipe = recipeRepository.saveAndFlush(getRecipe());
        mockMvc.perform(get("/recipe/" + recipe.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(recipe.getId()))
                .andExpect(jsonPath("name").value(recipe.getName()))
                .andExpect(jsonPath("noOfServing").value(recipe.getNoOfServing()))
                .andExpect(jsonPath("servingSize").value(recipe.getServingSize()));

    }

    @Test
    @Transactional
    void getAllRecipeEndpoint_shouldReturnThrowError_forWrongInputs() throws Exception {
        recipeRepository.deleteAll();

        Recipe recipe = recipeRepository.saveAndFlush(getRecipe());
        Recipe recipe1 = getRecipe();
        recipe1.setName("Another recipe");
        recipe1.setInstructions("I large text of instructions");
        recipeRepository.saveAndFlush(recipe1);
        mockMvc.perform(get("/recipe/?page=aLetter&size=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Recipe recipe2 = getRecipe();
        recipe2.setName("Yet Another recipe");
        recipe2.setInstructions("I large text of instructions");
        recipe2.setServingSize(13);
        recipeRepository.saveAndFlush(recipe2);
        mockMvc.perform(get("/recipe/?page=3&size=3")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void getAllRecipeEndpoint_shouldReturn200ok_forRecipeThatExists() throws Exception {
        recipeRepository.deleteAll();

        Recipe recipe = recipeRepository.saveAndFlush(getRecipe());
        Recipe recipe1 = getRecipe();
        recipe1.setName("Another recipe");
        recipe1.setInstructions("I large text of instructions");
        recipeRepository.saveAndFlush(recipe1);
        mockMvc.perform(get("/recipe/")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[0].name").value(recipe.getName()))
                .andExpect(jsonPath("$.[0].noOfServing").value(recipe.getNoOfServing()))
                .andExpect(jsonPath("$.[0].servingSize").value(recipe.getServingSize()))
                .andExpect(jsonPath("$.[1].name").value(recipe1.getName()))
                .andExpect(jsonPath("$.[1].noOfServing").value(recipe1.getNoOfServing()))
                .andExpect(jsonPath("$.[1].servingSize").value(recipe1.getServingSize()));

        Recipe recipe2 = getRecipe();
        recipe2.setName("Yet Another recipe");
        recipe2.setInstructions("I large text of instructions");
        recipe2.setServingSize(13);
        recipeRepository.saveAndFlush(recipe2);
        mockMvc.perform(get("/recipe/?page=2&size=2")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$.[0].name").value(recipe2.getName()))
                .andExpect(jsonPath("$.[0].noOfServing").value(recipe2.getNoOfServing()))
                .andExpect(jsonPath("$.[0].servingSize").value(recipe2.getServingSize()));
    }

    @Test
    @Transactional
    void deleteRecipeEndpoint_shouldDeleteRecipe_forRecipeThatExists() throws Exception {
        recipeRepository.deleteAll();

        Recipe recipe1 = getRecipe();
        recipe1.setName("Another recipe");
        recipe1.setInstructions("I large text of instructions");
        recipeRepository.saveAndFlush(recipe1);
        mockMvc.perform(delete("/recipe/" + recipe1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(recipe1.getId()))
                .andExpect(jsonPath("name").value(recipe1.getName()))
                .andExpect(jsonPath("noOfServing").value(recipe1.getNoOfServing()))
                .andExpect(jsonPath("servingSize").value(recipe1.getServingSize()));


        mockMvc.perform(delete("/recipe/" + recipe1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateRecipeEndpoint_shouldUpdateRecipe_forRecipeThatExists() throws Exception {
        recipeRepository.deleteAll();
        Recipe recipe = recipeRepository.saveAndFlush(getRecipe());
        RecipeDTO recipeDTO = getRecipeDTO();
        recipeDTO.setName("Another recipe");
        recipeDTO.setInstructions("I large text of instructions");
        recipeDTO.setNoOfServing(0);
        recipeDTO.setServingSize(0);

        mockMvc.perform(put("/recipe/" + recipe.getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(recipeDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(recipe.getId()))
                .andExpect(jsonPath("name").value(recipeDTO.getName()))
                .andExpect(jsonPath("instructions").value(recipeDTO.getInstructions()))
                .andExpect(jsonPath("noOfServing").value(recipe.getNoOfServing()))
                .andExpect(jsonPath("servingSize").value(recipe.getServingSize()))
                .andExpect(jsonPath("$.ingredient.[0].name").value(recipe.getIngredient().get(0).getName()))
                .andExpect(jsonPath("$.ingredient.[0].quantity").value(recipe.getIngredient().get(0).getQuantity()));


        mockMvc.perform(put("/recipe/200")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(recipeDTO)))
                .andExpect(status().isNotFound());
    }

    @Test
    @Transactional
    void updateRecipeIngredientEndpoint_shouldUpdateRecipe_forRecipeThatExists() throws Exception {
        recipeRepository.deleteAll();
        Recipe recipe = recipeRepository.saveAndFlush(getRecipe());
        IngredientDTO ingredientDTO = new IngredientDTO();
        ingredientDTO.setName("Another Ingredient name");
        ingredientDTO.setVariation("A different variation");
        ingredientDTO.setQuantity(0);
        ingredientDTO.setUnit(null);

        mockMvc.perform(put("/recipe/" + recipe.getId() +"/ingredient/"+ recipe.getIngredient().get(1).getId())
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(ingredientDTO)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("id").value(recipe.getId()))
                .andExpect(jsonPath("name").value(recipe.getName()))
                .andExpect(jsonPath("noOfServing").value(recipe.getNoOfServing()))
                .andExpect(jsonPath("servingSize").value(recipe.getServingSize()))
                .andExpect(jsonPath("$.ingredient.[1].name").value(ingredientDTO.getName()))
                .andExpect(jsonPath("$.ingredient.[1].variation").value(ingredientDTO.getVariation()))
                .andExpect(jsonPath("$.ingredient.[1].unit").value(recipe.getIngredient().get(1).getUnit()))
                .andExpect(jsonPath("$.ingredient.[1].quantity").value(recipe.getIngredient().get(1).getQuantity()));


        mockMvc.perform(put("/recipe/" + recipe.getId() +"/ingredient/6776776")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsBytes(ingredientDTO)))
                .andExpect(status().isNotFound());
    }
}