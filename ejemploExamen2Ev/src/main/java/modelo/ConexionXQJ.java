package modelo;

import javax.xml.xquery.XQConnection;
import javax.xml.xquery.XQDataSource;
import javax.xml.xquery.XQException;

import net.xqj.exist.ExistXQDataSource;

public class ConexionXQJ {

	private static ConexionXQJ instancia;

	private static XQConnection conn;

	private ConexionXQJ(String username, String contrasenia) {
		super();
		XQDataSource xqs = new ExistXQDataSource();
		try {
			xqs.setProperty("serverName", "localhost");
			xqs.setProperty("port", "8080");
			xqs.setProperty("user", username);
			xqs.setProperty("password", contrasenia);
			conn = xqs.getConnection();

		} catch (XQException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}

	public static ConexionXQJ getInstance(String username, String contrasenia) {
		if (instancia == null) {
			instancia = new ConexionXQJ(username, contrasenia);
		}
		return instancia;
	}

	public static XQConnection getConn() {
		return conn;
	}

}
