package com.github.doctormacc.common;

import com.nukkitx.protocol.bedrock.BedrockPong;
import com.nukkitx.protocol.bedrock.BedrockServerEventHandler;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.v389.Bedrock_v389;

import java.net.InetSocketAddress;

public class ConnectionServerEventHandler implements BedrockServerEventHandler {

    public boolean onConnectionRequest(InetSocketAddress inetSocketAddress) {
        return true;
    }

    public BedrockPong onQuery(InetSocketAddress inetSocketAddress) {
        BedrockBackwards.LOGGER.debug("Ping from " + inetSocketAddress.getAddress());
        BedrockPong pong = new BedrockPong();
        pong.setEdition("MCPE");
        pong.setGameType("Default");
        pong.setNintendoLimited(false);
        pong.setProtocolVersion(389);
        pong.setIpv4Port(19132);
        pong.setMotd("BedrockBackwards");
        pong.setSubMotd("BedrockBackwards");
        pong.setPlayerCount(0);
        pong.setMaximumPlayerCount(100);
        return pong;
    }

    public void onSessionCreation(BedrockServerSession bedrockServerSession) {
        BedrockBackwards.LOGGER.info("Creating session!");
        bedrockServerSession.setLogging(true);
        bedrockServerSession.setPacketHandler(new InitialPipelinePacketHandler(bedrockServerSession));
        bedrockServerSession.setPacketCodec(Bedrock_v389.V389_CODEC);
    }
}
