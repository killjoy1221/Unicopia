package com.sollace.util;

import java.util.List;

import com.google.common.base.Predicate;

import net.minecraft.entity.Entity;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;

public class VecHelper {
	
	/**
	 * Performs a ray cast from the given entity and returns a result for the first block that ray intercepts.
	 * 
	 * @param e				Entity to start from
	 * @param distance		Maximum distance
	 * @param partialTick	Client partial ticks
	 * 
	 * @return	RayTraceResult result or null
	 */
	public static RayTraceResult rayTrace(Entity e, double distance, float partialTick) {
        Vec3d pos = geteEyePosition(e, partialTick);
        Vec3d look = e.getLook(partialTick);
        Vec3d ray = pos.addVector(look.x * distance, look.y * distance, look.z * distance);
        return e.world.rayTraceBlocks(pos, ray, false, false, true);
	}
	
	/**
	 * Gets the position vector of an entity's eyes for ray tracing.
	 * 
	 * @param e				Entity
	 * @param partialTick	Client partial ticks
	 * 
	 * @return A vector of the entity's eye position
	 */
	public static Vec3d geteEyePosition(Entity e, float partialTick) {
		double eyeHeight = e.getEyeHeight();
        if (partialTick == 1) return new Vec3d(e.posX, e.posY + eyeHeight, e.posZ);
        double x = e.prevPosX + (e.posX - e.prevPosX) * partialTick;
        double y = e.prevPosY + (e.posY - e.prevPosY) * partialTick + eyeHeight;
        double z = e.prevPosZ + (e.posZ - e.prevPosZ) * partialTick;
        return new Vec3d(x, y, z);
	}
	
	/**
	 * Performs a ray trace from the given entity and returns a result for the first Entity or block that the ray intercepts.
	 * 
	 * @param e				Entity to start from
	 * @param distance		Maximum distance
	 * @param partialTick	Client partial ticks
	 * 
	 * @return	RayTraceResult result or null
	 */
	public static RayTraceResult getObjectMouseOver(Entity e, double distance, float partialTick) {
		return getObjectMouseOverExcept(e, distance, partialTick, EntitySelectors.NOT_SPECTATING);
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
	 * @return	RayTraceResult result or null
	 */
	public static RayTraceResult getObjectMouseOverExcept(Entity e, double distance, float partialTick, Predicate<Entity> predicate) {
        RayTraceResult tracedBlock = rayTrace(e, distance, partialTick);
        
        double totalTraceDistance = distance;
        
        Vec3d pos = geteEyePosition(e, partialTick);
        
        if (tracedBlock != null) totalTraceDistance = tracedBlock.hitVec.distanceTo(pos);
        
        Vec3d look = e.getLook(partialTick);
        Vec3d ray = pos.addVector(look.x * distance, look.y * distance, look.z * distance);
    	
        Vec3d hit = null;
        Entity pointedEntity = null;
        List<Entity> entitiesWithinRange = e.world.getEntitiesInAABBexcluding(e, e.getEntityBoundingBox().grow(look.x * distance, look.y * distance, look.z * distance).expand(1, 1, 1), predicate);
        
        double traceDistance = totalTraceDistance;
        for (Entity entity : entitiesWithinRange) {
            if (entity.canBeCollidedWith()) {
                double size = entity.getCollisionBorderSize();
                AxisAlignedBB entityAABB = entity.getEntityBoundingBox().expand(size, size, size);
                RayTraceResult intercept = entityAABB.calculateIntercept(pos, ray);
                
                if (entityAABB.contains(pos)) {
                    if (0 < traceDistance || traceDistance == 0) {
                        pointedEntity = entity;
                        hit = intercept == null ? pos : intercept.hitVec;
                        traceDistance = 0;
                    }
                } else if (intercept != null) {
                    double distanceToHit = pos.distanceTo(intercept.hitVec);
                    if (distanceToHit < traceDistance || traceDistance == 0) {
                        if (entity == e.getRidingEntity()) {
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
            return new RayTraceResult(pointedEntity, hit);
        }
        return tracedBlock;
    }
}
