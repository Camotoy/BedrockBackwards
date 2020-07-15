package com.github.doctormacc.common;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeType;
import com.github.doctormacc.common.protocols.v407_to_v390.v407_to_v390_DownstreamPacketHandler;
import com.github.doctormacc.common.protocols.v407_to_v390.v407_to_v390_UpstreamPacketHandler;
import com.github.doctormacc.common.protocols.v407_to_v390.versions.BedrockVersion;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JWSObject;
import com.nimbusds.jose.crypto.factories.DefaultJWSVerifierFactory;
import com.nimbusds.jwt.SignedJWT;
import com.nukkitx.network.util.Preconditions;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.packet.LoginPacket;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import io.netty.util.AsciiString;
import net.minidev.json.JSONObject;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.security.interfaces.ECPublicKey;
import java.util.UUID;

public class InitialPacketHandler implements BedrockPacketHandler {

    private final BedrockServerSession session;
    private PlayerSession player;
    private JSONObject skinData;
    private JSONObject extraData;
    private ArrayNode chainData;
    private AuthData authData;

    public InitialPacketHandler(BedrockServerSession session) {
        this.session = session;
    }

//    @Override
//    public boolean handle(LoginPacket packet) {
//        BedrockBackwards.LOGGER.info("Logging in!");
//
//        BedrockClient client = new BedrockClient(new InetSocketAddress("0.0.0.0", 12345));
//
//        client.setRakNetVersion(10);
//        client.bind().join();
//
//        InetSocketAddress addressToConnect = new InetSocketAddress("172.16.1.213", 19133);
//        client.connect(addressToConnect).whenComplete((session, throwable) -> {
//            if (throwable != null) {
//                // Unable to establish connection
//                return;
//            }
//            // Connection established
//            // Make sure to set the packet codec version you wish to use before sending out packets
//            session.setPacketCodec(Bedrock_v407.V407_CODEC);
//            // Add disconnect handler
//            session.addDisconnectHandler((reason) -> BedrockBackwards.LOGGER.info("Disconnected from remote server: " + reason.toString()));
//            // Remember to set a packet handler so you receive incoming packets
//            session.setPacketHandler(new v407_to_v390_UpstreamPacketHandler(this.session));
//            // Now send packets...
//        }).join();
//
//        if (packet.getProtocolVersion() == 390) {
//            BedrockBackwards.LOGGER.info("Setting protocol version to 390.");
//            session.setPacketCodec(Bedrock_v390.V390_CODEC);
//            session.setPacketHandler(new v407_to_v390_DownstreamPacketHandler(client, packet));
//        }
//
//        return false;
//    }

    private static boolean validateChainData(JsonNode data) throws Exception {
        ECPublicKey lastKey = null;
        boolean validChain = false;
        for (JsonNode node : data) {
            JWSObject jwt = JWSObject.parse(node.asText());

            if (!validChain) {
                validChain = verifyJwt(jwt, EncryptionUtils.getMojangPublicKey());
            }

            if (lastKey != null) {
                verifyJwt(jwt, lastKey);
            }

            JsonNode payloadNode = BedrockBackwards.JSON_MAPPER.readTree(jwt.getPayload().toString());
            JsonNode ipkNode = payloadNode.get("identityPublicKey");
            Preconditions.checkState(ipkNode != null && ipkNode.getNodeType() == JsonNodeType.STRING, "identityPublicKey node is missing in chain");
            lastKey = EncryptionUtils.generateKey(ipkNode.asText());
        }
        return validChain;
    }

    private static boolean verifyJwt(JWSObject jwt, ECPublicKey key) throws JOSEException {
        return jwt.verify(new DefaultJWSVerifierFactory().createJWSVerifier(jwt.getHeader(), key));
    }

