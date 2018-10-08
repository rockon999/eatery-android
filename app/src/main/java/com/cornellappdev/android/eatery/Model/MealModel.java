package com.cornellappdev.android.eatery.Model;

import android.support.annotation.NonNull;
import org.threeten.bp.LocalDateTime;

import java.io.Serializable;
import java.util.List;

/**
 * Model class to represent each meal
 */

public class MealModel extends Interval implements Serializable {
    private MealMenuModel menu;
    private MealType type;

    MealModel(@NonNull LocalDateTime start, @NonNull LocalDateTime end) {
        super(start, end);
    }

    // Keep it for now for testing. Get rid of it later
    public String mealToString() {
        StringBuilder info = new StringBuilder(
                String.format("%s from: %s to: %s\n", type, getStart(), getEnd()));

        for (String category : menu.getCategories()) {
            List<String> value = menu.getItems(category);
            info.append(String.format(" %s: %s\n", category, value.toString()));
        }

        return info.toString();
    }

    public MealMenuModel getMenu() {
        return menu;
    }

    public void setMenu(MealMenuModel menu) {
        this.menu = menu;
    }

    public MealType getType() {
        return type;
    }

    public void setType(MealType type) {
        this.type = type;
    }
}
