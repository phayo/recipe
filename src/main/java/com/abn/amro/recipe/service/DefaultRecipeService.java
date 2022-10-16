package com.abn.amro.recipe.service;

import com.abn.amro.recipe.dao.RecipeRepository;
import com.abn.amro.recipe.domain.Ingredient;
import com.abn.amro.recipe.domain.Recipe;
import com.abn.amro.recipe.domain.RecipeType;
import com.abn.amro.recipe.domain.spec.SearchOperation;
import com.abn.amro.recipe.domain.spec.SpecificationBuilder;
import com.abn.amro.recipe.service.dto.IngredientDTO;
import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultRecipeService implements RecipeService{

    private final RecipeRepository recipeRepository;

    public DefaultRecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    private final Pattern p = Pattern.compile("[^a-zA-Z0-9 ]");

    @Override
    public List<RecipeDTO> searchRecipe(String searchString) {
        SpecificationBuilder builder = new SpecificationBuilder();
        String operationSetExper = String.join("|", SearchOperation.SIMPLE_OPERATION_SET);
        String s = "(\\w+?)("+ operationSetExper +")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),";
        Pattern pattern = Pattern.compile(s);
        Matcher matcher = pattern.matcher(searchString + ",");
        while (matcher.find()) {
            builder.with(
                    matcher.group(1),
                    matcher.group(2),
                    matcher.group(4),
                    matcher.group(3),
                    matcher.group(5));
        }

        Specification<Recipe> spec = builder.build();
        return recipeRepository.findAll(spec).stream().map(this::toDTO).collect(Collectors.toList());
    }

    public void createRecipe(final RecipeDTO recipeDTO){
        checkPreconditions(recipeDTO);
        Recipe recipe = createEntity(recipeDTO);
        recipeRepository.save(recipe);
    }

    public List<RecipeDTO> findAllRecipe(Pageable pageable){
        return recipeRepository.findAll(pageable).stream().map(this::toDTO).collect(Collectors.toList());
    }

    private void checkPreconditions(RecipeDTO recipeDTO) {
        if(p.matcher(recipeDTO.getName()).find() || recipeDTO.getName().isEmpty()) throw new IllegalArgumentException("Recipe name should not be empty or contain illegal characters");
        if(recipeDTO.getNoOfServing() < 0 || recipeDTO.getServingSize() < 0) throw new IllegalArgumentException("Number of serving and serving size should not be less than 0.");

        try{
            RecipeType.valueOf(recipeDTO.getType().toUpperCase());
        }catch (Exception exception){
            throw new IllegalArgumentException("Choose a recipe type of VEGAN, VEGETARIAN, or NORMAL");
        }

        recipeDTO.getIngredient().forEach(ingredient -> {
            if(ingredient.getName().isEmpty() || p.matcher(recipeDTO.getName()).find()) throw new IllegalArgumentException("ingredient name should not be empty or contain illegal characters");
            if(ingredient.getQuantity() < 0) throw  new IllegalArgumentException("Ingredient quantity should not be less than 0");
        });
    }

    private Recipe createEntity(RecipeDTO recipeDTO) {
        Recipe recipe = new Recipe();
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setName(recipeDTO.getName().toLowerCase());
        recipe.setServingSize(recipeDTO.getServingSize());
        recipe.setNoOfServing(recipeDTO.getNoOfServing());
        recipe.setName(recipeDTO.getName());
        recipe.setType(RecipeType.valueOf(recipeDTO.getType().toUpperCase()));
        recipe.setIngredient(recipeDTO.getIngredient().stream().map(ingredientDTO -> {
            Ingredient quantity = new Ingredient();
            quantity.setUnit(ingredientDTO.getUnit());
            quantity.setQuantity(ingredientDTO.getQuantity());
            quantity.setName(ingredientDTO.getName().toLowerCase());
            quantity.setVariation(ingredientDTO.getVariation());
            return quantity;
        }).collect(Collectors.toList()));
        return recipe;
    }

    private RecipeDTO toDTO(Recipe recipe) {
        RecipeDTO recipeDTO = new RecipeDTO();
        recipeDTO.setInstructions(recipe.getInstructions());
        recipeDTO.setName(recipe.getName());
        recipeDTO.setId(recipe.getId());
        recipeDTO.setServingSize(recipe.getServingSize());
        recipeDTO.setNoOfServing(recipe.getNoOfServing());
        recipeDTO.setName(recipe.getName());
        recipeDTO.setType(String.valueOf(recipe.getType()));
        recipeDTO.setIngredient(recipe.getIngredient().stream().map(ingredient -> {
            IngredientDTO quantity = new IngredientDTO();
            quantity.setUnit(ingredient.getUnit());
            quantity.setQuantity(ingredient.getQuantity());
            quantity.setName(ingredient.getName());
            quantity.setVariation(ingredient.getVariation());
            return quantity;
        }).collect(Collectors.toList()));
        return recipeDTO;
    }
}
