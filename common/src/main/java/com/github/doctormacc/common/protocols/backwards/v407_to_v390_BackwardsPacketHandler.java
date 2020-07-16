package com.github.doctormacc.common.protocols.backwards;

import com.github.doctormacc.common.BedrockBackwards;
import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.data.entity.EntityData;
import com.nukkitx.protocol.bedrock.data.entity.EntityFlag;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;
import com.nukkitx.protocol.bedrock.packet.ItemStackRequestPacket;
import com.nukkitx.protocol.bedrock.packet.SetEntityDataPacket;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;

public class v407_to_v390_BackwardsPacketHandler extends BackwardsPacketHandler {

    private static final ObjectArrayList<EntityData> ENTITYDATA_REMOVE_LIST = new ObjectArrayList<>();
    private static final ObjectArrayList<EntityFlag> ENTITYFLAGS_REMOVE_LIST = new ObjectArrayList<>();

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
    public void translate(PlayerSession session, BedrockPacket packet, boolean upstream, int translatorIndex) {
        if (ItemStackRequestPacket.class.equals(packet.getClass())) {
            BedrockBackwards.LOGGER.info("Item stack request packet.");
        } else if (AddEntityPacket.class.equals(packet.getClass())) {
            for (EntityData data : ((AddEntityPacket) packet).getMetadata().keySet()) {
                if (ENTITYDATA_REMOVE_LIST.contains(data)) {
                    ((AddEntityPacket) packet).getMetadata().remove(data);
                }
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
    }
}
