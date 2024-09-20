package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.PacketByteBufs;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.HashMap;

public class MultiPartSupportMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();
        ResourceLocation location = new ResourceLocation(CommonClass.MOD_ID, EntityHitboxManager.HITBOX_DATA.getName().toLowerCase());
        ServerLifecycleEvents.SYNC_DATA_PACK_CONTENTS.register((player, joined) -> {
            if (joined) {
                ServerPlayNetworking.send(player, location, EntityHitboxManager.HITBOX_DATA.writeBuf(PacketByteBufs.create()));
            }//TODO: Client receives empty buf
        });
        ClientPlayNetworking.registerGlobalReceiver(location, (client, handler, buf, responseSender) -> {
            client.execute(() -> {
                EntityHitboxManager.HITBOX_DATA.replaceData(buf.readMap(HashMap::new, FriendlyByteBuf::readResourceLocation, EntityHitboxManager::readBuf));
            });
        });
    }
}
