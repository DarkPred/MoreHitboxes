package com.github.darkpred.multipartsupport.mixin;

import net.minecraft.server.level.ChunkMap;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

/**
 * Fabric has no PartEntity so we mixin our own
 *
 * @see ServerLevelMixin
 */
@Mixin(ChunkMap.class)
public abstract class ChunkMapMixin {

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    public void doNotAddMultiPart(Entity entity, CallbackInfo ci) {
         if (entity instanceof MultiPart) {
            ci.cancel();
        }
    }
}
