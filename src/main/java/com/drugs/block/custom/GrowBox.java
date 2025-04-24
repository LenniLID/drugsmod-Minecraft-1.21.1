package com.drugs.block.custom;

import com.drugs.block.entity.custom.GrowBoxBlockEntity;
import com.drugs.item.ModItems;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.server.world.ServerWorld;
import net.minecraft.sound.SoundCategory;
import net.minecraft.sound.SoundEvents;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.DirectionProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.ActionResult;
import net.minecraft.util.Hand;
import net.minecraft.util.ItemScatterer;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;
import net.minecraft.item.Items;
import net.minecraft.util.math.random.Random;

public class GrowBox extends BlockWithEntity implements BlockEntityProvider {
    private static final VoxelShape SHAPE = Block.createCuboidShape(2, 0, 2, 14, 13, 14);
    public static final MapCodec<GrowBox> CODEC = createCodec(GrowBox::new);

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty STAGE = IntProperty.of("stage", 0, 3);

    public GrowBox(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(STAGE, 0));
    }

    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    @Override
    protected BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrowBoxBlockEntity(pos, state);
    }

    @Override
    protected void onStateReplaced(BlockState oldState, World world, BlockPos pos,
                                   BlockState newState, boolean moved) {
        if (oldState.getBlock() != newState.getBlock() &&
                world.getBlockEntity(pos) instanceof GrowBoxBlockEntity be) {
            ItemScatterer.spawn(world, pos, be);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(oldState, world, pos, newState, moved);
    }

    @Override
    protected ActionResult onUse(BlockState state,
                                 World world,
                                 BlockPos pos,
                                 PlayerEntity player,
                                 BlockHitResult hit) {
        if (!(world instanceof ServerWorld)) return ActionResult.PASS;
        GrowBoxBlockEntity be = (GrowBoxBlockEntity) world.getBlockEntity(pos);
        if (be == null) return ActionResult.PASS;

        ItemStack handStack = player.getStackInHand(Hand.MAIN_HAND);

        // 1) Harvest with shears
        if (state.get(STAGE) == 3 && handStack.isOf(Items.SHEARS)) {
            int dropCount = 3 + world.getRandom().nextInt(3);
            DefaultedList<ItemStack> drops = DefaultedList.of();
            for(int i = 0; i < dropCount; i++) {
                drops.add(new ItemStack(ModItems.WILD_WEED));
            }

            // 1 Block über dem Block
            BlockPos dropPos = pos.up(); // <-- Wichtige Änderung
            drops.forEach(stack -> {
                ItemEntity itemEntity = new ItemEntity(world,
                        dropPos.getX() + 0.5,  // Mitte X
                        dropPos.getY() + 0,  // Mitte Y
                        dropPos.getZ() + 0.5,  // Mitte Z
                        stack
                );
                world.spawnEntity(itemEntity);
            });

            be.clear();
            world.setBlockState(pos, state.with(STAGE, 0), Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        // 2) Insert seed
        if (be.isEmpty() && handStack.getItem() == ModItems.WILD_CANNABIS_SEEDS) {
            be.setStack(0, new ItemStack(ModItems.WILD_CANNABIS_SEEDS, 1));
            world.setBlockState(pos, state.with(STAGE, 1), Block.NOTIFY_ALL); // Start at stage 1
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1.5f);
            ((WorldAccess) world).scheduleBlockTick(pos, this, 10);
            handStack.decrement(1);
            return ActionResult.SUCCESS;
        }

        // 3) Take item
        if (handStack.isEmpty() && !player.isSneaking()) {
            ItemStack onBox = be.getStack(0);
            player.setStackInHand(Hand.MAIN_HAND, onBox);
            world.playSound(player, pos, SoundEvents.ENTITY_ITEM_PICKUP, SoundCategory.BLOCKS, 1f, 1f);

            // Clear and reset
            be.clear();
            world.setBlockState(pos, state.with(STAGE, 0), Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    protected void scheduledTick(BlockState state,
                                 ServerWorld world,
                                 BlockPos pos,
                                 Random random) {
        int stage = state.get(STAGE);
        BlockEntity blockEntity = world.getBlockEntity(pos);

        // Only grow if seed present
        if (blockEntity instanceof GrowBoxBlockEntity be && !be.isEmpty()) {
            if (stage < 3) {
                world.setBlockState(pos, state.with(STAGE, stage + 1), Block.NOTIFY_ALL);
                if (stage + 1 < 3) {
                    ((WorldAccess) world).scheduleBlockTick(pos, this, 100);
                }
            }
        } else {
            // No seed - reset
            world.setBlockState(pos, state.with(STAGE, 0), Block.NOTIFY_ALL);
        }
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(STAGE, 0);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STAGE);
    }
}