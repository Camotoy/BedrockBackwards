package com.github.doctormacc.common.protocols.backwards;

import com.github.doctormacc.common.BedrockBackwards;
import com.github.doctormacc.common.PlayerSession;
import com.github.doctormacc.common.protocols.BasePacketHandler;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockPacketType;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import com.nukkitx.protocol.bedrock.data.inventory.ContainerId;
import com.nukkitx.protocol.bedrock.data.inventory.ItemData;
import com.nukkitx.protocol.bedrock.packet.*;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class v407_to_v390_BackwardsPacketHandler extends BackwardsPacketHandler {

    private static final ObjectArrayList<EntityData> ENTITYDATA_REMOVE_LIST = new ObjectArrayList<>();
    private static final ObjectArrayList<EntityFlag> ENTITYFLAGS_REMOVE_LIST = new ObjectArrayList<>();

    private static final ObjectArrayList<Class<? extends BedrockPacket>> IGNORE_PACKETS_LIST = new ObjectArrayList<>();

    static {
        ENTITYDATA_REMOVE_LIST.add(EntityData.LOW_TIER_CURED_TRADE_DISCOUNT);
        ENTITYDATA_REMOVE_LIST.add(EntityData.HIGH_TIER_CURED_TRADE_DISCOUNT);
        ENTITYDATA_REMOVE_LIST.add(EntityData.NEARBY_CURED_TRADE_DISCOUNT);
        ENTITYDATA_REMOVE_LIST.add(EntityData.NEARBY_CURED_DISCOUNT_TIME_STAMP);
        ENTITYDATA_REMOVE_LIST.add(EntityData.HITBOX);
        ENTITYDATA_REMOVE_LIST.add(EntityData.IS_BUOYANT);
        ENTITYDATA_REMOVE_LIST.add(EntityData.BUOYANCY_DATA);

        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.IS_AVOIDING_BLOCK);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.FACING_TARGET_TO_RANGE_ATTACK);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.HIDDEN_WHEN_INVISIBLE);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.IS_IN_UI);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.STALKING);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.EMOTING);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.CELEBRATING);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.ADMIRING);
        ENTITYFLAGS_REMOVE_LIST.add(EntityFlag.CELEBRATING_SPECIAL);
    }

    @Override
    @SuppressWarnings("deprecation")
    public boolean translate(PlayerSession session, BedrockPacket packet, boolean fromUpstream, int translatorIndex) {
        if (IGNORE_PACKETS_LIST.contains(packet.getClass())) {
            BedrockBackwards.LOGGER.debug("Ignoring packet " + packet.getPacketType());
            return false;
        }

        // From GopherTunnel:
        // CreativeContent is a packet sent by the server to set the creative inventory's content for a player.
        // Introduced in 1.16, this packet replaces the previous method - sending an InventoryContent packet with
        // creative inventory window ID.
        else if (CreativeContentPacket.class.equals(packet.getClass())) {
            InventoryContentPacket contentPacket = new InventoryContentPacket();
            contentPacket.setContainerId(ContainerId.CREATIVE);
            contentPacket.setContents(((CreativeContentPacket) packet).getEntries().values().toArray(new ItemData[0]));
            BasePacketHandler.translatePacket(session, contentPacket, fromUpstream, translatorIndex);
            return false;
        }

        else if (StartGamePacket.class.equals(packet.getClass())) {
            ((StartGamePacket) packet).setPremiumWorldTemplateId(((StartGamePacket) packet).getLevelName()); // Otherwise world name is a UUID
        }

        else if (AddEntityPacket.class.equals(packet.getClass())) {
            for (EntityData data : ((AddEntityPacket) packet).getMetadata().keySet()) {
                if (ENTITYDATA_REMOVE_LIST.contains(data)) {
                    ((AddEntityPacket) packet).getMetadata().remove(data);
                }
            }
            String entityReplacement = null;
            switch (((AddEntityPacket) packet).getIdentifier()) {
                case "minecraft:hoglin":
                case "minecraft:zoglin":
                    entityReplacement = "minecraft:pig";
                    break;
                case "minecraft:piglin":
                    entityReplacement = "minecraft:zombie_pigman";
                    break;
                case "minecraft:strider":
                    entityReplacement = "minecraft:magma_cube";
                default:
                    break;
            }
            if (entityReplacement != null) {
                ((AddEntityPacket) packet).setIdentifier(entityReplacement);
            }
//            for (EntityFlag flag : ENTITYFLAGS_REMOVE_LIST) {
//                ((AddEntityPacket) packet).getMetadata().getFlags().setFlag(flag, false);
//            }
        }
        else if (SetEntityDataPacket.class.equals(packet.getClass())) {
            for (EntityData data : ((SetEntityDataPacket) packet).getMetadata().keySet()) {
                if (ENTITYDATA_REMOVE_LIST.contains(data)) {
                    ((SetEntityDataPacket) packet).getMetadata().remove(data);
                }
            }
//            if (((SetEntityDataPacket) packet).getMetadata().getFlags() != null) {
//                for (EntityFlag flag : ENTITYFLAGS_REMOVE_LIST) {
//                    ((SetEntityDataPacket) packet).getMetadata().getFlags().setFlag(flag, false);
//                }
//            }
        }
        return true;
    }
}
