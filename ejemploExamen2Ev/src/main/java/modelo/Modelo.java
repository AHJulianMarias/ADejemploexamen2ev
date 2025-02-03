package modelo;

import static com.mongodb.client.model.Filters.and;
import static com.mongodb.client.model.Filters.eq;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Scanner;

import javax.xml.xquery.XQException;
import javax.xml.xquery.XQExpression;
import javax.xml.xquery.XQMetaData;
import javax.xml.xquery.XQResultSequence;

import org.bson.Document;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.hibernate.cfg.Configuration;
import org.xmldb.api.base.Collection;
import org.xmldb.api.base.Resource;
import org.xmldb.api.base.XMLDBException;
import org.xmldb.api.modules.XMLResource;

import com.mongodb.client.FindIterable;
import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoClients;
import com.mongodb.client.MongoDatabase;

import clasesHibernate.Departamentos;
import clasesHibernate.Empleados;
import jakarta.persistence.TypedQuery;

public class Modelo {
	/**
	 * SI NO LISTAS DEPARTAMENTOS O EMPLEADOS, NO SE LLENA EL ARRAYLIST Y DA ERROR
	 * 
	 * 
	 */
	// HIBERNATE
	private static final Configuration cfg = new Configuration().configure();
	private static final SessionFactory sf = cfg.buildSessionFactory();
	private static Session sesion;
	private static ArrayList<Departamentos> listDepartamentos = new ArrayList<Departamentos>();
	private static ArrayList<Empleados> listEmpleados = new ArrayList<Empleados>();

	// MONGO
	private static MongoClient cliente;
	private static MongoDatabase db;
	private static String connectionString = "mongodb://localhost:27017/";

	// EXISTDB
	private static String USERNAME = "admin";
	private static String CONTRASENIA = "toor";
	// xqj
	static XQMetaData xqj;
	// xmldb

	public static void main(String[] args) {
		menu();
	}

