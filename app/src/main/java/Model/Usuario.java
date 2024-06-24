package Model;

public class Usuario {
    private String email;
    private String senha;
    private String tipo;

    /**
     * Construtor padrão da classe Usuario
     */
    public Usuario() {
    }
    public Usuario(String email, String senha) {
        this.email = email;
        this.senha = senha;
    }
    public String getTipo() {
        return tipo;
    }
    public void setTipo(String tipo) {
        this.tipo = tipo;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getSenha() {
        return senha;
    }
    public void setSenha(String senha) {
        this.senha = senha;
    }
}