package com.commerzbank.heartbeatapiprovider;

import com.commerzbank.heartbeatapiprovider.business.ApiBC;
import com.commerzbank.heartbeatapiprovider.business.FileBC;
import io.vavr.Tuple2;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;

@Component
@RequiredArgsConstructor
public class ProgramController {

    private final ApiBC apiBC;

    private final FileBC fileBC;

    /**
     * - Loads APIs from another Service <br>
     * - Loads Heartbeat-Monitor-Configuration Files from a template <br>
     * - Inserts values like name of monitor, id of monitor,  ... into the config-templates <br>
     * - Extracts the backend-URL and BasePath of an API <br>
     * - Combines the backend-URL of all APIs and BasePath of all APIs and inserts it into the config-templates <br>
     * - Saves the config-templates into files in a directory <br>
     *
     * @throws Exception Any error that prevents the program to continue, basically every exception except when saving the files.
     * @return If the list contains 0 items, then everything was successful <br>
     * if it contains 1 item, then one file could be created and one not <br>
     * if it contains 2 items, then no file could be created
     */
    public Mono<List<IOException>> runProgram () throws Exception {
        Mono<Tuple2<String, String>> urls = apiBC.loadApiUrls();
        Mono<Tuple2<String, String>> files = fileBC.loadFiles();

        return files.zipWith(urls)
                .map(fileBC::insertUrlsInConfigs)
                .flatMapIterable(fileBC::createFiles)
                .flatMap(fileBC::saveFile).
                collectList();
    }
}
