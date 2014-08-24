package de.raysha.lib.dbc;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

/**
 * Stellt Hilfsmethoden bereit die zur Konvertierung von Daten verwendet werden können.
 * 
 * @author rainu
 */
public class ConvertHelper {

	/**
	 * Konvertiert ein beliebiges Objekt in ein byte-Array.
	 * @param o Objekt welches konvertiert werden soll.
	 * @return byte-Array-Representation des Objektes
	 * @throws IOException 
	 */
	public static byte[] convertObjectToByteArray(Object o) throws IOException{
		ByteArrayOutputStream bos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(bos);
		oos.writeObject(o);
		
		return bos.toByteArray();
	}
	
	/**
	 * Konvertiert ein beliebiges Objekt in ein byte-Array und schleust dieses durch ein InputStream.
	 * @param o
	 * @return
	 * @throws IOException
	 */
	public static InputStream convertObjectToInputStream(Object o) throws IOException{
		byte[] bObject = convertObjectToByteArray(o);
		return new ByteArrayInputStream(bObject);
	}
	
	/**
	 * Liest aus einen Inputstream ein Object aus.
	 * 
	 * @param is
	 * @return
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	public static Object convertInputStreamToObject(InputStream is) throws IOException, ClassNotFoundException{
		ObjectInputStream ois = new ObjectInputStream(is);
		return ois.readObject();
	}
}
