package com.github.darkpred.morehitboxes;

import com.github.darkpred.morehitboxes.internal.GeckoLibEvents;
import com.github.darkpred.morehitboxes.network.NetworkRegistry;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.ModList;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.loading.FMLLoader;
import net.neoforged.neoforge.common.NeoForge;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
@Mod(MoreHitboxesMod.MOD_ID)
public class NeoForgeMoreHitboxesMod {

    public NeoForgeMoreHitboxesMod(IEventBus modEventBus) {
        MoreHitboxesMod.init();
        if (ModList.get().isLoaded("geckolib") && FMLLoader.getDist() == Dist.CLIENT) {
            NeoForge.EVENT_BUS.addListener(GeckoLibEvents::incrementCurrentRenderTick);
        }
        modEventBus.addListener(NetworkRegistry::init);
    }
}