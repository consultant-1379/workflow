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

import javax.ejb.Local;

import com.camunda.eric.orion.ocwf.entity.ActionMessage;

@Local
public interface ActionMessageProcessorLocal {

    public void processAction(final ActionMessage actionMessage);
}
