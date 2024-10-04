package com.github.darkpred.multipartsupport.platform;

import com.github.darkpred.multipartsupport.CommonClass;
import com.github.darkpred.multipartsupport.ResourcePackRegistry;
import com.github.darkpred.multipartsupport.entity.MultiPart;
import com.github.darkpred.multipartsupport.platform.services.IPlatformHelper;
import org.jetbrains.annotations.ApiStatus;

import java.util.ServiceLoader;

@ApiStatus.Internal
public class Services {

    public static final IPlatformHelper PLATFORM = load(IPlatformHelper.class);
    public static final ResourcePackRegistry RESOURCE_PACK_PROVIDER = load(ResourcePackRegistry.class);
    public static final MultiPart.Factory MULTI_PART = Services.load(MultiPart.Factory.class);

    public static <T> T load(Class<T> clazz) {

        final T loadedService = ServiceLoader.load(clazz)
                .findFirst()
                .orElseThrow(() -> new NullPointerException("Failed to load service for " + clazz.getName()));
        CommonClass.LOGGER.debug("Loaded {} for service {}", loadedService, clazz);
        return loadedService;
    }
}
