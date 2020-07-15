package com.github.doctormacc.common.protocols.v407_to_v390;

import com.github.doctormacc.common.BedrockBackwards;
import com.github.doctormacc.common.PlayerSession;
import com.github.doctormacc.common.UpstreamProtocolPacketHandler;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ResourcePackClientResponsePacket;

public class v407_to_v390_UpstreamPacketHandler extends UpstreamProtocolPacketHandler {

    public v407_to_v390_UpstreamPacketHandler(PlayerSession session) {
        super(session);
    }

    @Override
    public boolean defaultHandler(BedrockPacket packet) {
        BedrockBackwards.LOGGER.info("Client packet: " + packet);
        session.getDownstream().sendPacket(packet);
        return true;
    }

//    @Override
//    public boolean handle(ResourcePackClientResponsePacket packet) {
//        BedrockBackwards.LOGGER.info("Received resource pack ")
//    }
}
