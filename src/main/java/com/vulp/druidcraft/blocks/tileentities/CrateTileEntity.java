package com.vulp.druidcraft.blocks.tileentities;

import com.vulp.druidcraft.Druidcraft;
import com.vulp.druidcraft.api.CrateType;
import com.vulp.druidcraft.blocks.CrateBlock;
import com.vulp.druidcraft.inventory.DoubleSidedItemStackHandler;
import com.vulp.druidcraft.inventory.OctoSidedInventory;
import com.vulp.druidcraft.inventory.QuadSidedInventory;
import com.vulp.druidcraft.inventory.container.CrateContainer;
import com.vulp.druidcraft.registry.BlockRegistry;
import com.vulp.druidcraft.registry.SoundEventRegistry;
import com.vulp.druidcraft.registry.TileEntityRegistry;
import net.minecraft.block.Block;
import net.minecraft.block.BlockState;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.DoubleSidedInventory;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.ItemStackHelper;
import net.minecraft.inventory.container.ChestContainer;
import net.minecraft.inventory.container.Container;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompoundNBT;
import net.minecraft.tileentity.*;
import net.minecraft.util.NonNullList;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TranslationTextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.IItemHandler;
import net.minecraftforge.items.ItemStackHandler;

import javax.annotation.Nullable;
import java.util.ArrayList;

public class CrateTileEntity extends LockableLootTileEntity {
    private ItemStackHandler itemHandler = mainHandler();
    private ArrayList<BlockPos> neighbors;
    private int numPlayersUsing;
    private LazyOptional<IItemHandler> handler = LazyOptional.of(() -> itemHandler);

    private CrateTileEntity(TileEntityType<?> tileEntityType) {
        super(tileEntityType);
    }

    public CrateTileEntity() {
        this(TileEntityRegistry.crate);
    }

    public ArrayList<BlockPos> getNeighbors() {
        return this.neighbors;
    }

    public NonNullList<ItemStack> getItemList() {
        NonNullList<ItemStack> inv = NonNullList.create();
        for (int i = 0; i < 27; i++) {
            inv.add(this.itemHandler.getStackInSlot(i));
        }
        return inv;
    }

    @Override
    public CompoundNBT write(CompoundNBT compound) {
        super.write(compound);
        ArrayList<Integer> x = new ArrayList<>();
        ArrayList<Integer> y = new ArrayList<>();
        ArrayList<Integer> z = new ArrayList<>();
        for (int i = 0; i < this.neighbors.size(); i++) {
            x.add(this.neighbors.get(i).getX());
            y.add(this.neighbors.get(i).getY());
            z.add(this.neighbors.get(i).getZ());
        }
        compound.putIntArray("CoordX", x);
        compound.putIntArray("CoordY", y);
        compound.putIntArray("CoordZ", z);
        if (!this.checkLootAndWrite(compound)) {
            ItemStackHelper.saveAllItems(compound, this.itemHandler);
        }

        return compound;
    }

    @Override
    public void read(CompoundNBT compound) {
        super.read(compound);
        ArrayList<BlockPos> neighborArray = new ArrayList<>();
        for (int i = 0; i < compound.getIntArray("CoordX").length; i++) {
            neighborArray.add(new BlockPos(compound.getIntArray("CoordX")[i], compound.getIntArray("CoordY")[i], compound.getIntArray("CoordZ")[i]));
        }
        this.neighbors = neighborArray;
        this.itemHandler = NonNullList.withSize(this.getSizeInventory(), ItemStack.EMPTY);
        if (!this.checkLootAndRead(compound)) {
            ItemStackHelper.loadAllItems(compound, this.itemHandler);
        }

    }

    @Override
    public void onLoad() {
        super.onLoad();
        if (this.neighbors == null)
        this.neighbors = CrateBlock.getBlockPositions(world, pos);
        for (int i = 0; i < this.neighbors.size(); i++) {
            Druidcraft.LOGGER.debug("onLoad() : " + this.neighbors.get(i));
        }
    }

