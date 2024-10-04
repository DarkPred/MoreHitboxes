package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

@ApiStatus.Internal
public class MultiPartSupportModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        com.github.darkpred.multipartsupport.ClientInit.clientInit();
        ResourceLocation location = new ResourceLocation(com.github.darkpred.multipartsupport.CommonClass.MOD_ID, EntityHitboxManager.HITBOX_DATA.getName().toLowerCase());
        ClientPlayNetworking.registerGlobalReceiver(location, (client, handler, buf, responseSender) -> {
            var map = buf.readMap(HashMap::new, FriendlyByteBuf::readResourceLocation, EntityHitboxManager::readBuf);
            client.execute(() -> EntityHitboxManager.HITBOX_DATA.replaceData(map));
        });
    }
}
