package io.microsamples.integration.sqltransfer;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jdbc.StoredProcExecutor;
import org.springframework.integration.jdbc.StoredProcOutboundGateway;
import org.springframework.integration.jdbc.StoredProcPollingChannelAdapter;
import org.springframework.integration.jdbc.storedproc.ProcedureParameter;
import org.springframework.integration.scheduling.PollerMetadata;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.scheduling.TriggerContext;
import org.springframework.scheduling.support.PeriodicTrigger;

import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@SpringBootApplication
@Slf4j
public class SqlTransferApplication {

	@Autowired
	private DataSource dataSource;

	public static void main(String[] args) {
		SpringApplication.run(SqlTransferApplication.class, args);
	}

	@Bean(name = PollerMetadata.DEFAULT_POLLER)
	public PollerMetadata defaultPoller() {

		PollerMetadata pollerMetadata = new PollerMetadata();
		pollerMetadata.setTrigger(new RunOnceTrigger(1000));
		return pollerMetadata;
	}

	@Bean
	public IntegrationFlow triggeredFlow(StoredProcPollingChannelAdapter storedProcPollingChannelAdapter) {
		return IntegrationFlows.from(storedProcPollingChannelAdapter)
				.split()
				.handle(message -> log.info("👀 processing message {}", message.getPayload()))
				.get();
	}

	@Bean
	public StoredProcPollingChannelAdapter storedProcPollingChannelAdapter(StoredProcExecutor storedProcExecutor){
		StoredProcPollingChannelAdapter storedProcPollingChannelAdapter = new StoredProcPollingChannelAdapter(storedProcExecutor);
		storedProcPollingChannelAdapter.setExpectSingleResult(true);
		return storedProcPollingChannelAdapter;
	}

	@Bean
	public StoredProcExecutor storedProcExecutor(){
		StoredProcExecutor storedProcExecutor = new StoredProcExecutor(this.dataSource);
		storedProcExecutor.setStoredProcedureName("chachkiesproc");

		List<ProcedureParameter> procedureParameters = new ArrayList<ProcedureParameter>();
		procedureParameters.add(new ProcedureParameter("latitude", 0.0, null));
		storedProcExecutor.setProcedureParameters(procedureParameters);

		List<SqlParameter> sqlParameters = new ArrayList<>();
		sqlParameters.add(new SqlParameter("latitude", Types.DOUBLE));
		storedProcExecutor.setSqlParameters(sqlParameters);

		storedProcExecutor.setReturningResultSetRowMappers(Collections.singletonMap("out", new ChachkieMapper()));
		storedProcExecutor.setUsePayloadAsParameterSource(false);
		return storedProcExecutor;
	}

}

class ChachkieMapper implements RowMapper<Chachkie> {

	public Chachkie mapRow(ResultSet rs, int rowNum) throws SQLException {
		return new Chachkie(rs.getString("id"), rs.getDouble("latitude"), rs.getDouble("longitude"));
	}
}

@Data
@AllArgsConstructor
class Chachkie {
	private String id;
	private Double latitude;
	private Double longitude;
}

class RunOnceTrigger extends PeriodicTrigger {
	public RunOnceTrigger(long period) {
		super(period);
		setInitialDelay(period);
	}

	@Override
	public Date nextExecutionTime(TriggerContext triggerContext) {
		if(triggerContext.lastCompletionTime() == null) {   // hasn't executed yet
			return super.nextExecutionTime(triggerContext);
		}
		return null;
	}
}