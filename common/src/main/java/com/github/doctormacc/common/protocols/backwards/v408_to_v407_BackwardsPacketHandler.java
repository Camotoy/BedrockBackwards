package com.github.doctormacc.common.protocols.backwards;

import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.*;

public class v408_to_v407_BackwardsPacketHandler extends BackwardsPacketHandler {

    @Override
    public boolean translate(PlayerSession session, BedrockPacket packet, boolean fromUpstream, int translatorIndex) {
       if (AddEntityPacket.class.equals(packet.getClass())) {
            String entityReplacement = null;
            if (((AddEntityPacket) packet).getIdentifier().equals("minecraft:piglin_brute")) {
                entityReplacement = "minecraft:piglin";
            }
            if (entityReplacement != null) {
                ((AddEntityPacket) packet).setIdentifier(entityReplacement);
            }
        }
        return true;
    }
}