package cl.isisur.testdemqtt;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

public class MainActivity extends AppCompatActivity {

    private MqttAndroidClient client;
    private final MemoryPersistence persistence = new MemoryPersistence();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //++++++++++++++++++++++++++++++++++++++++++++
        final MqttAndroidClient mqttAndroidClient = new MqttAndroidClient(this.getApplicationContext(), "tcp://209.126.106.184:1883", "SensorSantos", persistence);
        mqttAndroidClient.setCallback(new MqttCallback() {
            @Override
            public void connectionLost(Throwable cause) {
                System.out.println("Se ha perdió la conexión!");
            }

            @Override
            public void messageArrived(String topic, MqttMessage message) throws Exception {
                System.out.println("Mensaje Recibido: " + topic + ": " + new String(message.getPayload()));
            }

            @Override
            public void deliveryComplete(IMqttDeliveryToken token) {
                System.out.println("Entrega Completada");
            }
        });

        MqttConnectOptions mqttConnectOptions = new MqttConnectOptions();
        mqttConnectOptions.setCleanSession(true);
        try {
            mqttAndroidClient.connect(mqttConnectOptions, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    System.out.println("Conexión Exitosa");
                    try {
                        System.out.println("Suscribirse a / prueba");
                        mqttAndroidClient.subscribe("/prueba", 0);
                        System.out.println("Suscrito a / prueba");
                        System.out.println("Publicando mensaje espere...");
                        mqttAndroidClient.publish("/prueba", new MqttMessage("Hola Mundo esto es una prueba...".getBytes()));
                    } catch (MqttException ex) {

                    }
                }
                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    System.out.println("Conexion Fallida");
                    System.out.println("Arrojable: " + exception.toString());
                }
            });
        } catch (MqttException ex) {
            System.out.println(ex.toString());
        }

        //++++++++++++++++++++++++++++++++++++++++++++++
    }
}