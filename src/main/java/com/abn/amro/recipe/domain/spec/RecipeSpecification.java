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
    public Predicate toPredicate(Root<Recipe> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        boolean isIngredient = false;
        Join<Recipe, Ingredient> ingredientJoin = root.join("ingredient");
        String ingredientProperty = "";
        if(criteria.getKey().toLowerCase().contains("ingredient_")){
            isIngredient = true;
            ingredientProperty = criteria.getKey().split("_")[1];
        }

        switch (criteria.getOperation()) {
            case EQUALITY:
                return criteriaBuilder.equal(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), criteria.getValue());
            case NEGATION:
                return criteriaBuilder.notEqual(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), criteria.getValue());
            case GREATER_THAN:
                return criteriaBuilder.greaterThan(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), criteria.getValue().toString());
            case LESS_THAN:
                return criteriaBuilder.lessThan(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), criteria.getValue().toString());
            case LIKE:
                return criteriaBuilder.like(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), criteria.getValue().toString());
            case STARTS_WITH:
                return criteriaBuilder.like(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), criteria.getValue() + "%");
            case ENDS_WITH:
                return criteriaBuilder.like(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), "%" + criteria.getValue());
            case CONTAINS:
                return criteriaBuilder.like(isIngredient ? ingredientJoin.get(ingredientProperty) : root.get(criteria.getKey()), "%" + criteria.getValue() + "%");
            default:
                return null;
        }
    }
}
