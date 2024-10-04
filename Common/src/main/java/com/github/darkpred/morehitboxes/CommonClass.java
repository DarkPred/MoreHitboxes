package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import com.github.darkpred.multipartsupport.platform.Services;
import net.minecraft.server.packs.PackType;
import org.jetbrains.annotations.ApiStatus;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApiStatus.Internal
//TODO: Rename classes
public class CommonClass {
    //TODO: Think of better mod name
    public static final String MOD_ID = "multipartsupport";
    public static final String MOD_NAME = "MultiPartSupport";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    public static void init() {
        Services.RESOURCE_PACK_PROVIDER.register(PackType.SERVER_DATA, EntityHitboxManager.HITBOX_DATA);
    }
}