package com.commerzbank.heartbeatapiprovider.dataccess.platformApi;

import com.commerzbank.heartbeatapiprovider.dataccess.model.ApiInstance;
import org.junit.jupiter.api.Test;
import ressources.TestData;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.nullValue;

public class ApiInstanceConverterTest {

    private ApiInstanceConverter uut = new ApiInstanceConverter();

    private TestData data = new TestData();


    @Test
    public void Test_convert_AllProvided_ReturnOk () {
        // given
        // when
        ApiInstance result = uut.convert(data.OK);

        // then
        assertThat(result.getName(), equalTo(data.OK.getName()));
        assertThat(result.getApiId(), equalTo(data.OK.getId()));
        assertThat(result.getBaseUrl(), equalTo(data.OK.getBasePath()));
        assertThat(result.getBackendUrl(), equalTo(data.OK.getBackend().getUri()));
    }


    @Test
    public void Test_convert_BackendMissing_ReturnOk () {
        // given
        // when
        ApiInstance result = uut.convert(data.MISSING_BACKEND);

        // then
        assertThat(result.getName(), equalTo(data.MISSING_BACKEND.getName()));
        assertThat(result.getApiId(), equalTo(data.MISSING_BACKEND.getId()));
        assertThat(result.getBaseUrl(), equalTo(data.MISSING_BACKEND.getBasePath()));
        assertThat(result.getBackendUrl(), nullValue());
    }
}
