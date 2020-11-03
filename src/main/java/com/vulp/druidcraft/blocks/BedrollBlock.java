package com.vulp.druidcraft.blocks;

import com.vulp.druidcraft.Druidcraft;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.entity.Entity;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.fluid.Fluid;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.item.DyeColor;
import net.minecraft.item.ItemStack;
import net.minecraft.state.BooleanProperty;
import net.minecraft.state.StateContainer;
import net.minecraft.state.properties.BedPart;
import net.minecraft.state.properties.BlockStateProperties;
import net.minecraft.util.ActionResultType;
import net.minecraft.util.DamageSource;
import net.minecraft.util.Direction;
import net.minecraft.util.Hand;
import net.minecraft.util.math.*;
import net.minecraft.util.math.shapes.ISelectionContext;
import net.minecraft.util.math.shapes.VoxelShape;
import net.minecraft.util.math.shapes.VoxelShapes;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.*;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;

import javax.annotation.Nullable;
import java.util.List;
import java.util.Random;

public class BedrollBlock extends BedBlock implements IBucketPickupHandler, ILiquidContainer {
    private final DyeColor color;
    public static final BooleanProperty WATERLOGGED = BlockStateProperties.WATERLOGGED;

    public BedrollBlock(DyeColor colorIn, Properties properties) {
        super(colorIn, properties);
        this.color = colorIn;
        this.setDefaultState(this.stateContainer.getBaseState().with(PART, BedPart.FOOT).with(OCCUPIED, false).with(WATERLOGGED, false));
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(HORIZONTAL_FACING, PART, OCCUPIED, WATERLOGGED);
    }

    public static boolean doesBedWork(World world) {
        return world.getDimensionType().doesBedWork();
    }

    public ActionResultType onBlockActivated(BlockState state, World worldIn, BlockPos pos, PlayerEntity player, Hand handIn, BlockRayTraceResult hit) {
        if (worldIn.isRemote) {
            return ActionResultType.CONSUME;
        } else {
            if (state.get(PART) != BedPart.HEAD) {
                pos = pos.offset(state.get(HORIZONTAL_FACING));
                state = worldIn.getBlockState(pos);
                if (!state.isIn(this)) {
                    return ActionResultType.CONSUME;
                }
            }

            if (!doesBedWork(worldIn)) {
                worldIn.removeBlock(pos, false);
                BlockPos blockpos = pos.offset(state.get(HORIZONTAL_FACING).getOpposite());
                if (worldIn.getBlockState(blockpos).isIn(this)) {
                    worldIn.removeBlock(blockpos, false);
                }

                worldIn.createExplosion((Entity)null, DamageSource.func_233546_a_(), (ExplosionContext) null, (double)pos.getX() + 0.5D, (double)pos.getY() + 0.5D, (double)pos.getZ() + 0.5D, 5.0F, true, Explosion.Mode.DESTROY);
                return ActionResultType.SUCCESS;
            } else if (state.get(OCCUPIED)) {
                player.sendStatusMessage(new TranslationTextComponent("block.druidcraft.bedroll.occupied"), true);
                return ActionResultType.SUCCESS;
            } else {
                player.trySleep(pos).ifLeft((result) -> {
                    if (result != null) {
                        player.sendStatusMessage(result.getMessage(), true);
                    }

                });
                return ActionResultType.SUCCESS;
            }
        }
    }

    @Override
    public Fluid pickupFluid(IWorld worldIn, BlockPos pos, BlockState state) {
        if (state.get(WATERLOGGED)) {
            worldIn.setBlockState(pos, state.with(WATERLOGGED, false), 3);
            return Fluids.WATER;
        } else {
            return Fluids.EMPTY;
        }
    }

