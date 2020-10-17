package com.vulp.druidcraft.pathfinding;

import net.minecraft.entity.Entity;
import net.minecraft.entity.MobEntity;
import net.minecraft.pathfinding.FlyingNodeProcessor;
import net.minecraft.pathfinding.Path;
import net.minecraft.pathfinding.PathFinder;
import net.minecraft.pathfinding.SwimmerPathNavigator;
import net.minecraft.util.math.RayTraceContext;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.vector.Vector3d;
import net.minecraft.world.World;

public class FlyingFishPathNavigator extends SwimmerPathNavigator {

    public FlyingFishPathNavigator(MobEntity entitylivingIn, World worldIn) {
        super(entitylivingIn, worldIn);
    }

    @Override
    protected PathFinder getPathFinder(int p_179679_1_) {
        this.nodeProcessor = new FlyingNodeProcessor();
        return new PathFinder(this.nodeProcessor, p_179679_1_);
    }

    @Override
    protected boolean canNavigate() {
        return true;
    }

    @Override
    public boolean tryMoveToEntityLiving(Entity entityIn, double speedIn) {
        Path path = this.getPathToEntity(entityIn, 1);
        return path != null && this.setPath(path, speedIn);
    }

    @Override
    protected boolean isDirectPathBetweenPoints(Vector3d posVec31, Vector3d posVec32, int sizeX, int sizeY, int sizeZ) {
        Vector3d vec3d = new Vector3d(posVec32.x, posVec32.y + (double)this.entity.getHeight() * 0.5D, posVec32.z);
        return this.world.rayTraceBlocks(new RayTraceContext(posVec31, vec3d, RayTraceContext.BlockMode.COLLIDER, RayTraceContext.FluidMode.ANY, this.entity)).getType() == RayTraceResult.Type.MISS;
    }

    @Override
	public void setCanSwim(boolean canSwim) {
    }
}
