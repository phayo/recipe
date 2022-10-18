package com.abn.amro.recipe.resource;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;


import com.abn.amro.recipe.RecipeApplication;
import com.abn.amro.recipe.dao.RecipeRepository;
import com.abn.amro.recipe.domain.Recipe;
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

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest(classes = {RecipeApplication.class})
@ExtendWith(MockitoExtension.class)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class RecipeResourceTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    RecipeRepository recipeRepository;

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
}