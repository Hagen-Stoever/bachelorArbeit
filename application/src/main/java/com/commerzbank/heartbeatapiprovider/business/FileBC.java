package com.commerzbank.heartbeatapiprovider.business;

import io.vavr.Tuple2;
import reactor.core.publisher.Mono;

import java.io.File;
import java.io.IOException;
import java.util.List;

public interface FileBC {

    /**
     * Loads the templates for the Heartbeat-Monitor-Configurations, for backend and gateway,
     * and inserts values for name, id and schedule.
     *
     * @return left-tuple: backend-urls | right-tuple: gateway-urls
     * @throws IOException Any Exception that occurred while retrieving the data
     */
    Mono<Tuple2<String, String>> loadFiles () throws IOException;

    /**
     * Creates the files for the config-Templates and maps the content of the files to the files.
     * Files will not, yet, be saved
     *
     * @param contentOfFiles left-element: backendContent | right-element: gatewayContent
     * @return The files with the content of the files
     */
    List<Tuple2<File, String>> createFiles (Tuple2<String, String> contentOfFiles);


    /**
     * Saves the file with the provided content.
     *
     * @param fileFlux A stream of tuples containing a file and the content of the file
     * @return An Errors that occurred while saving the files or an empty Mono
     */
    Mono<IOException> saveFile (Tuple2<File, String> fileFlux);

    /**
     * Inserts the given urls in the given file (string).
     */
    Tuple2<String, String> insertUrlsInConfigs (reactor.util.function.Tuple2<Tuple2<String, String>, Tuple2<String, String>> tupleOfTuples);
}
