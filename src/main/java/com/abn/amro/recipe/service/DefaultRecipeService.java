package com.abn.amro.recipe.service;

import com.abn.amro.recipe.dao.RecipeRepository;
import com.abn.amro.recipe.domain.Ingredient;
import com.abn.amro.recipe.domain.Recipe;
import com.abn.amro.recipe.domain.RecipeType;
import com.abn.amro.recipe.domain.spec.SearchOperation;
import com.abn.amro.recipe.domain.spec.SpecificationBuilder;
import com.abn.amro.recipe.resource.exception.ResourceNotFoundException;
import com.abn.amro.recipe.service.dto.IngredientDTO;
import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class DefaultRecipeService implements RecipeService{
    private static final Logger LOG = LoggerFactory.getLogger(DefaultRecipeService.class);

    private final RecipeRepository recipeRepository;

    public DefaultRecipeService(RecipeRepository recipeRepository) {
        this.recipeRepository = recipeRepository;
    }

    private final Pattern p = Pattern.compile("[^a-zA-Z0-9 ]");

    @Override
    public Set<RecipeDTO> searchRecipe(String searchString) {
        LOG.info("Searching recipes that meet search term {}", searchString);
        SpecificationBuilder builder = new SpecificationBuilder();
        String operationSetExpr = String.join("|", SearchOperation.SIMPLE_OPERATION_SET);
        String s = "(\\w+?)("+ operationSetExpr +")(\\p{Punct}?)(\\w+?)(\\p{Punct}?),";
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
        return recipeRepository.findAll(spec).stream().map(this::toDTO).collect(Collectors.toSet());
    }

    public RecipeDTO createRecipe(final RecipeDTO recipeDTO){
        checkPreconditions(recipeDTO);
        Recipe recipe = createEntity(recipeDTO);
        return toDTO(recipeRepository.save(recipe));
    }

    public RecipeDTO fineOneRecipe(Long id){
        return toDTO(recipeRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Recipe with id "+ id + " does not exist.")));
    }

    public List<RecipeDTO> findAllRecipe(Pageable pageable){
        LOG.info("Getting paged recipes response. Page {}, size {}", pageable.getPageNumber(), pageable.getPageSize());
        List<RecipeDTO> recipes = recipeRepository.findAll(pageable).stream().map(this::toDTO).collect(Collectors.toList());

        if(recipes.isEmpty()) throw new ResourceNotFoundException("Recipes not found");

        return recipes;
    }

    protected void checkPreconditions(RecipeDTO recipeDTO) {
        LOG.info("Checking preconditions for creating recipe");
        if(p.matcher(recipeDTO.getName()).find() || recipeDTO.getName().isEmpty()) throw new IllegalArgumentException("Recipe name should not be empty or contain illegal characters");
        if(recipeDTO.getNoOfServing() < 0 || recipeDTO.getServingSize() < 0) throw new IllegalArgumentException("Number of serving and serving size should not be less than 0.");

        try{
            RecipeType.valueOf(recipeDTO.getType().toUpperCase());
        }catch (Exception exception){
            throw new IllegalArgumentException("Choose a recipe type of VEGAN, VEGETARIAN, or NORMAL");
        }

        recipeDTO.getIngredient().forEach(ingredient -> {
            if(ingredient.getName().isEmpty() || p.matcher(ingredient.getName()).find()) throw new IllegalArgumentException("ingredient name should not be empty or contain illegal characters");
            if(ingredient.getQuantity() < 0) throw  new IllegalArgumentException("Ingredient quantity should not be less than 0");
        });
    }

    protected Recipe createEntity(RecipeDTO recipeDTO) {
        Recipe recipe = new Recipe();
        recipe.setInstructions(recipeDTO.getInstructions());
        recipe.setName(recipeDTO.getName());
        recipe.setServingSize(recipeDTO.getServingSize());
        recipe.setNoOfServing(recipeDTO.getNoOfServing());
        recipe.setName(recipeDTO.getName());
        recipe.setType(RecipeType.valueOf(recipeDTO.getType().toUpperCase()));
        recipe.setIngredient(recipeDTO.getIngredient().stream().map(ingredientDTO -> {
            Ingredient quantity = new Ingredient();
            quantity.setUnit(ingredientDTO.getUnit());
            quantity.setQuantity(ingredientDTO.getQuantity());
            quantity.setName(ingredientDTO.getName());
            quantity.setVariation(ingredientDTO.getVariation());
            return quantity;
        }).collect(Collectors.toList()));
        return recipe;
    }

    protected RecipeDTO toDTO(Recipe recipe) {
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
