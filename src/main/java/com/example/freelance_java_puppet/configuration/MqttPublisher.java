package com.example.freelance_java_puppet.configuration;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisher {

    private final String broker = ""; // Mosquitto broker
    private final String clientId = "spring-mqtt-publisher";
    private MqttClient mqttClient;

}
