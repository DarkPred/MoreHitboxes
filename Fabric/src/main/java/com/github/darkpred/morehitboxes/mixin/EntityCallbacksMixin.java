package com.github.darkpred.morehitboxes.mixin;


import com.github.darkpred.morehitboxes.MultiPartServerLevel;
import com.github.darkpred.morehitboxes.entity.MultiPart;
import com.github.darkpred.morehitboxes.entity.MultiPartEntity;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;


/**
 * Equivalent to what forge does with PartEntity
 */
@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public abstract class EntityCallbacksMixin {

    @Final
    @Shadow
    ServerLevel field_26936;

    @Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "RETURN"))
    public void addMultiPartOnTrackingStart(Entity entity, CallbackInfo ci) {
        if (entity instanceof MultiPartEntity<?> multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                ((MultiPartServerLevel) field_26936).moreHitboxes$addMultiPart(part.getEntity());
            }
        }
    }

    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "RETURN"))
    public void removeMultiPartOnTrackingEnd(Entity entity, CallbackInfo ci) {
        if (entity instanceof MultiPartEntity<?> multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                ((MultiPartServerLevel) field_26936).moreHitboxes$removeMultiPart(part.getEntity());
            }
        }
    }
}