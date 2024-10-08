package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.MultiPartServerLevel;
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
public abstract class ServerLevelMixin implements MultiPartServerLevel {
    @Unique
    private final Int2ObjectMap<Entity> multiParts = new Int2ObjectOpenHashMap<>();

    @Shadow
    protected abstract LevelEntityGetter<Entity> getEntities();

    @Override
    public Collection<Entity> moreHitboxes$getMultiParts() {
        return multiParts.values();
    }

    @Override
    public void moreHitboxes$addMultiPart(Entity part) {
        multiParts.put(part.getId(), part);
    }

    @Override
    public void moreHitboxes$removeMultiPart(Entity part) {
        multiParts.remove(part.getId());
    }


    @Inject(method = "getEntityOrPart(I)Lnet/minecraft/world/entity/Entity;", at = @At("TAIL"), cancellable = true)
    private void getEntityOrMultiPart(int id, CallbackInfoReturnable<Entity> cir) {
        if (getEntities().get(id) == null && multiParts.containsKey(id)) {
            cir.setReturnValue(multiParts.get(id));
        }
    }
}
