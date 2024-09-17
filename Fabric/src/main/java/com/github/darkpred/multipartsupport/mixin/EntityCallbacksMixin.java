package com.github.darkpred.multipartsupport.mixin;


import com.fossil.fossil.entity.prehistoric.base.Prehistoric;
import com.fossil.fossil.entity.prehistoric.parts.MultiPart;
import com.fossil.fossil.fabric.MultiPartServerLevel;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric has no PartEntity so we mixin our own
 *
 * @see ServerLevelMixin
 */
@Mixin(targets = "net/minecraft/server/level/ServerLevel$EntityCallbacks")
public abstract class EntityCallbacksMixin {

    @Final
    @Shadow
    ServerLevel field_26936;

    @Inject(method = "onTrackingStart(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "RETURN"))
    public void addMultiPartOnTrackingStart(Entity entity, CallbackInfo ci) {
        if (entity instanceof Prehistoric prehistoric) {
            for (MultiPart part : prehistoric.getCustomParts()) {
                ((MultiPartServerLevel) field_26936).fossilsArcheologyRevival$addMultiPart(part.getEntity());
            }
        }
    }

    @Inject(method = "onTrackingEnd(Lnet/minecraft/world/entity/Entity;)V", at = @At(value = "RETURN"))
    public void removeMultiPartOnTrackingEnd(Entity entity, CallbackInfo ci) {
        if (entity instanceof Prehistoric prehistoric) {
            for (MultiPart part : prehistoric.getCustomParts()) {
                ((MultiPartServerLevel) field_26936).fossilsArcheologyRevival$removeMultiPart(part.getEntity());
            }
        }
    }
}