package com.github.doctormacc.common.protocols.versions;

import com.github.doctormacc.common.protocols.forwards.v389_to_v390_ForwardsPacketHandler;
import com.nukkitx.protocol.bedrock.v389.Bedrock_v389;

public class v389 extends BedrockVersion {

    public v389() {
        this.bedrockCodec = Bedrock_v389.V389_CODEC;
        this.protocolVersion = this.bedrockCodec.getProtocolVersion();
        this.raknetVersion = 9;
        this.forwardsPacketHandler = v389_to_v390_ForwardsPacketHandler.class;
    }

}
