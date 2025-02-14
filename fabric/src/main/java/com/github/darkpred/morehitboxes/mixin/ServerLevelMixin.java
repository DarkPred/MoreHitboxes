package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.MultiPartLevel;
import com.github.darkpred.morehitboxes.api.MultiPart;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.LevelEntityGetter;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;

/**
 * Based on what forge does with PartEntity and ServerLevel#dragonParts
 */
@Mixin(ServerLevel.class)
public abstract class ServerLevelMixin implements MultiPartLevel {
    @Unique
    private final Int2ObjectMap<MultiPart<?>> moreHitboxes$multiParts = new Int2ObjectOpenHashMap<>();

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Override
    public Collection<MultiPart<?>> moreHitboxes$getMultiParts() {
        return moreHitboxes$multiParts.values();
    }

    @Override
    public void moreHitboxes$addMultiPart(MultiPart<?> part) {
        moreHitboxes$multiParts.put(part.getEntity().getId(), part);
    }

    @Override
    public void moreHitboxes$removeMultiPart(MultiPart<?> part) {
        moreHitboxes$multiParts.remove(part.getEntity().getId());
    }


    @Inject(method = "getEntityOrPart(I)Lnet/minecraft/world/entity/Entity;", at = @At("TAIL"), cancellable = true)
    private void getEntityOrMultiPart(int id, CallbackInfoReturnable<Entity> cir) {
        if (getEntities().get(id) == null && moreHitboxes$multiParts.containsKey(id)) {
            cir.setReturnValue(moreHitboxes$multiParts.get(id).getEntity());
        }
    }
}
