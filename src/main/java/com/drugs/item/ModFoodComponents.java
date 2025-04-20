package com.drugs.item;

import net.minecraft.component.type.FoodComponent;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.entity.effect.StatusEffects;

public class ModFoodComponents {
    public static final FoodComponent joint = new FoodComponent.Builder()
            .statusEffect(new StatusEffectInstance(StatusEffects.HUNGER, 600, 15), 1f)
            .statusEffect(new StatusEffectInstance(StatusEffects.NAUSEA, 200), 0.5f)
            .alwaysEdible()
            .build();
}
