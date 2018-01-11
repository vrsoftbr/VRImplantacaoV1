package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vrframework.classe.Conexao;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorFornecedorVO;

/**
 * DAO para trabalhar com a tabela código anterior do fornecedor.
 * @author Leandro
 */
public class CodigoAnteriorFornecedorDAO {
    
    private Map<String, CodigoAnteriorFornecedorVO> codigosAnteriores;

    public Map<String, CodigoAnteriorFornecedorVO> getCodigosAnteriores() {
        return codigosAnteriores;
    }
    
    /**
     * Retorna a listagem dos codigos anteriores do fornecedor.
     * @return Lista de códigos anteriores.
     * @throws Exception 
     */
    public static Map<String, CodigoAnteriorFornecedorVO> carregarListagem() throws Exception {
        Map<String, CodigoAnteriorFornecedorVO> result = new LinkedHashMap<>();
        
        try(Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	caf.codigoanterior, \n" +
                    "	caf.codigoatual, \n" +
                    "	caf.id_loja,\n" +
                    "	f.id_estado\n" +
                    "from \n" +
                    "	implantacao.codigoanteriorforn caf\n" +
                    "	join fornecedor f on caf.codigoatual = f.id\n" +
                    "order by \n" +
                    "	caf.codigoanterior"
            )) {
                while (rst.next()) {
                    CodigoAnteriorFornecedorVO vo = new CodigoAnteriorFornecedorVO();
                    
                    vo.setCodigoAnterior(rst.getLong("codigoanterior"));
                    vo.setCodigoAtual(rst.getInt("codigoatual"));
                    vo.setIdLoja(rst.getInt("id_loja"));
                    vo.setId_uf(rst.getInt("id_estado"));
                    
                    result.put(vo.getChaveUnica(), vo);
                }
            }
        }         
        return result;
    }
    
    /**
     * Atualiza a listagem de códigos anteriores no DAO.
     * @throws Exception 
     */
    public void carregarCodigosAnteriores() throws Exception {
        codigosAnteriores = CodigoAnteriorFornecedorDAO.carregarListagem();
    }
    
    public String getSalvarSQL(CodigoAnteriorFornecedorVO codigoAnterior) throws Exception {
        if (codigosAnteriores == null) {
            carregarCodigosAnteriores();
        }        
        
        if (!codigosAnteriores.containsKey(codigoAnterior.getChaveUnica())) {
            return "INSERT INTO implantacao.codigoanteriorforn(\n" +
                    "codigoanterior, codigoatual, id_loja)\n" +
                    "VALUES (" + codigoAnterior.getCodigoAnterior() + ",\n" + 
                    codigoAnterior.getCodigoAtual() + ",\n" +
                    codigoAnterior.getIdLoja() + "\n" + 
                    ");";           
        } else {
            return "";
        }
    }
    
    public void salvar(CodigoAnteriorFornecedorVO codigoAnterior) throws Exception {
        String sql = getSalvarSQL(codigoAnterior);
        
        if (!sql.equals("")) {
            try (Statement stm = Conexao.createStatement()) {                
                stm.executeUpdate(sql);
            }
        }
    }

    public boolean existe(long codigoAnterior, int idLojaCliente) throws Exception {
        if (codigosAnteriores == null) {
            carregarCodigosAnteriores();
        }        
        return codigosAnteriores.containsKey(CodigoAnteriorFornecedorVO.makeChaveUnica(idLojaCliente,codigoAnterior));
    }
    
    public CodigoAnteriorFornecedorVO get(long codigoAnterior, int idLojaCliente) throws Exception {
        if (codigosAnteriores == null) {
            carregarCodigosAnteriores();
        }        
        return codigosAnteriores.get(CodigoAnteriorFornecedorVO.makeChaveUnica(idLojaCliente,codigoAnterior));
    }
    
}