    /**
     * Returns the number of slots in the inventory.
     */
    @Override
    public int getSizeInventory() {
        return 27;
    }

    @Override
    public boolean isEmpty() {
        for(ItemStack itemstack : this.itemHandler) {
            if (!itemstack.isEmpty()) {
                return false;
            }
        }

        return true;
    }

    /**
     * Returns the stack in the given slot.
     */
    @Override
    public ItemStack getStackInSlot(int index) {
        return this.itemHandler.get(index);
    }

    /**
     * Removes up to a specified number of items from an inventory slot and returns them in a new stack.
     */
    @Override
    public ItemStack decrStackSize(int index, int count) {
        return ItemStackHelper.getAndSplit(this.itemHandler, index, count);
    }

    /**
     * Removes a stack from the given slot and returns it.
     */
    @Override
    public ItemStack removeStackFromSlot(int index) {
        return ItemStackHelper.getAndRemove(this.itemHandler, index);
    }

    /**
     * Sets the given item stack to the specified slot in the inventory (can be crafting or armor sections).
     */
    @Override
    public void setInventorySlotContents(int index, ItemStack stack) {
        this.itemHandler.set(index, stack);
        if (stack.getCount() > this.getInventoryStackLimit()) {
            stack.setCount(this.getInventoryStackLimit());
        }

    }

    @Override
    public void clear() {
        this.itemHandler.clear();
    }

    @Override
    protected NonNullList<ItemStack> getItems() {
        return this.itemHandler;
    }

    @Override
    protected void setItems(NonNullList<ItemStack> itemsIn) {
        this.itemHandler = itemsIn;
    }

    @Override
    protected ITextComponent getDefaultName() {
        return new TranslationTextComponent("container.druidcraft.crate");
    }

    @Override
    protected Container createMenu(int id, PlayerInventory player) {
        return ChestContainer.createGeneric9X3(id, player, this);
    }

    public void crateTick() {
        int i = this.pos.getX();
        int j = this.pos.getY();
        int k = this.pos.getZ();
        BlockState blockState = world.getBlockState(new BlockPos(i, j, k));
        this.numPlayersUsing = calculatePlayersUsing(this.world, this, i, j, k, (blockState.get(CrateBlock.INDEX).getType() == CrateType.QUAD_X ||
                blockState.get(CrateBlock.INDEX).getType() == CrateType.QUAD_Y || blockState.get(CrateBlock.INDEX).getType() == CrateType.QUAD_Z || blockState.get(CrateBlock.INDEX).getType() == CrateType.OCTO));
        if (this.numPlayersUsing > 0) {
            this.scheduleTick();
        } else {
            BlockState blockstate = this.getBlockState();
            if (blockstate.getBlock() != BlockRegistry.crate) {
                this.remove();
                return;
            }

            boolean flag = blockstate.get(CrateBlock.PROPERTY_OPEN);
            if (flag) {
                if (blockState.get(CrateBlock.INDEX).isParent()) {
                    this.playSound(blockState, SoundEventRegistry.close_crate);
                }
                this.setCrateState(blockstate, false);
            }
        }

        for (int b = 0; b < this.neighbors.size(); b++) {
            Druidcraft.LOGGER.debug("crateTick() : " + this.neighbors.get(b));
        }

    }

