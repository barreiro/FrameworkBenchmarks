package com.networknt.techempower.db.mysql;

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
public class MysqlStartupHookProvider implements StartupHookProvider {

    static String CONFIG_NAME = "mysql";
    public static AgroalDataSource ds;

    @Override
    public void onStartup() {
        initDataSource();
    }

    static void initDataSource() {
        MysqlConfig config = (MysqlConfig) Config.getInstance().getJsonObjectConfig( CONFIG_NAME, MysqlConfig.class );

        AgroalDataSourceConfigurationSupplier configuration = new AgroalDataSourceConfigurationSupplier()
                .connectionPoolConfiguration( cp -> cp
                        .maxSize( config.getMaximumPoolSize() )
                        .connectionFactoryConfiguration( cf -> cf
                                .driverClassName( "com.mysql.jdbc.Driver" ) // AG-29
                                .jdbcUrl( config.getJdbcUrl() )
                                .principal( new NamePrincipal( config.getUsername() ) )
                                .credential( new SimplePassword( config.getPassword() ) )

                                .jdbcProperty( "useServerPrepStmts", "" + config.isUseServerPrepStmts() )
                                .jdbcProperty( "CachePrepStmts", "" + config.isCachePrepStmts() )
                                .jdbcProperty( "CacheCallableStmts", "" + config.isCacheCallableStmts() )
                                .jdbcProperty( "PrepStmtCacheSize", "" + config.getPrepStmtCacheSize() )
                                .jdbcProperty( "PrepStmtCacheSqlLimit", "" + config.getPrepStmtCacheSqlLimit() )
                        )
                );

        try {
            ds = AgroalDataSource.from( configuration );
        }
        catch ( SQLException e ) {
            // ignore
        }
    }
}
