package com.github.darkpred.morehitboxes;

import com.github.darkpred.morehitboxes.entity.HitboxDataLoader;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;

@ApiStatus.Internal
public class FabricMoreHitboxesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        com.github.darkpred.morehitboxes.ClientInit.clientInit();
        ResourceLocation location = new ResourceLocation(MoreHitboxesMod.MOD_ID, HitboxDataLoader.HITBOX_DATA.getName().toLowerCase());
        ClientPlayNetworking.registerGlobalReceiver(location, (client, handler, buf, responseSender) -> {
            var map = buf.readMap(HashMap::new, FriendlyByteBuf::readResourceLocation, HitboxDataLoader::readBuf);
            client.execute(() -> HitboxDataLoader.HITBOX_DATA.replaceData(map));
        });
    }
}
