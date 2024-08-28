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

import java.util.*;
import java.util.logging.Logger;

import javax.ejb.Stateless;
import javax.enterprise.concurrent.ManagedThreadFactory;
import javax.inject.Inject;

import org.camunda.bpm.engine.ProcessEngine;

import com.camunda.eric.orion.ocwf.entity.ActionMessage;
import com.camunda.eric.orion.ocwf.entity.AffectedObjects;
import com.camunda.eric.orion.ocwf.util.Utils;

@Stateless
public class ActionMessageProcessor {

    public static Logger logger = Logger.getLogger(ActionMessageProcessor.class.getName());

    @Inject
    ProcessEngine processEngine;

    //@Resource(name = "java:jboss/ee/concurrency/factory/default")
    private ManagedThreadFactory threadFactory;

    //@Override
    public void processAction(final ActionMessage actionMessage) {

        if (actionMessage != null && actionMessage.getFaultAssetType().equalsIgnoreCase("vm")) {

            if (actionMessage.getAction().equalsIgnoreCase("start") || actionMessage.getAction().equalsIgnoreCase("stop")
                    || actionMessage.getAction().equalsIgnoreCase("recreate")) {
                logger.info("###########start process with id::" + Utils.starStoptVmProcessId);

                final Map<String, Object> vars = new HashMap<String, Object>();
                vars.put("vm_id", actionMessage.getFaultAssetId());
                vars.put("command", actionMessage.getAction());
                vars.put("base_url", Utils.ecmUrl + "/ecm_service");
                vars.put("service", "vms");
                vars.put("tenant_id", Utils.tenantId);
                vars.put("auth_header", Utils.authHeader);

                try {

                    if (processEngine != null) {

                        logger.info("process engine is not null");
                        final WorkerThread workerThread = new WorkerThread(processEngine, vars, Utils.starStoptVmProcessId);

                        final Thread thread = threadFactory.newThread(workerThread);
                        thread.start();
                    } else {
                        logger.info("process engine is null");
                    }

                } catch (final Exception ex) {
                    ex.printStackTrace();
                    logger.info(ex.getMessage());
                }

                logger.info("process end action message");

            } else if (actionMessage.getAction().equalsIgnoreCase("restart")) {
                logger.info("########### restart process with id::" + Utils.restartVmProcessId);

                final Map<String, Object> vars = new HashMap<String, Object>();
                vars.put("vm_id", actionMessage.getFaultAssetId());
                vars.put("firstCommand", "stop");
                vars.put("secondCommand", "start");
                vars.put("base_url", Utils.ecmUrl + "/ecm_service");
                vars.put("service", "vms");
                vars.put("tenant_id", Utils.tenantId);
                vars.put("auth_header", Utils.authHeader);

                try {

                    if (processEngine != null) {

                        logger.info("process engine is not null");
                        final WorkerThread workerThread = new WorkerThread(processEngine, vars, Utils.restartVmProcessId);

                        final Thread thread = threadFactory.newThread(workerThread);
                        thread.start();
                    } else {
                        logger.info("process engine is null");
                    }

                } catch (final Exception ex) {
                    ex.printStackTrace();
                    logger.info(ex.getMessage());
                }

                logger.info("process end action message");

            }

        } else if (actionMessage != null && actionMessage.getFaultAssetType().equalsIgnoreCase("host")) { //host UC

            if (actionMessage.getAction().equalsIgnoreCase("recreatevms")) {

                final List<AffectedObjects> affectedObjects = actionMessage.getAffectedObjects();

                if (affectedObjects != null && affectedObjects.size() > 0) {

                    for (int i = 0; i < affectedObjects.size(); i++) {

                        logger.info("########### recreate VMs process with id::" + Utils.starStoptVmProcessId);

                        final Map<String, Object> vars = new HashMap<String, Object>();
                        vars.put("vm_id", actionMessage.getAffectedObjects().get(i).getAssetId());
                        vars.put("command", "recreate");
                        vars.put("base_url", Utils.ecmUrl + "/ecm_service");
                        vars.put("service", "vms");
                        vars.put("tenant_id", Utils.tenantId);
                        vars.put("auth_header", Utils.authHeader);

                        try {

                            if (processEngine != null) {

                                logger.info("process engine is not null");
                                final WorkerThread workerThread = new WorkerThread(processEngine, vars, Utils.starStoptVmProcessId);

                                if (threadFactory != null) {
                                    final Thread thread = threadFactory.newThread(workerThread);
                                    thread.start();
                                } else {
                                    logger.info("threadfactory is null");
                                }
                            } else {
                                logger.info("process engine is null");
                            }

                        } catch (final Exception ex) {
                            ex.printStackTrace();
                            logger.info(ex.getMessage());
                        }

                        logger.info("process end action message");
                    }
                }
            }

        }
    }

}
