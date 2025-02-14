package com.github.darkpred.morehitboxes.mixin;

import com.github.darkpred.morehitboxes.MultiPartLevel;
import com.github.darkpred.morehitboxes.api.MultiPart;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import net.minecraft.client.multiplayer.ClientLevel;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;

import java.util.Collection;

/**
 * Based on what forge does with PartEntity and ClientLevel#dragonParts
 */
@Mixin(ClientLevel.class)
public abstract class ClientLevelMixin implements MultiPartLevel {
    @Unique
    private final Int2ObjectMap<MultiPart<?>> moreHitboxes$multiParts = new Int2ObjectOpenHashMap<>();

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
}
