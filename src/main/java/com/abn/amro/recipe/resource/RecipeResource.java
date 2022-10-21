package com.abn.amro.recipe.resource;

import com.abn.amro.recipe.service.RecipeService;
import com.abn.amro.recipe.service.dto.IngredientDTO;
import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
public class RecipeResource {
    private static final Logger LOG = LoggerFactory.getLogger(RecipeResource.class);

    private final RecipeService recipeService;

    public RecipeResource(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> find(@RequestParam("search_term") String search){
        LOG.info("Receive REST request to fetch recipe by search String {}", search);
        return ResponseEntity.ok(recipeService.searchRecipe(search));
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "1", required = false) String page,
                                     @RequestParam(defaultValue = "5", required = false) String size){
        LOG.info("Receive REST request to fetch paged recipe response of page {} and size {}", page, size);
        int pageNumber;
        int pageSize;
        try{
            pageNumber = Integer.parseInt(page);
            pageSize = Integer.parseInt(size);
        }catch (Exception ex){
            throw new IllegalArgumentException("One or more Invalid parameter for page" + page + " or size "+ size);
        }
        return ResponseEntity.ok(recipeService.findAllRecipe(PageRequest.of(--pageNumber, pageSize)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RecipeDTO recipeDTO){
        LOG.info("Receive REST request to create recipe {}", recipeDTO);
        RecipeDTO savedRecipe = recipeService.createRecipe(recipeDTO);
        return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneRecipe(@PathVariable("id") String id){
        LOG.info("Receive REST request to fetch recipe of id {}", id);
        long recipeId;
        try{
            recipeId = Long.parseLong(id);
        }catch (Exception ex){
            throw new IllegalArgumentException("Invalid id parameter " + id);
        }
        return ResponseEntity.ok(recipeService.fineOneRecipe(recipeId));
    }

    @PutMapping("/{id}")
    public ResponseEntity<?> updateRecipe(@PathVariable("id") String id, @RequestBody RecipeDTO recipeDTO) {
        LOG.info("Receive REST request to update recipe of id {} with object {}", id, recipeDTO);
        long recipeId;
        try{
            recipeId = Long.parseLong(id);
        }catch (Exception ex){
            throw new IllegalArgumentException("Invalid id parameter " + id);
        }
        return new ResponseEntity<>(recipeService.updateRecipe(recipeId, recipeDTO), HttpStatus.OK);
    }

    @PutMapping("/{id}/ingredient/{IngredientId}")
    public ResponseEntity<?> updateRecipeIngredient(@PathVariable("id") String recipeId, @PathVariable("IngredientId") String ingredientId, @RequestBody IngredientDTO ingredientDTO) {
        LOG.info("Receive REST request to update ingredient {} for recipe of id {} with object {}",ingredientId, recipeId, ingredientDTO);
        long recipeID;
        long ingredientID;
        try{
            recipeID = Long.parseLong(recipeId);
            ingredientID = Long.parseLong(ingredientId);
        }catch (Exception ex){
            throw new IllegalArgumentException("One or more Invalid parameter for recipeId" + recipeId + " or ingredientId "+ ingredientId);
        }
        return new ResponseEntity<>(recipeService.updateRecipeIngredient(recipeID, ingredientID, ingredientDTO), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteRecipe(@PathVariable("id") String id) {
        LOG.info("Receive REST request to delete recipe of id {}", id);
        long recipeId;
        try{
            recipeId = Long.parseLong(id);
        }catch (Exception ex){
            throw new IllegalArgumentException("Invalid id parameter " + id);
        }
        return new ResponseEntity<>(recipeService.deleteRecipe(recipeId), HttpStatus.OK);
    }
}
