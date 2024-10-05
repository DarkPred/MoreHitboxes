package com.github.darkpred.morehitboxes;

import com.github.darkpred.morehitboxes.internal.HitboxDataLoader;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricMoreHitboxesMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        MoreHitboxesMod.init();
        ResourceLocation location = new ResourceLocation(MoreHitboxesMod.MOD_ID, HitboxDataLoader.HITBOX_DATA.getName().toLowerCase());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (joined) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                HitboxDataLoader.HITBOX_DATA.writeBuf(buf);
                ServerPlayNetworking.send(player, location, buf);
            }
        });
    }
}
