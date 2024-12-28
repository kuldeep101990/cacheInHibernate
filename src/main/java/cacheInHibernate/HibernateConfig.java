package cacheInHibernate;
import org.hibernate.cfg.Configuration;

public class HibernateConfig {
 
    public static Configuration getConfig() {
        Configuration config = new Configuration();
 
        config.setProperty("hibernate.connection.driver_class", "com.mysql.cj.jdbc.Driver");
        config.setProperty("hibernate.connection.url", "jdbc:mysql://localhost:3306/cacheInHibernate?createDatabaseIfNotExist=true");
        config.setProperty("hibernate.connection.username", "root");
        config.setProperty("hibernate.connection.password", "D@2491219997k");
        config.setProperty("hibernate.connection.pool_size", "10");
        config.setProperty("hibernate.dialect", "org.hibernate.dialect.MySQLDialect");
        config.setProperty("hibernate.current_session_context_class", "thread");
        config.setProperty("hibernate.show_sql", "true");
        config.setProperty("hibernate.format_sql", "true");
        config.setProperty("hibernate.hbm2ddl.auto", "create");
        config.setProperty("hibernate.use_sql_comments", "true");
        config.setProperty("hibernate.generate_statistics", "true");
        config.setProperty("org.hibernate.SQL", "DEBUG");
        config.setProperty("org.hibernate.type", "TRACE");
 
        return config;
    }
}