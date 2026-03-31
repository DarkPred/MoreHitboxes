package com.github.darkpred.morehitboxes.network;

import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.DirectionalPayloadHandler;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.ApiStatus;

@ApiStatus.Internal
public class NetworkRegistry {
    private static final String PROTOCOL_VERSION = "1";

    public static void init(RegisterPayloadHandlersEvent event) {
        // Sets the current network version
        PayloadRegistrar registrar = event.registrar(PROTOCOL_VERSION);
        registrar.playToClient(
                SyncHitboxDataPayload.TYPE,
                SyncHitboxDataPayload.STREAM_CODEC,
                new DirectionalPayloadHandler<>(
                        ClientPayloadHandler::handle,
                        (payload, context) -> context.finishCurrentTask(SyncHitboxDataTask.TYPE)
                )
        );
    }
}
