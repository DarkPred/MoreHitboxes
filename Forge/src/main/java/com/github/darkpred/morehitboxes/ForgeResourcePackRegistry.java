package com.github.darkpred.morehitboxes;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import org.jetbrains.annotations.ApiStatus;

import java.util.List;

@ApiStatus.Internal
@Mod.EventBusSubscriber(modid = MoreHitboxesMod.MOD_ID)
@AutoService(com.github.darkpred.morehitboxes.ResourcePackRegistry.class)
public class ForgeResourcePackRegistry implements com.github.darkpred.morehitboxes.ResourcePackRegistry {
    private static final List<PreparableReloadListener> serverDataReloadListeners = Lists.newArrayList();

    @Override
    public void register(PackType type, PreparableReloadListener listener) {
        if (type == PackType.SERVER_DATA) {
            serverDataReloadListeners.add(listener);
        } else if (type == PackType.CLIENT_RESOURCES) {
            registerClient(listener);
        }
    }

    private static void registerClient(PreparableReloadListener listener) {
        if (Minecraft.getInstance() != null) {
            ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
        }
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        for (PreparableReloadListener listener : serverDataReloadListeners) {
            event.addListener(listener);
        }
    }
}
