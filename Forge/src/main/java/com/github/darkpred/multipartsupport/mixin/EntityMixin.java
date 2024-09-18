package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.phys.AABB;
import net.minecraftforge.common.extensions.IForgePlayer;
import net.minecraftforge.entity.PartEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import javax.annotation.Nullable;
import java.util.List;

@Mixin(Entity.class)
public abstract class EntityMixin implements IForgePlayer {

    @Inject(method = "refreshDimensions", at = @At("RETURN"))
    public void refreshDimensionsForParts(CallbackInfo ci) {
        //TODO: setPos if flag set in entity
        if (this instanceof MultiPartEntity multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                part.getEntity().refreshDimensions();
            }
        }
    }
    @Inject(method = "isPickable", at = @At("HEAD"), cancellable = true)
    public void preventPickable(CallbackInfoReturnable<Boolean> cir) {
        if (this instanceof MultiPartEntity multiPartEntity && multiPartEntity.getPlaceHolderName().hasCustomParts()) {
            cir.setReturnValue(false);
        }
    }
    @Inject(method = "setBoundingBox", at = @At("RETURN"))
    public void updateBounds(AABB aABB, CallbackInfo ci) {
        this.attackBounds = makeAttackBounds();
        this.cullingBounds = makeBoundingBoxForCulling();
    }

    @Inject(method = "setId", at = @At("RETURN"))
    public void setPartIds(int id, CallbackInfo ci) {
        if (this instanceof MultiPartEntity multiPartEntity) {
            var list = multiPartEntity.getPlaceHolderName().getCustomParts();
            for (int i = 0; i < list.size(); i++) {
                list.get(i).getEntity().setId(id + i + 1);
            }
        }
    }

    @Inject(method = "remove", at = @At("RETURN"))
    public void callRemoveCallback(Entity.RemovalReason removalReason, CallbackInfo ci) {
        if (this instanceof MultiPartEntity multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                part.getEntity().remove(removalReason);
            }
        }
    }

    @Inject(method = "onClientRemoval", at = @At("RETURN"))
    public void removePartsOnClientRemoval(CallbackInfo ci) {
        if (this instanceof MultiPartEntity multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                part.getEntity().remove(Entity.RemovalReason.DISCARDED);
            }
        }
    }
}
