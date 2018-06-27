package Ejercicio_2;

import java.util.Arrays;

/**
 * @author Christian Carl
 * @author Ricardo Bibiloni
 */
public class Ascensor extends Thread {

    int piso_Ascensor = 0; //El piso donde se encuentra el ascensor en todo momento.
    private boolean ocupado = false; //Variable donde se puede ver cuando el ascensor esta ocupado.

    public synchronized void llamarAscensor(int Piso_actual_persona) throws InterruptedException {  //Apretar el boton de afuera del ascensor. Para llamarlo y que vaya a mi piso.  
        while (ocupado) {
            wait(); //Espera a que el ascensor este libre.
        }
        ocupado = true; //El ascensor ha sido llamado por una persona.
        movimientoAscensor(Piso_actual_persona); //El ascensor se mueve hasta el piso de la persona al que ha sido llamado.
    }

    public synchronized void IrDestinoAscensor(int Piso_destino_persona) throws InterruptedException {  //Apretar el boton de dentro del ascensor para seleccionar el piso donde quieres ir. 
        movimientoAscensor(Piso_destino_persona); //El ascensor se mueve hacia el destino de la persona.
        ocupado = false; //Viaje finalizado. La persona sale del ascendor dejando el ascensor libre.
        notifyAll();
    }

    public void movimientoAscensor(int destino) { //Subir o bajar de piso
        while (piso_Ascensor != destino) { //Mientras el ascensor no esta en el piso del que ha sido llamado se mueve. Si ya esta en ese piso, las puertas se abren para dejar entrar al usuario.
            if (piso_Ascensor < destino) {
                piso_Ascensor++; //El ascensor sube un piso.
            } else {
                piso_Ascensor--; //El ascensor baja un piso.
            }
            System.out.println("Ascensor al pis " + piso_Ascensor);
            try {
                sleep(100); //Tiempo que tarda el ascensor en cambiar de piso.
            } catch (InterruptedException ex) {
            }
        }
    }

    public void run() { //Declaración inicial de las personas que han a hacer los trayectos. Iguales que el enunciado.
        //Maria va a ir al piso 4, 7 y 8. Antes de empezar el trayecto esta en el piso 1.
        int[] a = {4, 7, 8};
        Persona p1 = new Persona(this, "Maria", 1, a);
        //Pep va a ir al piso 8. Antes de empezar el trayecto esta en el piso 3.
        int[] b = {8};
        Persona p2 = new Persona(this, "Pep", 3, b);
        //Jaume va a ir al piso 0 y 5. Antes de empezar el trayecto esta en el piso 8.
        int[] c = {0, 5};
        Persona p3 = new Persona(this, "Jaume", 8, c);

        //Inicio de los thread.
        p1.start();
        p2.start();
        p3.start();
    }

    class Persona extends Thread {

        String n; //Nombre de la persona.
        int pisoP_actual; //Piso donde esta la persona en ese momento.
        int[] Array_pisoP_destino; //Array de los pisos que faltan para ir a hacer las gestiones.

        Ascensor mon; //Classe monitor ascensor.

        Persona(Ascensor m, String nombre, int pisoActual, int[] ArraypisoDestino) {
            this.mon = m; //Se guarda el monitor. La classe Ascensor que se va a usar, será comun para todos los usuarios del ascensor. 
            this.n = nombre; //Nombre de la persona que va a realizar el trayecto.
            this.pisoP_actual = pisoActual; //Piso inicial. Es donde esta la persona cuando el programa se enciende.
            this.Array_pisoP_destino = ArraypisoDestino; //Pisos donde va a ir la persona a realizar las gestiones.
        }

        public void run() {
            System.out.println(n + " ha d'anar a" + Arrays.toString(Array_pisoP_destino).replace('[', ' ').replace(']', ' ')); //Print de los sitios donde las personas tienen que ir.
            System.out.println(n + " fa unes gestions a " + pisoP_actual); //Print para decir que las personas en el piso inicial estan haciendo unas gestiones. Como esta en la simulación del enunciado.
            try {
                sleep(12); //Tiempo que le damos a la persona para que vaya al ascensor para llamarlo.
            } catch (InterruptedException ex) {
            }
            int viajes_realizados = 0; //Contador para comprobar si la persona ya ha realizado todos los trayectos.

            while (viajes_realizados != Array_pisoP_destino.length) { //Se comprueba que la persona no haya realizado todos los trayectos.
                try {
                    System.out.println(n + " està al pis " + pisoP_actual + " i espera per " + Array_pisoP_destino[viajes_realizados]); //Visualizar donde esta la persona y a donde quiere ir.
                    mon.llamarAscensor(pisoP_actual); //La persona esta en el piso y llama al ascensor.
                    System.out.println(n + " puja a " + pisoP_actual + " i va a " + Array_pisoP_destino[viajes_realizados]); //Visualizar que el ascensor ha sido llamado.
                    mon.IrDestinoAscensor(Array_pisoP_destino[viajes_realizados]); //Cuando la persona esta dentro del ascensor, apreta el boton del piso destino donde quiere ir.
                    pisoP_actual = Array_pisoP_destino[viajes_realizados]; //La persona sale del ascensor. Ha llegado al piso destino. 
                    viajes_realizados++; //Se actualiza el contador. Se ha finalizado un viaje.
                    System.out.println(n + " fa unes gestions a " + this.pisoP_actual);
                    sleep(12); //Tiempo que esta la persona haciendo las gestiones en el piso y el tiempo que tarda a ir al ascensor para llamarlo.
                } catch (InterruptedException ex) {
                }
            }
        }
    }

    public static void main(String[] args) {
        new Ascensor().start(); //Empieza la ejecución.
    }
}
