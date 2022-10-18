package com.abn.amro.recipe.domain.spec;

import com.abn.amro.recipe.domain.Ingredient;
import com.abn.amro.recipe.domain.Recipe;
import lombok.Data;
import org.springframework.data.jpa.domain.Specification;

import javax.persistence.criteria.*;
import java.util.List;

@Data
public class RecipeSpecification implements Specification<Recipe> {
    private SearchCriteria criteria;

    public RecipeSpecification(SearchCriteria criteria) {
        this.criteria = criteria;
    }

    @Override
    public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
        boolean isIngredient = false;
        Join<Recipe, Ingredient> ingredientJoin = root.join("ingredient");
        String ingredientProperty = "";
        if(criteria.getKey().toLowerCase().contains("ingredient_")){
            isIngredient = true;
            ingredientProperty = criteria.getKey().split("_")[1];
        }

        switch (criteria.getOperation()) {
            case EQUALITY:
                return cb.equal(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), criteria.getValue());
            case NEGATION:
                return cb.notEqual(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), criteria.getValue());
            case GREATER_THAN:
                return cb.greaterThan(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), criteria.getValue().toString());
            case LESS_THAN:
                return cb.lessThan(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), criteria.getValue().toString());
            case LIKE:
                return cb.like(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), criteria.getValue().toString());
            case STARTS_WITH:
                return cb.like(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), criteria.getValue() + "%");
            case ENDS_WITH:
                return cb.like(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), "%" + criteria.getValue());
            case CONTAINS:
                return cb.like(isIngredient ? cb.lower(ingredientJoin.get(ingredientProperty)) : cb.lower(root.get(criteria.getKey())), "%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }
}
