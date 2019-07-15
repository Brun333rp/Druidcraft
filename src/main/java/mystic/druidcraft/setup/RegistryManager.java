package mystic.druidcraft.setup;

import epicsquid.mysticallib.factories.ItemGenerator;
import epicsquid.mysticallib.factories.ToolFactories;
import epicsquid.mysticallib.material.BaseItemTier;
import epicsquid.mysticallib.material.MaterialProperties;
import mystic.druidcraft.Druidcraft;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = Druidcraft.MODID, bus = Mod.EventBusSubscriber.Bus.MOD)
public class RegistryManager {

	@SubscribeEvent
	public static void registerBlocks(RegistryEvent.Register<Block> event) {

	}

	@SubscribeEvent
	public static void registerItems(RegistryEvent.Register<Item> event) {
		event.getRegistry().register(new Item(new Item.Properties().group(Druidcraft.setup.tab)).setRegistryName("amber"));
		ItemGenerator generator = new ItemGenerator();
		generator.addAllFactories(ToolFactories.getFactories());
		generator.execute(new MaterialProperties()
										.setItemProps(() -> new Item.Properties().maxDamage(256).maxStackSize(1).group(Druidcraft.setup.tab))
										.setName("bone")
										.setDamage("SWORD", 7)
										.setAttackSpeed("SWORD", 1.6f)
										.setTier(() -> new BaseItemTier(256, 5.0f, 4.0f, 4, 4,
														() -> Ingredient.fromItems(Items.IRON_INGOT)))
						, event);
	}
}
