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
package com.camunda.eric.orion.ocwf.processor;

import java.util.Map;

import org.camunda.bpm.engine.ProcessEngine;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class WorkerThread implements Runnable {

    private final Logger LOGGER = LoggerFactory.getLogger(WorkerThread.class.getName());

    private ProcessEngine processEngine;

    private Map<String, Object> vars;

    private String processId;

    public WorkerThread(final ProcessEngine processEngine, final Map<String, Object> vars, final String processId) {

        this.processEngine = processEngine;
        this.vars = vars;
        this.processId = processId;
    }

    @Override
    public void run() {

        try {
            processEngine.getRuntimeService().startProcessInstanceByKey(processId, vars);

        } catch (final Exception ex) {

            ex.printStackTrace();
        }
    }
}
