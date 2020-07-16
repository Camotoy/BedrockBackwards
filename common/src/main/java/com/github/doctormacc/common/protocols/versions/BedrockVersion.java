package com.github.doctormacc.common.protocols.versions;

import com.github.doctormacc.common.BedrockBackwards;
import com.github.doctormacc.common.protocols.backwards.BackwardsPacketHandler;
import com.github.doctormacc.common.protocols.forwards.ForwardsPacketHandler;
import com.nukkitx.protocol.bedrock.BedrockPacketCodec;
import it.unimi.dsi.fastutil.ints.Int2ObjectMap;
import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;
import lombok.Getter;

import java.util.Arrays;

@Getter
public abstract class BedrockVersion {

    private static final Int2ObjectMap<Class<? extends BedrockVersion>> PROTOCOL_VERSIONS = new Int2ObjectOpenHashMap<>();

    public static final int[] VERSIONS;

    static {
        PROTOCOL_VERSIONS.put(389, v389.class);
        PROTOCOL_VERSIONS.put(390, v390.class);
        PROTOCOL_VERSIONS.put(407, v407.class);

        VERSIONS = PROTOCOL_VERSIONS.keySet().toIntArray();
        System.out.println("Versions: " + Arrays.toString(VERSIONS));
    }

    public static BedrockVersion getBedrockVersion(int protocolVersion) {
        try {
            return BedrockVersion.PROTOCOL_VERSIONS.get(protocolVersion).newInstance();
        } catch (Exception e) {
            BedrockBackwards.LOGGER.info("Unable to find supported protocol version for " + protocolVersion);
            return new v407();
        }
    }

    protected BedrockPacketCodec bedrockCodec;
    protected int protocolVersion;
    protected int raknetVersion;
    protected Class<? extends BackwardsPacketHandler> backwardsPacketHandler = null;
    protected Class<? extends ForwardsPacketHandler> forwardsPacketHandler = null;
}
