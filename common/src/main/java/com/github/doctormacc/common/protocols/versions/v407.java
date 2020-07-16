package com.github.doctormacc.common.protocols.versions;

import com.github.doctormacc.common.protocols.backwards.v407_to_v390_BackwardsPacketHandler;
import com.nukkitx.protocol.bedrock.v407.Bedrock_v407;

public class v407 extends BedrockVersion {

    public v407() {
        this.bedrockCodec = Bedrock_v407.V407_CODEC;
        this.protocolVersion = 407;
        this.raknetVersion = 10;
        this.backwardsPacketHandler = v407_to_v390_BackwardsPacketHandler.class;
    }

}
