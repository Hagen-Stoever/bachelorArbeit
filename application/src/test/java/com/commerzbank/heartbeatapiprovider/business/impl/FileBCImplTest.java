package com.commerzbank.heartbeatapiprovider.business.impl;

import com.commerzbank.heartbeatapiprovider.configuration.HeartbeatConfig;
import com.commerzbank.heartbeatapiprovider.dataccess.files.FileDAO;
import io.vavr.Tuple2;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import ressources.TestData;

import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileBCImplTest {

    @Mock
    private HeartbeatConfig heartbeatConfig;
    @Mock
    private FileDAO fileDAO;

    private FileBCImpl uut;
    private TestData data;


    @BeforeEach
    void setUp () {
        data = new TestData();
        heartbeatConfig = data.HEARTBEAT_CONFIG;
        fileDAO = mock(FileDAO.class);

        uut = new FileBCImpl(fileDAO, heartbeatConfig);
    }


    @Test
    void Test_createFiles_CanCreateFiles () {
        // given
        Tuple2<String, String> contents = new Tuple2<String, String>("content1", "content2");

        // when
        List<Tuple2<File, String>> result = uut.createFiles(contents);

        // then
        MatcherAssert.assertThat(result.size(), Matchers.equalTo(2));

        MatcherAssert.assertThat(result.get(0)._1.getName(), Matchers.equalTo(heartbeatConfig.getBackendMonitor().getFileName()));
        MatcherAssert.assertThat(result.get(1)._1.getName(), Matchers.equalTo(heartbeatConfig.getGatewayMonitor().getFileName()));

        MatcherAssert.assertThat(result.get(0)._2, Matchers.equalTo("content1"));
        MatcherAssert.assertThat(result.get(1)._2, Matchers.equalTo("content2"));
    }


    @Test
    void Test_loadFiles_insertedValues () throws IOException {
        // given
        when(fileDAO.loadConfigFiles()).thenReturn(data.CONFIG_TEMPLATES);

        // when
        Tuple2<String, String> result = uut.loadFiles().block();

        // then
        Assertions.assertTrue(result._1.contains(heartbeatConfig.getBackendMonitor().getName()));
        Assertions.assertTrue(result._1.contains(heartbeatConfig.getBackendMonitor().getId()));
        Assertions.assertTrue(result._1.contains(heartbeatConfig.getBackendMonitor().getSchedule()));
        Assertions.assertTrue(result._1.contains("{Insert APIs URl}"));

        Assertions.assertFalse(result._1.contains("{Insert Monitor Name}"));
        Assertions.assertFalse(result._1.contains("{Insert Monitor Id}"));
        Assertions.assertFalse(result._1.contains("{Insert Monitor Schedule}"));


        Assertions.assertTrue(result._2.contains(heartbeatConfig.getGatewayMonitor().getName()));
        Assertions.assertTrue(result._2.contains(heartbeatConfig.getGatewayMonitor().getId()));
        Assertions.assertTrue(result._2.contains(heartbeatConfig.getGatewayMonitor().getSchedule()));
        Assertions.assertTrue(result._2.contains("{Insert APIs URl}"));

        Assertions.assertFalse(result._2.contains("{Insert Monitor Name}"));
        Assertions.assertFalse(result._2.contains("{Insert Monitor Id}"));
        Assertions.assertFalse(result._2.contains("{Insert Monitor Schedule}"));
    }

    @Test
    void Test_loadFiles_ignoreInvalidConfig () throws IOException {
        // given
        when(fileDAO.loadConfigFiles()).thenReturn(data.CONFIG_TEMPLATES_INVALID);

        // when
        Tuple2<String, String> result = uut.loadFiles().block();

        // then
        MatcherAssert.assertThat(result._1, Matchers.equalTo(data.CONFIG_TEMPLATES_INVALID._1));
        MatcherAssert.assertThat(result._2, Matchers.equalTo(data.CONFIG_TEMPLATES_INVALID._2));
    }


    @Test
    void Test_saveFile_ReturnEmpty () {
        // given
        when(fileDAO.saveFile(data.SAVEABLE_TUPLE)).thenReturn(null);

        // when
        IOException result = uut.saveFile(data.SAVEABLE_TUPLE).block();

        // then
        MatcherAssert.assertThat(result, CoreMatchers.nullValue());
    }

    @Test
    void Test_saveFile_ReturnExceptionFromDao () {
        // given
        when(fileDAO.saveFile(data.SAVEABLE_TUPLE)).thenReturn(new IOException("file not saved"));

        // when
        IOException result = uut.saveFile(data.SAVEABLE_TUPLE).block();

        // then
        MatcherAssert.assertThat(result.getMessage(), CoreMatchers.equalTo("file not saved"));
    }
}
