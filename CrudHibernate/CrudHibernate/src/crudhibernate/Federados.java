package crudhibernate;
// Generated 5/05/2023 02:48:48 PM by Hibernate Tools 4.3.1



/**
 * Federados generated by hbm2java
 */
public class Federados  implements java.io.Serializable {


     private Integer idFederado;
     private String nombre;
     private String apellido;
     private String direccion;
     private String email;
     private String password;
     private String estado;

    public Federados() {
    }

    public Federados(String nombre, String apellido, String direccion, String email, String password, String estado) {
       this.nombre = nombre;
       this.apellido = apellido;
       this.direccion = direccion;
       this.email = email;
       this.password = password;
       this.estado = estado;
    }
   
    public Integer getIdFederado() {
        return this.idFederado;
    }
    
    public void setIdFederado(Integer idFederado) {
        this.idFederado = idFederado;
    }
    public String getNombre() {
        return this.nombre;
    }
    
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
    public String getApellido() {
        return this.apellido;
    }
    
    public void setApellido(String apellido) {
        this.apellido = apellido;
    }
    public String getDireccion() {
        return this.direccion;
    }
    
    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    public String getEmail() {
        return this.email;
    }
    
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return this.password;
    }
    
    public void setPassword(String password) {
        this.password = password;
    }
    public String getEstado() {
        return this.estado;
    }
    
    public void setEstado(String estado) {
        this.estado = estado;
    }




}


