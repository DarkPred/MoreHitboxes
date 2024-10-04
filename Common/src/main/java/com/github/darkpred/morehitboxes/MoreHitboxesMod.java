package com.github.darkpred.morehitboxes;

import com.github.darkpred.morehitboxes.entity.HitboxDataLoader;
import com.github.darkpred.morehitboxes.platform.Services;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal

public class MoreHitboxesMod {
    public static final String MOD_ID = "morehitboxes";
    public static final String MOD_NAME = "More Hitboxes";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        Services.RESOURCE_PACK_PROVIDER.register(PackType.SERVER_DATA, HitboxDataLoader.HITBOX_DATA);
    }
}