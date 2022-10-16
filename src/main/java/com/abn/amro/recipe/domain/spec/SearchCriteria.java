package com.abn.amro.recipe.domain.spec;

import com.abn.amro.recipe.domain.RecipeType;
import lombok.Data;

@Data
public class SearchCriteria {
    private String key;
    private SearchOperation operation;
    private Object value;

    public SearchCriteria(String key, SearchOperation operation, Object value) {

        this.key = key;
        this.operation = operation;
        this.value = value;
    }
}
