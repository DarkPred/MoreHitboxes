package com.github.darkpred.morehitboxes.network;

import com.github.darkpred.morehitboxes.MoreHitboxesMod;
import com.github.darkpred.morehitboxes.internal.HitboxDataLoader;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.network.ConfigurationTask;
import net.neoforged.neoforge.network.configuration.ICustomConfigurationTask;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.function.Consumer;

@ApiStatus.Internal
public record SyncHitboxDataTask() implements ICustomConfigurationTask {
    public static final ConfigurationTask.Type TYPE = new ConfigurationTask.Type(new ResourceLocation(MoreHitboxesMod.MOD_ID, "sync_hitbox_data"));

    @Override
    public void run(Consumer<CustomPacketPayload> sender) {
        SyncHitboxDataPayload payload = new SyncHitboxDataPayload(HitboxDataLoader.HITBOX_DATA.getHitboxData());
        sender.accept(payload);
    }

    @Override
    public @NotNull Type type() {
        return TYPE;
    }
}
