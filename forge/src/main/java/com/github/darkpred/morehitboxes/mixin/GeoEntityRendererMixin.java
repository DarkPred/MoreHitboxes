package com.github.darkpred.morehitboxes.mixin;


import com.github.darkpred.morehitboxes.api.*;
import com.github.darkpred.morehitboxes.internal.GeckoLibMultiPartMob;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.renderer.MultiBufferSource;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.Vec3;
import org.joml.Matrix4f;
import org.joml.Vector3d;
import org.joml.Vector3fc;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib.cache.object.GeoBone;
import software.bernie.geckolib.core.animatable.GeoAnimatable;
import software.bernie.geckolib.renderer.GeoEntityRenderer;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends Entity & GeoAnimatable> {

    /**
     * Save localMatrix for next mixin
     */
    @Inject(method = "renderRecursively(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            require = 0, remap = false, at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lsoftware/bernie/geckolib/cache/object/GeoBone;setModelSpaceMatrix(Lorg/joml/Matrix4f;)V"))
    public void getBonePositions(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer,
                                 boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red,
                                 float green, float blue, float alpha, CallbackInfo ci, @Local(name = "localMatrix") Matrix4f localMatrix,
                                 @Share("localMatrix") LocalRef<Matrix4f> shared) {
        shared.set(new Matrix4f(localMatrix));
    }

    /**
     * Fixes bug where original call uses translation instead of translate
     */
    @WrapOperation(method = "renderRecursively(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            require = 0, remap = false, at = @At(value = "INVOKE",
            target = "Lorg/joml/Matrix4f;translation(Lorg/joml/Vector3fc;)Lorg/joml/Matrix4f;"))
    public Matrix4f getBonePositions(Matrix4f instance, Vector3fc offset, Operation<Matrix4f> original, @Share("localMatrix") LocalRef<Matrix4f> shared) {
        //Only if no other mod has fixed the bug. Also need to reset the value for the 2nd call
        if (instance.equals(shared.get())) {
            shared.set(new Matrix4f(instance.translate(offset)));
            return shared.get();
        }
        return instance;
    }

    @Inject(method = "renderRecursively(Lcom/mojang/blaze3d/vertex/PoseStack;Lnet/minecraft/world/entity/Entity;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;ZFIIFFFF)V",
            require = 0, remap = false, at = @At(value = "INVOKE", shift = At.Shift.AFTER,
            target = "Lsoftware/bernie/geckolib/renderer/GeoEntityRenderer;applyRenderLayersForBone(Lcom/mojang/blaze3d/vertex/PoseStack;Lsoftware/bernie/geckolib/core/animatable/GeoAnimatable;Lsoftware/bernie/geckolib/cache/object/GeoBone;Lnet/minecraft/client/renderer/RenderType;Lnet/minecraft/client/renderer/MultiBufferSource;Lcom/mojang/blaze3d/vertex/VertexConsumer;FII)V"))
    public void getBonePositions(PoseStack poseStack, T animatable, GeoBone bone, RenderType renderType, MultiBufferSource bufferSource, VertexConsumer buffer, boolean isReRender, float partialTick, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        if (animatable instanceof GeckoLibMultiPartEntity<?> multiPartEntity) {
            if (animatable instanceof GeckoLibMultiPartMob multiPartMob && !multiPartMob.moreHitboxes$isNewRenderTick()) {
                return;
            }
            MultiPart<?> part = multiPartEntity.getEntityHitboxData().getCustomPart(bone.getName());
            if (part != null) {
                //Tick hitboxes
                Vector3d localPos = bone.getLocalPosition();
                part.setOverride(new AnimationOverride(new Vec3(localPos.x, localPos.y, localPos.z), bone.getScaleX(), bone.getScaleY()));
                //TODO: Could also update the position of the part directly but that would make separating the library from geckolib more tedious
            } else if (multiPartEntity.getEntityHitboxData().getAnchorData().isAnchor(bone.getName())) {
                Vector3d localPos = bone.getLocalPosition();
                multiPartEntity.getEntityHitboxData().getAnchorData().updatePosition(bone.getName(), new Vec3(localPos.x, localPos.y, localPos.z));
            } else if (multiPartEntity.canSetAnchorPos(bone.getName())) {
                Vector3d localPos = bone.getLocalPosition();
                multiPartEntity.setAnchorPos(bone.getName(), new Vec3(localPos.x, localPos.y, localPos.z));
            } else {
                AttackBoxData attackBoxData = multiPartEntity.getEntityHitboxData().getAttackBoxData();
                HitboxData attackBox = attackBoxData.getAttackBox(bone.getName());
                if (attackBox != null && attackBoxData.isAttackBoxActive(attackBox)) {
                    Vector3d worldPos = bone.getWorldPosition();
                    multiPartEntity.getEntityHitboxData().getAttackBoxData().moveActiveAttackBox(attackBox, new Vec3(worldPos.x, worldPos.y, worldPos.z));
                }
            }
        }
    }
}
