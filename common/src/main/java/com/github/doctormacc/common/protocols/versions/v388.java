package com.github.doctormacc.common.protocols.versions;

import com.github.doctormacc.common.protocols.forwards.v388_to_v389_ForwardsPacketHandler;
import com.nukkitx.protocol.bedrock.v388.Bedrock_v388;

public class v388 extends BedrockVersion {

    public v388() {
        this.bedrockCodec = Bedrock_v388.V388_CODEC;
        this.protocolVersion = this.bedrockCodec.getProtocolVersion();
        this.raknetVersion = 9;
        this.forwardsPacketHandler = v388_to_v389_ForwardsPacketHandler.class;
    }

}
