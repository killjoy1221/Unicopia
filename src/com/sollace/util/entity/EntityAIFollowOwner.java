package com.sollace.util.entity;

import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.EntityAIBase;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.BlockPos;
import net.minecraft.util.MathHelper;
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
    private boolean avoidsWater;

    public EntityAIFollowOwner(T thePetIn, double followSpeedIn, float minDistIn, float maxDistIn) {
        thePet = thePetIn;
        theWorld = thePetIn.worldObj;
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
        avoidsWater = petPathfinder.getAvoidsWater();
        petPathfinder.setAvoidsWater(false);
    }
    
    public void resetTask() {
        theOwner = null;
        petPathfinder.clearPathEntity();
        petPathfinder.setAvoidsWater(avoidsWater);
    }
    
    public void updateTask() {
        thePet.getLookHelper().setLookPositionWithEntity(theOwner, 10, thePet.getVerticalFaceSpeed());
        if (!thePet.isSitting()) {
            if (--updateTimeout <= 0) {
                updateTimeout = 10;
                if (!petPathfinder.tryMoveToEntityLiving(theOwner, followSpeed)) {
                    if (!thePet.getLeashed()) {
                        if (thePet.getDistanceSqToEntity(theOwner) >= 144) {
                            int x = MathHelper.floor_double(theOwner.posX) - 2;
                            int z = MathHelper.floor_double(theOwner.posZ) - 2;
                            int y = MathHelper.floor_double(theOwner.getEntityBoundingBox().minY);
                            for (int difX = 0; difX <= 4; difX++) {
                                for (int difY = 0; difY <= 4; difY++) {
                                    if ((difX < 1 || difY < 1 || difX > 3 || difY > 3) && World.doesBlockHaveSolidTopSurface(theWorld, new BlockPos(x + difX, y - 1, z + difY)) && !theWorld.getBlockState(new BlockPos(x + difX, y, z + difY)).getBlock().isFullCube() && !theWorld.getBlockState(new BlockPos(x + difX, y + 1, z + difY)).getBlock().isFullCube()) {
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
