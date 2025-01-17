package com.vulp.druidcraft.registry;

import com.vulp.druidcraft.DruidcraftRegistry;
import com.vulp.druidcraft.world.biomes.BiomeCreation;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.registry.Registry;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeManager;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.HashSet;
import java.util.Set;

@Mod.EventBusSubscriber(bus = Mod.EventBusSubscriber.Bus.MOD)
public class BiomeRegistry {
    private static final Set<Biome> BIOMES = new HashSet<>();

    public static class BiomeKeys {

        public static final RegistryKey<Biome> darkwood_forest = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, DruidcraftRegistry.location("darkwood_forest"));
        public static final RegistryKey<Biome> fervid_jungle = RegistryKey.getOrCreateKey(Registry.BIOME_KEY, DruidcraftRegistry.location("fervid_jungle"));

    }

    public static Biome register(String name, Biome biome) {
        biome.setRegistryName(DruidcraftRegistry.location(name));
        BIOMES.add(biome);
        return biome;
    }

    @SubscribeEvent
    public static void register (RegistryEvent.Register<Biome> event) {

        register("darkwood_forest", BiomeCreation.makeDarkwoodForest());
        register("fervid_jungle", BiomeCreation.makeFervidJungleBiome());

        event.getRegistry().registerAll(BIOMES.toArray(new Biome[0]));
        registerBiomes();
    }

    public static void registerBiomes() {
        // TODO: Make weight of darkwood forest configurable
        BiomeManager.addBiome(BiomeManager.BiomeType.COOL, new BiomeManager.BiomeEntry(BiomeKeys.darkwood_forest, 1));
        BiomeDictionary.addTypes(BiomeKeys.darkwood_forest, BiomeDictionary.Type.CONIFEROUS, BiomeDictionary.Type.OVERWORLD, BiomeDictionary.Type.DENSE, BiomeDictionary.Type.MAGICAL);

        BiomeManager.addBiome(BiomeManager.BiomeType.WARM, new BiomeManager.BiomeEntry(BiomeKeys.fervid_jungle, 10));
        BiomeDictionary.addTypes(BiomeKeys.fervid_jungle, BiomeDictionary.Type.HOT, BiomeDictionary.Type.JUNGLE, BiomeDictionary.Type.DRY, BiomeDictionary.Type.NETHER);
    }
}
