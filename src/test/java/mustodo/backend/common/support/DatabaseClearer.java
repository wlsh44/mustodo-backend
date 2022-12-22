package mustodo.backend.common.support;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.sql.DataSource;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

@Profile("test")
@Component
public class DatabaseClearer implements InitializingBean {

    public static final String REFERENTIAL_INTEGRITY = "SET REFERENTIAL_INTEGRITY %s";
    public static final String TRUNCATE_TABLE = "TRUNCATE TABLE %s";
    public static final String ID_RESET = "ALTER TABLE %s ALTER COLUMN ID RESTART WITH 1";

    @PersistenceContext
    private EntityManager entityManager;

    @Autowired
    private DataSource dataSource;

    private List<String> tableNames;

    @Transactional
    public void clear() {
        entityManager.createNativeQuery(String.format(REFERENTIAL_INTEGRITY, "FALSE")).executeUpdate();
        for (String tableName : tableNames) {
            entityManager.createNativeQuery(String.format(TRUNCATE_TABLE, tableName)).executeUpdate();
            entityManager.createNativeQuery(String.format(ID_RESET, tableName)).executeUpdate();
        }
        entityManager.createNativeQuery(String.format(REFERENTIAL_INTEGRITY, "TRUE")).executeUpdate();
    }

    @Override
    public void afterPropertiesSet() {
        tableNames = new ArrayList<>();
        try (Connection connection = dataSource.getConnection()){
            DatabaseMetaData metaData = connection.getMetaData();
            ResultSet tables = metaData.getTables(null, "PUBLIC", null, new String[]{"TABLE"});
            while (tables.next()) {
                tableNames.add(tables.getString("TABLE_NAME"));
            }
        } catch (SQLException e) {
            throw new RuntimeException("table properties setting failed");
        }
    }
}
