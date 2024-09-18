package com.github.darkpred.multipartsupport;

import com.github.darkpred.multipartsupport.platform.Services;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.TextComponent;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.minecraft.world.item.TooltipFlag;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class CommonClass {
    public static final String MOD_ID = "multipartsupport";
    public static final String MOD_NAME = "MultiPartSupport";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_NAME);

    // This method serves as an initialization hook for the mod. The vanilla
    // game has no mechanism to load tooltip listeners so this must be
    // invoked from a mod loader specific project like Forge or Fabric.
    public static void init() {

    }
}