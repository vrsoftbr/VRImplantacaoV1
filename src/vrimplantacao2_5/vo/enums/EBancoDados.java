package vrimplantacao2_5.vo.enums;

/**
 *
 * @author guilhermegomes
 */
public enum EBancoDados {
    
    ACCESS(1, "ACCESS"),
    ADS(2, "ADS"),
    CACHE(3, "CACHE"),
    DBF(4, "DBF"),
    FIREBIRD(5, "FIREBIRD"),
    DB2(6, "DB2"),
    INFORMIX(7, "INFORMIX"),
    MYSQL(8, "MYSQL"),
    ORACLE(9, "ORACLE"),
    PARADOX(10, "PARADOX"),
    POSTGRESQL(11, "POSTGRESQL"),
    SQLITE(12, "SQLITE"),
    SQLSERVER(13, "SQLSERVER");
    
    public static EBancoDados getById(int id) {
        for (EBancoDados bd: values()) {
            if (bd.getId() == id) {
                return bd;
            }
        }
        return null;
    }
    
    private int id;
    private String nome;

    private EBancoDados(int id, String nome) {
        this.id = id;
        this.nome = nome;
    }

    public int getId() {
        return id;
    }
    
    public String getNome() {
        return nome;
    }
}
