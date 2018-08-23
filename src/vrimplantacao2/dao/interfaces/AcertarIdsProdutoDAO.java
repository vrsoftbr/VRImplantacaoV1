/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoIDStack;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;

/**
 *
 * @author lucasrafael
 */
public class AcertarIdsProdutoDAO {

    public String lojaOrigem;
    private final ProdutoRepositoryProvider provider;
    
    public AcertarIdsProdutoDAO() {
        ProdutoRepositoryProvider provider = new ProdutoRepositoryProvider();
        this.provider = provider;
    }    

    public List<ProdutoVO> carregarProdutosBalanca() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.id, p.descricaocompleta,\n"
                    + "ant.impid codigoanterior_getway, ant.codigoatual codigoatual_vr,\n"
                    + "ean.ean codigobalanca\n"
                    + "from produto p \n"
                    + "inner join implantacao.codant_produto ant on ant.codigoatual = p.id\n"
                    + "inner join implantacao.codant_ean ean on ean.importid = ant.impid\n"
                    + "where ean.importloja = '" + lojaOrigem + "'\n"
                    + "and ant.imploja = '" + lojaOrigem + "'\n"
                    + "and trim(leading '0' from ean.ean) in (select codigo::varchar from implantacao.produtobalanca)\n"
                    + "and char_length(ean.ean) <= 6\n"
                    + "order by ean.ean"
            )) {
                int cont = 1;
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setIdProduto2(rst.getInt("codigobalanca"));
                    result.add(vo);

                    ProgressBar.setStatus("Carregando dados...Produtos Balança..." + cont);
                    cont++;
                }
            }
        }
        return result;
    }

    public List<ProdutoVO> carregarProdutosNormais() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.id, p.descricaocompleta,\n"
                    + "ant.impid, ant.codigoatual\n"
                    + "from produto p \n"
                    + "inner join implantacao.codant_produto ant on ant.codigoatual = p.id\n"
                    + "where p.id_produto2 = 0\n"
                    + "and ant.imploja = '" + lojaOrigem + "'\n"
                    + "order by p.id"
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("id"));
                    vo.setImpId(rst.getString("impid"));
                    result.add(vo);
                }
            }
        }
        return result;
    }

    public void importarProdutosBalanca() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Produtos Balança...");
        result = carregarProdutosBalanca();        
        if (!result.isEmpty()) {
            gravarCodigoBalanca(result);
        }
    }

    public void importarProdutosNormais() throws Exception {
        List<ProdutoVO> result = new ArrayList<>();
        ProgressBar.setStatus("Carregando dados...Produtos Normais...");
        result = carregarProdutosNormais();        
        if (!result.isEmpty()) {
            gravarCodigoNormal(result);
        }
    }

    public void gravarCodigoBalanca(List<ProdutoVO> vo) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setStatus("Gravar Código Balança...");
            ProgressBar.setMaximum(vo.size());

            for (ProdutoVO i : vo) {

                sql = new StringBuilder();
                sql.append("update produto set id_produto2 = " + i.getIdProduto2() + " where id = " + i.getId());
                stm.execute(sql.toString());
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void gravarCodigoNormal(List<ProdutoVO> vo) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        int id_produto2 = 0;        

        try {
            ProdutoIDStack idStack = provider.getIDStack();
            MultiMap<Integer, ProdutoVO> gravados = getProdutosId2();
            Conexao.begin();
            stm = Conexao.createStatement();
            ProgressBar.setStatus("Gravar Código Normal...");
            ProgressBar.setMaximum(vo.size());

            for (ProdutoVO i : vo) {
                ProdutoVO gravado = gravados.get(
                        Integer.parseInt(i.getImpId())
                );
                
                sql = new StringBuilder();

                if (gravado == null) {
                    id_produto2 = Integer.parseInt(i.getImpId());
                } else {
                    id_produto2 = idStack.obterID(i.getImpId(), false);
                }
                
                sql.append("update produto set id_produto2 = " + id_produto2 + " where id = " + i.getId());
                System.out.println(sql.toString() + " Gravado: " + gravado);
                stm.execute(sql.toString());
                ProgressBar.next();
            }
            stm.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public MultiMap<Integer, ProdutoVO> getProdutosId2() throws Exception {
        MultiMap<Integer, ProdutoVO> result = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "id_produto2 "
                    + "from produto "
            )) {
                while (rst.next()) {
                    ProdutoVO vo = new ProdutoVO();
                    vo.setId(rst.getInt("id_produto2"));
                    result.put(vo, rst.getInt("id_produto2"));
                }
            }
        }
        return result;
    }
}
