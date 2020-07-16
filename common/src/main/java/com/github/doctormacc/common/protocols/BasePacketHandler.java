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
     * @param upstream
     * @param translatorIndex the current index of the translator array. This is used in order to pass on created packets
     *                        to newer versions.
     * @return Whether the packet should be translated. false means that the translation should be cancelled.
     */
    public abstract boolean translate(PlayerSession session, BedrockPacket packet, boolean upstream, int translatorIndex);

    public static void translatePacket(PlayerSession session, BedrockPacket packet, boolean upstream, int translatorIndex) {
        // System.out.println("Is upstream: " + upstream);

        if (PacketViolationWarningPacket.class.equals(packet.getClass())) {
            BedrockBackwards.LOGGER.info("Packet violation warning: " + packet);
        }

        if (packet == null) return;
        for (int i = translatorIndex; i < session.getTranslators().length; i++) {
            if (!session.getTranslators()[i].translate(session, packet, false, i)) {
                // If the translation should be cancelled and the packet should be ignored
                return;
            }
        }
        try {
            if (upstream) {
                session.getDownstream().sendPacket(packet);
            } else {
                session.getUpstream().sendPacket(packet);
            }
        } catch (Exception e) {
            if (e instanceof IllegalStateException && e.getMessage() == "Connection has been closed") {
                BedrockBackwards.LOGGER.info("Connection from " + session.getClientAddress() + " closed");
                session.endSession();
                return;
            }
            BedrockBackwards.LOGGER.info("Failed to translate packet of type " + packet.getPacketType());
            e.printStackTrace();
        }
    }

}
