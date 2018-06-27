import com.rabbitmq.client.Channel;
import com.rabbitmq.client.Connection;
import com.rabbitmq.client.ConnectionFactory;
import com.rabbitmq.client.MessageProperties;
import static java.lang.Thread.sleep;
import java.util.Random;

/**
 * @author Christian Carl * @author Ricardo Bibiloni
 */

public class Productor {

	private static final String TASK_QUEUE_NAME = "task_queue"; //Nombre del canal donde se envían los mensajes
	private static final int N_Produccion = 20; //Cantidad de mensajes a enviar/producir
	public static void main(String[] argv) throws Exception {
		
		String ID = argv[0]; //La ID del Productor, principalmente usaremos String de nombres
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		Connection connection = factory.newConnection();
		Channel channel = connection.createChannel();

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null); //Declarar la cola de mensajes
		//Explicación de los parametros anteriores:
		//Nombre de la cola (TASK_QUEUE_NAME)
		//Si la conexión del canal ya esta creada (true)
		//Descactivar cola cuando el servidor se desconecte (false)
		//Desactivar cula cuando no está en uso durante un periodo de tiempo (false)
		//Argumentos de construcción (null)
		System.out.println(" [*] Produciendo... To exit press CTRL+C");
		
		String message;  
		int producidos=0;
		Random randomGenerator = new Random(); //Declaración de numero aleatorio, lo usaremos para que el productor produzca en intervalos de tiempo aleatorios.
		while(producidos<N_Produccion){
			
			message="Productor: "+ID+", nº "+String.valueOf(producidos); //Crear el mensaje con la ID del productor y el valor producido 
			channel.basicPublish("", TASK_QUEUE_NAME, MessageProperties.PERSISTENT_TEXT_PLAIN, message.getBytes("UTF-8")); //Colocar el mensaje en la cola determinada y declarar el mensaje como persistente (existe hasta que se desconecta RabbitMQ)
			producidos++; //Aumentar el contador total de producción.
			System.out.println(" [x] Sent '" + "Productor: "+ID+" --> Produccion nº: "+String.valueOf(producidos) + "'"); //Visualizar el valor producido
			sleep(randomGenerator.nextInt(3000)); //El productor va produciendo en tiempos aleatorios.
		}
		channel.close(); //Cerrar la conexión con el canal de mensajes
		connection.close(); //Cerrar la conexión con RabbitMQ
	}
}
