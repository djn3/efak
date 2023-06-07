/**
 * KafkaClusterFetcher.java
 * <p>
 * Copyright 2023 smartloli
 * <p>
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * <p>
 * http://www.apache.org/licenses/LICENSE-2.0
 * <p>
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.kafka.eagle.core.kafka;

import lombok.extern.slf4j.Slf4j;
import org.kafka.eagle.common.constants.JmxConstants;
import org.kafka.eagle.common.utils.NetUtil;
import org.kafka.eagle.plugins.kafka.JMXFactoryUtil;
import org.kafka.eagle.pojo.cluster.BrokerInfo;
import org.kafka.eagle.pojo.kafka.JMXInitializeInfo;

import javax.management.MBeanServerConnection;
import javax.management.ObjectName;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXServiceURL;
import java.io.IOException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;

/**
 * Description: TODO
 *
 * @Author: smartloli
 * @Date: 2023/6/7 14:48
 * @Version: 3.4.0
 */
@Slf4j
public class KafkaClusterFetcher {
    private KafkaClusterFetcher() {
    }

    public static BrokerInfo getKafkaVersion(JMXInitializeInfo initializeInfo) {
        BrokerInfo brokerInfo = new BrokerInfo();
        JMXConnector connector = null;
        String JMX = initializeInfo.getUri();
        try {
            JMXServiceURL jmxSeriverUrl = new JMXServiceURL(String.format(JMX, initializeInfo.getHost() + ":" + initializeInfo.getPort()));
            initializeInfo.setUrl(jmxSeriverUrl);
            connector = JMXFactoryUtil.connectWithTimeout(initializeInfo);
            MBeanServerConnection mbeanConnection = connector.getMBeanServerConnection();
            String version = mbeanConnection.getAttribute(new ObjectName(String.format(JmxConstants.BrokerServer.BROKER_APP_INFO.getValue(), initializeInfo.getBrokerId())), JmxConstants.BrokerServer.BROKER_VERSION_VALUE.getValue()).toString();
            String startTimemsStr = mbeanConnection.getAttribute(new ObjectName(String.format(JmxConstants.BrokerServer.BROKER_APP_INFO.getValue(), initializeInfo.getBrokerId())), JmxConstants.BrokerServer.BROKER_STARTTIME_VALUE.getValue()).toString();
            brokerInfo.setBrokerVersion(version);
            brokerInfo.setBrokerStartupTime(dateConvert(Long.parseLong(startTimemsStr)));
        } catch (Exception e) {
            log.error("Get kafka version from jmx has error, JMXInitializeInfo[{}], error msg is {}", initializeInfo, e);
        } finally {
            if (connector != null) {
                try {
                    connector.close();
                } catch (IOException e) {
                    log.error("Close jmx connector has error, msg is {}", e);
                }
            }
        }
        return brokerInfo;
    }

    private static LocalDateTime dateConvert(long timestamp){
        LocalDateTime dateTime = Instant.ofEpochMilli(timestamp)
                .atZone(ZoneId.systemDefault())
                .toLocalDateTime();
        return dateTime;
    }

    public static boolean getKafkaAliveStatus(String host, int port) {
        return NetUtil.telnet(host, port);
    }

}
