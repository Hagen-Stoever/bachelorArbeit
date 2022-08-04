package com.commerzbank.heartbeatapiprovider.business.impl;

import com.commerzbank.heartbeatapiprovider.business.FileBC;
import com.commerzbank.heartbeatapiprovider.configuration.HeartbeatConfig;
import com.commerzbank.heartbeatapiprovider.dataccess.files.FileDAO;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
class FileBCImpl implements FileBC {

    private static final String NAME_PLACEHOLDER = "{Insert Monitor Name}";
    private static final String ID_PLACEHOLDER = "{Insert Monitor Id}";
    private static final String SCHEDULE_PLACEHOLDER = "{Insert Monitor Schedule}";
    private static final String URL_PLACEHOLDER = "{Insert APIs URl}";

    private final FileDAO fileDAO;
    private final HeartbeatConfig beatConfig;


    @Override
    public Mono<Tuple2<String, String>> loadFiles () throws IOException {
        return Mono.just(fileDAO.loadConfigFiles())
                .map(this::insertValuesInConfigTemplates);
    }

    @Override
    public List<Tuple2<File, String>> createFiles (Tuple2<String, String> contentOfFiles) {
        File backendFile = new File(beatConfig.getConfigFilePath() + beatConfig.getBackendMonitor().getFileName());
        File gatewayFile = new File(beatConfig.getConfigFilePath() + beatConfig.getGatewayMonitor().getFileName());

        return List.of(new Tuple2(backendFile, contentOfFiles._1), new Tuple2(gatewayFile, contentOfFiles._2));
    }

    @Override
    public Mono<IOException> saveFile (Tuple2<File, String> fileFlux) {
        IOException response = fileDAO.saveFile(fileFlux);

        if (response != null) {
            return Mono.just(response);
        } else {
            return Mono.empty();
        }
    }

    @Override
    public Tuple2<String, String> insertUrlsInConfigs (reactor.util.function.Tuple2<Tuple2<String, String>, Tuple2<String, String>> tupleOfTuples) {
        String backendFile = tupleOfTuples.getT1()._1.replace(URL_PLACEHOLDER, tupleOfTuples.getT2()._1);
        String gatewayFile = tupleOfTuples.getT1()._2.replace(URL_PLACEHOLDER, tupleOfTuples.getT2()._2);

        return new Tuple2(backendFile, gatewayFile);
    }


    // Private
    //==================================================================================================================

    /**
     * Inserts the values (e.g. name, ... , not urls!) for the config-files into them
     */
    private Tuple2<String, String> insertValuesInConfigTemplates (Tuple2<String, String> tuple) {
        HeartbeatConfig.Monitor backend = beatConfig.getBackendMonitor();
        String backendConfig = replaceStaticPlaceholder(tuple._1(), backend.getName(), backend.getId(), backend.getSchedule());

        HeartbeatConfig.Monitor gw = beatConfig.getGatewayMonitor();
        String gatewayConfig = replaceStaticPlaceholder(tuple._2(), gw.getName(), gw.getId(), gw.getSchedule());

        return new Tuple2(backendConfig, gatewayConfig);
    }

    private String replaceStaticPlaceholder (String config, String name, String id, String schedule) {
        return config.replace(ID_PLACEHOLDER, id)
                     .replace(NAME_PLACEHOLDER, name)
                     .replace(SCHEDULE_PLACEHOLDER, schedule);
    }
}
