package com.github.darkpred.morehitboxes.internal;


import software.bernie.geckolib.event.GeoRenderEvent;

public class GeckoLibEvents {

    public static void incrementCurrentRenderTick(GeoRenderEvent.Entity.Post event) {
        if (event.getEntity() instanceof GeckoLibMultiPartMob multiPartMob) {
            multiPartMob.moreHitboxes$updateRenderTick();
        }
    }
}
