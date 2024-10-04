package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import com.github.darkpred.multipartsupport.platform.Services;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class ClientInit {

    public static void clientInit() {
        Services.RESOURCE_PACK_PROVIDER.register(PackType.CLIENT_RESOURCES, EntityHitboxManager.HITBOX_DATA);
    }
}
