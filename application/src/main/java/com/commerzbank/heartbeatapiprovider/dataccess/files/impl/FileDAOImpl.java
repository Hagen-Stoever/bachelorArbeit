package com.commerzbank.heartbeatapiprovider.dataccess.files.impl;

import com.commerzbank.heartbeatapiprovider.dataccess.files.FileDAO;
import io.vavr.Tuple2;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.stereotype.Component;

import java.io.*;
import java.nio.charset.StandardCharsets;

@Component
@Slf4j
class FileDAOImpl implements FileDAO {


    private final ResourceLoader loader;

    @Autowired
    public FileDAOImpl (ResourceLoader loader) {
        this.loader = loader;
    }


    /**
     * Returns 2 copies of the Heartbeat-Monitor-Configuration-File
     * One for backend-config, one for the gateway-config
     */
    @Override
    public Tuple2<String, String> loadConfigFiles () throws IOException {
        InputStream fileStream = loader.getClassLoader().getResourceAsStream("monitorTemplate.yml");
        String template = IOUtils.toString(fileStream, StandardCharsets.UTF_8);
        return new Tuple2(template, template);
    }


    @Override
    public IOException saveFile (Tuple2<File, String> fileTuple) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(fileTuple._1));
            writer.write(fileTuple._2);
            writer.close();
            log.info("Saved File " + fileTuple._1.getName());
            return null;
        } catch (Exception exception) {
            return new IOException("Could not save file to " + fileTuple._1.getPath(), exception);
        }
    }
}