    @Override
    public boolean handle(LoginPacket packet) {

        BedrockVersion version = BedrockVersion.getBedrockVersion(packet.getProtocolVersion());

        session.setPacketCodec(version.getBedrockCodec());
        BedrockBackwards.LOGGER.info("Player detected as Protocol version " + session.getPacketCodec().getProtocolVersion());

        JsonNode certData;
        try {
            certData = BedrockBackwards.JSON_MAPPER.readTree(packet.getChainData().toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Certificate JSON can not be read.");
        }

        JsonNode certChainData = certData.get("chain");
        if (certChainData.getNodeType() != JsonNodeType.ARRAY) {
            throw new RuntimeException("Certificate data is not valid");
        }
        chainData = (ArrayNode) certChainData;

        boolean validChain;
        try {
            validChain = validateChainData(certChainData);

            BedrockBackwards.LOGGER.info("Is player data valid? " + validChain);
            JWSObject jwt = JWSObject.parse(certChainData.get(certChainData.size() - 1).asText());
            JsonNode payload = BedrockBackwards.JSON_MAPPER.readTree(jwt.getPayload().toBytes());

            if (payload.get("extraData").getNodeType() != JsonNodeType.OBJECT) {
                throw new RuntimeException("AuthData was not found!");
            }

            extraData = (JSONObject) jwt.getPayload().toJSONObject().get("extraData");

            this.authData = new AuthData(extraData.getAsString("displayName"),
                    UUID.fromString(extraData.getAsString("identity")), extraData.getAsString("XUID"));

            if (payload.get("identityPublicKey").getNodeType() != JsonNodeType.STRING) {
                throw new RuntimeException("Identity Public Key was not found!");
            }
            ECPublicKey identityPublicKey = EncryptionUtils.generateKey(payload.get("identityPublicKey").textValue());

            JWSObject clientJwt = JWSObject.parse(packet.getSkinData().toString());
            verifyJwt(clientJwt, identityPublicKey);
            skinData = clientJwt.getPayload().toJSONObject();

            initializeProxySession();
        } catch (Exception e) {
            session.disconnect("disconnectionScreen.internalError.cantConnect");
            throw new RuntimeException("Unable to complete login", e);
        }
        return true;
    }

    private void initializeProxySession() {
        //TODO: Config or autodetect
        BedrockVersion serverVersion = BedrockVersion.getBedrockVersion(407);
        BedrockClient client = BedrockBackwards.newClient();
        client.setRakNetVersion(serverVersion.getRaknetVersion());
        //TODO: Use config
        client.connect(new InetSocketAddress("172.16.1.213", 19133)).whenComplete((downstream, throwable) -> {
            if (throwable != null) {
                //log.error("Unable to connect to downstream server " + proxy.getTargetAddress(), throwable);
                BedrockBackwards.LOGGER.info("Unable to connect to downstream server.");
                return;
            }
            downstream.setPacketCodec(serverVersion.getBedrockCodec());
            PlayerSession proxySession = new PlayerSession(this.session, downstream, this.authData);
            this.player = proxySession;

            SignedJWT authData = ForgeryUtils.forgeAuthData(proxySession.getProxyKeyPair(), extraData);
            JWSObject skinData = ForgeryUtils.forgeSkinData(proxySession.getProxyKeyPair(), this.skinData);
            chainData.remove(chainData.size() - 1);
            chainData.add(authData.serialize());
            JsonNode json = BedrockBackwards.JSON_MAPPER.createObjectNode().set("chain", chainData);
            AsciiString chainData;
            try {
                chainData = new AsciiString(BedrockBackwards.JSON_MAPPER.writeValueAsBytes(json));
            } catch (JsonProcessingException e) {
                throw new RuntimeException(e);
            }

            LoginPacket login = new LoginPacket();
            login.setChainData(chainData);
            login.setSkinData(AsciiString.of(skinData.serialize()));
            login.setProtocolVersion(serverVersion.getProtocolVersion());

            downstream.sendPacketImmediately(login);
            this.session.setBatchHandler(proxySession.getUpstreamBatchHandler());
            downstream.setBatchHandler(proxySession.getDownstreamTailHandler());
            downstream.setLogging(true);
            session.setPacketHandler(new v407_to_v390_UpstreamPacketHandler(this.player));
            downstream.setPacketHandler(new v407_to_v390_DownstreamPacketHandler(this.player));
        });
    }

}
