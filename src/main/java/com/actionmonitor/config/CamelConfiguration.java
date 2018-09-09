package com.actionmonitor.config;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.activemq.pool.PooledConnectionFactory;
import org.apache.camel.builder.RouteBuilder;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.jms.ConnectionFactory;

@Configuration
public class CamelConfiguration {

    @Value("${ACTIVE_MQ_URL:tcp://localhost:61616}")
    private String brokerUrl;

    @Value("${broker.queue.name:messages}")
    private String queueName;

    @Value("${broker.queue.handler:queueHandler}")
    private String queueHandler;

    @Bean
    public ConnectionFactory jmsConnectionFactory() {
        PooledConnectionFactory pool = new PooledConnectionFactory();
        ActiveMQConnectionFactory activeMQConnectionFactory = new ActiveMQConnectionFactory(brokerUrl);
        activeMQConnectionFactory.setTrustAllPackages(true);
        pool.setConnectionFactory(activeMQConnectionFactory);
        return pool;
    }

    @Bean
    public RouteBuilder router() {
        return new RouteBuilder() {
            @Override
            public void configure() {
                from(String.format("activemq:%s", queueName)).to(String.format("bean:%s", queueHandler));
            }
        };
    }

}
