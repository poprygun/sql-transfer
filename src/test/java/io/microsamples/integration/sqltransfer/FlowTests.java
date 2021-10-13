package io.microsamples.integration.sqltransfer;

import io.microsamples.integration.sqltransfer.config.FlowInvoker;
import org.assertj.core.api.recursive.comparison.RecursiveComparisonConfiguration;
import org.jeasy.random.EasyRandom;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.integration.test.context.MockIntegrationContext;
import org.springframework.integration.test.context.SpringIntegrationTest;
import org.springframework.integration.test.mock.MockIntegration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.test.context.ActiveProfiles;

import javax.sql.DataSource;
import java.util.List;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
@SpringIntegrationTest(noAutoStartup = {"procOutboundGateway"})
class FlowTests {

    @MockBean(name = "sourceDatasource")
    private DataSource sourceDatasource;

    @Autowired
    private FlowInvoker flowInvoker;

    @Autowired
    private MockIntegrationContext mockIntegrationContext;

    @Autowired
    private ChachkieRepository chachkieRepository;

    @Test
    void happyPathFlowProcess() {

        final EasyRandom easyRandom = new EasyRandom();

        List<Chachkie> chachkies = easyRandom.objects(Chachkie.class, 5).collect(Collectors.toList());

        final ArgumentCaptor<Message<?>> messageArgumentCaptor = ArgumentCaptor.forClass(Message.class);

        final MessageHandler mockMessageHandler = MockIntegration.mockMessageHandler(messageArgumentCaptor)
                .handleNextAndReply(m -> chachkies);

        mockIntegrationContext.substituteMessageHandlerFor("procEndpoint", mockMessageHandler);

        flowInvoker.trigger("startSignal");

        RecursiveComparisonConfiguration configuration = RecursiveComparisonConfiguration.builder().build();

        assertThat(chachkieRepository.findAll())
                .usingRecursiveFieldByFieldElementComparator(configuration)
                .containsAll(chachkies);

    }

    @AfterEach
    void tearDown(){
        this.mockIntegrationContext.resetBeans();
    }

}
