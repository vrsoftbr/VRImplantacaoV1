package vrimplantacao2_5.vo.enums;

/**
 *
 * @author guilhermegomes
 */
public enum EBancoDados {
    
    ACCESS(1),
    ADS(2),
    CACHE(3),
    DBF(4),
    FIREBIRD(5),
    DB2(6),
    INFORMIX(7),
    MYSQL(8),
    ORACLE(9),
    PARADOX(10),
    POSTGRESQL(11),
    SQLITE(12),
    SQLSERVER(13);
    
    public static EBancoDados getById(int id) {
        for (EBancoDados bd: values()) {
            if (bd.getId() == id) {
                return bd;
            }
        }
        return null;
    }
    
    private int id;

    private EBancoDados(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }
}
