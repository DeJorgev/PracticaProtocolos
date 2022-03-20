package http;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class HiloHTTP implements Runnable{
    
    private Socket socketHilo;
    private InputStream inputStream;
    private String peticionInicial;

    public HiloHTTP(Socket socketHilo, InputStream inputStream, String peticionInicial) {
        this.socketHilo = socketHilo;
        this.inputStream = inputStream;
        this.peticionInicial = peticionInicial;
    }
    
    @Override
    public void run(){
       
        String peticionActual = peticionInicial , cabeceraActual = ""; 
        int codigoMensaje = 1; 
        HerramientasStream herramientasSt = new HerramientasStream();
        OutputStream outputStream = herramientasSt.getOutputStream(socketHilo);
        
        //Mientras no surja un error al recibir o enviar mensaje continua escuchando peticiones.
        while (cabeceraActual != null && codigoMensaje != -1){
            
            enviarDatos(outputStream, peticionActual);
            
            if ((cabeceraActual = herramientasSt.extraerCabeceraGET(inputStream)) != null) 
                peticionActual = herramientasSt.extraerPeticionCabeceraGET(cabeceraActual);                
        }
    }

    //Metodo que envia los datos procurados al cliente.
    private int enviarDatos(OutputStream outputStream, String peticion){
        int codigoMensaje;
        try {            
            if(peticion.split("\\.").length > 1){
                outputStream.write(new CompositorHTTP().componerMensajeSegunTipo(peticion,peticion.split("\\.")[1]));
                outputStream.flush();
            }else{
                outputStream.write(new CompositorHTTP().componerMensajeSegunTipo(peticion,peticion));
                outputStream.flush();
            }
            codigoMensaje = 1;
        } catch (IOException ex) {
            codigoMensaje = -1;
        }
        return codigoMensaje;
    }
}