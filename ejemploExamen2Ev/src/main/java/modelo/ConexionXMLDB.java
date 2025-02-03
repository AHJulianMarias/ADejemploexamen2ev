package modelo;

import org.xmldb.api.DatabaseManager;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Database;
import org.xmldb.api.base.XMLDBException;

public class ConexionXMLDB {

	private static ConexionXMLDB instancia;
	private static Collection col;
	private static String currentURICollection;

	private ConexionXMLDB(String username, String contrasenia, String URICollection) {
		super();
		try {
			Class cl = Class.forName("org.exist.xmldb.DatabaseImpl");

			Database database = (Database) cl.getDeclaredConstructor().newInstance();
			DatabaseManager.registerDatabase(database);

			col = DatabaseManager.getCollection("xmldb:exist://localhost:8080/exist/xmlrpc" + URICollection, username,
					contrasenia);
			currentURICollection = URICollection;
			System.out.println("Conectado a " + URICollection);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static synchronized ConexionXMLDB getInstance(String username, String contrasenia, String URICollection) {
		if (instancia == null || !currentURICollection.equals(URICollection)) {
			close(); // Close current connection if exists
			instancia = new ConexionXMLDB(username, contrasenia, URICollection);
		}
		return instancia;
	}

	public static Collection getCollection() {
		return col;
	}

	public static void close() {
		if (col != null) {
			try {
				col.close();
				System.out.println("Desconectado de " + currentURICollection);
			} catch (XMLDBException e) {
				e.printStackTrace();
			} finally {
				col = null; // Ensure the collection is set to null after closing
			}
		}
	}
}
