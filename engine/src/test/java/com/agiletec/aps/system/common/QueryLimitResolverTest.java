package com.agiletec.aps.system.common;

import static org.junit.jupiter.api.Assertions.assertEquals;

import org.apache.commons.dbcp2.BasicDataSource;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
public class QueryLimitResolverTest {

    @Mock
    private BasicDataSource dataSource;

    @Test
    public void testDerbyDriver() throws Exception {
        testCreateLimitBlock("org.apache.derby.jdbc.EmbeddedDriver",
                " OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ");
    }

    @Test
    public void testPostgresDriver() throws Exception {
        testCreateLimitBlock("org.postgresql.Driver",
                " OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ");
    }

    @Test
    public void testMySQLDriver() throws Exception {
        testCreateLimitBlock("com.mysql.jdbc.Driver",
                " LIMIT 1 OFFSET 0 ");
    }

    @Test
    public void testDeprecatedOracleDriver() throws Exception {
        testCreateLimitBlock("oracle.jdbc.driver.OracleDriver",
                " OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ");
    }

    @Test
    public void testOracleDriver() throws Exception {
        testCreateLimitBlock("oracle.jdbc.OracleDriver",
                " OFFSET 0 ROWS FETCH NEXT 1 ROWS ONLY ");
    }

    private void testCreateLimitBlock(String driverClassName, String expected) throws  Exception {
        Mockito.when(dataSource.getDriverClassName()).thenReturn(driverClassName);

        FieldSearchFilter filter = new FieldSearchFilter();
        filter.setLimit(1);
        filter.setOffset(0);

        assertEquals(expected, QueryLimitResolver.createLimitBlock(filter, dataSource, driverClassName));
    }
}
