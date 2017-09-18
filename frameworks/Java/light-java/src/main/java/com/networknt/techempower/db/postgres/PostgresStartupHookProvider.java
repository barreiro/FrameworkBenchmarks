package com.networknt.techempower.db.postgres;

import com.networknt.config.Config;
import com.networknt.server.StartupHookProvider;
import io.agroal.api.AgroalDataSource;
import io.agroal.api.configuration.supplier.AgroalDataSourceConfigurationSupplier;
import io.agroal.api.security.NamePrincipal;
import io.agroal.api.security.SimplePassword;

import java.sql.SQLException;

/**
 * Created by steve on 10/02/17.
 */
public class PostgresStartupHookProvider implements StartupHookProvider {

    static String CONFIG_NAME = "postgres";
    public static AgroalDataSource ds;

    @Override
    public void onStartup() {
        initDataSource();
    }

    static void initDataSource() {
        PostgresConfig config = (PostgresConfig) Config.getInstance().getJsonObjectConfig(CONFIG_NAME, PostgresConfig.class);

        AgroalDataSourceConfigurationSupplier configuration = new AgroalDataSourceConfigurationSupplier()
                .connectionPoolConfiguration( cp -> cp
                        .maxSize( config.getMaximumPoolSize() )
                        .connectionFactoryConfiguration( cf -> cf
                                .driverClassName( "org.postgresql.Driver" ) // AG-29
                                .jdbcUrl( config.getJdbcUrl() )
                                .principal( new NamePrincipal( config.getUsername() ) )
                                .credential( new SimplePassword( config.getPassword() ) )
                        )
                );

        try {
            ds = AgroalDataSource.from( configuration );
        } catch ( SQLException e ) {
            throw new IllegalArgumentException( e );
        }
    }
}
