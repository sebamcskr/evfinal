package com.example.evfinal;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

public class Mqtt extends AppCompatActivity {
    // elementos de conexion
    private static final String BROKER = "tcp://broker.emqx.io:1883";
    private static final String CLIENT_ID = "mqtt123444";
    private static final String TOPIC_SUB = "lab/redes/android";
    private MqttHandler mqttHandler;

    // elementos de la interfaz
    private EditText mensaje;
    private TextView MostrarMensaje, MensajeRecibido;
    private Button BtnPublicar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_mqtt);

        // inicializar componentes de nuestra lista
        mensaje = findViewById(R.id.editTextMessage);
        MostrarMensaje = findViewById(R.id.textViewStatus);
        MensajeRecibido = findViewById(R.id.textViewReceived);
        BtnPublicar = findViewById(R.id.buttonPublish);

        // inicializamos mqtt
        mqttHandler = new MqttHandler();
        // escuchara en 2 plano
        mqttHandler.setMessageListener(new MqttHandler.MessageListener() {
            @Override
            public void onMessageReceived(String topic, String message) {
                runOnUiThread(()->MensajeRecibido.setText("Topico: ["+ topic + "] : " + message)); // nos permitira interactuar con nuestros procesos en 2 plano
            }
        });

        mqttHandler.connect(BROKER, CLIENT_ID);
        mqttHandler.subscribe(TOPIC_SUB);

        BtnPublicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String dato = mensaje.getText().toString();
                if (!dato.isEmpty()){
                    mqttHandler.publish(TOPIC_SUB, dato);
                    MostrarMensaje.setText("Mensaje publicado: " + dato);
                }
            }
        });

    }

    @Override
    protected void onDestroy(){
        mqttHandler.disconnect();
        super.onDestroy();
    }
}

