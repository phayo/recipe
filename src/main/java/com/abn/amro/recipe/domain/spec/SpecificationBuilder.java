package com.abn.amro.recipe.domain.spec;

import com.abn.amro.recipe.domain.Recipe;
import com.abn.amro.recipe.domain.RecipeType;
import org.springframework.data.jpa.domain.Specification;

import java.util.ArrayList;
import java.util.List;

public class SpecificationBuilder {
    private List<SearchCriteria> params;
    private static final List<String> VALID_SEARCH_TERMS = List.of("type", "name", "noOfServing", "servingSize", "instructions", "ingredient_name", "ingredient_quantity", "ingredient_variation", "ingredient_unit");

    public SpecificationBuilder with(String key, String operation, Object value, String prefix, String suffix) {
        if(!VALID_SEARCH_TERMS.contains(key)){
            throw new IllegalArgumentException("One or more search term incorrect, review supplied searchTerm.");
        }
        params = new ArrayList<>();

        SearchOperation op = SearchOperation.getSimpleOperation(operation.charAt(0));
        if (op != null) {
            if (op == SearchOperation.EQUALITY) {
                boolean startWithAsterisk = prefix.contains("*");
                boolean endWithAsterisk = suffix.contains("*");

                if (startWithAsterisk && endWithAsterisk) {
                    op = SearchOperation.CONTAINS;
                } else if (startWithAsterisk) {
                    op = SearchOperation.ENDS_WITH;
                } else if (endWithAsterisk) {
                    op = SearchOperation.STARTS_WITH;
                }
            }

            if("type".equalsIgnoreCase(key)){
                try{
                    RecipeType.valueOf(((String) value).toUpperCase());
                }catch (Exception e){
                    throw new IllegalArgumentException("Choose a recipe type of VEGAN, VEGETARIAN, or NORMAL");
                }
                op = SearchOperation.EQUALITY;
                value = RecipeType.valueOf(String.valueOf(value).toUpperCase());
            }
            params.add(new SearchCriteria(key, op, value));
        }
        return this;
    }

    public Specification<Recipe> build() {
        if (params.size() == 0) {
            return null;
        }

        Specification<Recipe> result = new RecipeSpecification(params.get(0));

        for (int i = 1; i < params.size(); i++) {
            result = Specification.where(result).and(new RecipeSpecification(params.get(i)));
        }

        return result;
    }
}
