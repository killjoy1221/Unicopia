package com.minelittlepony.unicopia.spell;

import com.minelittlepony.util.MagicalDamageSource;
import com.minelittlepony.util.collection.IStateMapping;
import com.minelittlepony.util.collection.StateMapList;
import com.minelittlepony.util.shape.IShape;
import com.minelittlepony.util.shape.Sphere;

import net.minecraft.block.BlockBush;
import net.minecraft.block.BlockOre;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class SpellInferno extends SpellFire {

    public final StateMapList hellFireAffected = new StateMapList();

    public SpellInferno() {
        hellFireAffected.add(IStateMapping.build(
                s -> s.getBlock() == Blocks.GRASS || s.getBlock() == Blocks.DIRT || s.getBlock() == Blocks.STONE,
                s -> Blocks.NETHERRACK.getDefaultState()));

        hellFireAffected.replaceBlock(Blocks.SAND, Blocks.SOUL_SAND);
        hellFireAffected.replaceBlock(Blocks.GRAVEL, Blocks.SOUL_SAND);

        hellFireAffected.add(IStateMapping.build(
                s -> s.getMaterial() == Material.WATER,
                s -> Blocks.OBSIDIAN.getDefaultState()));

        hellFireAffected.add(IStateMapping.build(
                s -> s.getBlock() instanceof BlockBush,
                s -> Blocks.NETHER_WART.getDefaultState()));

        hellFireAffected.add(IStateMapping.build(
                s -> (s.getBlock() != Blocks.QUARTZ_ORE) && (s.getBlock() instanceof BlockOre),
                s -> Blocks.QUARTZ_ORE.getDefaultState()));
    }

    @Override
    public String getName() {
        return "inferno";
    }

    @Override
    public SpellAffinity getAffinity() {
        return SpellAffinity.BAD;
    }

    @Override
    public int getTint() {
        return 0xF00F00;
    }

    @Override
    public SpellCastResult onUse(ItemStack stack, SpellAffinity affinity, EntityPlayer player, World world, BlockPos pos, EnumFacing side, float hitX, float hitY, float hitZ) {
        return SpellCastResult.PLACE;
    }

    @Override
    public boolean update(ICaster<?> source) {
        World w = source.getWorld();

        if (!w.isRemote) {
            int radius = 4 + (source.getCurrentLevel() * 4);
            IShape shape = new Sphere(false, radius);

            Vec3d origin = source.getOriginVector();

            for (int i = 0; i < radius; i++) {
                BlockPos pos = new BlockPos(shape.computePoint(w.rand).add(origin));

                IBlockState state = w.getBlockState(pos);
                IBlockState newState = hellFireAffected.getConverted(state);

                if (!state.equals(newState)) {
                    w.setBlockState(pos, newState, 3);

                    playEffect(w, pos);
                }
            }

            shape = new Sphere(false, radius - 1);
            for (int i = 0; i < radius * 2; i++) {
                if (w.rand.nextInt(12) == 0) {
                    BlockPos pos = new BlockPos(shape.computePoint(w.rand).add(origin));

                    if (!applyBlocks(w, pos)) {
                        applyEntities(source.getOwner(), w, pos);
                    }
                }
            }
        }
        return false;
    }

    @Override
    protected DamageSource getDamageCause(Entity target, EntityLivingBase attacker) {
        if (attacker != null && attacker.getUniqueID().equals(target.getUniqueID())) {
            return MagicalDamageSource.causeMobDamage("fire.own", null);
        }
        return MagicalDamageSource.causeMobDamage("fire", attacker);
    }
}
