package com.github.darkpred.morehitboxes;

import com.github.darkpred.morehitboxes.internal.HitboxDataLoader;
import com.github.darkpred.morehitboxes.platform.Services;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ClientInit {

    public static void clientInit() {
        Services.RESOURCE_PACK_PROVIDER.register(PackType.CLIENT_RESOURCES, HitboxDataLoader.HITBOX_DATA);
    }
}
