package com.example.evfinal;

import android.util.Log;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MqttHandler {

    // Etiqueta para los logs
    private static final String TAG = "MqttHandler";

    // Cliente MQTT para manejar la conexión
    private MqttClient client;

    // Listener para notificar mensajes recibidos
    private MessageListener messageListener;

    // Interfaz para implementar un callback cuando se recibe un mensaje
    public interface MessageListener {
        void onMessageReceived(String topic, String message);
    }

    // Método para configurar el listener
    public void setMessageListener(MessageListener listener) {
        this.messageListener = listener;
    }

    // Método para conectar el cliente al broker MQTT
    public void connect(String brokerUrl, String clientId) {
        try {
            // Configuración de persistencia en memoria
            MemoryPersistence persistence = new MemoryPersistence();

            // Crear una nueva instancia del cliente MQTT
            client = new MqttClient(brokerUrl, clientId, persistence);

            // Opciones de conexión
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true); // Limpiar sesión previa

            // Configurar el callback para manejar eventos
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    // Log de cuando se pierde la conexión
                    Log.e(TAG, "Conexión perdida: " + cause.getMessage());
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) {
                    // Convertir el mensaje recibido a String
                    String receivedMessage = new String(message.getPayload());
                    // Log del mensaje recibido
                    Log.d(TAG, "Mensaje recibido en el tópico " + topic + ": " + receivedMessage);

                    // Notificar al listener si está configurado
                    if (messageListener != null) {
                        messageListener.onMessageReceived(topic, receivedMessage);
                    }
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Log cuando un mensaje ha sido entregado correctamente
                    Log.d(TAG, "Entrega del mensaje completada.");
                }
            });

            // Conectar al broker
            client.connect(connectOptions);
            Log.i(TAG, "Conectado al broker: " + brokerUrl);

        } catch (MqttException e) {
            // Log de error al conectar
            Log.e(TAG, "Error al conectar con el broker: " + e.getMessage(), e);
        }
    }

    // Método para desconectar el cliente del broker
    public void disconnect() {
        try {
            if (client != null && client.isConnected()) {
                // Desconectar si el cliente está conectado
                client.disconnect();
                Log.i(TAG, "Desconectado del broker.");
            }
        } catch (MqttException e) {
            // Log de error al desconectar
            Log.e(TAG, "Error al desconectar: " + e.getMessage(), e);
        }
    }

    // Método para publicar un mensaje en un tópico
    public void publish(String topic, String message) {
        try {
            if (client != null && client.isConnected()) {
                // Crear el mensaje MQTT y publicarlo
                client.publish(topic, new MqttMessage(message.getBytes()));
                Log.i(TAG, "Mensaje publicado en el tópico " + topic + ": " + message);
            } else {
                // Log de advertencia si el cliente no está conectado
                Log.w(TAG, "El cliente no está conectado. No se puede publicar.");
            }
        } catch (MqttException e) {
            // Log de error al publicar
            Log.e(TAG, "Error al publicar el mensaje: " + e.getMessage(), e);
        }
    }

    // Método para suscribirse a un tópico
    public void subscribe(String topic) {
        try {
            if (client != null && client.isConnected()) {
                // Suscribirse al tópico
                client.subscribe(topic);
                Log.i(TAG, "Suscrito al tópico: " + topic);
            } else {
                // Log de advertencia si el cliente no está conectado
                Log.w(TAG, "El cliente no está conectado. No se puede suscribir.");
            }
        } catch (MqttException e) {
            // Log de error al suscribirse
            Log.e(TAG, "Error al suscribirse al tópico: " + e.getMessage(), e);
        }
    }
}
