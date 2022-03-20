package http;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.Socket;
import java.util.logging.Level;
import java.util.logging.Logger;

//clase con metodos que permiten generar input/output streams con facilidad y trabajar con ellos.
public class HerramientasStream {
    public InputStream getInputStream(Socket socket){
        InputStream inputStream = null;
        try {
            inputStream = socket.getInputStream();
        } catch (IOException ex) {
            Logger.getLogger(HiloHTTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return inputStream;
    }
    
    public OutputStream getOutputStream(Socket socket) {
        OutputStream outputStream = null;
        try {
            outputStream = socket.getOutputStream();
        } catch (IOException ex) {
            Logger.getLogger(HiloHTTP.class.getName()).log(Level.SEVERE, null, ex);
        }
        return outputStream;
    }
    
    //Extrae la primera linea y comprueba que sea un mensaje GET
    public String extraerCabeceraGET(InputStream inputStream){
        InputStreamReader isr = new InputStreamReader(inputStream);
        BufferedReader bReader = new BufferedReader(isr);    
        String primeraLinea;
       
        try {
            if ((primeraLinea = bReader.readLine()) != null)
                if (!primeraLinea.contains("GET "))
                    primeraLinea = "";                 
            
        } catch (IOException e) {
            primeraLinea = null;
        }
        return primeraLinea;
    }
    
    //A partir de una cabecera GET extrae su Peticion.
    public String extraerPeticionCabeceraGET(String cabecera){
        String peticion = cabecera.split(" ")[1];
        return peticion;
    }

}
