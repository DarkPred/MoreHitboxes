package com.github.darkpred.multipartsupport;

import com.google.auto.service.AutoService;
import com.google.common.collect.Lists;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.resources.PreparableReloadListener;
import net.minecraft.server.packs.resources.ReloadableResourceManager;
import net.minecraftforge.event.AddReloadListenerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.List;

@Mod.EventBusSubscriber(modid = CommonClass.MOD_ID)
@AutoService(ResourcePackRegistry.class)
public class ForgeResourcePackRegistry implements ResourcePackRegistry {
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
        ((ReloadableResourceManager) Minecraft.getInstance().getResourceManager()).registerReloadListener(listener);
    }

    @SubscribeEvent
    public static void addReloadListeners(AddReloadListenerEvent event) {
        for (PreparableReloadListener listener : serverDataReloadListeners) {
            event.addListener(listener);
        }
    }
}
