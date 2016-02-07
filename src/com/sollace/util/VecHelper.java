package com.sollace.util;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.command.IEntitySelector;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.util.MovingObjectPosition;
import net.minecraft.util.Vec3;

public class VecHelper {
	
	/**
	 * Performs a ray cast from the given entity and returns a result for the first block that ray intercepts.
	 * 
	 * @param e				Entity to start from
	 * @param distance		Maximum distance
	 * @param partialTick	Client partial ticks
	 * 
	 * @return	MovingObjectPosition result or null
	 */
	public static MovingObjectPosition rayTrace(Entity e, double distance, float partialTick) {
        Vec3 pos = geteEyePosition(e, partialTick);
        Vec3 look = e.getLook(partialTick);
        Vec3 ray = pos.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
        return e.worldObj.rayTraceBlocks(pos, ray, false, false, true);
	}
	
	/**
	 * Gets the position vector of an entity's eyes for ray tracing.
	 * 
	 * @param e				Entity
	 * @param partialTick	Client partial ticks
	 * 
	 * @return A vector of the entity's eye position
	 */
	public static Vec3 geteEyePosition(Entity e, float partialTick) {
		double eyeHeight = e.getEyeHeight();
        if (partialTick == 1) return new Vec3(e.posX, e.posY + eyeHeight, e.posZ);
        double x = e.prevPosX + (e.posX - e.prevPosX) * partialTick;
        double y = e.prevPosY + (e.posY - e.prevPosY) * partialTick + eyeHeight;
        double z = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTick;
        return new Vec3(x, y, z);
	}
	
	/**
	 * Performs a ray trace from the given entity and returns a result for the first Entity or block that the ray intercepts.
	 * 
	 * @param e				Entity to start from
	 * @param distance		Maximum distance
	 * @param partialTick	Client partial ticks
	 * 
	 * @return	MovingObjectPosition result or null
	 */
	public static MovingObjectPosition getObjectMouseOver(Entity e, double distance, float partialTick) {
		return getObjectMouseOverExcept(e, distance, partialTick, IEntitySelector.NOT_SPECTATING);
	}
	
	/**
	 * Performs a ray trace from the given entity and returns a result for the first Entity that passing the given predicate or block that the ray intercepts.
	 * <p>
	 * 
	 * 
	 * @param e				Entity to start from
	 * @param distance		Maximum distance
	 * @param partialTick	Client partial ticks
	 * @param predicate		Predicate test to filter entities
	 * 
	 * @return	MovingObjectPosition result or null
	 */
	public static MovingObjectPosition getObjectMouseOverExcept(Entity e, double distance, float partialTick, Predicate predicate) {
        MovingObjectPosition tracedBlock = rayTrace(e, distance, partialTick);
        
        double totalTraceDistance = distance;
        
        Vec3 pos = geteEyePosition(e, partialTick);
        
        if (tracedBlock != null) totalTraceDistance = tracedBlock.hitVec.distanceTo(pos);
        
        Vec3 look = e.getLook(partialTick);
        Vec3 ray = pos.addVector(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance);
    	
        Vec3 hit = null;
        Entity pointedEntity = null;
        List<Entity> entitiesWithinRange = e.worldObj.getEntitiesInAABBexcluding(e, e.getEntityBoundingBox().addCoord(look.xCoord * distance, look.yCoord * distance, look.zCoord * distance).expand(1, 1, 1), predicate);
        
        double traceDistance = totalTraceDistance;
        for (Entity entity : entitiesWithinRange) {
            if (entity.canBeCollidedWith()) {
                double size = entity.getCollisionBorderSize();
                AxisAlignedBB entityAABB = entity.getEntityBoundingBox().expand(size, size, size);
                MovingObjectPosition intercept = entityAABB.calculateIntercept(pos, ray);
                
                if (entityAABB.isVecInside(pos)) {
                    if (0 < traceDistance || traceDistance == 0) {
                        pointedEntity = entity;
                        hit = intercept == null ? pos : intercept.hitVec;
                        traceDistance = 0;
                    }
                } else if (intercept != null) {
                    double distanceToHit = pos.distanceTo(intercept.hitVec);
                    if (distanceToHit < traceDistance || traceDistance == 0) {
                        if (entity == e.ridingEntity) {
                            if (traceDistance == 0) {
                                pointedEntity = entity;
                                hit = intercept.hitVec;
                            }
                        } else {
                            pointedEntity = entity;
                            hit = intercept.hitVec;
                            traceDistance = distanceToHit;
                        }
                    }
                }
            }
        }
        
        if (pointedEntity != null && (traceDistance < totalTraceDistance || tracedBlock == null)) {
            return new MovingObjectPosition(pointedEntity, hit);
        }
        return tracedBlock;
    }
}
