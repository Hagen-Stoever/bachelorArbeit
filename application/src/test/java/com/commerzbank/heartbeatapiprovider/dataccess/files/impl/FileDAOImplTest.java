package com.commerzbank.heartbeatapiprovider.dataccess.files.impl;

import com.commerzbank.heartbeatapiprovider.dataccess.files.FileDAO;
import io.vavr.Tuple2;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ResourceLoader;
import ressources.TestData;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;

import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class FileDAOImplTest {


    private TestData data;

    private ResourceLoader resourceLoader;

    private FileDAO uut;

    @BeforeEach
    void setUp () {
        resourceLoader = mock(ResourceLoader.class);
        uut = new FileDAOImpl(resourceLoader);
        data = new TestData();
    }

    @Test
    void Test_loadConfigFiles_Ok () throws IOException {
        // given
        ByteArrayInputStream fileStream = new ByteArrayInputStream("Example Content".getBytes(StandardCharsets.UTF_8));
        ClassLoader loader = mock(ClassLoader.class);
        when(resourceLoader.getClassLoader()).thenReturn(loader);
        when(loader.getResourceAsStream(anyString())).thenReturn(fileStream);

        // when
        Tuple2<String, String> result = uut.loadConfigFiles();

        // then
        MatcherAssert.assertThat(result._1, CoreMatchers.notNullValue());
        MatcherAssert.assertThat(result._2, CoreMatchers.notNullValue());

        Assertions.assertFalse(result._1.isEmpty());
        Assertions.assertFalse(result._2.isEmpty());
    }
}
