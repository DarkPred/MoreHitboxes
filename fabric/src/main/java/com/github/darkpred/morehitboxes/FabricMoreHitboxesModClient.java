package com.github.darkpred.morehitboxes;

import com.github.darkpred.morehitboxes.internal.GeckoLibEvents;
import com.github.darkpred.morehitboxes.internal.HitboxDataLoader;
import com.github.darkpred.morehitboxes.network.SyncHitboxDataPayload;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.loader.api.FabricLoader;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class FabricMoreHitboxesModClient implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientPlayNetworking.registerGlobalReceiver(SyncHitboxDataPayload.TYPE, (payload, context) -> {
            context.client().execute(() -> HitboxDataLoader.HITBOX_DATA.replaceData(payload.hitboxes()));
        });
        if (FabricLoader.getInstance().isModLoaded("geckolib")) {
            GeckoLibEvents.init();
        }
    }
}
