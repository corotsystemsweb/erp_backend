package com.sms.util;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@Configuration
public class  DatabaseUtil {

    @Value("${spring.datasource.url}")
    private String databaseUrl;

    @Value("${spring.datasource.username}")
    private String databaseUser;

    @Value("${spring.datasource.password}")
    private String databasePassword;

    private static DatabaseUtil instance;

    public DatabaseUtil() {
        instance = this;
    }

    public static JdbcTemplate getJdbctemplateForSchool(String schoolCode) {
        HikariConfig config = new HikariConfig();
        config.setJdbcUrl(instance.databaseUrl);
        config.setUsername(instance.databaseUser);
        config.setPassword(instance.databasePassword);
        config.addDataSourceProperty("currentSchema", schoolCode);
        HikariDataSource dataSource = new HikariDataSource(config);
        return new JdbcTemplate(dataSource);
    }
    public static void closeDataSource(JdbcTemplate jdbcTemplate) {
        if (jdbcTemplate != null) {
            HikariDataSource dataSource = (HikariDataSource) jdbcTemplate.getDataSource();
            if (dataSource != null) {
                dataSource.close();
            }
        }
    }


}

//package com.sms.util;
//
//import com.zaxxer.hikari.HikariConfig;
//import com.zaxxer.hikari.HikariDataSource;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Component;
//
//import javax.annotation.PreDestroy;
//import java.util.Map;
//import java.util.concurrent.ConcurrentHashMap;
//
//@Component
//@Configuration
//public class DatabaseUtil {
//
//    @Value("${spring.datasource.url}")
//    private String databaseUrl;
//
//    @Value("${spring.datasource.username}")
//    private String databaseUser;
//
//    @Value("${spring.datasource.password}")
//    private String databasePassword;
//
//    // Cache data sources per school
//    private static final Map<String, HikariDataSource> dataSourceCache = new ConcurrentHashMap<>();
//
//    // Fixed: Remove static instance reference
//
//    public JdbcTemplate getJdbctemplateForSchool(String schoolCode) {
//        HikariDataSource dataSource = dataSourceCache.computeIfAbsent(schoolCode, k -> {
//            HikariConfig config = new HikariConfig();
//            config.setJdbcUrl(this.databaseUrl);
//            config.setUsername(this.databaseUser);
//            config.setPassword(this.databasePassword);
//            config.addDataSourceProperty("currentSchema", schoolCode);
//
//            // Optimized Hikari settings
//            config.setConnectionTimeout(30000);
//            config.setMaximumPoolSize(10);
//            config.setMinimumIdle(2);
//            config.setIdleTimeout(600000);
//            config.setMaxLifetime(1800000);
//            config.setConnectionTestQuery("SELECT 1");
//
//            return new HikariDataSource(config);
//        });
//
//        return new JdbcTemplate(dataSource);
//    }
//
//    @PreDestroy
//    public void closeAllDataSources() {
//        dataSourceCache.forEach((schoolCode, dataSource) -> {
//            if (dataSource != null && !dataSource.isClosed()) {
//                dataSource.close();
//            }
//        });
//        dataSourceCache.clear();
//    }
//}
