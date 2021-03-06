package com.minelittlepony.util.collection;

import java.util.function.Function;
import java.util.function.Predicate;

import javax.annotation.Nonnull;

import net.minecraft.block.Block;
import net.minecraft.block.properties.IProperty;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;

public interface IStateMapping extends Predicate<IBlockState>, Function<IBlockState, IBlockState> {

    static IStateMapping removeBlock(Predicate<IBlockState> mapper) {
        return build(
                mapper,
                s -> Blocks.AIR.getDefaultState());
    }

    static IStateMapping replaceBlock(Block from, Block to) {
        return build(
                s -> s.getBlock() == from,
                s -> to.getDefaultState(),
                s -> replaceBlock(to, from));
    }

    static <T extends Comparable<T>> IStateMapping replaceProperty(Block block, IProperty<T> property, T from, T to) {
        return build(
                s -> s.getBlock() == block && s.getValue(property) == from,
                s -> s.withProperty(property, to),
                s -> replaceProperty(block, property, to, from));
    }

    static <T extends Comparable<T>> IStateMapping setProperty(Block block, IProperty<T> property, T to) {
        return build(
                s -> s.getBlock() == block,
                s -> s.withProperty(property, to));
    }

    static IStateMapping build(Predicate<IBlockState> predicate, Function<IBlockState, IBlockState> converter) {
        return build(predicate, converter, s -> s);
    }

    static IStateMapping build(Predicate<IBlockState> predicate, Function<IBlockState, IBlockState> converter, Function<IStateMapping, IStateMapping> inverter) {
        return new IStateMapping() {
            private IStateMapping inverse;

            @Override
            public boolean test(IBlockState state) {
                return predicate.test(state);
            }

            @Override
            public IBlockState apply(IBlockState state) {
                return converter.apply(state);
            }

            @Override
            public IStateMapping inverse() {
                if (inverse == null) {
                    inverse = inverter.apply(this);
                }
                return inverse;
            }
        };
    }

    /**
     * Checks if this state can be converted by this mapping
     *
     * @param state    State to check
     *
     * @return    True if the state can be converted
     */
    @Override
    default boolean test(@Nonnull IBlockState state) {
        return true;
    }

    /**
     * Converts the given state based on this mapping
     *
     * @param state    State to convert
     *
     * @return    The converted state
     */
    @Nonnull
    @Override
    default IBlockState apply(@Nonnull IBlockState state) {
        return state;
    }

    /**
     * Gets the inverse of this mapping if one exists. Otherwise returns itself.
     */
    @Nonnull
    default IStateMapping inverse() {
        return this;
    }
}
