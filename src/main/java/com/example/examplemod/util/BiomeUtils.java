package com.example.examplemod.util;

import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.ListMultimap;
import net.minecraft.Util;
import net.minecraft.client.resources.language.I18n;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.core.Registry;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.*;

/**
 * 该类用于处理Minecraft的生物群系，通过使用ForgeRegistries的生物群系来操作生物群系。
 */
public class BiomeUtils {

    // 通过level 获得对应世界的所有的生物群系的optional对象，如果无法访问则返回的optional丢向不包含值
    public static Optional<? extends Registry<Biome>> getBiomeRegistry(Level level) {
        return level.registryAccess().registry(ForgeRegistries.Keys.BIOMES);
    }

    // 接受level和Biome对象，返回一个包含给定世界给定生物群系的对应的键的optional对象，如果不存在则返回optional.empty
    public static Optional<ResourceLocation> getKeyForBiome(Level level, Biome biome) {
        return getBiomeRegistry(level).isPresent() ? Optional.of(getBiomeRegistry(level).get().getKey(biome)) : Optional.empty();
    }

    // 接受一个level 和一个 biome，返回一个包含给定生物群系在给定级别的生物群系注册表中对应的键Optional对象
    public static Optional<Biome> getBiomeForKey(Level level, ResourceLocation key) {
        return getBiomeRegistry(level).isPresent() ? getBiomeRegistry(level).get().getOptional(key) : Optional.empty();
    }

    // 接收一个Level对象作为参数，返回一个包含给定级别的生物群系注册表中所有允许的生物群系的键的列表
    public static List<ResourceLocation> getAllowedBiomeKeys(Level level) {
        final List<ResourceLocation> biomeKeys = new ArrayList<ResourceLocation>();

        if (getBiomeRegistry(level).isPresent()) {
                for (Map.Entry<ResourceKey<Biome>, Biome> entry : getBiomeRegistry(level).get().entrySet()) {
                Biome biome = entry.getValue();
                if (biome != null) {
                    Optional<ResourceLocation> optionalBiomeKey = getKeyForBiome(level, biome);
                    if (biome != null && optionalBiomeKey.isPresent()) {
                        biomeKeys.add(optionalBiomeKey.get());
                    }
                }
            }
        }
        return biomeKeys;
    }
    // 个方法接收一个ServerLevel对象和一个Biome对象作为参数，返回一个包含给定生物群系可能生成的所有维度的键的列表
    public static List<ResourceLocation> getGeneratingDimensionKeys(ServerLevel serverLevel, Biome biome) {
        final List<ResourceLocation> dimensions = new ArrayList<ResourceLocation>();
        final Registry<Biome> biomeRegistry = getBiomeRegistry(serverLevel).get();
        for (ServerLevel level : serverLevel.getServer().getAllLevels()) {
            Set<Holder<Biome>> biomeSet = level.getChunkSource().getGenerator().getBiomeSource().possibleBiomes();
            Holder<Biome> biomeHolder = biomeRegistry.getHolder(biomeRegistry.getResourceKey(biome).get()).get();
            if (biomeSet.contains(biomeHolder)) {
                dimensions.add(level.dimension().location());
            }
        }
        return dimensions;
    }

    // ：这个方法接收一个ServerLevel对象作为参数，返回一个映射，该映射的键是给定服务器级别的生物群系注册表中所有允许的生物群系的键，值是这些生物群系可能生成的所有维度的键的列表。
    public static ListMultimap<ResourceLocation, ResourceLocation> getGeneratingDimensionsForAllowedBiomes(ServerLevel serverLevel) {
        ListMultimap<ResourceLocation, ResourceLocation> dimensionsForAllowedStructures = ArrayListMultimap.create();
        for (ResourceLocation biomeKey : getAllowedBiomeKeys(serverLevel)) {
            Optional<Biome> optionalBiome = getBiomeForKey(serverLevel, biomeKey);
            if (optionalBiome.isPresent()) {
                dimensionsForAllowedStructures.putAll(biomeKey, getGeneratingDimensionKeys(serverLevel, optionalBiome.get()));
            }
        }
        return dimensionsForAllowedStructures;

    }

    // 这两个方法分别接收一个Level对象和一个ResourceLocation对象或Biome对象作为参数，返回给定生物群系在给定级别的生物群系注册表中的显示名称。如果给定级别的生物群系注册表不存在或者给定的生物群系在注册表中没有对应的键，则返回的字符串将为空。
    @OnlyIn(Dist.CLIENT)
    public static String getBiomeNameForDisplay(Level level, ResourceLocation biome) {
        if (getBiomeForKey(level, biome).isPresent()) {
            return getBiomeNameForDisplay(level, getBiomeForKey(level, biome).get());
        }
        return "";
    }

    // 这两个方法分别接收一个Level对象和一个ResourceLocation对象或Biome对象作为参数，返回给定生物群系在给定级别的生物群系注册表中的显示名称。如果给定级别的生物群系注册表不存在或者给定的生物群系在注册表中没有对应的键，则返回的字符串将为空。
    @OnlyIn(Dist.CLIENT)
    public static String getBiomeNameForDisplay(Level level, Biome biome) {
        if (biome != null) {
            final String original = getBiomeName(level, biome);
            // 由于获得的name是系统用的，这里需要坐下处理
            String fixed = "";
            char pre = ' ';
            for (int i = 0; i < original.length(); i++) {
                final char c = original.charAt(i);
                if (Character.isUpperCase(c) && Character.isLowerCase(pre) && Character.isAlphabetic(pre)) {
                    fixed = fixed + " ";
                }
                fixed = fixed + String.valueOf(c);
                pre = c;
            }
            return fixed;
        }
        return "";

    }


    // 获得生物群系名字，通过biome
    @OnlyIn(Dist.CLIENT)
    public static String getBiomeName (Level level, Biome biome){
        return getKeyForBiome(level, biome).isPresent() ? I18n.get(Util.makeDescriptionId("biome", getKeyForBiome(level, biome).get())) : "";
    }
    // 获得生物群系的名字，通过key
    @OnlyIn(Dist.CLIENT)
    public static String getBiomeName (Level level, ResourceLocation key){
        if (getBiomeForKey(level, key).isPresent()) {
            return getBiomeName(level, getBiomeForKey(level, key).get());
        }
        return "";
    }

}