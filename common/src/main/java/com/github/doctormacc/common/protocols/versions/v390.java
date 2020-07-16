package com.github.doctormacc.common.protocols.versions;

import com.github.doctormacc.common.protocols.backwards.v390_to_v389_BackwardsPacketHandler;
import com.github.doctormacc.common.protocols.forwards.v390_to_v407_ForwardsPacketHandler;
import com.nukkitx.protocol.bedrock.v390.Bedrock_v390;

public class v390 extends BedrockVersion {

    public v390() {
        this.bedrockCodec = Bedrock_v390.V390_CODEC;
        this.protocolVersion = this.bedrockCodec.getProtocolVersion();
        this.raknetVersion = 9;
        this.backwardsPacketHandler = v390_to_v389_BackwardsPacketHandler.class;
        this.forwardsPacketHandler = v390_to_v407_ForwardsPacketHandler.class;
    }

}
