package com.abn.amro.recipe.domain;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import java.util.Objects;

@Entity
public class IngredientQuantity {
    @Id
    private Long id;

    public IngredientQuantity() {

    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getId() {
        return id;
    }

    public static enum QuantityUnit {
        PIECE, MILLILITRE, GRAMME
    }

    @ManyToOne
    private Ingredient ingredient;
    private QuantityUnit unit;
    private double quantity;

    public IngredientQuantity(Ingredient ingredient, QuantityUnit unit,
                              double quantity) {
        this.ingredient = Objects.requireNonNull(ingredient);
        this.unit = Objects.requireNonNull(unit);
        this.quantity = quantity;
        if (quantity < 0) {
            throw new IllegalArgumentException("Quantity cannot be negative.");
        }
    }

    public Ingredient getIngredient() {
        return ingredient;
    }

    public void setIngredient(Ingredient ingredient) {
        this.ingredient = ingredient;
    }

    public QuantityUnit getUnit() {
        return unit;
    }

    public void setUnit(QuantityUnit unit) {
        this.unit = unit;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        IngredientQuantity that = (IngredientQuantity) o;

        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }

    @Override
    public String toString() {
        return "IngredientQuantity{" +
                "id=" + id +
                ", ingredient=" + ingredient +
                ", unit=" + unit +
                ", quantity=" + quantity +
                '}';
    }
}
