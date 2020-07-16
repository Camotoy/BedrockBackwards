package com.github.doctormacc.standalone;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.doctormacc.common.BedrockBackwards;

import java.io.File;
import java.io.IOException;
import java.net.URL;

import lombok.Getter;
import org.apache.commons.io.FileUtils;

public class BedrockBackwardsStandalone {

    @Getter
    private static BedrockBackwardsStandaloneConfiguration config;

    public static void main(String[] args) {
        StandaloneLogger logger = new StandaloneLogger();

        File configFile = new File("config.yml");
        if (!configFile.exists()) {
            URL inputUrl = BedrockBackwardsStandalone.class.getResource("/config.yml");
            File dest = new File("config.yml");
            try {
                FileUtils.copyURLToFile(inputUrl, dest);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        ObjectMapper objectMapper = new ObjectMapper(new YAMLFactory());
        try {
            config = objectMapper.readValue(configFile, BedrockBackwardsStandaloneConfiguration.class);
        } catch (IOException e) {
            e.printStackTrace();
            logger.info("Cannot load config file - exiting...");
            System.exit(1);
        }

        BedrockBackwards.start(logger, config);
        logger.start();
    }

}