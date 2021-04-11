package com.vulp.druidcraft.client.renders;

import com.vulp.druidcraft.Druidcraft;
import com.vulp.druidcraft.client.models.BeetleEntityModel;
import com.vulp.druidcraft.entities.BeetleEntity;
import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererManager;
import net.minecraft.client.renderer.entity.MobRenderer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class BeetleEntityRenderer extends MobRenderer<BeetleEntity, BeetleEntityModel<BeetleEntity>>
{
    private static final ResourceLocation BEETLE = new ResourceLocation(Druidcraft.MODID, "textures/entity/beetle/beetle.png");

    public BeetleEntityRenderer(EntityRendererManager manager)
    {
        super(manager, new BeetleEntityModel<>(), 1.2f);
    }

    @Override
    public ResourceLocation getEntityTexture(BeetleEntity entity) {
        return BEETLE;
    }

    public static class RenderFactory implements IRenderFactory<BeetleEntity>
    {
        @Override
        public EntityRenderer<? super BeetleEntity> createRenderFor(EntityRendererManager manager)
        {
            return new BeetleEntityRenderer(manager);
        }
    }
}