    @Override
    @SuppressWarnings("deprecation")
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStillFluidState(false) : super.getFluidState(state);
    }

    @Override
    public boolean canContainFluid(IBlockReader worldIn, BlockPos pos, BlockState state, Fluid fluidIn) {
        return fluidIn == Fluids.WATER;
    }

    @Override
    public boolean receiveFluid(IWorld worldIn, BlockPos pos, BlockState state, FluidState fluidStateIn) {
        if (fluidStateIn.getFluid() == Fluids.WATER) {
            if (!worldIn.isRemote()) {
                worldIn.setBlockState(pos, state.with(WATERLOGGED, true), 3);
                worldIn.getPendingFluidTicks().scheduleTick(pos, fluidStateIn.getFluid(), fluidStateIn.getFluid().getTickRate(worldIn));
            }
            return true;
        } else {
            return false;
        }
    }

    @Override
    public BlockState updatePostPlacement(BlockState stateIn, Direction facing, BlockState facingState, IWorld worldIn, BlockPos currentPos, BlockPos facingPos) {
        if (stateIn.get(WATERLOGGED)) {
            worldIn.getPendingFluidTicks().scheduleTick(currentPos, Fluids.WATER, Fluids.WATER.getTickRate(worldIn));
        }
        return super.updatePostPlacement(stateIn, facing, facingState, worldIn, currentPos, facingPos);
    }

    @Override
    public BlockState getStateForPlacement(BlockItemUseContext context) {
        boolean water = context.getWorld().getBlockState(context.getPos()).getBlock() == Blocks.WATER;
        BlockState state = super.getStateForPlacement(context);
        if (state == null) {
            return null;
        }
        return state.with(WATERLOGGED, water);
    }

    @Override
    public void onFallenUpon(World worldIn, BlockPos pos, Entity entityIn, float fallDistance) {
        super.onFallenUpon(worldIn, pos, entityIn, fallDistance);
    }

    @Override
    public void onLanded(IBlockReader worldIn, Entity entityIn) {
        entityIn.setMotion(entityIn.getMotion().mul(1.0D, 0.0D, 1.0D));
    }

    @OnlyIn(Dist.CLIENT)
    @Override
    public void addInformation(ItemStack stack, @Nullable IBlockReader worldIn, List<ITextComponent> tooltip, ITooltipFlag flagIn) {
        if (worldIn == null) return;
        if (!Screen.hasShiftDown()) {
            tooltip.add(new TranslationTextComponent("block.druidcraft.hold_shift").mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        } else {
            tooltip.add(new TranslationTextComponent("block.druidcraft.bedroll.description1").mergeStyle(TextFormatting.GRAY, TextFormatting.ITALIC));
        }
    }

    @Override
    public VoxelShape getShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        if (state.get(PART) == BedPart.FOOT) {
            Direction direction = state.get(HORIZONTAL_FACING);
                if (direction == Direction.NORTH || direction == Direction.SOUTH) {
                    return Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 2.0D, 16.0D);
                } else {
                    return Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 16.0D, 2.0D, 15.0D);
                }
            } else {
            Direction direction = state.get(HORIZONTAL_FACING).getOpposite();
            VoxelShape matNS = Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 2.0D, 16.0D);
            VoxelShape matEW = Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 16.0D, 2.0D, 15.0D);
            switch(direction) {
                case NORTH:
                    return VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(2.0D, 0.0D, 9.0D, 14.0D, 4.0D, 15.0D), Block.makeCuboidShape(0.0D, 0.0D, 2.0D, 16.0D, 3.0D, 8.0D)), matNS);
                case SOUTH:
                    return VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(2.0D, 2.0D, 1.0D, 14.0D, 4.0D, 7.0D), Block.makeCuboidShape(0.0D, 0.0D, 8.0D, 16.0D, 3.0D, 14.0D)), matNS);
                case WEST:
                    return VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(9.0D, 0.0D, 2.0D, 15.0D, 4.0D, 14.0D), Block.makeCuboidShape(2.0D, 0.0D, 0.0D, 8.0D, 3.0D, 16.0D)), matEW);
                default:
                    return VoxelShapes.or(VoxelShapes.or(Block.makeCuboidShape(1.0D, 0.0D, 2.0D, 7.0D, 4.0D, 14.0D), Block.makeCuboidShape(8.0D, 0.0D, 0.0D, 14.0D, 3.0D, 16.0D)), matEW);
            }
        }
    }

    @Override
    public VoxelShape getCollisionShape(BlockState state, IBlockReader worldIn, BlockPos pos, ISelectionContext context) {
        Direction direction = state.get(HORIZONTAL_FACING);
        switch (direction) {
            case NORTH:
            case SOUTH:
                return Block.makeCuboidShape(1.0D, 0.0D, 0.0D, 15.0D, 2.0D, 16.0D);
            default:
                return Block.makeCuboidShape(0.0D, 0.0D, 1.0D, 16.0D, 2.0D, 15.0D);
        }
    }


    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

}
