package Model;

public class User {
    private String nome;
    private String phoneNumber;

    public User(){

    }

    public User(String nome, String phoneNumber){
        this.nome = nome;
        this.phoneNumber = phoneNumber;
    }

    public String getNome(){
        return nome;
    }

    public void setNome(String nome){
        this.nome = nome;
    }

    public String getphoneNumber(){
        return phoneNumber;
    }

    public void setphoneNumber(String phoneNumber){
        this.phoneNumber = phoneNumber;
    }
}
