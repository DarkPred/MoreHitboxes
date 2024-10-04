package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import net.minecraft.world.entity.Mob;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Mob.class)
public abstract class MobMixin {

    @Inject(method = "aiStep", at = @At("RETURN"))
    public void tickCustomParts(CallbackInfo ci) {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                part.updatePosition();
            }
        }
    }
}
