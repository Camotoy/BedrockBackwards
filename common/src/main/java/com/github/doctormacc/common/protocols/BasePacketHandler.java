package com.github.doctormacc.common.protocols;

import com.github.doctormacc.common.BedrockBackwards;
import com.github.doctormacc.common.PlayerSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.PacketViolationWarningPacket;

public abstract class BasePacketHandler {

    /**
     *
     * @param session
     * @param packet
     * @param fromUpstream if the packet was sent from upstream
     * @param translatorIndex the current index of the translator array. This is used in order to pass on created packets
     *                        to newer versions.
     * @return Whether the packet should be translated. false means that the translation should be cancelled.
     */
    public abstract boolean translate(PlayerSession session, BedrockPacket packet, boolean fromUpstream, int translatorIndex);

    public static void translatePacket(PlayerSession session, BedrockPacket packet, boolean fromUpstream, int translatorIndex) {
        //System.out.println("Is upstream: " + fromUpstream + " packet: " + packet.getPacketType());

        if (PacketViolationWarningPacket.class.equals(packet.getClass())) {
            BedrockBackwards.LOGGER.info("Packet violation warning: " + packet);
        }

        if (fromUpstream) {
            // Translate backwards
            for (int i = session.getTranslators().length - 1 - translatorIndex; i >= 0; i--) {
                if (!session.getTranslators()[i].translate(session, packet, true, i)) {
                    // If the translation should be cancelled and the packet should be ignored
                    return;
                }
            }
        } else {
            for (int i = translatorIndex; i < session.getTranslators().length; i++) {
                if (!session.getTranslators()[i].translate(session, packet, false, i)) {
                    // If the translation should be cancelled and the packet should be ignored
                    return;
                }
            }
        }
        try {
            if (fromUpstream) {
                session.getDownstream().sendPacket(packet);
            } else {
                session.getUpstream().sendPacket(packet);
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException && e.getMessage().equals("Connection has been closed")) {
                BedrockBackwards.LOGGER.info("Connection from " + session.getClientAddress() + " closed");
                session.endSession();
                return;
            }
            BedrockBackwards.LOGGER.info("Failed to send packet of type " + packet.getPacketType());
            e.printStackTrace();
        }
    }

}
