package com.github.doctormacc.common;

import com.github.doctormacc.common.protocols.BasePacketHandler;
import com.github.doctormacc.common.protocols.versions.BedrockVersion;
import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import io.netty.buffer.ByteBuf;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;

import java.net.InetSocketAddress;
import java.security.KeyPair;
import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

@Getter
public class PlayerSession {
    private static final ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
    private final BedrockServerSession upstream;
    private final BedrockClientSession downstream;
    private final AuthData authData;
    @Getter(AccessLevel.PACKAGE)
    private final KeyPair proxyKeyPair = EncryptionUtils.createKeyPair();
    private volatile boolean closed = false;

    private final BasePacketHandler[] translators;

    // For debugging purposes
    @Setter
    private BedrockPacket lastPacket;

    public PlayerSession(BedrockServerSession upstream, BedrockClientSession downstream, AuthData authData) {
        this.upstream = upstream;
        this.downstream = downstream;
        this.authData = authData;
        this.upstream.addDisconnectHandler(reason -> {
            BedrockBackwards.LOGGER.info("Client disconnected from server: " + reason);
            // Is this check needed if we have endSession instead of directly disconnecting?
            // It should be disconnected anyway but we might want to handle other things there too
            // if (reason != DisconnectReason.DISCONNECTED) {
                endSession();
            // }
        });
        this.downstream.addDisconnectHandler(reason -> {
            BedrockBackwards.LOGGER.info("Server disconnected client: " + reason);
            endSession();
        });

        boolean isNewerClient = this.upstream.getPacketCodec().getProtocolVersion() > this.downstream.getPacketCodec().getProtocolVersion();
        BedrockBackwards.LOGGER.info("Is newer client: " + isNewerClient);
        ObjectArrayList<BasePacketHandler> translators = new ObjectArrayList<>();
        for (int protocolVersion : BedrockVersion.VERSIONS) {
            if (isNewerClient && protocolVersion < this.upstream.getPacketCodec().getProtocolVersion() &&
            protocolVersion >= this.downstream.getPacketCodec().getProtocolVersion()) {
                try {
                    translators.add(BedrockVersion.getBedrockVersion(protocolVersion).getForwardsPacketHandler().newInstance());
                } catch (Exception e) {
                    BedrockBackwards.LOGGER.info("Exception whilst adding translator to collection.");
                    e.printStackTrace();
                }
            } else if (!isNewerClient && protocolVersion > this.upstream.getPacketCodec().getProtocolVersion() &&
            protocolVersion <= this.downstream.getPacketCodec().getProtocolVersion()) {
                try {
                    translators.add(BedrockVersion.getBedrockVersion(protocolVersion).getBackwardsPacketHandler().newInstance());
                } catch (Exception e) {
                    BedrockBackwards.LOGGER.info("Exception whilst adding translator to collection.");
                    e.printStackTrace();
                }
            }
        }
        BedrockBackwards.LOGGER.info("Translators: " + translators.toString());
        this.translators = translators.toArray(new BasePacketHandler[0]);
    }

    public BatchHandler getUpstreamBatchHandler() {
        return new ProxyBatchHandler(downstream);
    }

    public BatchHandler getDownstreamTailHandler() {
        return new ProxyBatchHandler(upstream);
    }

    private static class ProxyBatchHandler implements BatchHandler {
        private final BedrockSession session;

        private ProxyBatchHandler(BedrockSession session) {
            this.session = session;
        }

        @Override
        public void handle(BedrockSession session, ByteBuf compressed, Collection<BedrockPacket> packets) {
            boolean batchHandled = false;
            List<BedrockPacket> unhandled = new ArrayList<>();
            for (BedrockPacket packet : packets) {

                BedrockPacketHandler handler = session.getPacketHandler();

                if (handler != null && packet.handle(handler)) {
                    batchHandled = true;
                } else {
                    unhandled.add(packet);
                }

//                if (packetTesting) {
//                    int packetId = ProxyPass.CODEC.getId(packet.getClass());
//                    ByteBuf buffer = ByteBufAllocator.DEFAULT.ioBuffer();
//                    try {
//                        ProxyPass.CODEC.tryEncode(buffer, packet);
//                        BedrockPacket packet2 = ProxyPass.CODEC.tryDecode(buffer, packetId);
//                        if (!Objects.equals(packet, packet2)) {
//                            // Something went wrong in serialization.
//                            log.warn("Packets instances not equal:\n Original  : {}\nRe-encoded : {}",
//                                    packet, packet2);
//                        }
//                    } catch (PacketSerializeException e) {
//                        //ignore
//                    } finally {
//                        buffer.release();
//                    }
//                }
//            }

                if (!batchHandled) {
                    compressed.resetReaderIndex();
                    this.session.sendWrapped(compressed, true);
                } else if (!unhandled.isEmpty()) {
                    this.session.sendWrapped(unhandled, true);
                }
            }
        }
    }

    public InetSocketAddress getClientAddress() {
        return downstream.getAddress();
    }

    public void endSession() {
        if (lastPacket != null) {
            BedrockBackwards.LOGGER.info("Session ended. Last packet type: " + lastPacket.getPacketType());
        } else {
            BedrockBackwards.LOGGER.info("Session ended.");
        }
        try {
            upstream.disconnect();
        } catch (IllegalStateException e) {} // Connection already closed

        try {
            downstream.disconnect();
        } catch (IllegalStateException e) {} // Connection already closed
    }
}

