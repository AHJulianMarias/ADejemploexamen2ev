package clasesHibernate;
// Generated 2 feb 2025 17:36:22 by Hibernate Tools 6.5.1.Final

/**
 * Empleados generated by hbm2java
 */
public class Empleados implements java.io.Serializable {

	private Integer id;
	private Departamentos departamentos;
	private String nombre;
	private String apellido1;
	private String apellido2;

	public Empleados() {
	}

	public Empleados(String nombre, String apellido1) {
		this.nombre = nombre;
		this.apellido1 = apellido1;
	}

	public Empleados(Departamentos departamentos, String nombre, String apellido1, String apellido2) {
		this.departamentos = departamentos;
		this.nombre = nombre;
		this.apellido1 = apellido1;
		this.apellido2 = apellido2;
	}

	public Integer getId() {
		return this.id;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public Departamentos getDepartamentos() {
		return this.departamentos;
	}

	public void setDepartamentos(Departamentos departamentos) {
		this.departamentos = departamentos;
	}

	public String getNombre() {
		return this.nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public String getApellido1() {
		return this.apellido1;
	}

	public void setApellido1(String apellido1) {
		this.apellido1 = apellido1;
	}

	public String getApellido2() {
		return this.apellido2;
	}

	public void setApellido2(String apellido2) {
		this.apellido2 = apellido2;
	}

	@Override
	public String toString() {
		return "Empleados [id=" + id + ", departamentos=" + departamentos + ", nombre=" + nombre + ", apellido1="
				+ apellido1 + ", apellido2=" + apellido2 + "]";
	}

}
