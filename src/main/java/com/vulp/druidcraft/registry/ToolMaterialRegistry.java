package com.vulp.druidcraft.registry;

import net.minecraft.item.IItemTier;
import net.minecraft.item.Item;
import net.minecraft.item.Items;
import net.minecraft.item.crafting.Ingredient;

public enum ToolMaterialRegistry implements IItemTier
{
    bone(1.0f, 5.0f, 250, 1, 18, Items.BONE),
    moonstone(4.0f, 9.0f, 1921, 4, 15, ItemRegistry.moonstone),
    fiery(3.0f, 8.0f, 1731, 4, 20, ItemRegistry.tempered_fiery_glass);

    private float attackDamage, efficiency;
    private int durability, harvestLevel, enchantability;
    private Item repairMaterial;

    ToolMaterialRegistry(float attackDamage, float efficiency, int durability, int harvestLevel, int enchantability, Item repairMaterial) {
        this.attackDamage = attackDamage;
        this.efficiency = efficiency;
        this.durability = durability;
        this.harvestLevel = harvestLevel;
        this.enchantability = enchantability;
        this.repairMaterial = repairMaterial;
    }

    @Override
    public int getMaxUses() {
        return this.durability;
    }

    @Override
    public float getEfficiency() {
        return this.efficiency;
    }

    @Override
    public float getAttackDamage() {
        return this.attackDamage;
    }

    @Override
    public int getHarvestLevel() {
        return this.harvestLevel;
    }

    @Override
    public int getEnchantability() {
        return this.enchantability;
    }

    @Override
    public Ingredient getRepairMaterial() {
        return Ingredient.fromItems(this.repairMaterial);
    }
}
