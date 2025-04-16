package com.example.freelance_java_puppet.configuration;

import jakarta.annotation.PostConstruct;
import org.eclipse.paho.client.mqttv3.*;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class MqttPublisher {

    @Value("${mqtt.broker.uri}")
    private String broker;

    @Value("${mqtt.client.id}")
    private String clientId;

    private MqttClient mqttClient;

    @PostConstruct
    private void connect() {
        try {
            mqttClient = new MqttClient(broker, clientId, new MemoryPersistence());
            MqttConnectOptions options = new MqttConnectOptions();
            options.setCleanSession(true);
            options.setAutomaticReconnect(true); // ✅ Enable auto-reconnect
            mqttClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    System.out.println("❌ Connection lost. Trying to reconnect...");
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // Not used for publisher
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    System.out.println("✅ Delivery complete for message ID: " + token.getMessageId());
                }
            });

            mqttClient.connect(options);
            System.out.println("✅ MQTT connected to broker: " + broker);

        } catch (MqttException e) {
            e.printStackTrace();
            System.out.println("❌ Initial connection failed. Will retry in 5 seconds...");
            retryConnectWithDelay();
        }
    }

    private void retryConnectWithDelay() {
        new Thread(() -> {
            try {
                Thread.sleep(5000); // Retry delay
                connect();
            } catch (InterruptedException ignored) {}
        }).start();
    }

    public void publishMessage(String topic, String message) {
        int maxRetries = 3; // 👈 You can change this if needed
        int attempt = 0;
        boolean published = false;

        while (attempt < maxRetries && !published) {
            try {
                if (mqttClient == null || !mqttClient.isConnected()) {
                    System.out.println("🔄 MQTT not connected. Reconnecting...");
                    connect();
                }

                MqttMessage mqttMessage = new MqttMessage(message.getBytes());
                mqttMessage.setQos(2); // Exactly once
                mqttMessage.setRetained(true); // Keep for offline toy
                mqttClient.publish(topic, mqttMessage);

                System.out.println("✅ Message published on attempt " + (attempt + 1));
                published = true;

            } catch (MqttException e) {
                attempt++;
                System.err.println("❌ Attempt " + attempt + " failed: " + e.getMessage());
                try {
                    Thread.sleep(2000); // Wait 2 seconds before retry
                } catch (InterruptedException ie) {
                    Thread.currentThread().interrupt();
                }
            }
        }

        if (!published) {
            System.err.println("❌ Failed to publish after " + maxRetries + " attempts.");
        }
    }

}





    /*

This ensures: ✅ The toy only gets history purchased by its registered user.
✅ It cannot modify anything in the database.

    SELECT h.download_link
FROM history h
JOIN user_purchases up ON h.id = up.history_id
JOIN toys t ON up.user_id = t.user_id
WHERE t.serial_number = ? AND up.user_id = ?;

     */