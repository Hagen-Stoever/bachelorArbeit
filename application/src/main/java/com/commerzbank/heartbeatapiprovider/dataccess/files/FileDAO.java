package com.commerzbank.heartbeatapiprovider.dataccess.files;

import io.vavr.Tuple2;

import java.io.File;
import java.io.IOException;

public interface FileDAO {

    Tuple2<String, String> loadConfigFiles () throws IOException;

    IOException saveFile (Tuple2<File, String> fileTuple);

}
