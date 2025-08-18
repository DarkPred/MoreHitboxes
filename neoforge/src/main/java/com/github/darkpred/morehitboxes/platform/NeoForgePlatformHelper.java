package com.github.darkpred.morehitboxes.platform;

import com.github.darkpred.morehitboxes.platform.services.IPlatformHelper;
import com.google.auto.service.AutoService;
import net.neoforged.fml.ModList;
import net.neoforged.fml.loading.FMLLoader;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@AutoService(IPlatformHelper.class)
public class NeoForgePlatformHelper implements IPlatformHelper {

    @Override
    public String getPlatformName() {
        return "Forge";
    }

    @Override
    public boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    @Override
    public boolean isDevelopmentEnvironment() {
        return !FMLLoader.isProduction();
    }
}
