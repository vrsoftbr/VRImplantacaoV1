package vr.implantacao.vo.enums;

/**
 *
 * @author Desenvolvimento
 */
public enum BancoDados {

    ACCESS("ACCESS"),
    ADS("ADS"),
    CACHE("CACHE"),
    DBF("DBF"),
    FIREBIRD("FIREBIRD"),
    DB2("DB2"),
    INFORMIX("INFORMIX"),
    MYSQL("MYSQL"),
    ORACLE("ORACLE"),
    PARADOX("PARADOX"),
    POSTGRESQL("POSTGRESQL"),
    SQLITE("SQLITE"),
    SQL_SERVER("SQL SERVER");
    
    private String nome;
    
    public String getNome() {
        return this.nome;
    }
    
    public void setNome(String nome) {
        this.nome = nome;
    }
    
    BancoDados(String nome) {
        setNome(nome);
    }

}
