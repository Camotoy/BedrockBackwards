package com.github.doctormacc.common.protocols.v407_to_v390;

import com.github.doctormacc.common.BedrockBackwards;
import com.github.doctormacc.common.DownstreamProtocolPacketHandler;
import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;

public class v407_to_v390_DownstreamPacketHandler extends DownstreamProtocolPacketHandler {

    public v407_to_v390_DownstreamPacketHandler(PlayerSession session) {
        super(session);
    }

    @Override
    public boolean defaultHandler(BedrockPacket packet) {
        BedrockBackwards.LOGGER.info("Server packet: " + packet);
        session.getUpstream().sendPacket(packet);
        return true;
    }
}
