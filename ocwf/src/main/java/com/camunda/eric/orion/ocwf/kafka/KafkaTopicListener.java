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
package com.camunda.eric.orion.ocwf.kafka;

import java.util.logging.Logger;

import javax.ejb.EJB;

import com.camunda.eric.orion.ocwf.entity.ActionMessage;
import com.camunda.eric.orion.ocwf.parser.JsonParser;
import com.camunda.eric.orion.ocwf.processor.ActionMessageProcessorLocal;

import net.wessendorf.kafka.cdi.annotation.Consumer;
import net.wessendorf.kafka.cdi.annotation.KafkaConfig;

//@KafkaConfig(bootstrapServers = "#{KAFKA_HOST}:#{KAFKA_PORT}")
@KafkaConfig(bootstrapServers = "servir.seli.gic.ericsson.se:39095")
public class KafkaTopicListener {

    Logger logger = Logger.getLogger(KafkaTopicListener.class.getName());

    @EJB
    private ActionMessageProcessorLocal actionMessageProcessorLocal;

    @Consumer(topic = "Action", groupId = "default-group-id")
    public void onMessage(final String payload) {
        logger.warning("Message received from kafka: " + payload);

        logger.info("thread name::" + Thread.currentThread().getName());

        final ActionMessage actionMessage = JsonParser.parseActionMessage(payload);

        if (actionMessage != null) {

            actionMessageProcessorLocal.processAction(actionMessage);

        } else {
            logger.warning("action message is rejected");
        }

        logger.info("process end message received" + Thread.currentThread().getName());
    }
}
