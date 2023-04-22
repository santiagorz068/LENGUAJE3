
package chatarchivo;

/*Estas librerías se utilizan comúnmente en Java para realizar operaciones
de entrada/salida, comunicación en red, manipulación de archivos 
y almacenamiento de colecciones de datos.*/

import java.io.*;
import java.net.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;


/* En esta clase se instancia el puerto de conexion y
se manejan las solicitudes por medio de los sockets que
aceptaran dentro de un bucle todas las conexiones */
public class Servidor {
    private static final int PUERTO = 4444;
    private List<ManejadorCliente> clientes = new ArrayList<>();

    public Servidor() {
        try (ServerSocket servidor = new ServerSocket(PUERTO)) {
            System.out.println("Servidor iniciado en el puerto " + PUERTO);
            while (true) {
                Socket socketCliente = servidor.accept();
                System.out.println("Nuevo cliente conectado: " + socketCliente.getInetAddress());
                ManejadorCliente manejador = new ManejadorCliente(socketCliente);
                clientes.add(manejador);
                manejador.start();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    /*Esta clase ManejadorCliente es una clase interna que se utiliza para manejar 
    las solicitudes entrantes de los clientes conectados al servidor. La clase utiliza los objetos Socket, 
    BufferedReader y PrintWriter para leer los mensajes de entrada y enviar mensajes de salida al cliente.
*/
    private class ManejadorCliente extends Thread {
        private Socket socketCliente;
        private BufferedReader entrada;
        private PrintWriter salida;

        public ManejadorCliente(Socket socketCliente) {
            this.socketCliente = socketCliente;
            try {
                entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
                salida = new PrintWriter(socketCliente.getOutputStream(), true);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
/*El método run() es un método que se ejecuta automáticamente cuando se 
  inicia un hilo utilizando el método start() de la clase Thread.
  En este caso, el método run() se utiliza para manejar las 
  solicitudes entrantes de un cliente conectado al servidor.*/
        
        public void run() {
            try {
                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    if (mensaje.equals("FIN")) {
                        break;
                    } else if (mensaje.equals("ARCHIVO")) {
                        recibirArchivo();
                    } else {
                        enviarMensaje(mensaje);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                cerrarConexion();
            }
        }

        //Este método recibirArchivo() se utiliza para recibir un archivo del cliente que se ha conectado al servidor
        private void recibirArchivo() throws IOException {
            String nombreArchivo = entrada.readLine();
            int tamArchivo = Integer.parseInt(entrada.readLine());
            byte[] contenidoArchivo = new byte[tamArchivo];
            int bytesLeidos = 0;
            while (bytesLeidos < tamArchivo) {
                bytesLeidos += socketCliente.getInputStream().read(contenidoArchivo, bytesLeidos, tamArchivo - bytesLeidos);
            }
            FileOutputStream archivoSalida = new FileOutputStream(nombreArchivo);
            archivoSalida.write(contenidoArchivo);
            archivoSalida.close();
            enviarMensaje("Archivo recibido: " + nombreArchivo);
        }

        /*Este método enviarMensaje() se utiliza para enviar un mensaje a
        todos los clientes que se han conectado al servidor
        Recibe como parámetro una cadena de texto mensaje que se va a enviar a todos los clientes.*/
        
        private void enviarMensaje(String mensaje) {
            for (ManejadorCliente cliente : clientes) {
                cliente.salida.println(mensaje);
            }
        }

        // Este indica que se ha cerrado la conexion del Cliente y para la ejecucion del Socket
        private void cerrarConexion() {
            try {
                clientes.remove(this);
                socketCliente.close();
                entrada.close();
                salida.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
    // El main es el punto de entrada de la clase Cliente, es la ejecución
    public static void main(String[] args) {
        new Servidor();
    }
}


