package com.github.doctormacc.common.protocols.backwards;

import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.*;

public class v390_to_v389_BackwardsPacketHandler extends BackwardsPacketHandler {

    @Override
    public boolean translate(PlayerSession session, BedrockPacket packet, boolean fromUpstream, int translatorIndex) {

        return true;
    }
}
