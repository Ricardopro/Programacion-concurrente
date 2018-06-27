package ejercicio1;

import static java.lang.Thread.sleep;
import java.util.concurrent.Semaphore;

/**
 * @author Christian Carl 
 * @author Ricardo Bibiloni 
 */
public class Barberia extends Thread {

    public static Semaphore barberolisto = new Semaphore(0);     //Semadoro para controlar cuando el Barbero esta listo para cortar el pelo a los clientes.
    public static Semaphore sillasaccesibles = new Semaphore(1); //Semaforo para controlar las sillas de la barberia para sentarse y que el barbero los atienda.
    public static Semaphore clientes = new Semaphore(0);         //Semaforo para saber si hay clientes esperando en la barberia.
    public static final int sillastotales = 5;                   //Sillas totales que hay en la barberia para que los clientes esperen para ser atendidos.
    public static int sillaslibres = sillastotales;              //Cantidad de sillas disponibles para que los clientes se sientan.
    public int cortados = 0;                                     //Cantidad de clientes atendidos.
    public int totalparacortar = 10;                             //Cantidad total de clientes que se tienen que atender para hacer el jornal.

    public static void main(String args[]) throws InterruptedException {

        Barberia peluqueria = new Barberia();  //Crear nueva peluqueria
        peluqueria.start(); // Abrir la barberia
        peluqueria.join();

    }

    public void run() {
        new Barbero().start();
        int i = 1;
        //Comprueba si ya se ha completado el jornal de cortes diarios. Sumando la cantidad de clientes que hay en la barberia y la cantidad que el barbero ha cortado.
        //Si es así, la barberia cierra y los clientes dejan de entrar en la barberia.
        //La cantidad de clientes que hay en la Barberia se obtiene con el numero de sillas ocupadas. SillasOcupadas = SillasTotales - SillasLibres.
        
        while (cortados + sillastotales - sillaslibres < totalparacortar) { 
            new Cliente(i++).start();
            try {
                sleep(10); //Tiempo de llegada de los clientes.
            } catch (InterruptedException ex) {};
        }
        //Cantidad de clientes que aun estan esperando en la Barberia cuando la Barberia cierra.
        System.out.println("Tanca la barberia, clients esperant: " + (sillastotales - sillaslibres));
    }

    class Barbero extends Thread {

        public void run() {
            
            while (cortados != totalparacortar) {  //El barbero va cortando hasta que ha completado el jornal.
                try {
                    clientes.acquire();         //Trata de atender a un cliente. Si no hay clientes, se pone en espera hasta que haya clientes.
                    sillasaccesibles.acquire(); //El barbero se ha despertado y tratar de modificar el numero de sillas disponibles
                    sillaslibres++;             //Queda disponible una silla en la sala
                    barberolisto.release();     //El barbero esta listo para cortar
                    sillasaccesibles.release(); //Ya no se modifica el numero de sillas libres
                    this.cortarpelo();          //El barbero corta el pelo de un cliente. Ya hemos salido de la sección critica.

                } catch (InterruptedException ex) {}
            }
            //Barbero ha terminado de cortal el pelo y se va a casa con el jornal hecho.
            System.out.println("Acaba el jornal i el barberolisto se'n va a casa seva");
        }

        public void cortarpelo() {
            cortados = cortados + 1; //Se añade al jornal un corte mas.
            System.out.println("El barber talla cabell, clients atesos: " + cortados);

            try {
                sleep(12); //Tiempo que tarda el Barbero en cortar el pelo al cliente.
            } catch (InterruptedException ex) {}
        }
    }

    class Cliente extends Thread {

        int identificador; //identificador del cliente
         
		public Cliente(int i) {
            identificador = i;
        }
		
		public void run() {
             
            try {
                sillasaccesibles.acquire();  //El cliente trata de sentarse en una silla.
                if (sillaslibres > 0) {  //Si hay sillas disponibles el cliente entra en la barberia y se sienta.
                    System.out.println("El client " + this.identificador + " es seu i espera");
                    sillaslibres--;  //El cliente se sienta en una silla. Se decrementa el numero de sillas libres en la barberia.
                    clientes.release();  //Avisar al barbero que está esperando que hay un cliente esperando en una silla.
                    sillasaccesibles.release();  //El semaforo del control de sillas de la barberia se deja disponible. Ya que no se necesita modificar el numero de sillas disponibles.
                    try {
                        barberolisto.acquire();  //El cliente espera a que el barbero esté listo para atenderlo.
                                                 //Aqui es donde le cortan el pelo al cliente.
                        this.ClientePaga();      //El cliente le han cortado el pelo, paga y sale de la barberia.
                    } catch (InterruptedException ex) {
                    }
                } else {  //Si no hay sillas libres el cliente deja la barberia.
                    System.out.println("No hi ha cadires lliures. El client " + this.identificador + " se'n va");
                    sillasaccesibles.release();  //Desbloquear la modificación de sillas libres para otros procesos/hebras.
                                                //El cliente se va de la barbería, ya que no hay sillas para esperar disponibles.
                }
            } catch (InterruptedException ex) {}
            

        }

        public void ClientePaga() {

            try {
                sleep(6); //Tiempo que tarda el cliente en pagar y salir de la Barberia.
            } catch (InterruptedException ex) {
            }
            System.out.println("El client " + this.identificador + " surt amb el cabell tallat");
        }
    }
}