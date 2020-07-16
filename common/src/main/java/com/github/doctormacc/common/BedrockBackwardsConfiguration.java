package com.github.doctormacc.common;

public interface BedrockBackwardsConfiguration {
    ListenConfiguration getListen();
    interface ListenConfiguration {
        String getAddress();
        int getPort();
    }
    RemoteConfiguration getRemote();
    interface RemoteConfiguration {
        String getAddress();
        int getPort();
    }
    boolean isDebugMode();
    int getConfigVersion();
}
