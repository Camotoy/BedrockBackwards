package com.github.doctormacc.standalone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.doctormacc.common.BedrockBackwardsConfiguration;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class BedrockBackwardsStandaloneConfiguration implements BedrockBackwardsConfiguration {

    private ListenConfiguration listen;
    @Getter
    public static class ListenConfiguration implements BedrockBackwardsConfiguration.ListenConfiguration {
        private String address;
        private int port;
    }

    private RemoteConfiguration remote;
    @Getter
    public static class RemoteConfiguration implements BedrockBackwardsConfiguration.RemoteConfiguration {
        private String address;
        private int port;
    }

    @JsonProperty("debug-mode")
    private boolean debugMode;

    // Currently unused, but included for future-proofing
    @JsonProperty("config-version")
    private int configVersion;
}