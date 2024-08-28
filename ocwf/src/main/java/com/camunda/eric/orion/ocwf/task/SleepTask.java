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
package com.camunda.eric.orion.ocwf.task;

import org.camunda.bpm.engine.delegate.DelegateExecution;
import org.camunda.bpm.engine.delegate.JavaDelegate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class SleepTask implements JavaDelegate {

    private final Logger LOGGER = LoggerFactory.getLogger(SleepTask.class.getName());

    @Override
    public void execute(final DelegateExecution execution) {

        try {
            Thread.sleep(30000L);
        } catch (final InterruptedException e) {
            // TODO Auto-generated catch block
            LOGGER.info("####sleep failed::" + e.getMessage());
        }

    }

}
