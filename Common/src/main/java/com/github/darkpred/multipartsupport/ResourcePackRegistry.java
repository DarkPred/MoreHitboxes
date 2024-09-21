package com.github.darkpred.multipartsupport;

import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public interface ResourcePackRegistry {
    void register(PackType type, PreparableReloadListener listener);
}
