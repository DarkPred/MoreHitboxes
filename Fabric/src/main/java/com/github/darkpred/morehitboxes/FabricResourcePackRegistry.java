package com.github.darkpred.morehitboxes;

import com.google.auto.service.AutoService;
import net.fabricmc.fabric.api.resource.IdentifiableResourceReloadListener;
import net.fabricmc.fabric.api.resource.ResourceManagerHelper;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.util.profiling.ProfilerFiller;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.Executor;

@ApiStatus.Internal
@AutoService(com.github.darkpred.morehitboxes.ResourcePackRegistry.class)
public class FabricResourcePackRegistry implements com.github.darkpred.morehitboxes.ResourcePackRegistry {
    private static final Collection<ResourceLocation> EMPTY = List.of();

    @Override
    public void register(PackType type, PreparableReloadListener listener) {
        var id = new ResourceLocation(MoreHitboxesMod.MOD_ID + ":reload_" + listener.getName().toLowerCase());
        ResourceManagerHelper.get(type).registerReloadListener(new IdentifiableResourceReloadListener() {
            @Override
            public ResourceLocation getFabricId() {
                return id;
            }

            @Override
            public @NotNull String getName() {
                return listener.getName();
            }

            @Override
            public Collection<ResourceLocation> getFabricDependencies() {
                return EMPTY;
            }

            @Override
            public @NotNull CompletableFuture<Void> reload(PreparationBarrier preparationBarrier, ResourceManager resourceManager, ProfilerFiller profilerFiller, ProfilerFiller profilerFiller2, Executor executor, Executor executor2) {
                return listener.reload(preparationBarrier, resourceManager, profilerFiller, profilerFiller2, executor, executor2);
            }
        });
    }
}
