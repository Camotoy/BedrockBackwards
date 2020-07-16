package com.github.doctormacc.common;

import com.github.doctormacc.common.protocols.BasePacketHandler;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.packet.ClientToServerHandshakePacket;
import com.nukkitx.protocol.bedrock.packet.DisconnectPacket;
import com.nukkitx.protocol.bedrock.packet.ServerToClientHandshakePacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import lombok.AllArgsConstructor;

import javax.crypto.SecretKey;
import java.net.URI;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.security.interfaces.ECPublicKey;
import java.security.spec.InvalidKeySpecException;
import java.text.ParseException;
import java.util.Base64;

/**
 * Handles all packets coming in from the remote server.
 */
@AllArgsConstructor
public class DownstreamPipelinePacketHandler extends ProtocolPacketHandler {

    protected PlayerSession session;

    @Override
    public boolean defaultHandler(BedrockPacket packet) {
        BasePacketHandler.translatePacket(session, packet, false, 0);
        return true;
    }

    @Override
    public boolean handle(DisconnectPacket packet) {
        session.getUpstream().disconnect();
        return false;
    }

    @Override
    public boolean handle(ServerToClientHandshakePacket packet) {
        BedrockBackwards.LOGGER.info("ServerToClient handshake");
        try {
            SignedJWT saltJwt = SignedJWT.parse(packet.getJwt());
            URI x5u = saltJwt.getHeader().getX509CertURL();
            ECPublicKey serverKey = EncryptionUtils.generateKey(x5u.toASCIIString());
            SecretKey key = EncryptionUtils.getSecretKey(session.getProxyKeyPair().getPrivate(), serverKey,
                    Base64.getDecoder().decode(saltJwt.getJWTClaimsSet().getStringClaim("salt")));
            session.getDownstream().enableEncryption(key);
        } catch (ParseException | NoSuchAlgorithmException | InvalidKeySpecException | InvalidKeyException e) {
            throw new RuntimeException(e);
        }

        ClientToServerHandshakePacket clientToServerHandshake = new ClientToServerHandshakePacket();
        session.getDownstream().sendPacketImmediately(clientToServerHandshake);
        return true;
    }

}
