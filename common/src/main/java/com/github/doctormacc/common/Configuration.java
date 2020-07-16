package com.github.doctormacc.common;

public interface Configuration {

    ListenConfiguration getListen();
    interface ListenConfiguration {
        String getAddress();
        int getPort();
    }

    RemoteConfiguration getRemote();
    interface RemoteConfiguration {
        String getAddress();
        int getPort();
        int getProtocolVersion();
    }

    boolean isDebugMode();

    int getConfigVersion();
}
