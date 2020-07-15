package com.github.doctormacc.common;

import com.nukkitx.network.util.DisconnectReason;
import com.nukkitx.protocol.bedrock.BedrockClientSession;
import com.nukkitx.protocol.bedrock.BedrockPacket;
import com.nukkitx.protocol.bedrock.BedrockServerSession;
import com.nukkitx.protocol.bedrock.BedrockSession;
import com.nukkitx.protocol.bedrock.handler.BatchHandler;
import com.nukkitx.protocol.bedrock.handler.BedrockPacketHandler;
import com.nukkitx.protocol.bedrock.util.EncryptionUtils;
import io.netty.buffer.ByteBuf;
import lombok.AccessLevel;
import lombok.Getter;

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

    public PlayerSession(BedrockServerSession upstream, BedrockClientSession downstream, AuthData authData) {
        this.upstream = upstream;
        this.downstream = downstream;
        this.authData = authData;
        this.upstream.addDisconnectHandler(reason -> {
            if (reason != DisconnectReason.DISCONNECTED) {
                this.downstream.disconnect();
            }
        });
    }

    public BatchHandler getUpstreamBatchHandler() {
        return new ProxyBatchHandler(downstream, true);
    }

    public BatchHandler getDownstreamTailHandler() {
        return new ProxyBatchHandler(upstream, false);
    }

    private class ProxyBatchHandler implements BatchHandler {
        private final BedrockSession session;

        private ProxyBatchHandler(BedrockSession session, boolean upstream) {
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
}

