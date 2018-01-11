/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.CodigoAnteriorDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.CodigoAnteriorVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoLojaVO;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

/**
 *
 * @author LeandroCaires
 */
public class ProdutoAutomacaoDAO {
    private boolean alterarBarraAnterior = true;
    private boolean automacaoLoja = false;

    public void setAlterarBarraAnterior(boolean alterarBarraAnterior) {
        this.alterarBarraAnterior = alterarBarraAnterior;
    }
    
    public Map<Long, Integer> carregarCodigoBarras() throws Exception {
        Map<Long, Integer> vProdutoDestino = new HashMap<>();
        
        try (Statement stm = Conexao.createStatement()) {
            
            try (ResultSet rst = stm.executeQuery(
                "SELECT codigobarras, id_produto FROM produtoautomacao"
            )) {
                while (rst.next()) {
                    long codBarrasDestino = rst.getLong("codigobarras");
                    int id_produto = rst.getInt("id_produto");

                    vProdutoDestino.put(codBarrasDestino, id_produto);
                }
            }

            return vProdutoDestino;
        }
    }
    
    public void salvar(List<ProdutoVO> v_produto) throws Exception {
        
        Map<String, CodigoAnteriorVO> codigoAnterior = new CodigoAnteriorDAO().carregarCodigoAnteriorV2();
        Map<Long, Integer> eansExistentes = carregarCodigoBarras();
        
        ProgressBar.setMaximum(v_produto.size());
        ProgressBar.setStatus("Atualizando dados produto...Codigo Barra...");
        
        try (Statement stm = Conexao.createStatement()) {
            for (ProdutoVO i_produto : v_produto) {
                for (ProdutoAutomacaoVO oAutomacao : i_produto.vAutomacao) {
                    //Se o ean não existir, grava
                    if (!eansExistentes.containsKey(oAutomacao.getCodigoBarras())) {
                        if (String.valueOf(oAutomacao.getCodigoBarras()).length() >= 7
                            && String.valueOf(oAutomacao.getCodigoBarras()).length() <= 14) {

                            String strAnterior = null;
                            
                            
                            if ((!i_produto.getvCodigoAnterior().isEmpty()) && (i_produto.getvCodigoAnterior().get(0) != null)) {
                                //Util.exibirMensagem("1", "");
                                strAnterior = i_produto.getvCodigoAnterior().get(0).getCodigoAnteriorStr();
                            } 
                            if (strAnterior == null || "".equals(strAnterior.trim())) {
                                if (i_produto.getIdDouble() > 0) {
                                    strAnterior = String.valueOf((long) i_produto.getIdDouble());
                                    //Util.exibirMensagem("2", "");
                                } else {
                                    strAnterior = String.valueOf((int) i_produto.getId());
                                    //Util.exibirMensagem("3", "");
                                }
                            }

                            CodigoAnteriorVO oAnterior = codigoAnterior.get(strAnterior);
                            //Se houver produto cadastrado importa o EAN
                            if (oAnterior != null) {                            
                                //Só grava o ean de produtos que não forem balança
                                if (!oAnterior.isE_balanca()) {
                                    //Se o ean não estiver cadastrado ainda, inclui

                                    int qtdEmbalagem = oAutomacao.qtdEmbalagem;
                                    if (qtdEmbalagem <= 0) {
                                        qtdEmbalagem = 1;
                                    }
                                    double codigoatual = oAnterior.getCodigoatual();

                                    String sql = "insert into produtoautomacao (" +
                                        "id_produto, codigobarras, qtdembalagem, id_tipoembalagem) " +
                                        "values (" +
                                        (long) codigoatual + "," + 
                                        oAutomacao.codigoBarras + "," +
                                        qtdEmbalagem + "," +
                                        (oAutomacao.idTipoEmbalagem == -1 ? "(select id_tipoembalagem from produto where id = " + codigoatual + ")" : oAutomacao.idTipoEmbalagem) + ");";

                                    if (alterarBarraAnterior) {
                                        sql += ("update implantacao.codigoanterior set "
                                                + "barras = " + (oAutomacao.codigoBarras > 0
                                                        ? Utils.quoteSQL(oAutomacao.codigoBarras + "")
                                                        : null) + " where codigoatual = " + codigoatual + ";");
                                    }
                                    stm.execute(sql);

                                    //Inclui nos eans existentes
                                    if (!eansExistentes.containsKey(oAutomacao.codigoBarras)) {
                                        eansExistentes.put(oAutomacao.codigoBarras, (int) codigoatual);
                                    }                            
                                }
                            }
                        }
                    }
                }
                
                //TODO: Melhorar este ponto
                if (automacaoLoja) {
                    for (ProdutoAutomacaoLojaVO oAutomacaoLoja : i_produto.vAutomacaoLoja) {
                        StringBuilder sql = new StringBuilder();
                        sql.append("select * from produtoautomacaoloja ");
                        sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras);
                        try (Statement stm2 = Conexao.createStatement()) {
                            try (ResultSet rst3 = stm2.executeQuery(sql.toString())) {
                                if (rst3.next()) {
                                    sql = new StringBuilder();
                                    sql.append("update produtoautomacaoloja set ");
                                    sql.append("precovenda = " + oAutomacaoLoja.precovenda + " ");
                                    sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras + " ");
                                    sql.append("and id_loja = " + oAutomacaoLoja.id_loja);
                                    Conexao.createStatement().execute(sql.toString());
                                } else {
                                    sql = new StringBuilder();
                                    sql.append("insert into produtoautomacaoloja (");
                                    sql.append("codigobarras, precovenda, id_loja) ");
                                    sql.append("values (");
                                    sql.append(oAutomacaoLoja.codigobarras + ", ");
                                    sql.append(oAutomacaoLoja.precovenda + ", ");
                                    sql.append(oAutomacaoLoja.id_loja + ");");
                                    Conexao.createStatement().execute(sql.toString());
                                }

                                sql = new StringBuilder();
                                sql.append("update produtoautomacao set qtdembalagem = " + oAutomacaoLoja.qtdEmbalagem + " ");
                                sql.append("where codigobarras = " + oAutomacaoLoja.codigobarras +";");
                                Conexao.createStatement().execute(sql.toString());
                            }
                        }
                    }
                }
                
                ProgressBar.next();
            }            
        }
    }
    
}
