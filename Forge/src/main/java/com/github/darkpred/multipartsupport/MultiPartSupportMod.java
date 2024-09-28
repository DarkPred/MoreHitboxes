package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.entity.EntityHitboxManager;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.OnDatapackSyncEvent;
import net.minecraftforge.fml.DistExecutor;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.PacketDistributor;
import net.minecraftforge.network.simple.SimpleChannel;
import org.jetbrains.annotations.ApiStatus;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

@ApiStatus.Internal
@Mod(CommonClass.MOD_ID)
public class MultiPartSupportMod {
    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(CommonClass.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public MultiPartSupportMod() {
        CommonClass.init();
        MinecraftForge.EVENT_BUS.addListener(this::onDatapackSyncEvent);
        INSTANCE.registerMessage(0, SyncHitboxDataMessage.class, SyncHitboxDataMessage::write, SyncHitboxDataMessage::new, SyncHitboxDataMessage::handle);
        DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> ClientInit::clientInit);
    }

    public void onDatapackSyncEvent(OnDatapackSyncEvent event) {
        INSTANCE.send(PacketDistributor.PLAYER.with(event::getPlayer), new SyncHitboxDataMessage(EntityHitboxManager.HITBOX_DATA.getEntities()));
    }

    private static class SyncHitboxDataMessage {
        private final Map<ResourceLocation, List<EntityHitboxManager.HitboxData>> hitboxes;

        public SyncHitboxDataMessage(FriendlyByteBuf buf) {
            this.hitboxes = buf.readMap(HashMap::new, FriendlyByteBuf::readResourceLocation, EntityHitboxManager::readBuf);
        }

        public SyncHitboxDataMessage(Map<ResourceLocation, List<EntityHitboxManager.HitboxData>> hitboxes) {
            this.hitboxes = hitboxes;
        }

        public void write(FriendlyByteBuf buf) {
            EntityHitboxManager.HITBOX_DATA.writeBuf(buf);
        }

        public void handle(Supplier<NetworkEvent.Context> ctx) {
            ctx.get().enqueueWork(() -> DistExecutor.unsafeRunWhenOn(Dist.CLIENT, () -> () -> {
                EntityHitboxManager.HITBOX_DATA.replaceData(hitboxes);
            }));
            ctx.get().setPacketHandled(true);
        }
    }
}