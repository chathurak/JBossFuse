package com.nzx.esb.wits.pull;

import com.nzx.esb.test.AbstractCamelTest;
import com.nzx.esb.test.mock.MockDirectProcessor;
import org.apache.camel.EndpointInject;
import org.apache.camel.component.mock.MockEndpoint;
import org.apache.camel.test.spring.DisableJmx;
import org.junit.Test;
import org.springframework.test.annotation.DirtiesContext;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by faisalmasood on 13/2/17.
 */

@DisableJmx(true)
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
public class WitsQueryTest extends AbstractCamelTest {

    @EndpointInject(uri = "mock:mockErrorGenerator")
    private MockEndpoint mockErrorGenerator;

    @EndpointInject(uri = "mock:mockErrorGeneratorNotReachable")
    private MockEndpoint mockErrorGeneratorNotReachable;

    public WitsQueryTest() {
        super();
        this.setApplicationContext("test-application-context.xml");
    }


    @Test
    public void testParameterFromMEssageIdNotAvailableAndToMessageIdAvailable() throws Exception {
        //wits-query-route

        this.mockErrorGenerator.whenAnyExchangeReceived(MockDirectProcessor.newInstance());
        generateBaseMockedRoute()
                .replaceEndpoint("to-message-and-not-from-message", this.mockErrorGenerator)
                .configureMocks("wits-query-route");

        Map<String, Object> values = constructHeadersWithDefaultValues();
        values.put("toMessageId", "somevalue");
        this.mockProducer.sendBodyAndHeaders("", values);

        executeAsserts();


    }


    @Test
    public void testParameterFromDtmAvailableAndToDtmNotAvailable() throws Exception {
        //wits-query-route

        this.mockErrorGenerator.whenAnyExchangeReceived(MockDirectProcessor.newInstance());
        generateBaseMockedRoute()
                .replaceEndpoint("from-dtm-and-not-to-dtm", this.mockErrorGenerator)
                .configureMocks("wits-query-route");

        Map<String, Object> values = constructHeadersWithDefaultValues();
        values.put("fromDtm", "2014-02-01T09:28:56+1100");

        this.mockProducer.sendBodyAndHeaders("", values);

        executeAsserts();
    }


    @Test
    public void testParameterValidDtm() throws Exception {
        //wits-query-route

        this.mockErrorGenerator.whenAnyExchangeReceived(MockDirectProcessor.newInstance());
        generateBaseMockedRoute()
                .replaceEndpoint("send-to-wits-query", this.mockErrorGenerator)
                .configureMocks("wits-query-route");

        Map<String, Object> values = constructHeadersWithDefaultValues();
        values.put("fromDtm", "2014-02-01T09:28:56+1100");
        values.put("toDtm", "2014-02-01T09:28:56+1100");

        this.mockProducer.sendBodyAndHeaders("", values);

        executeAsserts();
    }


    private void executeAsserts() throws Exception {


        this.mockError.expectedMessageCount(0);
        this.mockErrorGeneratorNotReachable.expectedMessageCount(0);
        this.mockErrorGenerator.expectedMessageCount(1);


        this.mockError.assertIsSatisfied();
        this.mockErrorGenerator.assertIsSatisfied();
        this.mockErrorGeneratorNotReachable.assertIsSatisfied();
    }

    private AbstractCamelTest generateBaseMockedRoute() throws Exception {
        return
                this.replaceFromEndpointWithDefaultMock()
                        .replaceToEndpointWithDefaultMock()
                        .replaceEndpoint("to-message-and-not-from-message", this.mockErrorGeneratorNotReachable)
                        .replaceEndpoint("from-message-and-not-to-message", this.mockErrorGeneratorNotReachable)
                        .replaceEndpoint("to-dtm-and-not-from-dtm", this.mockErrorGeneratorNotReachable)
                        .replaceEndpoint("from-dtm-and-not-to-dtm", this.mockErrorGeneratorNotReachable)
                        .replaceEndpoint("from-dtm-not-valid", this.mockErrorGeneratorNotReachable)
                        .replaceEndpoint("to-dtm-not-valid", this.mockErrorGeneratorNotReachable)
                        .replaceEndpoint("commit-pull-sent-not-boolean", this.mockErrorGeneratorNotReachable)
                        .replaceEndpoint("send-to-wits-query", this.mockErrorGeneratorNotReachable);
    }

    public static Map<String, Object> constructHeadersWithDefaultValues() {


        Map<String, Object> headers = new HashMap<>();
        headers.put("toMessageId", "");
        headers.put("fromMessageId", "");
        headers.put("filename", "");
        headers.put("messageType", "");
        headers.put("fromDtm", "");
        headers.put("toDtm", "");
        headers.put("commitPullSent", "false");
        headers.put("pageIndex", "1");
        headers.put("pageSize", "1");


        headers.put("CamelHttpUrl", "http://www.google.com");



        return headers;

    }

}
