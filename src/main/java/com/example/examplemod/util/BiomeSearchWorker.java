package com.example.examplemod.util;

import com.example.examplemod.ExampleMod;
import com.example.examplemod.item.ModItems;
import com.example.examplemod.item.custom.NaturesCompassItem;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.QuartPos;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.WorldWorkerManager;

import java.util.Optional;

public class BiomeSearchWorker implements WorldWorkerManager.IWorker {
    private final int sampleSpace; // 搜索步长
    private final int maxSamples; // 最大搜索次数
    private final int maxRadius; // 最大搜索半径
    private ServerLevel level;
    private ResourceLocation biomeKey;
    private BlockPos startPos;
    private int samples; // 搜索次数
    private int nextLength; // 算法计数用的
    private Direction direction;// 搜索朝向
    private ItemStack stack;
    private Player player;
    private int x; // 位置
    private int z;
    private int[] yValues;
    private int length;
    private boolean finished;
    private int lastRadiusThreshold;

    // 我们搜索的主要的操作在这个类完成。
    public BiomeSearchWorker(ServerLevel level, Player player, ItemStack stack, Biome biome, BlockPos startPos) {
        this.level = level;
        this.player = player;
        this.stack = stack;
        this.startPos = startPos;
        x = startPos.getX();
        z = startPos.getZ();
        yValues = Mth.outFromOrigin(startPos.getY(), level.getMinBuildHeight() + 1, level.getMaxBuildHeight(), 64).toArray();
        //sampleSpaceModifier
        sampleSpace = 16 * BiomeUtils.getBiomeSize(level); // 64
        //maxSamples
        maxSamples = 50000;
        //radiusModifier
        maxRadius = 2500 * BiomeUtils.getBiomeSize(level);
        nextLength = sampleSpace;
        length = 0;
        samples = 0;
        direction = Direction.UP;
        finished = false;
        biomeKey = BiomeUtils.getKeyForBiome(level, biome).isPresent() ? BiomeUtils.getKeyForBiome(level, biome).get() : null;
        lastRadiusThreshold = 0;
    }
    // 是否还有需要做的事情，如果biomekey不为空，搜索没有完成，搜索半径未到最大半径，搜索次数未到最大次数，则表示还需要搜索。
    @Override
    public boolean hasWork() {
        return biomeKey != null && !finished && getRadius() <= maxRadius && samples <= maxSamples;
    }
    // 搜索的算法，这个方法会被系统回调，根据返回的boolean决定下一tick是否进行调用。如果我们任务完成则不要要继续调用返回false，否则返回true
    @Override
    public boolean doWork() {
        // 该if是主要的搜索算法，我这里简单解释下算法的逻辑
        if (hasWork()) {
            if (direction == Direction.NORTH) {
                z -= sampleSpace;
            } else if (direction == Direction.EAST) {
                x += sampleSpace;
            } else if (direction == Direction.SOUTH) {
                z += sampleSpace;
            } else if (direction == Direction.WEST) {
                x -= sampleSpace;
            }

            int sampleX = QuartPos.fromBlock(x);
            int sampleZ = QuartPos.fromBlock(z);

            for (int y : yValues) {
                int sampleY = QuartPos.fromBlock(y);
                final Biome biomeAtPos = level.getChunkSource().getGenerator().getBiomeSource().getNoiseBiome(sampleX, sampleY, sampleZ, level.getChunkSource().randomState().sampler()).value();
                final Optional<ResourceLocation> optionalBiomeAtPosKey = BiomeUtils.getKeyForBiome(level, biomeAtPos);
                if (optionalBiomeAtPosKey.isPresent() && optionalBiomeAtPosKey.get().equals(biomeKey)) {
                    // 成功
                    succeed();
                    return false;
                }
            }
            samples++;
            length += sampleSpace;
            if (length >= nextLength) { // 3 * 64
                if (direction != Direction.UP) {
                    nextLength += sampleSpace;
                    direction = direction.getClockWise();
                } else {
                    direction = Direction.NORTH;
                }
                length = 0;
            }
            int radius = getRadius();
            if (radius > 500 && radius / 500 > lastRadiusThreshold) {
                if (!stack.isEmpty() && stack.getItem() == ModItems.COMPASS_ITEM.get()) {
//                    ((NaturesCompassItem) stack.getItem()).setSearchRadius(stack, roundRadius(radius, 500), player);
                }
                lastRadiusThreshold = radius / 500;
            }
        }
        // 如果没有找到则继续
        if (hasWork()) {
            return true;
        }
        // 当达到了最大的半径或者搜索的次数上线，则失败
        if (!finished) {
            fail();
        }
        return false;
    }
    public void start() {
        if (!stack.isEmpty() && stack.getItem() == ModItems.COMPASS_ITEM.get()) {
            if (maxRadius > 0 && sampleSpace > 0) {
                ExampleMod.LOGGER.info("Starting search: " + sampleSpace + " sample space, " + maxSamples + " max samples, " + maxRadius + " max radius");
                WorldWorkerManager.addWorker(this);
            } else {
                fail();
            }
        }
    }
    private void succeed() {
        ExampleMod.LOGGER.info("Search succeeded: " + getRadius() + " radius, " + samples + " samples");
        if (!stack.isEmpty() && stack.getItem() == ModItems.COMPASS_ITEM.get()) {
            // 成功后会打印半径，和x，z坐标 这里先这样展示，之后我们根据这个x，z坐标和玩家的坐标以及玩家朝向计算指南针的指针方向
            ExampleMod.LOGGER.info("position x: " + x + " z: "+z);
            // 搜索成功的时候调用 ，并传入xz坐标
            ((NaturesCompassItem) stack.getItem()).succeed(stack, player, x, z, samples, true);
        } else {
            ExampleMod.LOGGER.error("Invalid compass after search");
        }
        finished = true;
    }

    private void fail() {
        ExampleMod.LOGGER.info("Search failed: " + getRadius() + " radius, " + samples + " samples");
        if (!stack.isEmpty() && stack.getItem() == ModItems.COMPASS_ITEM.get()) {
            // 失败时候调用失败的。
            ((NaturesCompassItem) stack.getItem()).fail(stack, player, roundRadius(getRadius(), 500), samples);
        } else {
            ExampleMod.LOGGER.error("Invalid compass after search");
        }
        finished = true;
    }

    public void stop() {
        ExampleMod.LOGGER.info("Search stopped: " + getRadius() + " radius, " + samples + " samples");
        finished = true;
    }
    // 算两个位置之间的距离
    private int getRadius() {
        return BiomeUtils.getDistanceToBiome(startPos, x, z);
    }
    // 将给定半径舍入到指定值的最接近倍数。
    private int roundRadius(int radius, int roundTo) {
        return ((int) radius / roundTo) * roundTo;
    }
}
