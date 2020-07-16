package com.github.doctormacc.common;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nukkitx.protocol.bedrock.BedrockClient;
import com.nukkitx.protocol.bedrock.BedrockServer;
import lombok.Getter;

import java.net.InetSocketAddress;
import java.util.Collections;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;

public class BedrockBackwards {

    public static final ObjectMapper JSON_MAPPER = new ObjectMapper().disable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES);
    private static final Set<BedrockClient> CLIENTS = Collections.newSetFromMap(new ConcurrentHashMap<>());

    public static Logger LOGGER;
    public static BedrockBackwardsConfiguration config;

    public static void start(Logger logger, BedrockBackwardsConfiguration config) {
        BedrockBackwards.config = config;
        LOGGER = logger;
        logger.setDebug(config.isDebugMode());
        BedrockServer server = new BedrockServer(new InetSocketAddress(config.getListen().getAddress(), config.getListen().getPort()));
        server.setHandler(new ConnectionServerEventHandler());
        server.bind().whenComplete((avoid, throwable) -> {
            if (throwable == null) {
                logger.info("BedrockBackwards started!");
            } else {
                logger.info("BedrockBackwards failed.");
                throwable.printStackTrace();
            }
        }).join();
    }

    public static BedrockClient newClient() {
        InetSocketAddress bindAddress = new InetSocketAddress("0.0.0.0", ThreadLocalRandom.current().nextInt(20000, 60000));
        BedrockClient client = new BedrockClient(bindAddress);
        CLIENTS.add(client);
        client.bind().join();
        return client;
    }

}
