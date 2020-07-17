package com.github.doctormacc.common.protocols.forwards;

import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;

public class v388_to_v389_ForwardsPacketHandler extends ForwardsPacketHandler {

    // Untested
    @Override
    public boolean translate(PlayerSession session, BedrockPacket packet, boolean fromUpstream, int translatorIndex) {
        return false;
    }
}
