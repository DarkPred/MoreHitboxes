package com.github.darkpred.multipartsupport.mixin;

import com.github.darkpred.multipartsupport.entity.GeckoLibMultiPartEntity;
import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.github.darkpred.multipartsupport.entity.MultiPartEntity;
import com.github.darkpred.multipartsupport.entity.MultiPartGeoEntityRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.world.entity.Entity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(Entity.class)
public abstract class ClientEntityMixin {

    @Inject(method = "onClientRemoval", at = @At("RETURN"))
    public void removePartsOnClientRemoval(CallbackInfo ci) {
        if (this instanceof MultiPartEntity<?> multiPartEntity) {
            for (MultiPart<?> part : multiPartEntity.getPlaceHolderName().getCustomParts()) {
                part.getEntity().remove(Entity.RemovalReason.DISCARDED);
            }
            if (this instanceof GeckoLibMultiPartEntity<?>) {
                var renderer = Minecraft.getInstance().getEntityRenderDispatcher().getRenderer(((Entity) (Object) this));
                if (renderer instanceof MultiPartGeoEntityRenderer renderer1) {
                    renderer1.removeTickForEntity(((Entity) (Object) this));
                }
            }
        }
    }
}
