package vrimplantacao2.vo.enums;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.IntegerObjectType;
import java.sql.SQLException;

/**
 * Tipo de desconto aplicados na venda.
 * @author Leandro
 */
public class TipoDesconto {
    
    public static final TipoDesconto PRECO_ERRADO = new TipoDesconto(1, "PRECO ERRADO");
    public static final TipoDesconto VENDA_ATACADO = new TipoDesconto(2, "VENDA ATACADO");
    public static final TipoDesconto FALTA_PRODUTO_OFERTA = new TipoDesconto(3, "FALTA PRODUTO OFERTA");

    public static TipoDesconto getById(int id) {
        switch (id) {
            case 1: return PRECO_ERRADO;
            case 2: return VENDA_ATACADO;
            case 3: return FALTA_PRODUTO_OFERTA;
            default: return null;
        }
    }
    
    private int id;
    private String descricao;

    public TipoDesconto(int id, String descricao) {
        this.id = id;
        this.descricao = descricao;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }
    
    /**
     * Classe utilizada para mapear o {@link TipoDesconto} no banco.
     */
    public static class TipoDescontoPersister extends IntegerObjectType {

        private static final TipoDescontoPersister singleTon = new TipoDescontoPersister();
        
        private TipoDescontoPersister() {
            super(SqlType.INTEGER, new Class<?>[] { TipoDesconto.class });
        }
        
        public static TipoDescontoPersister getSingleton() {
            return singleTon;
        }

        @Override
        public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {            
            if (javaObject != null) {
                if (fieldType.getType().equals(TipoDesconto.class)) {
                    return ((TipoDesconto) javaObject).getId();
                }
            }
            return super.javaToSqlArg(fieldType, javaObject);
        }    

        @Override
        public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
            if (sqlArg != null) {
                if (fieldType.getType().equals(TipoDesconto.class)) {
                    return TipoDesconto.getById((int) sqlArg);
                }
            }
            return super.sqlArgToJava(fieldType, sqlArg, columnPos);
        }
    
    }
    
}
