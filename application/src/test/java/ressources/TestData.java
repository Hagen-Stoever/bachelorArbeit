package ressources;

import com.commerzbank.heartbeatapiprovider.configuration.HeartbeatConfig;
import com.commerzbank.heartbeatapiprovider.configuration.ResourceServer;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiApiDTO;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiApiReferenceDTO;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiBackendDTO;
import com.commerzbank.heartbeatapiprovider.dataaccess.platformApi.model.PlatformApiInlineResponse200DTO;
import com.commerzbank.heartbeatapiprovider.dataccess.model.ApiInstance;
import com.commerzbank.heartbeatapiprovider.dataccess.model.TicketDTO;
import io.vavr.Tuple2;

import java.io.File;
import java.net.URI;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TestData {

    public final PlatformApiApiDTO MISSING_BACKEND = (PlatformApiApiDTO) new PlatformApiApiDTO()
                                                                        .basePath("/basePath1")
                                                                        .id(UUID.fromString("aaaaaaaa-4d0b-539c-a333-1e4404879741"))
                                                                        .name("exampleApi1");

    public final PlatformApiApiDTO MISSING_BACKEND2 = (PlatformApiApiDTO) new PlatformApiApiDTO()
                                                                        .basePath("/basePath2")
                                                                        .id(UUID.fromString("bbbbbbbb-4d0b-539c-a333-1e4404879741"))
                                                                        .name("exampleApi2");

    public final PlatformApiApiDTO OK = (PlatformApiApiDTO) new PlatformApiApiDTO()
                                                            .backend(new PlatformApiBackendDTO().uri(URI.create("example.path")))
                                                            .basePath("/basePath3")
                                                            .id(UUID.fromString("cccccccc-4d0b-539c-a333-1e4404879741"))
                                                            .name("exampleApi3");

    public final PlatformApiApiDTO OK2 = (PlatformApiApiDTO) new PlatformApiApiDTO()
                                                            .backend(new PlatformApiBackendDTO().uri(URI.create("example.path2")))
                                                            .basePath("/basePath4")
                                                            .id(UUID.fromString("dddddddd-4d0b-539c-a333-1e4404879741"))
                                                            .name("exampleApi4");


    public final List<PlatformApiApiReferenceDTO> EMPTY_LIST = new ArrayList<>();

    public final List<PlatformApiApiReferenceDTO> ALL_OK_LIST = List.of(OK, OK2);

    public final List<PlatformApiApiReferenceDTO> ALL_MISSING_BACKEND_LIST = List.of(MISSING_BACKEND, MISSING_BACKEND2);

    public final List<PlatformApiApiReferenceDTO> ALL_LIST = List.of(OK, MISSING_BACKEND, OK2, MISSING_BACKEND2);


    public final PlatformApiInlineResponse200DTO EMPTY = new PlatformApiInlineResponse200DTO().apis(EMPTY_LIST);

    public final PlatformApiInlineResponse200DTO ALL_OK = new PlatformApiInlineResponse200DTO().apis(ALL_OK_LIST);

    public final PlatformApiInlineResponse200DTO ALL_MISSING_BACKEND = new PlatformApiInlineResponse200DTO().apis(ALL_MISSING_BACKEND_LIST);

    public final PlatformApiInlineResponse200DTO ALL = new PlatformApiInlineResponse200DTO().apis(ALL_LIST);

    public final ApiInstance API_INSTANCE_MISSING_BACKEND = new ApiInstance(
            UUID.fromString("aaaaaaaa-4d0b-539c-a333-1e4404879741"),
            "exampleApi1",
            "example.path1",
            null
    );

    public final ApiInstance API_INSTANCE_MISSING_BACKEND2 = new ApiInstance(
            UUID.fromString("bbbbbbbb-4d0b-539c-a333-1e4404879741"),
            "exampleApi2",
            "example.path2",
            null
    );

    public final ApiInstance API_INSTANCE_OK = new ApiInstance(
            UUID.fromString("cccccccc-4d0b-539c-a333-1e4404879741"),
            "exampleApi3",
            "example.path",
            URI.create("/basePath3")
    );


    public final ApiInstance API_INSTANCE_OK2 = new ApiInstance(
            UUID.fromString("dddddddd-4d0b-539c-a333-1e4404879741"),
            "exampleApi4",
            "example.path4",
            URI.create("/basePath4")
    );

    public final List<ApiInstance> ALL_INSTANCES = List.of(API_INSTANCE_OK, API_INSTANCE_MISSING_BACKEND2, API_INSTANCE_OK2, API_INSTANCE_MISSING_BACKEND);

    public final List<ApiInstance> ALL_INSTANCES_MISSING_BACKENDS = List.of(API_INSTANCE_MISSING_BACKEND2, API_INSTANCE_MISSING_BACKEND);

    public final List<ApiInstance> ALL_INSTANCES_EMPTY = List.of();



    public final String TOKEN = "AToken";

    public final String COBA_ACTIVITY_ID = "AID";


    public final ResourceServer RESOURCE_SERVER = new ResourceServer(
            Duration.ZERO,
            "http://some.base.path",
            "/path/to/token",
            "client",
            "secret",
            "TechUsername",
            "TachPassword"
    );

    public final TicketDTO TICKET_DTO = new TicketDTO(
            "someAccessToken",
            "someRefreshToken",
            123,
            456
    );

    public final HeartbeatConfig.Monitor BACKEND_MONITOR = new HeartbeatConfig.Monitor(
            "backend",
            "backendId",
            "every 5 seconsd",
            "back.yml"
    );

    public final HeartbeatConfig.Monitor GATEWAY_MONITOR = new HeartbeatConfig.Monitor(
            "gateway",
            "gatewayId",
            "svsdv 33443 sss",
            "gate.dagdasd"
    );


    public final HeartbeatConfig HEARTBEAT_CONFIG = new HeartbeatConfig(
            "./test/out/",
            "/some/health/check",
            "http://gate.way",
            "    - %s  \n",
            BACKEND_MONITOR,
            GATEWAY_MONITOR
    );


    public final Tuple2<String, String> CONFIG_TEMPLATES = new Tuple2(
            "{Insert Monitor Name} {Insert Monitor Id} {Insert Monitor Schedule} {Insert APIs URl}",
            "{Insert Monitor Name} {Insert Monitor Id} {Insert Monitor Schedule} {Insert APIs URl}"
    );

    public final Tuple2<String, String> CONFIG_TEMPLATES_INVALID = new Tuple2(
            "no Placeholders",
            "also no placeHolder"
    );

    public final Tuple2<File, String> SAVEABLE_TUPLE = new Tuple2<>(
            new File("path/to/Imaginary/file"),
            "some Content"
    );
}
