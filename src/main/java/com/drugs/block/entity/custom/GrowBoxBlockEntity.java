// src/main/java/com/drugs/block/entity/custom/GrowBoxBlockEntity.java
package com.drugs.block.entity.custom;

import com.drugs.block.entity.ImplementedInventory;
import com.drugs.block.entity.ModBlockEntities;
import com.drugs.item.ModItems;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.inventory.Inventories;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.network.listener.ClientPlayPacketListener;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.s2c.play.BlockEntityUpdateS2CPacket;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.Map;

public class GrowBoxBlockEntity extends BlockEntity implements ImplementedInventory {
    // --- static seed→crop mapping ---
    private static final Map<Item, Item> SEED_TO_CROP = new HashMap<>();

    static {
        SEED_TO_CROP.put(ModItems.WILD_CANNABIS_SEEDS, ModItems.WILD_WEED);
        SEED_TO_CROP.put(ModItems.CALLY_CANNABIS_SEEDS,       ModItems.CALLY_WEED);
        SEED_TO_CROP.put(ModItems.SOUR_DIESEL_CANNABIS_SEEDS, ModItems.SOUR_DIESEL_WEED);
        SEED_TO_CROP.put(ModItems.GRANDDADDY_PURPLE_CANNABIS_SEEDS,       ModItems.GRANDDADDY_PURPLE_WEED);
    }

    /** Returns true if the given item is a valid seed for this GrowBox. */
    public static boolean isValidSeed(Item item) {
        return SEED_TO_CROP.containsKey(item);
    }

    /** Returns the crop Item corresponding to the planted seed. */
    public static Item getCropFor(Item seed) {
        return SEED_TO_CROP.getOrDefault(seed, ItemStack.EMPTY.getItem());
    }

    private final DefaultedList<ItemStack> inventory =
            DefaultedList.ofSize(1, ItemStack.EMPTY);

    public GrowBoxBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.GROW_BOX_BE, pos, state);
    }

    @Override
    public DefaultedList<ItemStack> getItems() {
        return inventory;
    }

    // NBT serialization (for world save & client sync)
    @Override
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.writeNbt(nbt, registryLookup);
        Inventories.writeNbt(nbt, inventory, registryLookup);
    }

    @Override
    protected void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        super.readNbt(nbt, registryLookup);
        Inventories.readNbt(nbt, inventory, registryLookup);
    }

    // Mark dirty and send update packet to client when necessary
    @Nullable
    @Override
    public Packet<ClientPlayPacketListener> toUpdatePacket() {
        return BlockEntityUpdateS2CPacket.create(this);
    }

    @Override
    public NbtCompound toInitialChunkDataNbt(RegistryWrapper.WrapperLookup registryLookup) {
        return createNbt(registryLookup);
    }

    // Convenience
    public boolean isEmpty() {
        return inventory.get(0).isEmpty();
    }

    public void clear() {
        inventory.set(0, ItemStack.EMPTY);
        markDirty();
    }

    public ItemStack getSeedStack() {
        return inventory.get(0);
    }

    public void setSeedStack(ItemStack stack) {
        inventory.set(0, stack);
        markDirty();
    }
}
