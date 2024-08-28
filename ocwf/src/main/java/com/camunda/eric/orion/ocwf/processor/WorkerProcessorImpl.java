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
import java.util.logging.Logger;

import javax.ejb.Asynchronous;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;

@Stateless
public class WorkerProcessorImpl implements WorkerProcessorLocal {

    public static Logger logger = Logger.getLogger(WorkerProcessorImpl.class.getName());

    @Inject
    private ProcessEngine processEngine;

    @Override
    @Asynchronous
    public void runWorkFlow(final Map<String, Object> vars, final String processId) {

        try {
            processEngine.getRuntimeService().startProcessInstanceByKey(processId, vars);

        } catch (final Exception ex) {

            ex.printStackTrace();
        }
    }

}
