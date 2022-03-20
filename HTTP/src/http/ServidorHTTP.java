package http;

import java.io.IOException;
import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ServidorHTTP {

    public static void main(String[] args) throws SocketException {
        ServerSocket serverSocket = getServerSockect();

        if (serverSocket != null) {
            
            while (true) {
                Socket socketNuevoCliente = aceptarConexiones(serverSocket);
                if (socketNuevoCliente != null) {
                    HerramientasStream herramientasSt = new HerramientasStream();
                    InputStream inputStream = herramientasSt.getInputStream(socketNuevoCliente);
                    String cabecera = "";
                   
                    //Si es un mensaje HTTP GET se extrae la cabecera y se crea un hilo que gestione la comunicación con el navegador.
                    if ((cabecera = herramientasSt.extraerCabeceraGET(inputStream)) != null) {
                        Thread hilo = new Thread(new HiloHTTP(socketNuevoCliente, inputStream, herramientasSt.extraerPeticionCabeceraGET(cabecera)));
                        hilo.start();
                    }
                }
            }
        }
    }

//Metodos para el control de la conexion
    private static ServerSocket getServerSockect() {
        ServerSocket serverSocket = null;

        try {
            serverSocket = new ServerSocket(8081);            
        } catch (IOException ex) {
            Logger.getLogger(ServidorHTTP.class.getName()).log(Level.SEVERE, null, ex);            
        }
        
        return serverSocket;
    }

    private static Socket aceptarConexiones(ServerSocket sSocket) {
        Socket socket = null;
        
        try {
            socket = sSocket.accept();
        } catch (IOException ex) {
            Logger.getLogger(ServidorHTTP.class.getName()).log(Level.SEVERE, null, ex);
        }

        return socket;
    }
}
