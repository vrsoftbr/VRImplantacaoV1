/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao.dao.interfaces;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.vo.vrimplantacao.ProdutoVO;

public class AlterarProdutoPdvVendaItemDAO {

    public Date dataInicio;
    public Date dataFim;
    public int idLoja;

    public List<ProdutoVO> carregarProdutosVenda() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        Conexao.begin();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct(id_produto) codigobarras "
                    + "from pdv.vendaitem "
                    + "where id_venda in (select id from pdv.venda "
                    + "where id_loja = " + idLoja + " "
                    + "and data >= '" + dataInicio + "' "
                    + "and data <= '" + dataFim + "') "
                    + "and id_produto in (select id_produto from produtoautomacao "
                    + "where codigobarras = id_produto "
                    + "group by id_produto "
                    + "having count(id_produto) = 1)"
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setCodigoBarras(rst.getLong("codigobarras"));
                    vResult.add(vo);
                }
            }
        }
        Conexao.commit();
        return vResult;
    }

    public List<ProdutoVO> carregarCodigoAnteriores() throws Exception {
        List<ProdutoVO> vProdutosItem = new ArrayList<>();
        List<ProdutoVO> vResult = new ArrayList<>();
        vProdutosItem = carregarProdutosVenda();
        Conexao.begin();

        for (ProdutoVO i_produto : vProdutosItem) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select distinct p.id, ant.impid, gw.barras, pa.codigobarras "
                        + "from implantacao.codant_produto ant "
                        + "inner join produto p on p.id = ant.codigoatual "
                        + "inner join implantacao.produtos_getway gw on gw.codprod = ant.impid "
                        + "inner join produtoautomacao pa on pa.id_produto = ant.codigoatual "
                        + "where p.id = pa.codigobarras "
                        + "and pa.codigobarras <= 999999 "
                        + "and p.id in (select id_produto from produtoautomacao "
                        + "group by id_produto "
                        + "having count(id_produto) = 1 )"
                        + "and gw.barras = '" + String.valueOf(i_produto.getCodigoBarras()) + "' "
                )) {
                    while (rst.next()) {
                        ProdutoVO vo = new ProdutoVO();
                        if (Long.parseLong(rst.getString("barras")) != rst.getLong("codigobarras")) {
                            vo.setId(rst.getInt("id"));
                            vo.setCodigoBarras(Long.parseLong(rst.getString("barras")));
                            vResult.add(vo);
                        }
                    }
                }
            }
        }
        Conexao.commit();
        return vResult;
    }

    private void alterarProdutoPdvVendaItem(List<ProdutoVO> v_list) throws Exception {
        File f = new File("C://vr//implantacao//produtos_alterados.txt");
        FileWriter fw = new FileWriter(f);
        BufferedWriter bw = new BufferedWriter(fw);
        StringBuilder sql = null;
        Statement stm = null;
        Conexao.begin();
        stm = Conexao.createStatement();
        ProgressBar.setMaximum(v_list.size());
        ProgressBar.setStatus("Alterando produtos pdv.vendaitem...");

        try {
            for (ProdutoVO i_list : v_list) {
                sql = new StringBuilder();
                sql.append("update pdv.vendaitem "
                        + "set "
                        + "id_produto = " + i_list.getId() + ","
                        + "codigobarras = " + i_list.getId() + " "
                        + "where id_produto = " + i_list.getCodigoBarras() + " "
                        + "and id_venda in ("
                        + "select id from pdv.venda "
                        + "where id_loja = " + idLoja + " "
                        + "and data >= '" + dataInicio + "' and data <= '" + dataFim + "'"
                        + ")"
                );
                bw.write("Produoto Anterior: " + i_list.getCodigoBarras() + ", Produto Novo: " + i_list.getId());
                bw.newLine();
                stm.execute(sql.toString());
                ProgressBar.next();
            }
            bw.flush();
            bw.close();
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void importarAlterarProdutoPdvVendaItem() throws Exception {
        List<ProdutoVO> vResult = new ArrayList<>();
        try {
            ProgressBar.setStatus("Carregando dados...");
            vResult = carregarCodigoAnteriores();
            if (!vResult.isEmpty()) {
                alterarProdutoPdvVendaItem(vResult);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }
}
