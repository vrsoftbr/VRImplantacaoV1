package vrimplantacao2.vo.enums;

import com.j256.ormlite.field.FieldType;
import com.j256.ormlite.field.SqlType;
import com.j256.ormlite.field.types.IntegerObjectType;
import java.sql.SQLException;

/**
 * Tipo de cancelamento aplicado na venda.
 * @author Leandro
 */
public class TipoCancelamento {
    
    public static final TipoCancelamento DEVOLUCAO_DE_MERCADORIA = new TipoCancelamento(1, "DEVOLUCAO DE MERCADORIA");
    public static final TipoCancelamento ERRO_DE_REGISTRO = new TipoCancelamento(2, "ERRO DE REGISTRO");
    public static final TipoCancelamento DINHEIRO_DO_CLIENTE_INSUFICIENTE = new TipoCancelamento(3, "DINHEIRO DO CLIENTE INSUFICIENTE");
    public static final TipoCancelamento PRODUTO_COM_PRECO_ERRADO = new TipoCancelamento(4, "PRODUTO COM PRECO ERRADO");
    public static final TipoCancelamento TESTE_DE_EQUIPAMENTO = new TipoCancelamento(5, "TESTE DE EQUIPAMENTO");
    public static final TipoCancelamento CHEQUE_DO_CLIENTE_RECUSADO = new TipoCancelamento(6, "CHEQUE DO CLIENTE RECUSADO");
    public static final TipoCancelamento CARTAO_RECUSADO_OU_SEM_SALDO = new TipoCancelamento(7, "CARTAO RECUSADO OU SEM SALDO");
    public static final TipoCancelamento PROBLEMA_NO_EQUIPAMENTO = new TipoCancelamento(8, "PROBLEMA NO EQUIPAMENTO");

    public static TipoCancelamento getById(int id) {
        switch (id) {
            case 1: return DEVOLUCAO_DE_MERCADORIA;
            case 2: return ERRO_DE_REGISTRO;
            case 3: return DINHEIRO_DO_CLIENTE_INSUFICIENTE;
            case 4: return PRODUTO_COM_PRECO_ERRADO;
            case 5: return TESTE_DE_EQUIPAMENTO;
            case 6: return CHEQUE_DO_CLIENTE_RECUSADO;
            case 7: return CARTAO_RECUSADO_OU_SEM_SALDO;
            case 8: return PROBLEMA_NO_EQUIPAMENTO;
            default: return null;
        }
    }
    
    private int id;
    private String descricao;

    public TipoCancelamento(int id, String descricao) {
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
     * Classe utilizada para mapear o {@link TipoCancelamento} no banco.
     */
    public static class TipoCancelamentoPersister extends IntegerObjectType {

        private static final TipoCancelamentoPersister singleTon = new TipoCancelamentoPersister();
        
        private TipoCancelamentoPersister() {
            super(SqlType.INTEGER, new Class<?>[] { TipoCancelamento.class });
        }
        
        public static TipoCancelamentoPersister getSingleton() {
            return singleTon;
        }

        @Override
        public Object javaToSqlArg(FieldType fieldType, Object javaObject) throws SQLException {            
            if (javaObject != null) {
                if (fieldType.getType().equals(TipoCancelamento.class)) {
                    return ((TipoCancelamento) javaObject).getId();
                }
            }
            return super.javaToSqlArg(fieldType, javaObject);
        }    

        @Override
        public Object sqlArgToJava(FieldType fieldType, Object sqlArg, int columnPos) throws SQLException {
            if (sqlArg != null) {
                if (fieldType.getType().equals(TipoCancelamento.class)) {
                    return TipoCancelamento.getById((int) sqlArg);
                }
            }
            return super.sqlArgToJava(fieldType, sqlArg, columnPos);
        }
    
    }
    
}
