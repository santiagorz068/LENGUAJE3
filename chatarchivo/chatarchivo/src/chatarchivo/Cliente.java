
package chatarchivo;

//Importamos las librerías necesarias

import java.net.*;
import java.io.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;


/*Este código define la clase Cliente, que es una ventana de chat con la que un usuario 
puede comunicarse con un servidor. La clase hereda de JFrame e implementa la interfaz ActionListener, 
lo que significa que la clase puede recibir eventos de acción de botones y otros componentes.*/

public class Cliente extends JFrame implements ActionListener {
    private static final long serialVersionUID = 1L;
    private JTextField campoTexto;
    private JTextArea areaTexto;
    private JButton botonEnviar;
    private JButton botonArchivo;
    private String host;
    private int puerto;
    private Socket socketCliente;
    private BufferedReader entrada;
    private PrintWriter salida;
    private JFileChooser selectorArchivo;

    public Cliente(String host, int puerto) {
        super("Chat Cliente");
        this.host = host;
        this.puerto = puerto;

        campoTexto = new JTextField();
        areaTexto = new JTextArea();
        areaTexto.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(areaTexto);

        botonEnviar = new JButton("Enviar");
        botonEnviar.addActionListener(this);
        botonArchivo = new JButton("Enviar archivo");
        botonArchivo.addActionListener(this);
        selectorArchivo = new JFileChooser();

        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout());
        panel.add(scrollPane, BorderLayout.CENTER);

        JPanel panel2 = new JPanel();
        panel2.setLayout(new GridLayout(1, 3));
        panel2.add(campoTexto);
        panel2.add(botonEnviar);
        panel2.add(botonArchivo);

        Container contenedor = getContentPane();
        contenedor.setLayout(new BorderLayout());
        contenedor.add(panel, BorderLayout.CENTER);
        contenedor.add(panel2, BorderLayout.SOUTH);

        setSize(400, 300);
        setVisible(true);

        conectar();
    }

    //Este método conectar() se encarga de establecer la conexión del cliente con el servidor
    public void conectar() {
        try {
            socketCliente = new Socket(host, puerto);
            entrada = new BufferedReader(new InputStreamReader(socketCliente.getInputStream()));
            salida = new PrintWriter(socketCliente.getOutputStream(), true);
            recibirMensajes();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    //El método desconectar() se encarga de cerrar la conexión con el servidor
    public void desconectar() {
        try {
            salida.println("FIN");
            socketCliente.close();
            entrada.close();
            salida.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
/*Este método se encarga de recibir los mensajes enviados por 
    el servidor y mostrarlos en la ventana del cliente
    Se crea un nuevo hilo para ejecutar la tarea de escuchar
    continuamente los mensajes entrantes*/
    public void recibirMensajes() {
        new Thread(() -> {
            try {
                String mensaje;
                while ((mensaje = entrada.readLine()) != null) {
                    if (mensaje.startsWith("ARCHIVO")) {
                        recibirArchivo(mensaje.substring(7));
                    } else {
                        areaTexto.append(mensaje + "\n");
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }).start();
    }
/*Este método es responsable de recibir un archivo enviado por el servidor y guardarlo en el sistema de archivos del cliente*/
    public void recibirArchivo(String nombreArchivo) {
        try {
            int tamaño = Integer.parseInt(entrada.readLine());
            byte[] contenido = new byte[tamaño];
            InputStream inputStream = socketCliente.getInputStream();
            inputStream.read(contenido, 0, tamaño);
            String usuario = socketCliente.getInetAddress().getHostName();
            Path path = Paths.get(nombreArchivo);
            Files.write(path, contenido);
            areaTexto.append("Archivo recibido: " + nombreArchivo + " (" + tamaño + " bytes) de " + usuario + "\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /*Este método implementa la interfaz ActionListener y 
    se encarga de manejar los eventos generados por los botones 
    de la interfaz gráfica del Cliente*/
    
    public void actionPerformed(ActionEvent evento) {
        if (evento.getSource() == botonEnviar) {
            String mensaje = campoTexto.getText();
            salida.println("MENSAJE");
            salida.println(mensaje);
            campoTexto.setText("");
        } else if (evento.getSource() == botonArchivo) {
            int resultado = selectorArchivo.showOpenDialog(this);
            if (resultado == JFileChooser.APPROVE_OPTION) {
                Path archivoSeleccionado = Paths.get(selectorArchivo.getSelectedFile().getAbsolutePath());
                try {
                    byte[] contenido = Files.readAllBytes(archivoSeleccionado);
                    salida.println("ARCHIVO");
                    salida.println(archivoSeleccionado.getFileName().toString());
                    salida.println(contenido.length);
                    salida.flush();
                    socketCliente.getOutputStream().write(contenido);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
/*Este código crea una instancia de la clase Cliente, que es una aplicación
  de chat cliente que se conecta a un servidor en la misma máquina 
  ("localhost") en el puerto 4444*/
    public static void main(String[] args) {
        Cliente cliente = new Cliente("localhost", 4444);
        cliente.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}


