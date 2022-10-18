package com.abn.amro.recipe.resource;

import com.abn.amro.recipe.service.RecipeService;
import com.abn.amro.recipe.service.dto.RecipeDTO;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/recipe")
public class RecipeResource {

    private final RecipeService recipeService;

    public RecipeResource(RecipeService recipeService) {
        this.recipeService = recipeService;
    }

    @GetMapping("/search")
    public ResponseEntity<?> find(@RequestParam("search_term") String search){
        return ResponseEntity.ok(recipeService.searchRecipe(search));
    }

    @GetMapping
    public ResponseEntity<?> findAll(@RequestParam(defaultValue = "1", required = false) int page,
                                     @RequestParam(defaultValue = "5", required = false) int size){

        return ResponseEntity.ok(recipeService.findAllRecipe(PageRequest.of(--page, size)));
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody RecipeDTO recipeDTO){
        RecipeDTO savedRecipe = recipeService.createRecipe(recipeDTO);
        return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getOneRecipe(@PathVariable("id") Long id){
        return ResponseEntity.ok(recipeService.fineOneRecipe(id));
    }
}
