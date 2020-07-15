package com.github.doctormacc.common.protocols.v407_to_v390.versions;

import com.nukkitx.protocol.bedrock.v390.Bedrock_v390;

public class v390 extends BedrockVersion {

    public v390() {
        this.bedrockCodec = Bedrock_v390.V390_CODEC;
        this.protocolVersion = 390;
        this.raknetVersion = 9;
    }

}
