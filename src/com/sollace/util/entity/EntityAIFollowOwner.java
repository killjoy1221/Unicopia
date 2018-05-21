package com.sollace.util.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class EntityAIFollowOwner<T extends EntityLiving & ITameable> extends EntityAIBase {
    private T thePet;
    private EntityLivingBase theOwner;
    World theWorld;
    
    private double followSpeed;
    private PathNavigateGround petPathfinder;
    private int updateTimeout;
    float maxDist;
    float minDist;
    private boolean canSwim;

    public EntityAIFollowOwner(T thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
        thePet = thePetIn;
        theWorld = thePetIn.world;
        followSpeed = followSpeedIn;
        minDist = minDistIn;
        maxDist = maxDistIn;
        setMutexBits(3);
        
        if (!(thePetIn.getNavigator() instanceof PathNavigateGround)) {
            throw new IllegalArgumentException("Unsupported mob type for FollowOwnerGoal");
        }
        
        petPathfinder = (PathNavigateGround)thePetIn.getNavigator();
    }
    
    public boolean shouldExecute() {
        EntityLivingBase owner = thePet.getOwner();
        if (owner == null || thePet.isSitting() || thePet.getDistanceSqToEntity(owner) < (minDist * minDist)) {
            return false;
        }
        theOwner = owner;
        return true;
    }
    
    public boolean continueExecuting() {
        return !petPathfinder.noPath() && thePet.getDistanceSqToEntity(theOwner) > (maxDist * maxDist) && !thePet.isSitting();
    }
    
    public void startExecuting() {
        updateTimeout = 0;
        canSwim = petPathfinder.getCanSwim();
        petPathfinder.setCanSwim(false);
    }
    
    public void resetTask() {
        theOwner = null;
        petPathfinder.clearPathEntity();
        petPathfinder.setCanSwim(canSwim);
    }
    
    public void updateTask() {
        thePet.getLookHelper().setLookPositionWithEntity(theOwner, 10, thePet.getVerticalFaceSpeed());
        if (!thePet.isSitting()) {
            if (--updateTimeout <= 0) {
                updateTimeout = 10;
                if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeed)) {
                    if (!thePet.getLeashed()) {
                        if (thePet.getDistanceSqToEntity(theOwner) >= 144) {
                            int x = MathHelper.floor(theOwner.posX) - 2;
                            int z = MathHelper.floor(theOwner.posZ) - 2;
                            int y = MathHelper.floor(theOwner.getEntityBoundingBox().minY);
                            for (int difX = 0; difX <= 4; difX++) {
                                for (int difY = 0; difY <= 4; difY++) {
                                    if ((difX < 1 || difY < 1 || difX > 3 || difY > 3)
                                    		&& theWorld.getBlockState(new BlockPos(x + difX, y - 1, z + difY)).isTopSolid()
                                    		&& !theWorld.getBlockState(new BlockPos(x + difX, y, z + difY)).isFullCube()
                                    		&& !theWorld.getBlockState(new BlockPos(x + difX, y + 1, z + difY)).isFullCube()) {
                                        thePet.setLocationAndAngles(x + difX + 0.5, y, z + difY + 0.5, thePet.rotationYaw, thePet.rotationPitch);
                                        petPathfinder.clearPathEntity();
                                        return;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