	private static void menu() {
		String eleccion = "";
		while (!eleccion.equalsIgnoreCase("X")) {
			Scanner sc = new Scanner(System.in);
			System.out.println("Elige la opción que quieras:" + "\n1-Conectarse a mongoDB" + "\n2-Conectar a existDB"
					+ "\n3-Listar departamentos" + "\n4-Listar empleados" + "\n5-Port completo (mongo-mysql)"
					+ "\n6-Portar de hibernate a existDB solo empleados"
					+ "\n7-Portar de hibernate a existDB solo departamentos" + "\n8-Port doble de hibernate a existDB"
					+ "\n9-Portar de mongoDB a existDB solo empleados"
					+ "\n10-Portar de mongoDB a existDB solo departamentos" + "\n11-Port doble de mongoDB a existDB"
					+ "\n12-Portar empleados de MySQL a mongoDB" + "\n13-Portar departamentos de MySQL a mongoDB"
					+ "\n14-Portar empleados y departamentos de MySQL a mongodb" + "\nX-Salir");
			eleccion = sc.next().toLowerCase();
			switch (eleccion) {
			case "1":
				if (conectarCompass()) {
					System.out.println("Conectado correctamente a la base de datos");
				} else {
					System.out.println("Error conectandote a la base de datos");
				}
				break;
			case "2":
				try {
					xqj = ConexionXQJ.getInstance("admin", "toor").getConn().getMetaData();
					System.out.println("Conectado correctamente a la base de datos");
				} catch (XQException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
					System.out.println("Error conectandote a la base de datos");
				}
				break;
			case "3":
				if (listarDepartamentos("mongodb")) {
					for (Departamentos d : listDepartamentos) {
						System.out.println(d.toString());
					}
				} else {
					System.out.println("Error listando los diferentes departamentos");
				}
				break;
			case "4":
				if (listarEmpleados("mongodb")) {
					for (Empleados e : listEmpleados) {
						System.out.println(e.toString());
					}
				} else {
					System.out.println("Error listando los diferentes empleados");
				}
				break;
			case "5":
				if (anadirEmpleadosYDepartamentosMongoAMySQL()) {
					System.out.println("Empleados y departamentos incluidos correctamente");
				} else {
					System.out.println("Error introduciendo empleados y departamentos");
				}
				break;
			case "6":
				if (importarEmpleadosDesdeListAExistDB(
						ConexionXMLDB.getInstance(USERNAME, CONTRASENIA, "/db/misDocumentosXML/").getCollection(),
						"empleados.xml")) {
					System.out.println("Port de empleados (hibernate-existDB) realizado correctamente");
				} else {
					System.out.println("Port de empleados (hibernate-existDB) no realizado");
				}
				break;
			case "7":
				if (importarDepartamentosDesdeListAExistDB(
						ConexionXMLDB.getInstance(USERNAME, CONTRASENIA, "/db/misDocumentosXML/").getCollection(),
						"departamentos.xml")) {
					System.out.println("Port de departamentos (hibernate-existDB) realizado correctamente");
				} else {
					System.out.println("Port de departamentos (hibernate-existDB) no realizado");
				}
				break;
			case "8":
				if (importDobleDesdeListAExistDB(
						ConexionXMLDB.getInstance(USERNAME, CONTRASENIA, "/db/misDocumentosXML/").getCollection(),
						"departamentos.xml", "empleados.xml")) {
					System.out.println("Port de departamentos (hibernate-existDB) realizado correctamente");
				} else {
					System.out.println("Port de departamentos (hibernate-existDB) no realizado");
				}
				break;
			case "9":
				if (importarEmpleadosDesdeListAExistDB(
						ConexionXMLDB.getInstance(USERNAME, CONTRASENIA, "/db/misDocumentosXML/").getCollection(),
						"empleados.xml")) {
					System.out.println("Port de empleados (hibernate-existDB) realizado correctamente");
				} else {
					System.out.println("Port de empleados (hibernate-existDB) no realizado");
				}
				break;
			case "10":
				if (importarDepartamentosDesdeListAExistDB(
						ConexionXMLDB.getInstance(USERNAME, CONTRASENIA, "/db/misDocumentosXML/").getCollection(),
						"departamentos.xml")) {
					System.out.println("Port de departamentos (hibernate-existDB) realizado correctamente");
				} else {
					System.out.println("Port de departamentos (hibernate-existDB) no realizado");
				}
				break;
			case "11":
				if (importDobleDesdeListAExistDB(
						ConexionXMLDB.getInstance(USERNAME, CONTRASENIA, "/db/misDocumentosXML/").getCollection(),
						"departamentos.xml", "empleados.xml")) {
					System.out.println("Port de departamentos (hibernate-existDB) realizado correctamente");
				} else {
					System.out.println("Port de departamentos (hibernate-existDB) no realizado");
				}
				break;
			case "12":
				if (listEmpleados.size() == 0) {
					System.out.println("La lista de empleados está vacía.");
					break;
				}
				if (portarEmpleadosMongoDB()) {
					System.out.println("Port de empleados hecho correctamente");
				} else {
					System.out.println("Error haciendo el port de empleados");
				}
				break;
			case "13":
				if (listDepartamentos.size() == 0) {
					System.out.println("La lista de departamentos está vacía.");
					break;
				}
				if (portarDepartamentosMongoDB()) {
					System.out.println("Port de departamentos hecho correctamente");
				} else {
					System.out.println("Error haciendo el port de departamentos");
				}
				break;
			case "14":
				if (porteDobleMongoDB()) {
					System.out.println("Port doble hecho correctamente");
				} else {
					System.out.println("Error haciendo el port doble");
				}
				break;
			case "x":
				if (desconectarCompass()) {
					System.out.println("Desconectado correctamente de la base de datos");
				}
				System.out.println("Saliendo del programa");

				break;
			default:
				break;
			}
		}
	}

	public static boolean conectarCompass() {
		try {
			cliente = MongoClients.create(connectionString);
			db = cliente.getDatabase("empresa");
			return true;
		} catch (Exception e) {
			return false;

		}

	}

