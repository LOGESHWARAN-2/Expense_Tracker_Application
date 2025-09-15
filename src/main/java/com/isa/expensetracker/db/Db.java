package com.isa.expensetracker.db;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.flywaydb.core.Flyway;

import javax.sql.DataSource;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.Properties;

public class Db {
    private static HikariDataSource ds;
    private static final Properties APP_PROPS = new Properties();

    static {
        try (InputStream is = Db.class.getClassLoader().getResourceAsStream("application.properties")) {
            if (is != null) APP_PROPS.load(is);
            else System.err.println("[Db] application.properties NOT found on classpath.");
        } catch (Exception e) {
            System.err.println("[Db] Failed to load application.properties: " + e.getMessage());
        }
    }

    public static void init() {
        if (ds != null) return;

        String url    = get("DB_URL", "jdbc:postgresql://localhost:5432/expensetracker");
        String user   = get("DB_USER", "expuser");
        String pass   = get("DB_PASSWORD", "exppass");
        String schema = get("DB_SCHEMA", "public");

        System.out.println("[Db] url=" + url + ", user=" + user + ", schema=" + schema);

        // Hikari
        HikariConfig cfg = new HikariConfig();
        cfg.setJdbcUrl(url);
        cfg.setUsername(user);
        cfg.setPassword(pass);
        cfg.setMaximumPoolSize(10);
        cfg.setPoolName("ExpenseTrackerPool");
        ds = new HikariDataSource(cfg);

        // Probes
        URL cp = Db.class.getClassLoader().getResource("db/migration/V1__init.sql");
        System.out.println("[Db] Probe classpath db/migration -> " + (cp != null ? cp : "NOT FOUND"));
        Path fsDir = Paths.get("src/main/resources/db/migration");
        System.out.println("[Db] Probe filesystem db/migration dir -> " + (Files.isDirectory(fsDir) ? fsDir.toAbsolutePath() : "NOT FOUND"));

        // Prefer filesystem (dev) if present; else fall back to classpath
        if (Files.isDirectory(fsDir)) {
            System.out.println("[Db] Using Flyway location: filesystem:" + fsDir.toAbsolutePath());
            Flyway.configure()
                    .dataSource(ds)
                    .schemas(schema)
                    .locations("filesystem:" + fsDir.toAbsolutePath().toString())
                    .baselineOnMigrate(true)
                    .load()
                    .migrate();
        } else {
            if (cp == null) {
                throw new IllegalStateException(
                        "Migration not found on classpath and filesystem folder missing.\n" +
                                "Expected either:\n" +
                                "  target/classes/db/migration/V1__init.sql (classpath)\n" +
                                "OR\n" +
                                "  src/main/resources/db/migration/V1__init.sql (filesystem)"
                );
            }
            System.out.println("[Db] Using Flyway default classpath: db/migration");
            Flyway.configure()
                    .dataSource(ds)
                    .schemas(schema)
                    .baselineOnMigrate(true)
                    .load()
                    .migrate();
        }
    }

    public static DataSource dataSource() { init(); return ds; }

    private static String get(String key, String def) {
        String v = System.getenv(key);
        if (v == null) v = System.getProperty(key);
        if (v == null) v = APP_PROPS.getProperty(key);
        return Objects.requireNonNullElse(v, def);
    }
}
