package com.abn.amro.recipe.domain.spec;

import com.abn.amro.recipe.domain.RecipeType;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class SpecificationBuilderTest {
    private SpecificationBuilder sb = new SpecificationBuilder();

    @Test
    void specificationBuilder_shouldThrowException_onInvalidSearchKey() {
        assertThrows(IllegalArgumentException.class,() -> sb.with("key", ":", "value", "", ""));
        assertThrows(IllegalArgumentException.class,() -> sb.with("name", "operation", "value", "", ""));
    }

    @Test
    void specificationBuilder_shouldHaveValidCriteria_whenCorrectInputsAreSupplied() {
        sb.with("name", ":", "value", "", "");
        assertEquals(SearchOperation.EQUALITY, sb.getParams().get(0).getOperation());
        assertEquals("name", sb.getParams().get(0).getKey());
        assertEquals("value", sb.getParams().get(0).getValue());

        sb.with("servingSize", ">", "3", "*", "");
        assertEquals(SearchOperation.GREATER_THAN, sb.getParams().get(0).getOperation());
        assertEquals("servingSize", sb.getParams().get(0).getKey());
        assertEquals("3", sb.getParams().get(0).getValue());

        sb.with("noOfServing", "!", "10", "", "*");
        assertEquals(SearchOperation.NEGATION, sb.getParams().get(0).getOperation());
        assertEquals("noOfServing", sb.getParams().get(0).getKey());
        assertEquals("10", sb.getParams().get(0).getValue());
    }

    @Test
    void specificationBuilder_shouldChangeOperation_whenPrefixAndSuffixAreIncluded_andOperationIsEquality() {
        sb.with("name", ":", "value", "*", "*");
        assertEquals(SearchOperation.CONTAINS, sb.getParams().get(0).getOperation());

        sb.with("name", ":", "value", "*", "");
        assertEquals(SearchOperation.ENDS_WITH, sb.getParams().get(0).getOperation());

        sb.with("name", ":", "value", "", "*");
        assertEquals(SearchOperation.STARTS_WITH, sb.getParams().get(0).getOperation());
    }

    @Test
    void specificationBuilder_shouldBehaveAsExpected_whenKeyIsType() {
        assertThrows(IllegalArgumentException.class,() -> sb.with("type", ":", "NOTHING", "", ""));

        sb.with("type", ":", "NORMAL", "*", "");
        assertEquals(SearchOperation.EQUALITY, sb.getParams().get(0).getOperation());
        assertEquals(RecipeType.NORMAL, sb.getParams().get(0).getValue());
    }
}