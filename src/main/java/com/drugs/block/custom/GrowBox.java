package com.drugs.block.custom;

import com.drugs.block.entity.custom.GrowBoxBlockEntity;
import com.mojang.serialization.MapCodec;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockWithEntity;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.entity.ItemEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.item.Item;
import net.minecraft.item.ItemPlacementContext;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
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
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.util.math.random.Random;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import net.minecraft.world.WorldAccess;
import org.jetbrains.annotations.Nullable;

public class GrowBox extends BlockWithEntity implements BlockEntityProvider {

    // 1) Required for BlockWithEntity in 1.21.1
    public static final MapCodec<GrowBox> CODEC = createCodec(GrowBox::new);
    @Override
    protected MapCodec<? extends BlockWithEntity> getCodec() {
        return CODEC;
    }

    public static final DirectionProperty FACING = Properties.HORIZONTAL_FACING;
    public static final IntProperty STAGE = IntProperty.of("stage", 0, 3);

    public GrowBox(Settings settings) {
        super(settings);
        this.setDefaultState(this.stateManager.getDefaultState()
                .with(FACING, Direction.NORTH)
                .with(STAGE, 0));
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Nullable
    @Override
    public BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new GrowBoxBlockEntity(pos, state);
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(FACING, STAGE);
    }

    @Nullable
    @Override
    public BlockState getPlacementState(ItemPlacementContext ctx) {
        return this.getDefaultState()
                .with(FACING, ctx.getHorizontalPlayerFacing().getOpposite())
                .with(STAGE, 0);
    }

    @Override
    public void onStateReplaced(BlockState oldState, World world,
                                BlockPos pos, BlockState newState,
                                boolean moved) {
        if (!oldState.isOf(newState.getBlock())
                && world.getBlockEntity(pos) instanceof GrowBoxBlockEntity be) {
            ItemScatterer.spawn(world, pos, be);
            world.updateComparators(pos, this);
        }
        super.onStateReplaced(oldState, world, pos, newState, moved);
    }

    @Override
    public ActionResult onUse(BlockState state, World world,
                              BlockPos pos, PlayerEntity player,
                               BlockHitResult hit) {
        if (!(world instanceof ServerWorld)) return ActionResult.PASS;
        if (!(world.getBlockEntity(pos) instanceof GrowBoxBlockEntity be))
            return ActionResult.PASS;

        ItemStack held = player.getStackInHand(Hand.MAIN_HAND);

        // --- Harvest with shears ---
        if (state.get(STAGE) == 3 && held.isOf(Items.SHEARS)) {
            ItemStack seedStack = be.getSeedStack();
            Item crop = GrowBoxBlockEntity.getCropFor(seedStack.getItem());
            int count = 3 + world.getRandom().nextInt(3);
            BlockPos dropPos = pos.up();

            for (int i = 0; i < count; i++) {
                ItemEntity drop = new ItemEntity(world,
                        dropPos.getX() + 0.5,
                        dropPos.getY(),
                        dropPos.getZ() + 0.5,
                        new ItemStack(crop, 1));
                world.spawnEntity(drop);
            }

            be.clear();
            world.setBlockState(pos, state.with(STAGE, 0),
                    Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        // --- Plant any valid seed ---
        if (be.isEmpty()
                && GrowBoxBlockEntity.isValidSeed(held.getItem())) {
            be.setSeedStack(new ItemStack(held.getItem(), 1));
            world.setBlockState(pos, state.with(STAGE, 1),
                    Block.NOTIFY_ALL);
            world.playSound(null, pos,
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.BLOCKS, 1f, 1.5f);
            ((WorldAccess)world).scheduleBlockTick(pos, this, 10);
            held.decrement(1);
            return ActionResult.SUCCESS;
        }

        // --- Pick the seed back up ---
        if (held.isEmpty() && !player.isSneaking() && !be.isEmpty()) {
            ItemStack seed = be.getSeedStack();
            player.setStackInHand(Hand.MAIN_HAND, seed);
            world.playSound(null, pos,
                    SoundEvents.ENTITY_ITEM_PICKUP,
                    SoundCategory.BLOCKS, 1f, 1f);
            be.clear();
            world.setBlockState(pos, state.with(STAGE, 0),
                    Block.NOTIFY_ALL);
            return ActionResult.SUCCESS;
        }

        return ActionResult.PASS;
    }

    @Override
    public void scheduledTick(BlockState state, ServerWorld world,
                              BlockPos pos, Random random) {
        BlockEntity raw = world.getBlockEntity(pos);
        if (!(raw instanceof GrowBoxBlockEntity be)) return;

        int stage = state.get(STAGE);
        if (!be.isEmpty()) {
            if (stage < 3) {
                world.setBlockState(pos,
                        state.with(STAGE, stage + 1),
                        Block.NOTIFY_ALL);
                if (stage + 1 < 3) {
                    ((WorldAccess)world).scheduleBlockTick(pos, this, 100);
                }
            }
        } else {
            // reset if no seed
            world.setBlockState(pos,
                    state.with(STAGE, 0),
                    Block.NOTIFY_ALL);
        }
    }
}
