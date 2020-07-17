package com.github.doctormacc.common.protocols.backwards;

import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.AddEntityPacket;

public class v389_to_v388_BackwardsPacketHandler extends BackwardsPacketHandler {

    @Override
    public boolean translate(PlayerSession session, BedrockPacket packet, boolean fromUpstream, int translatorIndex) {
        // translate bee
        if (AddEntityPacket.class.equals(packet.getClass())) {
            if (((AddEntityPacket) packet).getIdentifier().equals("minecraft:bee")) {
                ((AddEntityPacket) packet).setIdentifier("minecraft:pufferfish");
            }
        }
        return true;
    }
}
