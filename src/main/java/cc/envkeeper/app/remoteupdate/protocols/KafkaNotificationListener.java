/*
 * Copyright (c) 2022 Eric Thiebaut-George.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */

package cc.envkeeper.app.remoteupdate.protocols;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

import cc.envkeeper.app.config.KafkaConsumerConfig;
import cc.envkeeper.app.remoteupdate.domain.BuildMessage;
import cc.envkeeper.app.remoteupdate.domain.BuildStatMessage;
import cc.envkeeper.app.remoteupdate.domain.BuildStepMessage;
import cc.envkeeper.app.remoteupdate.domain.DeploymentMessage;
import cc.envkeeper.app.remoteupdate.domain.ProductComponentMessage;
import cc.envkeeper.app.remoteupdate.domain.ProductComponentVersionMessage;
import cc.envkeeper.app.remoteupdate.domain.ProductVersionMessage;
import cc.envkeeper.app.remoteupdate.domain.UpdateMessage;
import cc.envkeeper.app.remoteupdate.exception.CorruptedMessageException;
import cc.envkeeper.app.remoteupdate.service.RemoteUpdateService;
import cc.envkeeper.app.remoteupdate.utils.TimeFormat;

/**
 * Build notification listener.
 *
 * Listens to build notifications and updates EnvKeeper.
 */
@Profile(KafkaConsumerConfig.SPRING_PROFILE_KAFKA)
@Component
public class KafkaNotificationListener {

    private static final String TOPIC_NAME = "${kafka.eventHubsName}";
    private static final String CONSUMER_GROUP = "cc.envkeeper.app.remoteupdate.protocols.KafkaNotificationListener";
    private static final ObjectMapper mapper = new ObjectMapper();
    private static final Logger log = LoggerFactory.getLogger(KafkaNotificationListener.class);

    static {
        mapper.setDateFormat(TimeFormat.isoDateFormat);
        mapper.configure(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS, false);
        mapper.registerModule(new JavaTimeModule());
    }

    @Autowired
    private RemoteUpdateService remoteUpdateService;

    @KafkaListener(id = TOPIC_NAME, topics = TOPIC_NAME, groupId = CONSUMER_GROUP)
    public void processEnvKeeperPayload(String message) {
        log.debug("Received Kafka notification message: {}", message);

        try {
            UpdateMessage kafkaMessage = readValue(message, UpdateMessage.class);
            switch (kafkaMessage.getMessageType()) {
                case DeploymentMessage.MESSAGE_TYPE:
                    remoteUpdateService.processDeployment(readValue(message, DeploymentMessage.class));
                    break;
                case BuildMessage.MESSAGE_TYPE:
                    remoteUpdateService.processBuild(readValue(message, BuildMessage.class));
                    break;
                case BuildStepMessage.MESSAGE_TYPE:
                    remoteUpdateService.processBuildStep(readValue(message, BuildStepMessage.class));
                    break;
                case BuildStatMessage.MESSAGE_TYPE:
                    remoteUpdateService.processBuildStat(readValue(message, BuildStatMessage.class));
                    break;
                case ProductComponentMessage.MESSAGE_TYPE:
                    remoteUpdateService.processProductComponent(readValue(message, ProductComponentMessage.class));
                    break;
                case ProductComponentVersionMessage.MESSAGE_TYPE:
                    remoteUpdateService.processProductComponentVersionMessage(readValue(message, ProductComponentVersionMessage.class));
                    break;
                case ProductVersionMessage.MESSAGE_TYPE:
                    remoteUpdateService.processProductVersion(readValue(message, ProductVersionMessage.class));
                    break;
                default:
                    log.error("Could not process Kafka notification message, unknown type: {}", kafkaMessage.getMessageType());
            }
        } catch (CorruptedMessageException e) {
            log.error("Unreadable Kafka notification message: {}", message, e);
            // Eat the exception in order to remove message from queue
        } catch (Exception e) {
            log.error("Could not process Kafka notification message: {}", message, e);
            // Eat the exception in order to remove message from queue
        }
    }

    private <T> T readValue(String content, Class<T> valueType) throws CorruptedMessageException {
        try {
            return mapper.readValue(content, valueType);
        } catch (Exception e) {
            throw new CorruptedMessageException(e);
        }
    }
}
