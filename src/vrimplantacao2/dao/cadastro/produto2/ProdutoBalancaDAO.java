package vrimplantacao2.dao.cadastro.produto2;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vr.core.utils.StringUtils;
import vrframework.classe.Conexao;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;

/**
 * DAO do produto balanca.
 * @author Leandro
 */
public class ProdutoBalancaDAO {
    
    private final TipoConversao tipoConversao;

    public ProdutoBalancaDAO() {
        this(TipoConversao.getFromParametros());
    }
    
    public ProdutoBalancaDAO(TipoConversao tipoConversao) {
        this.tipoConversao = tipoConversao == null ? TipoConversao.SIMPLES : tipoConversao;
    }

    public Map<Integer, ProdutoBalancaVO> getProdutosBalanca() throws Exception {
        Map<Integer, ProdutoBalancaVO> result = this.tipoConversao.newMap();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select * from implantacao.produtobalanca order by codigo"
            )) {
                while (rst.next()) {
                    ProdutoBalancaVO vo = new ProdutoBalancaVO();
                    
                    vo.setCodigo(rst.getInt("codigo"));
                    vo.setDescricao(rst.getString("descricao"));
                    vo.setPesavel(rst.getString("pesavel"));
                    vo.setValidade(rst.getInt("validade"));
                    
                    result.put(vo.getCodigo(), vo);
                }
            }
        }
        return result;
    }
    
    public enum TipoConversao {
    
        SIMPLES ("SIMPLES") {
            @Override
            public int convert(String codigo) {
                long ean = StringUtils.toLong(String.valueOf(codigo), -2);
                if (ean < 1 || ean > 9999999) {
                    return -2;
                }
                return (int) ean;
            }
        },
        REMOVER_DIGITO ("REMOVER_DIGITO") {
            @Override
            public int convert(String codigo) {
                String strEan = String.valueOf(SIMPLES.convert(codigo));
                return StringUtils.toInt(strEan.substring(0, strEan.length() - 1), -2);
            }
        };
        
        private final String key;
        private TipoConversao(String key) {
            this.key = key;
        }

        public String getKey() {
            return key;
        }
        
        public static TipoConversao get(String driver) {
            for (TipoConversao d: values()) {
                if (d.key.equals(driver)) {
                    return d;
                }
            }
            return SIMPLES;
        }

        static TipoConversao getFromParametros() {
            return get(Parametros.get().getWithNull(SIMPLES.key, "BALANCA", "STRATEGY"));
        }

        public abstract int convert(String codigo);

        Map<Integer, ProdutoBalancaVO> newMap() {
            return new ProdutoBalancaMap(this);
        }
        
    }
    
    public static class ProdutoBalancaMap extends LinkedHashMap<Integer, ProdutoBalancaVO> {
        
        private final TipoConversao tipoConversao;

        public ProdutoBalancaMap(TipoConversao tipoConversao) {
            super();
            this.tipoConversao = tipoConversao;
        }
        
        private int conv(Object key) {
            int val = tipoConversao.convert(String.valueOf(key));
            return val;
        }
        
        @Override
        public ProdutoBalancaVO get(Object key) {
            return super.get(conv(key)); 
        }

        @Override
        public boolean containsKey(Object key) {
            return super.containsKey(conv(key));
        }        
        
    };
    
}

