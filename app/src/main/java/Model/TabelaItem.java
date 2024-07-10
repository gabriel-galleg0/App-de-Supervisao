package Model;

public class TabelaItem {
    public String func;
    public String nome;
    public String pdv;
    public String solicitado;
    public String situacao;

    public TabelaItem(String func, String nome, String pdv, String solicitado, String situacao) {
        this.func = func;
        this.nome = nome;
        this.pdv = pdv;
        this.solicitado = solicitado;
        this.situacao = situacao;
    }
}
