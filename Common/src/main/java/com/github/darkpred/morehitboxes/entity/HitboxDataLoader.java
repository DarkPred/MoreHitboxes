package com.github.darkpred.morehitboxes.entity;

import com.github.darkpred.morehitboxes.api.HitboxData;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.gson.*;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.resources.ResourceManager;
import net.minecraft.server.packs.resources.SimpleJsonResourceReloadListener;
import net.minecraft.util.GsonHelper;
import net.minecraft.util.profiling.ProfilerFiller;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.phys.Vec3;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.Map;

@ApiStatus.Internal
public class HitboxDataLoader extends SimpleJsonResourceReloadListener {
    private static final Gson GSON = new GsonBuilder().disableHtmlEscaping().create();
    public static final HitboxDataLoader HITBOX_DATA = new HitboxDataLoader(GSON);
    private ImmutableMap<ResourceLocation, List<HitboxData>> hitboxData = ImmutableMap.of();

    public HitboxDataLoader(Gson gson) {
        super(gson, "hitboxes");
    }

    /**
     * Returns a list of the hitbox data that was loaded from data/hitboxes
     *
     * @param entityLocation the {@link EntityType#getKey} for the mob
     * @return a list of the mobs hitbox data
     */
    public List<HitboxData> getHitboxes(ResourceLocation entityLocation) {
        return hitboxData.get(entityLocation);
    }

    public Map<ResourceLocation, List<HitboxData>> getHitboxData() {
        return hitboxData;
    }

    @Override
    protected void apply(Map<ResourceLocation, JsonElement> jsons, @NotNull ResourceManager resourceManager, @NotNull ProfilerFiller profiler) {
        ImmutableMap.Builder<ResourceLocation, List<HitboxData>> builder = ImmutableMap.builder();
        for (Map.Entry<ResourceLocation, JsonElement> fileEntry : jsons.entrySet()) {
            if (!(fileEntry.getValue() instanceof JsonObject root)) {
                continue;
            }
            JsonArray elements = GsonHelper.getAsJsonArray(root, "elements");
            ImmutableList.Builder<HitboxData> listBuilder = ImmutableList.builder();
            for (JsonElement element : elements) {
                JsonObject elemObject = element.getAsJsonObject();
                double[] pos = new double[3];
                JsonArray posArray = GsonHelper.getAsJsonArray(elemObject, "pos");
                for (int i = 0; i < pos.length; ++i) {
                    pos[i] = GsonHelper.convertToDouble(posArray.get(i), "pos[" + i + "]");
                }
                float width = GsonHelper.getAsFloat(elemObject, "width") / 16;
                float height = GsonHelper.getAsFloat(elemObject, "height") / 16;

                JsonElement refElement = elemObject.get("ref");
                String ref = refElement == null ? "" : refElement.getAsString();
                //TODO: Update blockbench plugin
                JsonElement attackElement = elemObject.get("is_attack_box");
                boolean isAttack = attackElement != null && attackElement.getAsBoolean();
                listBuilder.add(new HitboxData(elemObject.get("name").getAsString(), new Vec3(pos[0] / 16, pos[1] / 16, pos[2] / 16), width, height, ref, isAttack));
            }
            builder.put(fileEntry.getKey(), listBuilder.build());
        }
        hitboxData = builder.build();
    }

    /**
     * Replaces all hitbox data with a copy of the given map
     *
     * @param dataMap the new hitbox data
     */
    public void replaceData(Map<ResourceLocation, List<HitboxData>> dataMap) {
        hitboxData = ImmutableMap.copyOf(dataMap);
    }

    public static List<HitboxData> readBuf(FriendlyByteBuf buf) {
        return buf.readList(HitboxData::readBuf);
    }

    private static void writeBuf(FriendlyByteBuf buf, List<HitboxData> hitboxes) {
        buf.writeCollection(hitboxes, HitboxData::writeBuf);
    }

    public void writeBuf(FriendlyByteBuf buf) {
        buf.writeMap(hitboxData, (buffer, key) -> buf.writeResourceLocation(key), HitboxDataLoader::writeBuf);
    }
}
