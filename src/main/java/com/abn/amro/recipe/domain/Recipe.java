package com.abn.amro.recipe.domain;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
public class Recipe {
    @Id
    @GeneratedValue(
            strategy = GenerationType.SEQUENCE,
            generator = "recipe_seq"
    )
    @SequenceGenerator(
            name = "recipe_seq",
            allocationSize = 5
    )
    private Long id;

    private String name;

    @OneToMany(cascade = {CascadeType.PERSIST}, orphanRemoval = true, targetEntity=Ingredient.class)
    private List<Ingredient> ingredient;

    private int noOfServing;

    private int servingSize;

    @Enumerated(EnumType.STRING)
    private RecipeType type;

    private String instructions;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Ingredient> getIngredient() {
        return ingredient;
    }

    public void setIngredient(List<Ingredient> ingredient) {
        this.ingredient = ingredient;
    }

    public int getNoOfServing() {
        return noOfServing;
    }

    public void setNoOfServing(int noOfServing) {
        this.noOfServing = noOfServing;
    }

    public int getServingSize() {
        return servingSize;
    }

    public void setServingSize(int servingSize) {
        this.servingSize = servingSize;
    }

    public RecipeType getType() {
        return type;
    }

    public void setType(RecipeType type) {
        this.type = type;
    }

    public String getInstructions() {
        return instructions;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Recipe recipe = (Recipe) o;

        return !id.equals(recipe.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "Recipe{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", ingredient=" + ingredient +
                ", noOfServing=" + noOfServing +
                ", servingSize=" + servingSize +
                ", type=" + type +
                ", instructions=" + instructions +
                '}';
    }
}
