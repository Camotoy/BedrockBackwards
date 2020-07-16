package com.github.doctormacc.standalone;

import com.github.doctormacc.common.BedrockBackwards;

public class BedrockBackwardsStandalone {

    public static void main(String[] args) {
        StandaloneLogger logger = new StandaloneLogger();
        BedrockBackwards.start(logger);
        logger.start();
    }

}
