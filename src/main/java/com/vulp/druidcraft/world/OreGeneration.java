package com.vulp.druidcraft.world;

import com.vulp.druidcraft.config.WorldGenConfig;
import com.vulp.druidcraft.registry.BlockRegistry;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraft.world.gen.GenerationStage;
import net.minecraft.world.gen.feature.Feature;
import net.minecraft.world.gen.feature.OreFeatureConfig;
import net.minecraft.world.gen.placement.CountRangeConfig;
import net.minecraft.world.gen.placement.DepthAverageConfig;
import net.minecraft.world.gen.placement.Placement;
import net.minecraftforge.registries.ForgeRegistries;

public class OreGeneration {

    public static void setupOreGeneration() {
        if (WorldGenConfig.generate_ores.get()) {
            for (Biome biome : ForgeRegistries.BIOMES) {
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.amber_ore.getDefaultState(), WorldGenConfig.amber_size.get())).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(WorldGenConfig.amber_weight.get(), 0, 0, 256))));
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.moonstone_ore.getDefaultState(), WorldGenConfig.moonstone_size.get())).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(WorldGenConfig.moonstone_weight.get(), 0, 0, 256))));
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.fiery_glass_ore.getDefaultState(), WorldGenConfig.fiery_glass_size.get())).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(WorldGenConfig.fiery_glass_weight.get(), 0, 0, 32))));
                biome.addFeature(GenerationStage.Decoration.UNDERGROUND_ORES, Feature.ORE.withConfiguration(new OreFeatureConfig(OreFeatureConfig.FillerBlockType.NATURAL_STONE, BlockRegistry.rockroot_ore.getDefaultState(), WorldGenConfig.rockroot_size.get())).withPlacement(Placement.COUNT_RANGE.configure(new CountRangeConfig(WorldGenConfig.rockroot_weight.get(), 48, 16, 256))));
            }
        }
    }
}