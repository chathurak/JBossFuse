package com.nzx.esb.wits.pull;

import com.nzx.esb.test.AbstractCamelIntegrationTest;
import org.apache.camel.test.spring.DisableJmx;
import org.junit.Before;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.annotation.DirtiesContext;

import java.util.Arrays;
import java.util.HashSet;

/**
 * Created by faisalmasood on 13/2/17.
 */
@DisableJmx(true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WitsQueryIT extends AbstractCamelIntegrationTest {

    @Test
    public void testFullWitsQuery() throws Exception {
        String output = runWitsQuery("http://localhost:8080/wits/participant/C001");

        assertTrue(output.contains("\"count\":2"));
        assertTrue(output.contains("\"total\":2"));
    }

    @Test
    public void testFullWitsQueryPagination() throws Exception {

        String output = runWitsQuery("http://localhost:8080/wits/participant/C001?pageSize=1");


        assertTrue(output.contains("\"count\":1"));
        assertTrue(output.contains("\"total\":2"));
        assertTrue(output.contains("\"messageType\":\"TestType2\""));
        assertTrue(output.contains("\"next\":\"http://localhost:8080/wits/participant/C001?&pageSize=1&pageIndex=2\""));


    }



    @Test
    public void testFullWitsQueryPaginationAndFileName() throws Exception {

        String output = runWitsQuery("http://localhost:8080/wits/participant/C001?pageSize=1&filename=FILENAME002");


        assertTrue(output.contains("\"count\":1"));
        assertTrue(output.contains("\"total\":1"));
        assertTrue(output.contains("\"messageType\":\"TestType2\""));
        assertTrue(output.contains("\"next\":\"\""));
        assertTrue(output.contains("\"fileName\":\"FILENAME002\""));


    }

    @Test
    public void testFullWitsQueryPaginationPageNumber2() throws Exception {

        String output = runWitsQuery("http://localhost:8080/wits/participant/C001?pageSize=1&pageIndex=2");


        assertTrue(output.contains("\"count\":1"));
        assertTrue(output.contains("\"total\":2"));
        assertTrue(output.contains("\"messageType\":\"TESTType1\""));
        assertTrue(output.contains("\"prev\":\"http://localhost:8080/wits/participant/C001?&pageSize=1&pageIndex=1\""));
        assertTrue(output.contains("\"next\":\"\""));


    }


    @Test
    public void testFullWitsQueryPaginationWithDateAndNoData() throws Exception {

        String output = runWitsQuery("http://localhost:8080/wits/participant/C001?pageSize=1&pageIndex=2&fromDtm=2014-02-01T09:28:56%2B1100&toDtm=2014-02-01T09:28:56%2B1100");


        assertTrue(output.contains("\"count\":0"));
        assertTrue(output.contains("\"total\":0"));

    }
    @Test
    public void testFullWitsQueryPaginationWithDateData() throws Exception {

        String output = runWitsQuery("http://localhost:8080/wits/participant/C001?pageSize=1&pageIndex=2&fromDtm=2017-02-10T15:00:00%2B1100&toDtm=2017-02-12T16:00:00%2B1100");


        assertTrue(output.contains("\"count\":1"));
        assertTrue(output.contains("\"total\":2"));

    }

    private String runWitsQuery(String url) throws Exception {

        enableRoutesForThisRouteContextOnly(new HashSet<>(Arrays.asList("wits-query-routes")),
                new HashSet<>(Arrays.asList("wits-query-rest-dsl-route")));


        String output = placeHttpGet(url);

        System.out.println("\n\n\n\n^^^^" + output + "\n\n\n\n");

        return output;

    }


    @Before
    public void prepareDBData() {
        JdbcTemplate witsJdbc = getJdbcHandle("wits-data-source");
        witsJdbc.execute(MESSAGE_STORE_DATA_ROW_DELETE);
        witsJdbc.execute(CUSTOMER_PULL_REQUEST_DELETE);

        witsJdbc.execute(MESSAGE_STORE_DATA_ROW_1);
        witsJdbc.execute(MESSAGE_STORE_DATA_ROW_2);

        witsJdbc.execute(CUSTOMER_PULL_REQUEST_1);
        witsJdbc.execute(CUSTOMER_PULL_REQUEST_2);
        witsJdbc.execute(CUSTOMER_PULL_REQUEST_3);


    }


    private final static String MESSAGE_STORE_DATA_ROW_1 =
            "INSERT INTO TRADING.MESSAGE_STORE (MESSAGE_ID, CUST_MSG_SEQ_ID, FILENAME, CUSTOMER_CODE, MESSAGE_TYPE, ENDPOINT_ID, DATETIME_SENT, DATETIME_ACK, ERROR_MESSAGE) VALUES (12345, 12345, 'FILENAME001', 'C001', 'TESTType1', 1, TO_DATE('2017-02-10 15:06:05', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2017-02-11 15:06:10', 'YYYY-MM-DD HH24:MI:SS'), 'System Down')";

    private final static String MESSAGE_STORE_DATA_ROW_2 =
            "INSERT INTO TRADING.MESSAGE_STORE (MESSAGE_ID, CUST_MSG_SEQ_ID, FILENAME, CUSTOMER_CODE, MESSAGE_TYPE, ENDPOINT_ID, DATETIME_SENT, DATETIME_ACK, ERROR_MESSAGE) VALUES (12346, 12346, 'FILENAME002', 'C001', 'TestType2', 2, TO_DATE('2017-02-11 15:41:54', 'YYYY-MM-DD HH24:MI:SS'), TO_DATE('2017-02-11 15:47:01', 'YYYY-MM-DD HH24:MI:SS'), 'Http Error ')";

    private final static String MESSAGE_STORE_DATA_ROW_DELETE =
            "DELETE FROM TRADING.MESSAGE_STORE WHERE MESSAGE_ID IN (12345, 12346)";

    private final static String CUSTOMER_PULL_REQUEST_1 =
            "INSERT INTO TRADING.CUSTOMER_PULL_REQUESTS (CUSTOMER_CODE, DATE_RECEIVED, REQUEST_MESSAGE, RESPONSE_CODE, RESPONSE_MESSAGE, RECORD_ID, MESSAGE_ID) VALUES ( 'C001', TO_DATE('2017-01-10 04:41:53', 'YYYY-MM-DD HH24:MI:SS'), 'SAMEPL MESSAGE', 200, 'OK', '45B71A72A7082D14E053020012ACC234' , 12345)";

    private final static String CUSTOMER_PULL_REQUEST_2 =
            "INSERT INTO TRADING.CUSTOMER_PULL_REQUESTS (CUSTOMER_CODE, DATE_RECEIVED, REQUEST_MESSAGE, RESPONSE_CODE, RESPONSE_MESSAGE, RECORD_ID, MESSAGE_ID) VALUES ( 'C001', TO_DATE('2017-01-10 04:27:48', 'YYYY-MM-DD HH24:MI:SS'), 'SAMEPL MESSAGE 2', 200, 'OK', '45B6E812E95A2A7EE053020012ACE751' , 12345)";

    private final static String CUSTOMER_PULL_REQUEST_3 =
            "INSERT INTO TRADING.CUSTOMER_PULL_REQUESTS (CUSTOMER_CODE, DATE_RECEIVED, REQUEST_MESSAGE, RESPONSE_CODE, RESPONSE_MESSAGE, RECORD_ID, MESSAGE_ID) VALUES ( 'C001', TO_DATE('2017-01-10 04:41:45', 'YYYY-MM-DD HH24:MI:SS'), 'SAMEPL MESSAGE 3', 200, 'OK', '45B7178F09A52D37E053020012AC9FE1' , 12346)";

    private final static String CUSTOMER_PULL_REQUEST_DELETE =
            "DELETE FROM TRADING.CUSTOMER_PULL_REQUESTS WHERE RECORD_ID IN ('45B71A72A7082D14E053020012ACC234', '45B6E812E95A2A7EE053020012ACE751' , '45B7178F09A52D37E053020012AC9FE1')";

}
