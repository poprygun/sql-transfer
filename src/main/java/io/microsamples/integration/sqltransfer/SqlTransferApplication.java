package io.microsamples.integration.sqltransfer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.repository.CrudRepository;
import org.springframework.integration.dsl.IntegrationFlow;
import org.springframework.integration.dsl.IntegrationFlows;
import org.springframework.integration.jdbc.StoredProcExecutor;
import org.springframework.integration.jdbc.StoredProcOutboundGateway;
import org.springframework.integration.jdbc.storedproc.ProcedureParameter;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.stereotype.Repository;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.sql.DataSource;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@SpringBootApplication
@Slf4j
public class SqlTransferApplication {

    @Autowired
    @Qualifier("sourceDatasource")
    private DataSource sourceDatasource;

    public static void main(String[] args) {
        SpringApplication.run(SqlTransferApplication.class, args);
    }

    @Bean
    public IntegrationFlow triggeredFlow(ChachkieRepository chachkieRepository, StoredProcOutboundGateway procOutboundGateway) {
        return IntegrationFlows.from("triggerChannel")
                .handle(procOutboundGateway, e -> e.id("procEndpoint"))
                .log()
                .split()
                .handle(
                        message -> {
                            chachkieRepository.save((Chachkie) message.getPayload());
                            log.info("👀 processing message {}", message.getPayload());
                        }
                )
                .get();
    }

    @Bean
    public StoredProcOutboundGateway procOutboundGateway (StoredProcExecutor storedProcExecutor){
        StoredProcOutboundGateway procOutboundGateway = new StoredProcOutboundGateway(storedProcExecutor);
        procOutboundGateway.setExpectSingleResult(true);
        return procOutboundGateway;
    }

    @Bean
    public StoredProcExecutor storedProcExecutor() {
        StoredProcExecutor storedProcExecutor = new StoredProcExecutor(this.sourceDatasource);
        storedProcExecutor.setStoredProcedureName("chachkiesproc");

        List<ProcedureParameter> procedureParameters = new ArrayList<>();
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

@Repository
interface ChachkieRepository extends CrudRepository<Chachkie, String>{}

@Entity
@Table(name = "chachkies_destination")
@Getter
@AllArgsConstructor
@NoArgsConstructor
class Chachkie {

    @Id
    private String id;
    private Double latitude;
    private Double longitude;

    @Override
    public String toString() {
        return "Chachkie{" +
                "id='" + id + '\'' +
                ", latitude=" + latitude +
                ", longitude=" + longitude +
                '}';
    }
}
