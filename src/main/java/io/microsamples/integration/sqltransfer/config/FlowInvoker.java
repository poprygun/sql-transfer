package io.microsamples.integration.sqltransfer.config;

import org.springframework.integration.annotation.Gateway;
import org.springframework.integration.annotation.MessagingGateway;

@MessagingGateway
public interface FlowInvoker {
    @Gateway(requestChannel = "triggerChannel")
    void trigger(String flowStartSignal);
}