    public static int calculatePlayersUsing(World world, LockableTileEntity lockableTileEntity, int posX, int posY, int posZ, boolean isQuadOrOcto) {
        int i = 0;
        float f = 6.0F;

        for(PlayerEntity playerentity : world.getEntitiesWithinAABB(PlayerEntity.class, new AxisAlignedBB((double)((float)posX - f), (double)((float)posY - f), (double)((float)posZ - f), (double)((float)(posX + 1) + f), (double)((float)(posY + 1) + f), (double)((float)(posZ + 1) + f)))) {
            if (playerentity.openContainer instanceof CrateContainer && isQuadOrOcto) {
                IInventory iinventory = ((CrateContainer)playerentity.openContainer).getMainInventory();
                if ((iinventory == lockableTileEntity || (iinventory instanceof QuadSidedInventory && ((QuadSidedInventory)iinventory).isPartOfQuadCrate(lockableTileEntity))) || (iinventory instanceof OctoSidedInventory && ((OctoSidedInventory)iinventory).isPartOfOctoCrate(lockableTileEntity))) {
                    ++i;
                }
            }
            if (playerentity.openContainer instanceof ChestContainer && !isQuadOrOcto) {
                IInventory iinventory = ((ChestContainer)playerentity.openContainer).getLowerChestInventory();
                if (iinventory == lockableTileEntity || iinventory instanceof DoubleSidedInventory && ((DoubleSidedInventory)iinventory).isPartOfLargeChest(lockableTileEntity)) {
                    ++i;
                }
            }
        }

        return i;
    }

    @Override
    public void openInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            if (this.numPlayersUsing < 0) {
                this.numPlayersUsing = 0;
            }

            ++this.numPlayersUsing;
            BlockState blockstate = this.getBlockState();
            boolean flag = blockstate.get(CrateBlock.PROPERTY_OPEN);
            if (!flag) {
                if (blockstate.get(CrateBlock.INDEX).isParent()) {
                    this.playSound(blockstate, SoundEventRegistry.open_crate);
                }
                this.setCrateState(blockstate, true);
            }

