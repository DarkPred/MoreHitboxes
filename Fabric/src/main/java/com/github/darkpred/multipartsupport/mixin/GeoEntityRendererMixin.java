package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.AnimationOverride;
import com.github.darkpred.multipartsupport.entity.GeckoLibMultiPartEntity;
import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.github.darkpred.multipartsupport.entity.MultiPartGeoEntityRenderer;
import com.mojang.blaze3d.vertex.PoseStack;
import com.mojang.blaze3d.vertex.VertexConsumer;
import com.mojang.math.Vector3d;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.phys.Vec3;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import software.bernie.geckolib3.core.IAnimatable;
import software.bernie.geckolib3.geo.render.built.GeoBone;
import software.bernie.geckolib3.renderers.geo.GeoEntityRenderer;

import java.util.HashMap;
import java.util.Map;

@Mixin(GeoEntityRenderer.class)
public abstract class GeoEntityRendererMixin<T extends LivingEntity & IAnimatable> implements MultiPartGeoEntityRenderer {
    @Shadow
    protected T animatable;
    @Unique
    private final Map<Integer, Integer> tickForEntity = new HashMap<>();

    @Inject(method = "renderRecursively", at = @At(value = "INVOKE", shift = At.Shift.AFTER, target = "Lsoftware/bernie/geckolib3/geo/render/built/GeoBone;cubesAreHidden()Z"))
    public void getBonePositions(GeoBone bone, PoseStack poseStack, VertexConsumer buffer, int packedLight, int packedOverlay, float red, float green, float blue, float alpha, CallbackInfo ci) {
        //TODO: Move this into non mixin function for easier debugging
        if (animatable instanceof GeckoLibMultiPartEntity<?> multiPartEntity && entityTickMatchesRenderTick(animatable)) {
            MultiPart<?> part = multiPartEntity.getPlaceHolderName().getCustomPart(bone.name);
            if (part != null) {
                //Tick hitboxes
                Vector3d localPos = bone.getLocalPosition();
                part.setOverride(new AnimationOverride(new Vec3(localPos.x, localPos.y, localPos.z), bone.getScaleX(), bone.getScaleY()));
                //TODO: Could also update the position of the part directly but that would make separating the library from geckolib more tedious
            } else if (multiPartEntity.canSetAnchorPos(bone.name)) {
                Vector3d localPos = bone.getLocalPosition();
                multiPartEntity.setAnchorPos(bone.name, new Vec3(localPos.x, localPos.y, localPos.z));
            }
            //TODO: Attackbox stuff
        }
    }

    @Unique
    private boolean entityTickMatchesRenderTick(T animatable) {
        return getTickForEntity(animatable) == animatable.tickCount;
    }

    @Unique
    private int getTickForEntity(Entity entity) {
        //TODO: Maybe tickCount without +1
        return tickForEntity.computeIfAbsent(entity.getId(), integer -> entity.tickCount + 1);
    }

    @Unique
    public void removeTickForEntity(Entity entity) {
        tickForEntity.remove(entity.getId());
    }

    @Unique
    public void updateTickForEntity(Entity entity) {
        if (getTickForEntity(entity) <= entity.tickCount) {
            tickForEntity.put(entity.getId(), entity.tickCount);
        }
    }
}
