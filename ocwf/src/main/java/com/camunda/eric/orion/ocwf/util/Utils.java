/*------------------------------------------------------------------------------
 *******************************************************************************
 * COPYRIGHT Ericsson 2015
 *
 * The copyright to the computer program(s) herein is the property of
 * Ericsson Inc. The programs may be used and/or copied only with written
 * permission from Ericsson Inc. or in accordance with the terms and
 * conditions stipulated in the agreement/contract under which the
 * program(s) have been supplied.
 *******************************************************************************
 *----------------------------------------------------------------------------*/
package com.camunda.eric.orion.ocwf.util;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import javax.net.ssl.SSLContext;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.ssl.SSLContextBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.camunda.eric.orion.ocwf.entity.WebApiVmResponse;

public class Utils {

    private final static Logger LOGGER = LoggerFactory.getLogger(Utils.class.getName());

    public static final String tenantId = "eo-orion";
    public static final String authHeader = "Basic ZGVtb3VzZXIxOlRlbXBQYXNzdzByZA==";
    public static final String ecmUrl = "ieatecm11.athtem.eei.ericsson.se";
    public static final String starStoptVmProcessId = "start_stop_recreate_vm";
    public static final String restartVmProcessId = "restart_vm";
    public static final String imageName = "test_image";

    public static WebApiVmResponse sendPost(final String url, final String tenantId, final String authHeader, final String requestBody) {

        try {
            final HttpPost post = new HttpPost(url);

            // add header
            post.setHeader("Content-Type", "application/json");
            post.setHeader("tenantId", tenantId);
            post.setHeader("Authorization", authHeader);

            if (requestBody != null) {
                //final String jsonString = " \"bootSource\": {" + "\"imageName\":\"" + imageName + "\"}";

                final StringEntity requestEntity = new StringEntity(requestBody, ContentType.APPLICATION_JSON);
                post.setEntity(requestEntity);
            }

            final SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();

            final HttpClient client = HttpClientBuilder.create().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

            LOGGER.info("######Sending 'POST' request to URL : " + url);

            /*
             * for (final Header header : post.getAllHeaders()) { LOGGER.info("post header:: " + header.getName() + ":" + header.getValue()); }
             */

            final HttpResponse response = client.execute(post);

            final WebApiVmResponse webApiVmResponse = new WebApiVmResponse();

            final int responseCode = response.getStatusLine().getStatusCode();

            LOGGER.info("######Response Code : " + responseCode);
            if (responseCode != 200) {

                webApiVmResponse.setError(true);
                webApiVmResponse.setStatusCode(responseCode + "");
                return webApiVmResponse;
            }

            final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            final StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            LOGGER.info("#### Response Body::" + result.toString());

            webApiVmResponse.setError(false);
            webApiVmResponse.setStatusCode(responseCode + "");
            webApiVmResponse.setResponseBody(result.toString());

            return webApiVmResponse;

        } catch (final Exception ex) {

            LOGGER.info(ex.getMessage());

            final WebApiVmResponse webApiVmResponse = new WebApiVmResponse();
            webApiVmResponse.setError(true);
            webApiVmResponse.setStatusCode("1");
            webApiVmResponse.setMessage(ex.getMessage());
            return webApiVmResponse;
        }

    }

    public static WebApiVmResponse sendGet(final String url, final String tenantId, final String authHeader) {

        try {
            final HttpGet get = new HttpGet(url);

            // add header
            get.setHeader("Content-Type", "application/json");
            get.setHeader("tenantId", tenantId);
            get.setHeader("Authorization", authHeader);

            final SSLContext sslContext = new SSLContextBuilder().loadTrustMaterial(null, (certificate, authType) -> true).build();

            final HttpClient client = HttpClientBuilder.create().setSSLContext(sslContext).setSSLHostnameVerifier(new NoopHostnameVerifier()).build();

            LOGGER.info("######Sending 'GET' request to URL : " + url);

            /*
             * for (final Header header : post.getAllHeaders()) { LOGGER.info("post header:: " + header.getName() + ":" + header.getValue()); }
             */

            final HttpResponse response = client.execute(get);

            final WebApiVmResponse webApiVmResponse = new WebApiVmResponse();

            final int responseCode = response.getStatusLine().getStatusCode();

            LOGGER.info("######Response Code : " + responseCode);
            if (responseCode != 200) {

                webApiVmResponse.setError(true);
                webApiVmResponse.setStatusCode(responseCode + "");
                return webApiVmResponse;
            }

            final BufferedReader rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            final StringBuffer result = new StringBuffer();
            String line = "";
            while ((line = rd.readLine()) != null) {
                result.append(line);
            }

            LOGGER.info("#### Response Body::" + result.toString());

            webApiVmResponse.setError(false);
            webApiVmResponse.setStatusCode(responseCode + "");
            webApiVmResponse.setResponseBody(result.toString());

            return webApiVmResponse;

        } catch (final Exception ex) {

            LOGGER.info(ex.getMessage());

            final WebApiVmResponse webApiVmResponse = new WebApiVmResponse();
            webApiVmResponse.setError(true);
            webApiVmResponse.setStatusCode("1");
            webApiVmResponse.setMessage(ex.getMessage());
            return webApiVmResponse;
        }

    }

    /*
     * public void suspendTimerEventInOtherProcessInstances(final String processDefinitionKey, final Long customerId, final String boundaryEventName)
     * { final List<Job> timerJobs = getTimersForProcessByCustomerIdAndJobName(processDefinitionKey, customerId, boundaryEventName);
     *
     * for (final Job job : timerJobs) { managementService.suspendJobById(job.getId()); } }
     *
     *
     *
     *
     *
     * private List<Job> getTimersForProcessByCustomerIdAndJobName(final String processDefinitionKey, final Long customerId, final String
     * boundaryEventName) { final List<ProcessInstance> processInstanceList =
     * runtimeService.createProcessInstanceQuery().processDefinitionKey(processDefinitionKey) .variableValueEquals(ProcessVariables.CUSTOMER_ID,
     * customerId).list();
     *
     * final List<Job> jobList = new ArrayList<Job>(); for (final ProcessInstance processInstance : processInstanceList) { final List<Job> timerJobs =
     * managementService.createJobQuery().processInstanceId(processInstance.getId()).timers() .active().list();
     *
     * for (final Job job : timerJobs) { if (((org.camunda.bpm.engine.impl.persistence.entity.TimerEntity) job)
     * .getJobHandlerConfiguration().equals(boundaryEventName)) { jobList.add(job); } } } return jobList;
     *
     * }
     */

}
