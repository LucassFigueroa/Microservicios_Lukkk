package minzbook.authservice.dto;

public class UserResponse {
    private Long id;
    private String email;
    private String nombre;
    private String apellido;
    private String rol;

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }

    public String getApellido() { return apellido; }
    public void setApellido(String apellido) { this.apellido = apellido; }

    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
}
