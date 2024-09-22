package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.api.IPlaceHolderName;
import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.LevelRenderer;
import net.minecraft.client.renderer.entity.EntityRenderDispatcher;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.Mob;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Map;

/**
 * Used for debugging multiple hitboxes
 */
@Mixin(EntityRenderDispatcher.class)
public abstract class EntityRenderDispatcherMixin {

    @Inject(method = "renderHitbox", at = @At(value = "INVOKE", shift = At.Shift.AFTER, ordinal = 0, target = "Lnet/minecraft/client/renderer/LevelRenderer;renderLineBox(Lcom/mojang/blaze3d/vertex/PoseStack;Lcom/mojang/blaze3d/vertex/VertexConsumer;Lnet/minecraft/world/phys/AABB;FFFF)V"))
    private static void renderMultipartHitbox(PoseStack poseStack, VertexConsumer buffer, Entity entity, float partialTicks, CallbackInfo ci) {
        if (entity instanceof Mob mob && mob instanceof MultiPartEntity<?> multiPartEntity) {
            double d = -Mth.lerp(partialTicks, entity.xOld, entity.getX());
            double e = -Mth.lerp(partialTicks, entity.yOld, entity.getY());
            double f = -Mth.lerp(partialTicks, entity.zOld, entity.getZ());
            IPlaceHolderName<?> placeHolderName = multiPartEntity.getPlaceHolderName();
            AABB aABB = entity.getBoundingBoxForCulling().move(-entity.getX(), -entity.getY(), -entity.getZ());
            LevelRenderer.renderLineBox(poseStack, buffer, aABB, 1, 0, 1, 1);
            aABB = placeHolderName.getAttackBounds().move(-entity.getX(), -entity.getY(), -entity.getZ());
            LevelRenderer.renderLineBox(poseStack, buffer, aABB, 0, 0, 1, 1);
            for (Map.Entry<EntityHitboxManager.HitboxData, Vec3> entry : placeHolderName.getAttackBoxPlaceHolder().getActiveBoxes().entrySet()) {
                Vec3 pos = entry.getValue();
                EntityHitboxManager.HitboxData hitbox = entry.getKey();
                EntityDimensions size = EntityDimensions.scalable(hitbox.width(), hitbox.height()).scale(mob.getScale());
                AABB aabb = size.makeBoundingBox(pos);
                poseStack.pushPose();
                double g = d + pos.x;
                double h = e + pos.y;
                double i = f + pos.z;
                poseStack.translate(g, h, i);
                if (Minecraft.getInstance().player.getBoundingBox().intersects(aabb)) {
                    LevelRenderer.renderLineBox(poseStack, buffer, aabb.move(-pos.x, -pos.y, -pos.z), 1, 0, 0, 1);
                } else {
                    LevelRenderer.renderLineBox(poseStack, buffer, aabb.move(-pos.x, -pos.y, -pos.z), 0, 0, 1, 1);
                }
                poseStack.popPose();
            }
            for (MultiPart<?> multiPart : placeHolderName.getCustomParts()) {
                Entity part = multiPart.getEntity();
                poseStack.pushPose();
                double g = d + Mth.lerp(partialTicks, part.xOld, part.getX());
                double h = e + Mth.lerp(partialTicks, part.yOld, part.getY());
                double i = f + Mth.lerp(partialTicks, part.zOld, part.getZ());
                poseStack.translate(g, h, i);
                LevelRenderer.renderLineBox(poseStack, buffer, part.getBoundingBox().move(-part.getX(), -part.getY(), -part.getZ()), 0, 1, 0, 1);
                poseStack.popPose();
            }
        }
    }
}
