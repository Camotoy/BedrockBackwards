package com.github.doctormacc.standalone;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.github.doctormacc.common.Configuration;
import lombok.Getter;

@Getter
@JsonIgnoreProperties(ignoreUnknown = true)
public class StandaloneConfiguration implements Configuration {

    private ListenConfiguration listen;
    @Getter
    public static class ListenConfiguration implements Configuration.ListenConfiguration {
        private String address;
        private int port;
    }

    private RemoteConfiguration remote;
    @Getter
    public static class RemoteConfiguration implements Configuration.RemoteConfiguration {
        private String address;
        private int port;
        @JsonProperty("protocol-version")
        private int protocolVersion;
    }

    @JsonProperty("debug-mode")
    private boolean debugMode;

    // Currently unused, but included for future-proofing
    @JsonProperty("config-version")
    private int configVersion;
}