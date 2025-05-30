package com.drugs.block.entity;

import com.drugs.Drugsmod;
import com.drugs.block.ModBlocks;
import com.drugs.block.entity.custom.GrowBoxBlockEntity;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static final BlockEntityType<GrowBoxBlockEntity> GROW_BOX_BE =
            Registry.register(Registries.BLOCK_ENTITY_TYPE, Identifier.of(Drugsmod.MOD_ID, "grow_box_be"),
                    BlockEntityType.Builder.create(GrowBoxBlockEntity::new, ModBlocks.GROW_BOX).build(null));



    public static void registerBlockEntities() {
        Drugsmod.LOGGER.info("Registering Block Entities for " + Drugsmod.MOD_ID);
    }
}