	public static boolean desconectarCompass() {
		try {
			cliente.close();
			return true;
		} catch (Exception e) {
			return false;

		}

	}

	/**
	 * 
	 * Rellena la lista de departamentos
	 * 
	 * @return boolean, true si encuentra resultados, false si no los encuentra o si
	 *         hay algun error
	 */
	private static boolean listarDepartamentos(String bbdd) {
		listDepartamentos.clear();
		switch (bbdd) {
		case "hibernate":
			try {
				String hqlQuery = "from Departamentos";
				sesion = sf.openSession();
				TypedQuery<Departamentos> tqDptos = sesion.createQuery(hqlQuery, Departamentos.class);
				listDepartamentos = (ArrayList<Departamentos>) tqDptos.getResultList();
				if (listDepartamentos.size() > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		case "mongodb":
			FindIterable<Document> listaDocs = db.getCollection("departamentos").find();
			for (Document doc : listaDocs) {
				Departamentos tempDept = new Departamentos(doc.getInteger("dpto_no"), doc.getString("dnombre"),
						doc.getString("loc"), new HashSet());
				listDepartamentos.add(tempDept);
			}
			return true;

		default:
			return false;
		}

	}

	/**
	 * 
	 * Rellena la lista de empleados
	 * 
	 * @return boolean, true si encuentra resultados, false si no los encuentra o si
	 *         hay algun error
	 */
	private static boolean listarEmpleados(String bbdd) {
		listEmpleados.clear();
		switch (bbdd) {
		case "hibernate":
			try {
				String hqlQuery = "from Empleados";
				sesion = sf.openSession();
				TypedQuery<Empleados> tqEmp = sesion.createQuery(hqlQuery, Empleados.class);
				listEmpleados = (ArrayList<Empleados>) tqEmp.getResultList();
				if (listEmpleados.size() > 0) {
					return true;
				} else {
					return false;
				}
			} catch (Exception e) {
				e.printStackTrace();
				return false;
			}
		case "mongodb":
			FindIterable<Document> listaDocs = db.getCollection("empleados").find();
			for (Document doc : listaDocs) {
				Document dptoDoc = (Document) doc.get("Departamento");
				Empleados tempEmp = new Empleados(
						new Departamentos(dptoDoc.getInteger("dept_no"), dptoDoc.getString("dnombre"),
								dptoDoc.getString("loc"), new HashSet()),
						doc.getString("nombre"), doc.getString("apellido1"), doc.getString("apellido2"));
				listEmpleados.add(tempEmp);
			}
			return true;

		default:
			return false;
		}

	}

	private static boolean anadirEmpleadosYDepartamentosMongoAMySQL() {
		try {
			for (Empleados e : listEmpleados) {
				anadirEmpleado(e.getNombre(), e.getApellido1(), e.getApellido2(), e.getDepartamentos());
			}
			return true;
		} catch (Exception ex) {
			ex.printStackTrace();
			return false;
		}

	}

	/**
	 * Añade empleados, si no existe el departamento al que existe el empleado, lo
	 * crea
	 * 
	 * @param nombre
	 * @param ap1
	 * @param ap2
	 * @param d
	 */
	private static void anadirEmpleado(String nombre, String ap1, String ap2, Departamentos d) {
		if (!comprobarExistenciaEmpleado(nombre, ap1, ap2)) {
			Departamentos dptoObjeto = comprobarExistenciaDepartamento(d);
			sesion = sf.openSession();
			Transaction t = sesion.beginTransaction();
			Empleados empleadoAnadir = new Empleados(dptoObjeto, nombre, ap1, ap2);
			sesion.persist(empleadoAnadir);
			t.commit();
			if (comprobarExistenciaEmpleado(nombre, ap1, ap2)) {
				System.out.println("Se ha añadido correctamente el empleado");
			} else {
				System.out.println("Error añadiendo el empleado");
			}
			sesion.close();

		} else {
			System.out.println("El empleado ya existe, por eso no se hizo nada");
		}
	}

	/**
	 * Comprueba si existe el departamento, si no existe lo añade
	 * 
	 * @param d
	 * @return
	 */
	private static Departamentos comprobarExistenciaDepartamento(Departamentos d) {
		sesion = sf.openSession();
		String hql = "from Departamentos where dnombre='" + d.getDnombre() + "' and loc='" + d.getLoc()
				+ "' and deptNo='" + d.getDeptNo() + "'";
		TypedQuery<?> tqDptoComprExis = sesion.createQuery(hql, Departamentos.class);
		// resultado de la query
		ArrayList<Departamentos> tqDpto = (ArrayList<Departamentos>) tqDptoComprExis.getResultList();
		if (tqDpto.size() >= 1) {
			return d;
		}

		return anadirDepartamento(d);
	}

	/**
	 * Añade el departamento
	 * 
	 * @param d
	 * @return
	 */
	private static Departamentos anadirDepartamento(Departamentos d) {
		sesion = sf.openSession();
		Transaction t = sesion.beginTransaction();
		Departamentos dpto = new Departamentos(d.getDeptNo(), d.getDnombre(), d.getLoc(), new HashSet());
		sesion.persist(dpto);
		t.commit();
		sesion.close();
		return dpto;
	}

	/**
	 * Ejecuta la funcion de importarEmpleadosHibernateExistDB y
	 * importarDepartamentosHibernateExistDB
	 * 
	 * @param collection
	 * @param nombreArchivo1 = archivo empleados
	 * @param nombreArchivo2 = archivo departamentos
	 * @return true si se ejecutan exitosamente
	 */
	private static boolean importDobleDesdeListAExistDB(Collection collection, String nombreArchivo1,
			String nombreArchivo2) {
		if (collection == null) {
			System.out.println("La colección padre no existe");
			return false;
		}

		try {
			if (importarEmpleadosDesdeListAExistDB(collection, nombreArchivo2)) {
				if (importarDepartamentosDesdeListAExistDB(collection, nombreArchivo1)) {
					return true;
				}
			}
			return false;

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Importa empleados desde hibernate a existDB
	 * 
	 * @param collection
	 * @param nombreArchivo
	 * @return si el archivo ya existe, añade los empleados no existentes comparando
	 *         los que ya existen
	 */
	private static boolean importarEmpleadosDesdeListAExistDB(Collection collection, String nombreArchivo) {
		if (collection == null) {
			System.out.println("La colección padre no existe");
			return false;
		}

		try {
			Resource recursoExiste = collection.getResource(nombreArchivo);
			if (recursoExiste != null) {
				System.out.println("El archivo ya existe, añadiendo datos nuevos");
				return anadirModuloEmpleado();

			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String contenido = "";

		for (Empleados e : listEmpleados) {
			contenido += "<empleado><nombre>" + e.getNombre() + "</nombre>" + "<apellido1>" + e.getApellido1()
					+ "</apellido1>" + "<apellido2>" + e.getApellido2() + "</apellido2>" + "<Departamento id='"
					+ e.getDepartamentos().getDeptNo() + "'>" + "<dnombre>" + e.getDepartamentos().getDnombre()
					+ "</dnombre>" + "<loc>" + e.getDepartamentos().getLoc() + "</loc></Departamento></empleado>";
		}
		contenido = "<empleados>" + contenido + "</empleados>";
		try {
			Resource recurso = collection.createResource(nombreArchivo, XMLResource.RESOURCE_TYPE);
			recurso.setContent(contenido);
			collection.storeResource(recurso);
			return true;

		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Añade un empleado usando XQJ Revisa que el empleado existe, si no existe
	 * (!buscarEmpleadoExistDB(e)), hace el insert
	 * 
	 * @return true si se añade correctamente
	 */
	private static boolean anadirModuloEmpleado() {
		for (Empleados e : listEmpleados) {
			if (!buscarEmpleadoExistDB(e)) {
				String query = "update insert <empleado><nombre>" + e.getNombre() + "</nombre><apellido1>"
						+ e.getApellido1() + "</apellido1><apellido2>" + e.getApellido2()
						+ "</apellido2><Departamento id='" + e.getDepartamentos().getDeptNo() + "'><dnombre>"
						+ e.getDepartamentos().getDnombre() + "</dnombre><loc>" + e.getDepartamentos().getLoc()
						+ "</loc></Departamento></empleado> into doc('/db/misDocumentosXML/empleados.xml')/empleados";

				XQExpression xqe;
				try {
					xqe = ConexionXQJ.getInstance(USERNAME, CONTRASENIA).getConn().createExpression();
					xqe.executeCommand(query);
					return true;
				} catch (XQException ex) {
					ex.printStackTrace();
					return false;
				}
			}
		}
		return false;

	}

	/**
	 * Busca que exista el empleado
	 * 
	 * @param e
	 * @return devuelve true en caso de que si y false en caso de que no
	 */
	private static boolean buscarEmpleadoExistDB(Empleados e) {
		try {
			String query = "for $e in doc('/db/misDocumentosXML/empleados.xml')/empleados/empleado\n"
					+ "where $e/nombre = '" + e.getNombre() + "' and $e/apellido1 = '" + e.getApellido1() + "'"
					+ " and $e/apellido2 = '" + e.getApellido2() + "' and $e/Departamento/@id = '"
					+ e.getDepartamentos().getDeptNo() + "'\nreturn $e";
			XQExpression xqe = ConexionXQJ.getInstance(USERNAME, CONTRASENIA).getConn().createExpression();
			XQResultSequence xqresultado = xqe.executeQuery(query);
			return xqresultado.next();

		} catch (XQException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 * 
	 * @param collection
	 * @param nombreArchivo
	 * @return
	 */
	private static boolean importarDepartamentosDesdeListAExistDB(Collection collection, String nombreArchivo) {
		if (collection == null) {
			System.out.println("La colección padre no existe");
			return false;
		}

		try {
			Resource recursoExiste = collection.getResource(nombreArchivo);
			if (recursoExiste != null) {
				System.out.println("El archivo ya existe, añadiendo datos nuevos");
				return anadirModuloDepartamento();
			}

		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		String contenido = "";

		for (Departamentos d : listDepartamentos) {
			contenido += "<departamento>" + "<dnombre id='" + d.getDeptNo() + "'>" + d.getDnombre() + "</dnombre><loc>"
					+ d.getLoc() + "</loc></departamento>";

		}
		contenido = "<departamentos>" + contenido + "</departamentos>";
		try {
			Resource recurso = collection.createResource(nombreArchivo, XMLResource.RESOURCE_TYPE);
			recurso.setContent(contenido);
			collection.storeResource(recurso);
			return true;

		} catch (XMLDBException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}

	}

	private static boolean anadirModuloDepartamento() {
		for (Departamentos d : listDepartamentos) {
			if (!buscarDepartamentoExistDB(d)) {
				String query = "update insert <departamento><dnombre id='" + d.getDeptNo() + "'>" + d.getDnombre()
						+ "</dnombre><loc>" + d.getLoc()
						+ "</loc></departamento> into doc('/db/misDocumentosXML/departamentos.xml')/departamentos";

				XQExpression xqe;
				try {
					xqe = ConexionXQJ.getInstance(USERNAME, CONTRASENIA).getConn().createExpression();
					xqe.executeCommand(query);
					return true;
				} catch (XQException ex) {
					ex.printStackTrace();
					return false;
				}
			}
		}
		return false;

	}

	private static boolean buscarDepartamentoExistDB(Departamentos d) {
		try {
			String query = "for $d in doc('/db/misDocumentosXML/departamentos.xml')/departamentos/departamento\n"
					+ "where $d/dnombre = '" + d.getDeptNo() + "' and $d/dnombre/@id = '" + d.getDeptNo() + "'"
					+ " and $d/loc = '" + d.getLoc() + "'\nreturn $d";
			XQExpression xqe = ConexionXQJ.getInstance(USERNAME, CONTRASENIA).getConn().createExpression();
			XQResultSequence xqresultado = xqe.executeQuery(query);
			return xqresultado.next();

		} catch (XQException ex) {
			// TODO Auto-generated catch block
			ex.printStackTrace();
		}

		return false;
	}

	/**
	 * Porta todos los empleados desde mysql a mongoDB, departamento embebido
	 * 
	 * @return boolean false en caso de error al hacer el port
	 */
	private static boolean portarEmpleadosMongoDB() {
		// TODO Auto-generated method stub
		if (db == null) {
			System.out.println("Database connection is not initialized.");
			return false;
		}
		ArrayList<Document> listaDocumentosTemporal = new ArrayList<Document>();
		for (Empleados e : listEmpleados) {
			Document doc = new Document().append("nombre", e.getNombre()).append("apellido1", e.getApellido1())
					.append("apellido2", e.getApellido2()).append("Departamento",
							new Document().append("dept_no", e.getDepartamentos().getDeptNo())
									.append("dnombre", e.getDepartamentos().getDnombre())
									.append("loc", e.getDepartamentos().getLoc()));
			listaDocumentosTemporal.add(doc);
		}
		try {
			db.getCollection("empleados").insertMany(listaDocumentosTemporal);
			System.out.println("Número de elementos insertados: " + listaDocumentosTemporal.size());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * Porta todos los departamentos a mongoDB
	 * 
	 * @return boolean false en caso de error al hacer el port
	 */
	private static boolean portarDepartamentosMongoDB() {
		// TODO Auto-generated method stub
		ArrayList<Document> listaDocumentosTemporal = new ArrayList<Document>();
		for (Departamentos d : listDepartamentos) {
			Document doc = new Document().append("dpto_no", d.getDeptNo()).append("dnombre", d.getDnombre())
					.append("loc", d.getLoc());
			listaDocumentosTemporal.add(doc);
		}
		try {
			db.getCollection("departamentos").insertMany(listaDocumentosTemporal);
			System.out.println("Número de elementos insertados: " + listaDocumentosTemporal.size());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}

	}

	/**
	 * 
	 * Port de empleados y departamentos a MongoDB con el departamento referenciado
	 * en empleados Si ya existe no se inserta
	 * 
	 * @return boolean false en caso de error al hacer el port
	 */
	private static boolean porteDobleMongoDB() {
		ArrayList<Document> listaDocumentosTemporal = new ArrayList<Document>();
		Document docDpto;
		Object idDpto;
		for (Empleados e : listEmpleados) {
			if (!revisarEmpleadoExiste(e)) {
				docDpto = findDocDpto(e.getDepartamentos());
				if (docDpto == null) {
					docDpto = introducirDpto(e.getDepartamentos());
				}
				idDpto = docDpto.getObjectId("_id");
				Document doc = new Document().append("nombre", e.getNombre()).append("apellido1", e.getApellido1())
						.append("apellido2", e.getApellido2()).append("Departamento", idDpto);
				listaDocumentosTemporal.add(doc);
			}

		}
		try {
			db.getCollection("empleados").insertMany(listaDocumentosTemporal);
			System.out.println("Número de elementos insertados: " + listaDocumentosTemporal.size());
			return true;
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
	}

	/**
	 * Si existe el empleado devuelve True, si no existe devuelve False
	 * 
	 * @param e
	 * @return
	 */
	private static boolean revisarEmpleadoExiste(Empleados e) {
		Document docDpto;
		Object idDpto;
		docDpto = findDocDpto(e.getDepartamentos());
		if (docDpto == null) {
			return false;
		}
		idDpto = docDpto.getObjectId("_id");
		Document docEmpleado = db.getCollection("empleados").find(and(eq("nombre", e.getNombre()),
				eq("apellido1", e.getApellido1()), eq("apellido2", e.getApellido2()), eq("Departamento", idDpto)))
				.first();
		if (docEmpleado == null) {
			return false;
		}
		return true;
	}

	/**
	 * 
	 * @param departamentos
	 * @return document o null, document si se inserta correctamente, false si falla
	 *         en la inserción
	 */

	private static Document introducirDpto(Departamentos departamentos) {
		Document doc = new Document().append("dpto_no", departamentos.getDeptNo())
				.append("dnombre", departamentos.getDnombre()).append("loc", departamentos.getLoc());
		try {
			db.getCollection("departamentos").insertOne(doc);
			return doc;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	/**
	 * 
	 * @param departamentos
	 * @return Document o null
	 */
	private static Document findDocDpto(Departamentos departamentos) {
		// TODO Auto-generated method stub
		return db.getCollection("departamentos")
				.find(and(eq("dnombre", departamentos.getDnombre()), eq("loc", departamentos.getLoc()))).first();
	}

	/**
	 * 
	 * 
	 * 
	 * METODOS NO USADOS PERO PUEDE QUE UTILES
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * 
	 * @param nombre
	 * @param ap1
	 * @param ap2
	 * @return
	 */

	private static boolean comprobarExistenciaEmpleado(String nombre, String ap1, String ap2) {
		sesion = sf.openSession();
		String hql = "from Empleados where nombre='" + nombre + "' and apellido1='" + ap1 + "' and apellido2='" + ap2
				+ "'";
		TypedQuery tqDptotq = sesion.createQuery(hql, Departamentos.class);
		ArrayList<String> tqDpto = (ArrayList<String>) tqDptotq.getResultList();
		if (tqDpto.size() == 0) {
			sesion.close();
			return false;
		} else {
			sesion.close();
			return true;
		}

	}

	/**
	 * comprueba la existencia del departamento
	 * 
	 * @param dpto
	 * @return
	 */
	private static Departamentos comprobarExistenciaDepartamento(String dpto) {
		ArrayList<Departamentos> localidadesDptos = new ArrayList<Departamentos>();
		Scanner sc = new Scanner(System.in);
		sesion = sf.openSession();
		String elegirLocalidad = "Que localidad quieres elegir\n";
		// Query para que te de todos los departamentos con ese nombre
		String hql = "from Departamentos where dnombre='" + dpto + "'";
		TypedQuery<?> tqDptoComprExis = sesion.createQuery(hql, Departamentos.class);
		// resultado de la query
		ArrayList<Departamentos> tqDpto = (ArrayList<Departamentos>) tqDptoComprExis.getResultList();

		// recorre todos los resultados de la query, elegir localidad almacena cada
		// localidad para despues imprimir la lista
		for (Departamentos d : tqDpto) {
			if (d.getDnombre().equalsIgnoreCase(dpto)) {
				elegirLocalidad += d.getLoc() + "\n";
				localidadesDptos.add(d);
			}

		}
		// Si hay mas de una te da a elegir
		if (localidadesDptos.size() > 1) {
			System.out.println(elegirLocalidad);
			String localidad = sc.next();
			for (Departamentos d : localidadesDptos) {
				// devuelve la que has elegido
				if (d.getLoc().equalsIgnoreCase(localidad)) {
					return d;
				}

			}
			// si solo hay 1 en el arraylist, te devuelve esa
		} else if (localidadesDptos.size() == 1) {
			for (Departamentos d : localidadesDptos) {
				return d;
			}
		} else {
			// Si no hay ninguna. introduces el departamento
			System.out.println("Elige a que localidad pertenece el departamento");
			String localidad = sc.next();
			anadirDepartamento(dpto, localidad);

		}
		// se ejecuta de nuevo una vez ya tienes introducida la nueva localidad, solo se
		// ejecuta si llega al else
		return comprobarExistenciaDepartamento(dpto);

	}

	/**
	 * Creas el departamento
	 * 
	 * @param nombreDepartamento
	 * @param localidad
	 */
	private static void anadirDepartamento(String nombreDepartamento, String localidad) {
		sesion = sf.openSession();
		Transaction t = sesion.beginTransaction();
		Departamentos dpto = new Departamentos(nombreDepartamento, localidad, null);
		sesion.persist(dpto);
		t.commit();
		sesion.close();
	}
}