            this.scheduleTick();
        }

    }

    private void scheduleTick() {
        Block block = this.getBlockState().getBlock();
        this.world.getPendingBlockTicks().scheduleTick(this.getPos(), block, 5);
        this.world.notifyNeighborsOfStateChange(this.pos, block);
    }

    @Override
    public void updateContainingBlockInfo() {
        super.updateContainingBlockInfo();
        if (this.handler != null) {
            this.handler.invalidate();
            this.handler = null;
        }
    }

    @Override
    public <T> net.minecraftforge.common.util.LazyOptional<T> getCapability(net.minecraftforge.common.capabilities.Capability<T> cap, @javax.annotation.Nullable net.minecraft.util.Direction side) {
        if (!this.removed && cap == net.minecraftforge.items.CapabilityItemHandler.ITEM_HANDLER_CAPABILITY) {
            if (this.handler == null) {
                this.handler = net.minecraftforge.common.util.LazyOptional.of(this::DEFcreateHandler);
            }
            return this.handler.cast();
        }
        return super.getCapability(cap, side);
    }

    private ItemStackHandler mainHandler() {
        return new ItemStackHandler(27) {

            @Override
            protected void onContentsChanged(int slot) {
                markDirty();
            }
        };
    }
    @Nullable
    private ItemStackHandler createHandler() {
        if (world != null) {
            ArrayList<BlockPos> neighbors = this.neighbors;
            for (int i = 0; i < this.neighbors.size(); i++) {
                if (!(world.getBlockState(this.neighbors.get(i)).getBlock() instanceof CrateBlock && world.getTileEntity(this.neighbors.get(i)) instanceof CrateTileEntity)) {
                    return (ItemStackHandler)super.createUnSidedHandler();
                }
            }
            if (neighbors.size() > 1) {
                if (neighbors.size() == 2) {
                    return new DoubleSidedItemStackHandler((CrateTileEntity)this.world.getTileEntity(neighbors.get(0)).)
                            IInventory inven1 = (IInventory) this.getWorld().getTileEntity(neighbors.get(0));
                    IInventory inven2 = (IInventory) this.getWorld().getTileEntity(neighbors.get(1));
                }
                if (neighbors.size() == 4) {

                }
                if (neighbors.size() == 8) {

                }
            }
            return (ItemStackHandler)super.createUnSidedHandler();
        }
        return null;
    }

    public void markDirty() {
        if (world != null) {
            for (int i = 0; i < neighbors.size(); i++) {
                if (world.getTileEntity(neighbors.get(i)) instanceof CrateTileEntity) {
                    world.getTileEntity(neighbors.get(i)).markDirty();
                }
            }
        }
    }

    private net.minecraftforge.items.IItemHandlerModifiable DEFcreateHandler() {
        ArrayList<BlockPos> neighbors = getNeighbors();
        int size = neighbors.size();
        for (int i = 0; i < size; i++) {
            if (!(this.world.getBlockState(neighbors.get(i)).getBlock() instanceof CrateBlock) || size < 2) {
                return new net.minecraftforge.items.wrapper.InvWrapper(this);
            }
            if (!(this.getWorld().getTileEntity(neighbors.get(i)) instanceof CrateTileEntity)) {
                return new net.minecraftforge.items.wrapper.InvWrapper(this);
            }
        }

        IInventory inven1 = (IInventory) this.getWorld().getTileEntity(neighbors.get(0));
        IInventory inven2 = (IInventory) this.getWorld().getTileEntity(neighbors.get(1));
        if (size == 2) {
            return new net.minecraftforge.items.wrapper.CombinedInvWrapper(
                    new net.minecraftforge.items.wrapper.InvWrapper(inven1),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven2));
        }
        IInventory inven3 = (IInventory) this.getWorld().getTileEntity(neighbors.get(2));
        IInventory inven4 = (IInventory) this.getWorld().getTileEntity(neighbors.get(3));
        if (size == 4) {
            return new net.minecraftforge.items.wrapper.CombinedInvWrapper(
                    new net.minecraftforge.items.wrapper.InvWrapper(inven1),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven2),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven3),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven4));
        }
        IInventory inven5 = (IInventory) this.getWorld().getTileEntity(neighbors.get(4));
        IInventory inven6 = (IInventory) this.getWorld().getTileEntity(neighbors.get(5));
        IInventory inven7 = (IInventory) this.getWorld().getTileEntity(neighbors.get(6));
        IInventory inven8 = (IInventory) this.getWorld().getTileEntity(neighbors.get(7));
        if (size == 8) {
            return new net.minecraftforge.items.wrapper.CombinedInvWrapper(
                    new net.minecraftforge.items.wrapper.InvWrapper(inven1),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven2),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven3),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven4),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven5),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven6),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven7),
                    new net.minecraftforge.items.wrapper.InvWrapper(inven8));
        }
        return new net.minecraftforge.items.wrapper.InvWrapper(this);
    }

    /**
     * invalidates a tile entity
     */
    @Override
    public void remove() {
        super.remove();
        if (handler != null)
            handler.invalidate();
    }

    @Override
    public void closeInventory(PlayerEntity player) {
        if (!player.isSpectator()) {
            --this.numPlayersUsing;
        }
    }

    private void setCrateState(BlockState state, boolean isOpening) {
        this.world.setBlockState(this.getPos(), state.with(CrateBlock.PROPERTY_OPEN, isOpening), 3);
    }

    public float checkCrateShape(BlockState state) {
        CrateType type = (CrateType) state.get(CrateBlock.INDEX).getType();
        if (type == CrateType.DOUBLE_X || type == CrateType.DOUBLE_Y || type == CrateType.DOUBLE_Z)
            return 0.8F;
        if (type == CrateType.QUAD_X || type == CrateType.QUAD_Y || type == CrateType.QUAD_Z)
            return 0.7F;
        if (type == CrateType.OCTO)
            return 0.6F;
        return 0.9F;
    }

    private void playSound(BlockState state, SoundEvent p_213965_2_) {
        double d0 = (double)this.pos.getX() + 0.5D;
        double d1 = (double)this.pos.getY() + 0.5D;
        double d2 = (double)this.pos.getZ() + 0.5D;
        this.world.playSound((PlayerEntity)null, d0, d1, d2, p_213965_2_, SoundCategory.BLOCKS, 0.65F, this.world.rand.nextFloat() * 0.1F + checkCrateShape(state));
    }
}