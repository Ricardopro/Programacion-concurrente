import com.rabbitmq.client.*;
import java.io.IOException;
import java.util.Random;

/**
 * @author Christian Carl * @author Ricardo Bibiloni
 */
public class Consumidor {

	private static final String TASK_QUEUE_NAME = "task_queue"; //Nombre del canal donde se reciben los mensajes

	public static void main(String[] argv) throws Exception {
		final String ID = argv[0]; //La ID del Consumidor, usaremos String de nombres.
		ConnectionFactory factory = new ConnectionFactory();
		factory.setHost("localhost");
		final Connection connection = factory.newConnection();
		final Channel channel = connection.createChannel(); //Se asigna una conexión al canal.

		channel.queueDeclare(TASK_QUEUE_NAME, true, false, false, null); //Declarar la cola de mensajes
		//Parametros:
		//Primer parametro: Nombre de la cola: TASK_QUEUE_NAME
		//Segundo parametro: Si la conexión del canal ya esta creada (true)
		//Tercer parametro: Descactivar cola cuando el servidor se desconecte (false)
		//Cuarto parametro: Desactivar cola cuando no está en uso durante un periodo de tiempo (false)
		//Quinto parametro: Argumentos de construcción (null)
		System.out.println(" [*] Consumiendo... To exit press CTRL+C");

		channel.basicQos(1); //Le decimos a RabbitMQ de NO dar más de 1 mensaje a un Consumidor, para repartir la carga si hay varios Consumidores
		final Consumer consumer = new DefaultConsumer(channel) {
			int consumido=0; //Índice contador. Cantidad máxima de mensajes recibidos.
			@Override
			public void handleDelivery(String consumerTag, Envelope envelope, AMQP.BasicProperties properties, byte[] body) throws IOException {
				String message = new String(body, "UTF-8");
				
				System.out.println(" [x] Received '" + "Consumidor: " + ID + " --> " + message + "'"); //Visualizar por consola el mensaje recibidos
				try {
					consumido++; //Aumentar el índice de mensajes recibidos
					Random randomGenerator = new Random(); //Declaración de numero aleatorio. Lo usaremos para que el consumidor consuma en intervalos de tiempo aleatorios.
					try{
		  				Thread.sleep(randomGenerator.nextInt(3000)); //El thread se pone en espera un tiempo aleatorio 0-3000.
					}catch(Exception e){}
				} finally {
					System.out.println(" [x] Cantidad Consumida: "+consumido); //Visualizar la cantidad consumida
				  	channel.basicAck(envelope.getDeliveryTag(), false); //Avisar a RabbitMQ que se ha recibido el mensaje
				}
		      }
		};
		channel.basicConsume(TASK_QUEUE_NAME, false, consumer); //Crea un consumidor con el nombre del canal, 
																//boolean false para NO crear un auto-acknolegde de mensajes (usamos 'channel.basicAck')
																//la interfaz 'consumer' para decidir qué hacer con cada mensaje
	}
}

