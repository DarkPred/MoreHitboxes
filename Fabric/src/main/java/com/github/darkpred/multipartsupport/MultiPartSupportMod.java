package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

@ApiStatus.Internal
public class MultiPartSupportMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();
        ResourceLocation location = new ResourceLocation(CommonClass.MOD_ID, EntityHitboxManager.HITBOX_DATA.getName().toLowerCase());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (joined) {
                FriendlyByteBuf buf = PacketByteBufs.create();
                EntityHitboxManager.HITBOX_DATA.writeBuf(buf);
                ServerPlayNetworking.send(player, location, buf);
            }
        });
        ClientPlayNetworking.registerGlobalReceiver(location, (client, handler, buf, responseSender) -> {
            var map = buf.readMap(HashMap::new, FriendlyByteBuf::readResourceLocation, EntityHitboxManager::readBuf);
            client.execute(() -> EntityHitboxManager.HITBOX_DATA.replaceData(map));
        });
    }
}
