package com.eclecticlogic.pedal;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import javax.naming.NamingException;
import javax.naming.spi.NamingManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.containers.startupcheck.IsRunningStartupCheckStrategy;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.utility.DockerImageName;

public class SpringWithJNDIRunner5 {
    private static final Logger LOG = LoggerFactory.getLogger(SpringWithJNDIRunner5.class);
    private static final String PEDAL_DATABASE_NAME = "pedal";
    private static final String POSTGRESQL_VERSION = "postgis/postgis:16-3.4";
    private static final int STARTUP_ATTEMPTS = 10;
    private static boolean isJNDIactive;

    @Container
    private static final PostgreSQLContainer sqlContainer =
            new PostgreSQLContainer<>(DockerImageName.parse(POSTGRESQL_VERSION).asCompatibleSubstituteFor("postgres")).withDatabaseName(PEDAL_DATABASE_NAME)
                    .withUsername(PEDAL_DATABASE_NAME).withPassword(PEDAL_DATABASE_NAME).withStartupTimeout(Duration.ofSeconds(600))
                    .withCommand("postgres");

    /**
     * JNDI is activated with this constructor.
     */
    public SpringWithJNDIRunner5() {
        synchronized (SpringWithJNDIRunner5.class) {
            if (!isJNDIactive) {
                sqlContainer.setStartupCheckStrategy(new IsRunningStartupCheckStrategy());
                sqlContainer.setStartupAttempts(STARTUP_ATTEMPTS);
                sqlContainer.start();

                try {
                    final Map<String, DriverManagerDataSource> jndiDatasources = new HashMap<>();
                    jndiDatasources.put("java:pedal/datasources/pedal", maakDriverManagerDataSource());

                    NamingManager.setInitialContextFactoryBuilder(new IvInitialContextFactoryBuilder(jndiDatasources));
                } catch (final NamingException e) {
                    LOG.error("Fout opgetreden bij maken van JNDI datasource.", e);
                }
                isJNDIactive = true;
            }
        }
    }

    private DriverManagerDataSource maakDriverManagerDataSource() {
        return new DriverManagerDataSource(SpringWithJNDIRunner5.sqlContainer.getJdbcUrl(),
                SpringWithJNDIRunner5.sqlContainer.getUsername(), SpringWithJNDIRunner5.sqlContainer.getPassword());
    }

}
