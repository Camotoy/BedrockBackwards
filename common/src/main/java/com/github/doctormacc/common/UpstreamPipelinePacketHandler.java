package com.github.doctormacc.common;

import com.github.doctormacc.common.protocols.BasePacketHandler;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class UpstreamPipelinePacketHandler extends ProtocolPacketHandler {

    protected PlayerSession session;

    @Override
    public boolean defaultHandler(BedrockPacket packet) {
        BasePacketHandler.translatePacket(session, packet, true, 0);
        return true;
    }

}
