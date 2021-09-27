package io.microsamples.integration.sqltransfer;


import io.microsamples.integration.sqltransfer.config.FlowInvoker;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Collections;

@RestController
@RequestMapping("flow")
public class FlowController {

    private FlowInvoker flowInvoker;

    public FlowController(FlowInvoker flowInvoker) {
        this.flowInvoker = flowInvoker;
    }

    @GetMapping
    public ResponseEntity invoke() {

        flowInvoker.trigger("start-flow-signal");
        return ResponseEntity.ok(Collections.singletonMap("starFlow", HttpStatus.ACCEPTED));
    }
}
