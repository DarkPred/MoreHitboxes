package com.github.darkpred.multipartsupport;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.item.v1.ItemTooltipCallback;

public class MultiPartSupportMod implements ModInitializer {
    
    @Override
    public void onInitialize() {
        CommonClass.init();
    }
}
