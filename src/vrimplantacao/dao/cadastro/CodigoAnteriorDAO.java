package vrimplantacao.dao.cadastro;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.CodigoInternoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

/**
 *
 * @author lucasrafael
 */
public class CodigoAnteriorDAO {

    /**
     * Este método retorna um Map com todos os códigos anteriores, tendo o 
     * campo codigoAnterior com key.
     * @return Map com os códigos anteriores.
     * @throws SQLException 
     */
    public Map<Double, CodigoAnteriorVO> carregarCodigoAnterior() throws SQLException {
        Map<Double, CodigoAnteriorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "    codigoanterior,\n" +
                    "    codigoatual,\n" +
                    "    barras,\n" +
                    "    naturezareceita,\n" +
                    "    piscofinscredito,\n" +
                    "    piscofinsdebito,\n" +
                    "    ref_icmsdebito,\n" +
                    "    e_balanca,\n" +
                    "    codigobalanca,\n" +
                    "    custosemimposto,\n" +
                    "    custocomimposto,\n" +
                    "    margem,\n" +
                    "    precovenda,\n" +
                    "    referencia,\n" +
                    "    ncm,\n" +
                    "    estoque,\n" +
                    "    id_loja,\n" +
                    "    cest\n" +
                    " from implantacao.codigoanterior\n" +
                    " order by codigoanterior"
            )) {
                while (rst.next()) {
                    CodigoAnteriorVO vo = new CodigoAnteriorVO();
                    vo.setCodigoanterior(rst.getDouble("codigoanterior"));
                    vo.setCodigoatual(rst.getDouble("codigoatual"));
                    vo.setBarras(rst.getLong("barras"));
                    vo.setNaturezareceita(rst.getInt("naturezareceita"));
                    vo.setPiscofinscredito(rst.getInt("piscofinscredito"));
                    vo.setPiscofinsdebito(rst.getInt("piscofinsdebito"));
                    vo.setRef_icmsdebito(rst.getString("ref_icmsdebito"));
                    vo.setE_balanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setMargem(rst.getDouble("margem"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setReferencia(rst.getInt("referencia"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.setId_loja(rst.getInt("id_loja"));
                    vo.setCest(rst.getString("cest"));
                    
                    result.put(vo.getCodigoanterior(), vo);
                }
            }
        }
        return result;
    }
    
    /**
     * Este método retorna um Map com todos os códigos anteriores, tendo o 
     * campo codigoAnterior com key.
     * @return Map com os códigos anteriores.
     * @throws SQLException 
     */
    public Map<String, CodigoAnteriorVO> carregarCodigoAnteriorV2() throws SQLException {
        Map<String, CodigoAnteriorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "    codigoanterior,\n" +
                    "    codigoatual,\n" +
                    "    barras,\n" +
                    "    naturezareceita,\n" +
                    "    piscofinscredito,\n" +
                    "    piscofinsdebito,\n" +
                    "    ref_icmsdebito,\n" +
                    "    e_balanca,\n" +
                    "    codigobalanca,\n" +
                    "    custosemimposto,\n" +
                    "    custocomimposto,\n" +
                    "    margem,\n" +
                    "    precovenda,\n" +
                    "    referencia,\n" +
                    "    ncm,\n" +
                    "    estoque,\n" +
                    "    id_loja,\n" +
                    "    cest\n" +
                    " from implantacao.codigoanterior\n" +
                    " order by codigoanterior"
            )) {
                while (rst.next()) {
                    CodigoAnteriorVO vo = new CodigoAnteriorVO();
                    String id = rst.getString("codigoanterior");
                    id = id != null ? id : "";
                    if (id.matches("[0-9.]*")) {
                        vo.setCodigoanterior(Double.parseDouble(id));
                    } else {
                        vo.setCodigoanterior(-1);
                    }
                    vo.setCodigoAnteriorStr(id);
                    vo.setCodigoatual(rst.getDouble("codigoatual"));
                    vo.setBarras(rst.getLong("barras"));
                    vo.setNaturezareceita(rst.getInt("naturezareceita"));
                    vo.setPiscofinscredito(rst.getInt("piscofinscredito"));
                    vo.setPiscofinsdebito(rst.getInt("piscofinsdebito"));
                    vo.setRef_icmsdebito(rst.getString("ref_icmsdebito"));
                    vo.setE_balanca(rst.getBoolean("e_balanca"));
                    vo.setCustosemimposto(rst.getDouble("custosemimposto"));
                    vo.setCustocomimposto(rst.getDouble("custocomimposto"));
                    vo.setMargem(rst.getDouble("margem"));
                    vo.setPrecovenda(rst.getDouble("precovenda"));
                    vo.setReferencia(rst.getInt("referencia"));
                    vo.setNcm(rst.getString("ncm"));
                    vo.setEstoque(rst.getDouble("estoque"));
                    vo.setId_loja(rst.getInt("id_loja"));
                    vo.setCest(rst.getString("cest"));
                    
                    result.put(vo.getCodigoAnteriorStr(), vo);
                }
            }
        }
        return result;
    }

    public void salvarCodigoFreitas(List<CodigoAnteriorVO> v_codigoAnterior) throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null;
        ResultSet rst = null, rst2 = null;
        
        try {
            
            Conexao.begin();
            
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            
            ProgressBar.setStatus("Importando Produtos...Código RMS...");
            ProgressBar.setMaximum(v_codigoAnterior.size());
            
            for (CodigoAnteriorVO i_anterior : v_codigoAnterior) {
                
                sql = new StringBuilder();
                sql.append("select p.id, ant.codigoanterior from produto p ");
                sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                sql.append("where ant.codigoanterior = "+i_anterior.codigobarras_rms);
                
                rst = stm.executeQuery(sql.toString());
                
                if (rst.next()) {
                    
                    sql = new StringBuilder();
                    sql.append("update implantacao.codigoanterior set ");
                    sql.append("codigo_rms = "+i_anterior.codigo_rms+", ");
                    sql.append("descricao_rms = '"+i_anterior.descricao_rms+"', ");
                    sql.append("eRMS = true ");
                    sql.append("where codigoanterior = "+rst.getDouble("codigoanterior")+" ");
                    sql.append("and codigoatual = "+rst.getInt("id")+";");
                    
                    stm.execute(sql.toString());
                    
                } else {
                    
                    sql = new StringBuilder();
                    sql.append("select p.id, ant.codigoanterior from produto p ");
                    sql.append("inner join implantacao.codigoanterior ant on ant.codigoatual = p.id ");
                    sql.append("where ant.barras = " + i_anterior.codigobarras_rms);
                    
                    rst2 = stm2.executeQuery(sql.toString());
                    
                    if (rst2.next()) {
                        sql = new StringBuilder();
                        sql.append("update implantacao.codigoanterior set ");
                        sql.append("codigo_rms = " + i_anterior.codigo_rms + ", ");
                        sql.append("descricao_rms = '" + i_anterior.descricao_rms + "', ");
                        sql.append("eRMS = true ");                        
                        sql.append("where codigoanterior = " + rst2.getDouble("codigoanterior") + " ");
                        sql.append("and codigoatual = " + rst2.getInt("id") + ";");

                        stm2.execute(sql.toString());                                                
                    }
                }
                
                ProgressBar.next();
            }
            
            stm.close();
            stm2.close();
            Conexao.commit();
            
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void alterarIdProduto(List<ProdutoVO> v_produto) throws Exception {
        StringBuilder sql = null;
        Statement stm = null;
        
        try {
            
            Conexao.begin();
            
            stm = Conexao.createStatement();
            
            ProgressBar.setMaximum(v_produto.size());
            ProgressBar.setStatus("Alterando id_produto2 '0'...Produtos...");            
            
            for (ProdutoVO i_produto : v_produto) {
                
                if ((i_produto.id < 10000) && (i_produto.idTipoEmbalagem == 4) && (!i_produto.pesavel) ||
                    (i_produto.id < 10000) && (i_produto.idTipoEmbalagem == 0) && (i_produto.pesavel)) {
                                        
                    i_produto.idProduto2 = new CodigoInternoDAO().getIdProdutoBalanca2();
                    
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_produto2 = "+i_produto.idProduto2+" ");
                    sql.append("where id = "+i_produto.id);
                    
                    stm.execute(sql.toString());
                } else {
                    
                    i_produto.idProduto2 = new CodigoInternoDAO().getIdProduto2();
                    
                    sql = new StringBuilder();
                    sql.append("update produto set ");
                    sql.append("id_produto2 = "+i_produto.idProduto2+" ");
                    sql.append("where id = "+i_produto.id);
                    
                    stm.execute(sql.toString());
                }
                
                ProgressBar.next();
            }
            
            stm.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
    
    public void acertarCodigoBarras() throws Exception {
        StringBuilder sql = null;
        Statement stm = null, stm2 = null, stm3 = null;
        ResultSet rst = null, rst2 = null;
        long codigoBarras = 0;
        boolean pesavel = false;
        Utils util = new Utils();
        
        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            stm2 = Conexao.createStatement();
            stm3 = Conexao.createStatement();
            
            ProgressBar.setStatus("Iniciando correção de código de barras...");
            
            sql = new StringBuilder();
            sql.append("delete from produtoautomacao ");
            sql.append("where char_length(cast(codigobarras as varchar)) <= 6 ");
            
            stm.execute(sql.toString());
            
            ProgressBar.setStatus("Analisando Tabela produtoautomacao...");                        
            
            sql = new StringBuilder();
            sql.append("select id, id_tipoembalagem, pesavel from produto ");
            sql.append("where id not in (select id_produto from produtoautomacao) ");
            
            rst = stm.executeQuery(sql.toString());
            
            ProgressBar.setMaximum(rst.getRow());
            ProgressBar.setStatus("Importando código de barras...produtoautomacao...");
            
            while (rst.next()) {
                
                pesavel = rst.getBoolean("pesavel");
                
                if (rst.getInt("id") == 1198) {
                    System.out.println("aqui");
                }
                
                if ((rst.getInt("id") >= 10000) && (rst.getInt("id_tipoembalagem") == 4)) {
                    
                    codigoBarras = util.gerarEan13(rst.getInt("id"), false);
                } else if ((rst.getInt("id") >= 10000) && (rst.getInt("id_tipoembalagem") == 0) && (pesavel)) {
                    
                    codigoBarras = util.gerarEan13(rst.getInt("id"), false);
                } else if ((rst.getInt("id") < 10000) && (rst.getInt("id_tipoembalagem") == 4)) {
                    
                    codigoBarras = util.gerarEan13(rst.getInt("id"), false);
                } else if ((rst.getInt("id") < 10000) && (rst.getInt("id_tipoembalagem") == 0) && (pesavel) || (!pesavel)) {
                    
                    codigoBarras = util.gerarEan13(rst.getInt("id"), false);
                }else {
                    
                    codigoBarras = util.gerarEan13(rst.getInt("id"), true);
                }
                
                sql = new StringBuilder();
                sql.append("select * from produtoautomacao ");
                sql.append("where codigobarras = "+codigoBarras+" ");
                
                rst2 = stm3.executeQuery(sql.toString());
                
                if (!rst2.next()) {
                    
                    sql = new StringBuilder();
                    sql.append("insert into produtoautomacao ( ");
                    sql.append("id_produto, codigobarras, qtdembalagem, id_tipoembalagem) ");
                    sql.append("values ( ");
                    sql.append(rst.getInt("id") + ", ");
                    sql.append(codigoBarras + ", ");
                    sql.append("1, ");
                    sql.append(rst.getInt("id_tipoembalagem"));
                    sql.append(");");

                    stm2.execute(sql.toString());                
                }
                
                ProgressBar.next();
            }
            
            stm.close();
            stm2.close();
            stm3.close();
            Conexao.commit();
        } catch(Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void corrigirEANCodigoAnterior(List<ProdutoVO> v_produto) throws Exception{
        Statement stm = null;

        try {
            Conexao.begin();

            stm = Conexao.createStatement();

            ProgressBar.setMaximum(v_produto.size());

            ProgressBar.setStatus("Atualizando dados implantação...Código Anterior...");

            for (ProdutoVO i_produto : v_produto) {
                       
                for (ProdutoAutomacaoVO oAutomacao : i_produto.getvAutomacao()) {
                    
                    String sql = "update implantacao.codigoanterior set " +
                            "barras = " + oAutomacao.getCodigoBarras() + 
                            " where codigoanterior = " + i_produto.getId() + ";";
                            
                    stm.execute(sql);
                    
                }


                ProgressBar.next();
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }
}