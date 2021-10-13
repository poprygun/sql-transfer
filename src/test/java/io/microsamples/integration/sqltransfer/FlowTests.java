package io.microsamples.integration.sqltransfer;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import javax.sql.DataSource;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@ActiveProfiles("test")
class FlowTests {

    @MockBean(name = "sourceDatasource")
    private DataSource sourceDatasource;

    @Test
    void happyPathFlowProcess() {
        System.out.println("hi there");
    }

}
