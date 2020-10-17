package com.vulp.druidcraft.client.renders;

import com.mojang.blaze3d.matrix.MatrixStack;
import com.mojang.blaze3d.platform.GlStateManager;
import com.vulp.druidcraft.Druidcraft;
import com.vulp.druidcraft.DruidcraftRegistry;
import com.vulp.druidcraft.client.models.DreadfishEntityModel;
import com.vulp.druidcraft.entities.DreadfishEntity;
import net.minecraft.client.renderer.entity.*;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.vector.Vector3f;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.fml.client.registry.IRenderFactory;

@OnlyIn(Dist.CLIENT)
public class DreadfishEntityRender extends MobRenderer<DreadfishEntity, DreadfishEntityModel>
{
    private static final ResourceLocation DREADFISH_HEALTH_FULL = new ResourceLocation(Druidcraft.MODID, "textures/entity/dreadfish/dreadfish_0.png");
    private static final ResourceLocation DREADFISH_HEALTH_HIGH = new ResourceLocation(Druidcraft.MODID, "textures/entity/dreadfish/dreadfish_1.png");
    private static final ResourceLocation DREADFISH_HEALTH_MEDIUM = new ResourceLocation(Druidcraft.MODID, "textures/entity/dreadfish/dreadfish_2.png");
    private static final ResourceLocation DREADFISH_HEALTH_LOW = new ResourceLocation(Druidcraft.MODID, "textures/entity/dreadfish/dreadfish_3.png");

    public DreadfishEntityRender(EntityRendererManager manager)
    {
        super(manager, new DreadfishEntityModel(), 0.3f);
    }

    @Override
    public ResourceLocation getEntityTexture(DreadfishEntity entity) {
        if (entity.isTamed()) {
            if (entity.getHealth() >= entity.getMaxHealth()) {
                return DREADFISH_HEALTH_FULL;
            } else if ((entity.getHealth() < entity.getMaxHealth()) && (entity.getHealth() >= 16.0f)) {
                return DREADFISH_HEALTH_HIGH;
            } else if ((entity.getHealth() < 16.0f) && (entity.getHealth() >= 8.0f)) {
                return DREADFISH_HEALTH_MEDIUM;
            } else if ((entity.getHealth() < 8.0f)) {
                return DREADFISH_HEALTH_LOW;
            }
            else return DREADFISH_HEALTH_FULL;
        }
        else return DREADFISH_HEALTH_FULL;
    }

    public static class RenderFactory implements IRenderFactory<DreadfishEntity>
    {
        @Override
        public EntityRenderer<? super DreadfishEntity> createRenderFor(EntityRendererManager manager)
        {
            return new DreadfishEntityRender(manager);
        }

    }

    @Override
    protected void applyRotations(DreadfishEntity entityLiving, MatrixStack matrix, float ageInTicks, float rotationYaw, float partialTicks) {
        super.applyRotations(entityLiving, matrix, ageInTicks, rotationYaw, partialTicks);
        float f = 1.0F;
        float f1 = 1.0F;
        float f2 = f * 4.3F * MathHelper.sin(f1 * 0.6F * ageInTicks);
        if (!entityLiving.isSitting()) {
            matrix.rotate(Vector3f.YP.rotationDegrees(f2));
            matrix.rotate(Vector3f.XP.rotationDegrees(0));
        } else {
            matrix.rotate(Vector3f.YP.rotationDegrees(0));
            matrix.rotate(Vector3f.XP.rotationDegrees(f2 / 3));
            // Insert hover code.
        }
        matrix.translate(0.0D, 0.05 * MathHelper.sin(ageInTicks * 0.2F), -0.4000000059604645D);
    }
}