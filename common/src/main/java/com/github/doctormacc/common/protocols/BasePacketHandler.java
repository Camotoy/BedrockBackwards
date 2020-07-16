package com.github.doctormacc.common.protocols;

import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;

public abstract class BasePacketHandler {

    /**
     *
     * @param session
     * @param packet
     * @param upstream
     * @param translatorIndex the current index of the translator array. This is used in order to pass on created packets
     *                        to newer versions.
     */
    public abstract void translate(PlayerSession session, BedrockPacket packet, boolean upstream, int translatorIndex);

    public static void translatePacket(PlayerSession session, BedrockPacket packet, boolean upstream, int translatorIndex) {
        //System.out.println("Is upstream: " + upstream + " packet: " + packet);
        for (int i = translatorIndex; i < session.getTranslators().length; i++) {
            if (packet == null) return;
            session.getTranslators()[i].translate(session, packet, false, i);
        }
        if (packet == null) return;
        if (upstream) {
            session.getDownstream().sendPacket(packet);
        } else {
            session.getUpstream().sendPacket(packet);
        }
    }

}
