package com.eclecticlogic.pedal;

import java.util.HashMap;
import java.util.Hashtable;
import java.util.Map;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.naming.spi.InitialContextFactory;
import javax.naming.spi.InitialContextFactoryBuilder;
import org.springframework.jdbc.datasource.DriverManagerDataSource;

public class IvInitialContextFactoryBuilder implements InitialContextFactoryBuilder {

    private final Map<String, DriverManagerDataSource> jndiDatasources;

    IvInitialContextFactoryBuilder(final Map<String, DriverManagerDataSource> jndiDatasources) {
        this.jndiDatasources = jndiDatasources;
    }

    @Override
    public InitialContextFactory createInitialContextFactory(final Hashtable<?, ?> environment) throws NamingException {
        if (jndiDatasources == null || jndiDatasources.isEmpty()) {
            throw new NamingException("Unable to find datasource");
        }
        return maakInitialContextFactory();
    }

    private InitialContextFactory maakInitialContextFactory() {
        return environment -> new InitialContext() {
            private final HashMap<String, DriverManagerDataSource> dataSources = new HashMap<>();

            @Override
            public Object lookup(final String name) throws NamingException {
                if (dataSources.isEmpty()) {
                    dataSources.putAll(jndiDatasources);
                }

                if (dataSources.containsKey(name)) {
                    return dataSources.get(name);
                }

                throw new NamingException("Unable to find datasource: " + name);
            }
        };
    }
}
