package hilos;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.logging.FileHandler;
import java.util.logging.Logger;
import java.util.logging.Level;

import principal.MyFileReader;

public class  HiloServidorTCP extends Thread{
	
	private int id;
	private Socket conexion;
	
	private ObjectInputStream entrada;
	private ObjectOutputStream salida;
	private MyFileReader fileReader;
	
	private boolean conexionActiva = true;
	private boolean imagenRecibida = false;
	private long total = 0;
	private long start = 0;
	private long fin = 0;
	private Logger logger;
	public HiloServidorTCP(Socket conexion, int id)
	{
		this.conexion = conexion;
		this.id = id;
		// se le asigna el puerto 3000 y 25 que sporte 25 conexiones
		try {
			
			
			obtenerStreams();
			
			
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		logger = Logger.getLogger(HiloServidorTCP.class.getName());
        FileHandler fileHandler;
		try {
			fileHandler = new FileHandler("logCliente"+id+".log", true);
	        logger.addHandler(fileHandler);
		} catch (SecurityException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}        

	}
	
	public void run()
	{
		do{
			try {
				procesarConexion();
			} catch (ClassNotFoundException e) {
				
				e.printStackTrace();
			} catch (IOException e) {
				System.out.println("la sesion con el cliente " + id + "ha terminado");
				conexionActiva = false;
				try {
					conexion.close();
					entrada.close();
					salida.close();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}

			}
			
		}while(conexionActiva);
		
	}

	private void procesarConexion() throws ClassNotFoundException, IOException {
		String mensaje;
		mensaje =(String)entrada.readObject();
		System.out.println("El cliente " + id+ " dice: " +mensaje);
		
		
		
		if(mensaje.compareTo("iniciar conexion")==0){
			System.out.println("Conexion establecida con " + conexion.getInetAddress().getHostName() + " con id: " + id);
			enviarDatos("El servidor acepto su solicitud de conexion");
		}
		
		if(mensaje.compareTo("cerrar conexion")==0){
			System.out.println("la sesion con el cliente " + id + "ha terminado");
			conexionActiva = false;
			conexion.close();
			entrada.close();
			salida.close();
		}
		if(mensaje.compareTo("imagen")==0){
			start = System.currentTimeMillis();
			enviarImagen();
			
		}
		if(mensaje.compareTo("imagenReci")==0)
		{
			fin = System.currentTimeMillis();
			imagenRecibida = true;
			total = fin - start;
			System.out.println("el tiempo fue de " + total +" milis");
			hacerLog(Long.toString(total));
		}
		
		
		
	}

	private void enviarDatos(String mensaje) throws IOException {
		
		salida.writeObject(mensaje);
		
	}

	private void obtenerStreams() throws IOException {
		salida = new ObjectOutputStream(conexion.getOutputStream());
		salida.flush();//limpia el buffer
		entrada = new ObjectInputStream(conexion.getInputStream());
	}


	private void enviarImagen(){
		
		 fileReader = new MyFileReader();
	      try {
	    	  System.out.println("El Hash es = " + sacarHashImgen());
	    	byte[] data = fileReader.readFile("./res/Archivo500.jpg");
	    	this.conexion.getOutputStream().write(data, 0, data.length);
//			for (byte i : data) {
//	            this.conexion.getOutputStream().write(i);    
//	        }
//			
			System.out.println("\r\nSent " + data.length + " bytes to server.");
	      } catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}catch (NoSuchAlgorithmException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
	      System.out.println("La imagen salio");
	}
	
	public boolean imagenRecibida(){
		return imagenRecibida;
	}
	
	public void hacerLog(String me)
	{
		if (logger.isLoggable(Level.INFO)) {
            logger.info("Tiempo: " + me);
        }
		
	}
	
	public String sacarHashImgen() throws NoSuchAlgorithmException, IOException{
		MessageDigest md = MessageDigest.getInstance("MD5");
		byte[] data = fileReader.readFile("./res/carro.jpg");
		md.update(data);
		byte[] b = md.digest();
		StringBuffer sb = new StringBuffer();
		for (byte b1: b){
			sb.append(Integer.toHexString(b1 & 0xff).toString());
		}
		
		return sb.toString();
	}

}
