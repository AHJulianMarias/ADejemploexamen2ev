package clasesHibernate;

import java.util.ArrayList;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.cfg.Configuration;

import jakarta.persistence.TypedQuery;

public class test {

	private static final Configuration cfg = new Configuration().configure();
	// creas la sesión
	// OBJETO SESSION FACTORY, ESTO ES PA HIBERNATE, YA LO VERÁS
	private static final SessionFactory sf = cfg.buildSessionFactory();
	private static Session sesion;

	public static void main(String[] args) {

		ArrayList<Empleados> listEmpleados = listarEmpleados();
		if (listEmpleados.size() == 0) {
			System.out.println("No hay ningun empleado en este departamento");
		} else {
			for (Empleados e : listEmpleados) {
				System.out.println(e.toString());

			}
		}

	}

	private static ArrayList<Empleados> listarEmpleados() {
		String hql = "from Empleados";
		sesion = sf.openSession();
		TypedQuery<Empleados> tqEmp = sesion.createQuery(hql, Empleados.class);
		ArrayList<Empleados> listaEmpleados = (ArrayList<Empleados>) tqEmp.getResultList();
		return listaEmpleados;

	}
}
