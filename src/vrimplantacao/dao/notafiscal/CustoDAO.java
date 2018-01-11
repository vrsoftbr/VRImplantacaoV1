package vrimplantacao.dao.notafiscal;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import vrimplantacao.dao.LogCustoDAO;
import vrimplantacao.dao.ParametroDAO;
import vrimplantacao.dao.cadastro.FornecedorDAO;
import vrimplantacao.dao.cadastro.KitDAO;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.dao.cadastro.ReceitaDAO;
import vrimplantacao.dao.cadastro.TipoPisCofinsDAO;
import vrimplantacao.dao.estoque.PepsDAO;
import vrimplantacao.vo.administrativo.CustoEntradaVO;
import vrimplantacao.vo.administrativo.CustoVO;
import vrimplantacao.vo.administrativo.KitItemVO;
import vrimplantacao.vo.administrativo.KitVO;
import vrimplantacao.vo.administrativo.PepsVO;
import vrimplantacao.vo.administrativo.ReceitaItemVO;
import vrimplantacao.vo.administrativo.ReceitaProdutoVO;
import vrimplantacao.vo.administrativo.ReceitaVO;
import vrimplantacao.vo.loja.FornecedorVO;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao.vo.cadastro.SituacaoCadastro;
import vrframework.classe.Conexao;
import vrframework.classe.Util;
import vrframework.classe.VRException;

public class CustoDAO {

    public void alterarEntrada(CustoEntradaVO i_custo) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //calcula custo medio
            sql = new StringBuilder();
            sql.append("SELECT customediocomimposto, customediosemimposto, estoque");
            sql.append(" FROM produtocomplemento");
            sql.append(" WHERE id_produto = " + i_custo.idProduto);
            sql.append(" AND id_loja = " + i_custo.idLoja);

            rst = stm.executeQuery(sql.toString());

            if (!rst.next()) {
                throw new VRException("Produto " + i_custo.idProduto + " não encontrado!");
            }

            double estoque = 0;

            if (rst.getDouble("estoque") > 0) {
                estoque = rst.getDouble("estoque");
            }

            double custoMedioComImposto = Util.round(((estoque * rst.getDouble("customediocomimposto")) + (i_custo.quantidade * i_custo.custoComImposto)) / (estoque + i_custo.quantidade), 4);
            double custoMedioSemImposto = Util.round(((estoque * rst.getDouble("customediosemimposto")) + (i_custo.quantidade * i_custo.custoSemImposto)) / (estoque + i_custo.quantidade), 4);

            //atualiza custo
            sql = new StringBuilder();
            sql.append("UPDATE produtocomplemento SET");
            sql.append(" custosemimpostoanterior = custosemimposto,");
            sql.append(" custosemimposto = " + i_custo.custoSemImposto + ",");
            sql.append(" custocomimpostoanterior = custocomimposto,");
            sql.append(" custocomimposto = " + i_custo.custoComImposto + ",");
            sql.append(" custosemperdasemimpostoanterior = custosemperdasemimposto,");
            sql.append(" custosemperdasemimposto = " + i_custo.custoSemPerdaSemImposto + ",");
            sql.append(" customediocomimposto = " + custoMedioComImposto + ",");
            sql.append(" customediosemimposto = " + custoMedioSemImposto + ",");
            sql.append(" valoripi = " + i_custo.valorIpi + ",");
            sql.append(" valoricmssubstituicao = " + i_custo.valorIcmsSubstituicao + ",");
            sql.append(" id_aliquotacredito = " + i_custo.idAliquotaCredito);
            sql.append(" WHERE id_produto = " + i_custo.idProduto);
            sql.append(" AND id_loja = " + i_custo.idLoja);

            if (i_custo.bonificacao) {
                sql.append(" AND custosemimposto = 0");
            }

            stm.execute(sql.toString());

            new LogCustoDAO().gerarTransacao(i_custo.idProduto, i_custo.observacao, i_custo.idLoja);

            alterarReceita(i_custo.idProduto, i_custo.idLoja);

            alterarKit(i_custo.idProduto, i_custo.idLoja);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarReceita(int i_idProduto, int idLoja) throws Exception {
        Statement stm = null;
        StringBuilder sql = new StringBuilder();
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            sql.append("SELECT ri.id_receita");
            sql.append(" FROM receitaitem AS ri");
            sql.append(" INNER JOIN receita r ON r.id = ri.id_receita");
            sql.append(" WHERE ri.id_produto = " + i_idProduto);
            sql.append(" AND r.id_situacaocadastro = " + SituacaoCadastro.ATIVO.getId());

            rst = stm.executeQuery(sql.toString());

            while (rst.next()) {
                ReceitaVO oReceita = new ReceitaDAO().carregar(rst.getInt("id_receita"), idLoja);

                double totalCusto = 0;

                for (ReceitaItemVO oItem : oReceita.vItem) {
                    totalCusto += ((oItem.qtdEmbalagemReceita * oItem.custoEmbalagem / oItem.qtdEmbalagemProduto) / oItem.fatorConversao);
                }

                for (ReceitaProdutoVO oProduto : oReceita.vProduto) {
                    oProduto.totalCusto = totalCusto / oProduto.rendimento;

                    CustoVO oCusto = carregarProduto(oProduto.idProduto, idLoja);
                    oCusto.idProduto = oProduto.idProduto;
                    oCusto.custoComImposto = oProduto.totalCusto;
                    oCusto.custoSemImposto = new CustoDAO().calcularSemImposto(oCusto.custoComImposto, oCusto.icmsCredito, oCusto.pisCofinsCredito, oCusto.valorIcmsSubstituicao, oCusto.valorIpi);
                    oCusto.custoSemPerdaSemImposto = oCusto.custoSemImposto;
                    oCusto.quantidade = 1;
                    oCusto.observacao = "ALTERACAO RECEITA " + oReceita.id;
                    oCusto.idLoja = idLoja;

                    alterar(oCusto);
                }
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    private void alterarProduto(CustoVO i_custo) throws Exception {
        Statement stm = null;
        StringBuilder sql = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            //calcula custo medio
            sql = new StringBuilder();
            sql.append("SELECT customediocomimposto, customediosemimposto, estoque");
            sql.append(" FROM produtocomplemento");
            sql.append(" WHERE id_produto = " + i_custo.idProduto);
            sql.append(" AND id_loja = " + i_custo.idLoja);

            rst = stm.executeQuery(sql.toString());

            if (!rst.next()) {
                throw new VRException("Produto não encontrado!");
            }

            double estoque = 0;

            if (rst.getDouble("estoque") > 0) {
                estoque = rst.getDouble("estoque");
            }

            double custoMedioComImposto = Util.round(((estoque * rst.getDouble("customediocomimposto")) + (i_custo.quantidade * i_custo.custoComImposto)) / (estoque + i_custo.quantidade), 4);
            double custoMedioSemImposto = Util.round(((estoque * rst.getDouble("customediosemimposto")) + (i_custo.quantidade * i_custo.custoSemImposto)) / (estoque + i_custo.quantidade), 4);

            //atualiza custo
            sql = new StringBuilder();
            sql.append("UPDATE produtocomplemento SET");
            sql.append(" custosemimpostoanterior = custosemimposto,");
            sql.append(" custosemimposto = " + i_custo.custoSemImposto + ",");
            sql.append(" custocomimpostoanterior = custocomimposto,");
            sql.append(" custocomimposto = " + i_custo.custoComImposto + ",");
            sql.append(" custosemperdasemimpostoanterior = custosemperdasemimposto,");
            sql.append(" custosemperdasemimposto = " + i_custo.custoSemPerdaSemImposto + ",");
            sql.append(" customediocomimposto = " + custoMedioComImposto + ",");
            sql.append(" customediosemimposto = " + custoMedioSemImposto);
            sql.append(" WHERE id_produto = " + i_custo.idProduto);
            sql.append(" AND id_loja = " + i_custo.idLoja);

            stm.execute(sql.toString());

            new LogCustoDAO().gerarTransacao(i_custo.idProduto, i_custo.observacao, i_custo.idLoja);

            alterarReceita(i_custo.idProduto, i_custo.idLoja);

            alterarKit(i_custo.idProduto, i_custo.idLoja);

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterarKit(int idProduto, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;

        try {
            Conexao.begin();
            stm = Conexao.createStatement();

            rst = stm.executeQuery("SELECT id_kit FROM kititem WHERE id_produto = " + idProduto);

            while (rst.next()) {
                KitVO oKit = new KitDAO().carregar(rst.getLong("id_kit"), idLoja);

                double custoComImposto = 0;
                double custoSemImposto = 0;

                for (KitItemVO oKitItem : oKit.vProduto) {
                    custoComImposto += (oKitItem.quantidade * oKitItem.custoComImposto);
                    custoSemImposto += (oKitItem.quantidade * oKitItem.custoSemImposto);
                }

                CustoVO oCusto = new CustoVO();
                oCusto.idProduto = oKit.idProduto;
                oCusto.custoComImposto = Util.round(custoComImposto, 4);
                oCusto.custoSemImposto = Util.round(custoSemImposto, 4);
                oCusto.custoSemPerdaSemImposto = Util.round(custoSemImposto, 4);
                oCusto.quantidade = 1;
                oCusto.observacao = "KIT " + oKit.idProduto;
                oCusto.idLoja = idLoja;

                alterar(oCusto);
            }

            stm.close();
            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public void alterar(CustoVO i_custo) throws Exception {
        try {
            Conexao.begin();

            if (new ParametroDAO().get(129).getBoolean()) {
                ArrayList<PepsVO> vPeps = new PepsDAO().carregar(i_custo.idProduto, i_custo.idLoja);

                PepsVO oPeps = vPeps.get(0);

                for (int i = vPeps.indexOf(oPeps) + 1; i < vPeps.size(); i++) {
                    oPeps.estoque += vPeps.get(i).estoque;
                    vPeps.remove(i);
                    i--;
                }

                oPeps.custoSemImposto = i_custo.custoSemImposto;
                oPeps.custoComImposto = i_custo.custoComImposto;
                oPeps.custoSemPerdaSemImposto = i_custo.custoSemPerdaSemImposto;
                oPeps.valorIpi = i_custo.valorIpi;
                oPeps.idAliquotaCredito = i_custo.idAliquotaCredito;
                oPeps.valorIcmsSubstituicao = i_custo.valorIcmsSubstituicao;

                new PepsDAO().salvar(vPeps);
            }

            alterarProduto(i_custo);

            Conexao.commit();

        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public CustoVO carregarProduto(int i_idProduto, int idLoja) throws Exception {
        Statement stm = null;
        ResultSet rst = null;
        StringBuilder sql = null;

        stm = Conexao.createStatement();

        LojaVO oLoja = new LojaDAO().carregar(idLoja);

        FornecedorVO oFornecedor = new FornecedorDAO().carregar(oLoja.idFornecedor);

        sql = new StringBuilder();
        sql.append("SELECT pc.precovenda, pc.custocomimposto, pa.id_aliquotacredito, produto.id_tipopiscofinscredito, aliquota.porcentagem,");
        sql.append(" aliquota.reduzido, pc.valoricmssubstituicao, pc.valoripi");
        sql.append(" FROM produto");
        sql.append(" INNER JOIN produtocomplemento pc ON pc.id_produto = produto.id AND pc.id_loja = " + idLoja);
        sql.append(" INNER JOIN produtoaliquota pa ON pa.id_produto = produto.id AND pa.id_estado = " + oFornecedor.idEstado);
        sql.append(" INNER JOIN aliquota ON aliquota.id = pa.id_aliquotacredito");
        sql.append(" WHERE produto.id = " + i_idProduto);

        rst = stm.executeQuery(sql.toString());

        if (!rst.next()) {
            throw new VRException("Produto não encontrado!");
        }

        CustoVO oCusto = new CustoVO();
        oCusto.custoComImposto = rst.getDouble("custocomimposto");
        oCusto.idAliquotaCredito = rst.getInt("id_aliquotacredito");
        oCusto.idTipoPisCofinsCredito = rst.getInt("id_tipopiscofinscredito");
        oCusto.icmsCredito = Util.round(rst.getDouble("porcentagem") * (100 - rst.getDouble("reduzido")) / 100, 2);
        oCusto.precoVenda = rst.getDouble("precovenda");
        oCusto.pisCofinsCredito = new TipoPisCofinsDAO().getPisCofins(rst.getInt("id_tipopiscofinscredito"));
        oCusto.valorIcmsSubstituicao = rst.getDouble("valoricmssubstituicao");
        oCusto.valorIpi = rst.getDouble("valoripi");

        stm.close();

        return oCusto;
    }
    
    public double calcularSemImposto(double i_custoComImposto, double i_icmsCredito, double i_pisCofins, double i_valorIva, double i_valorIpi) throws Exception {
        double custoSemImposto = ((i_custoComImposto - i_valorIva - i_valorIpi) * ((100 - i_icmsCredito - i_pisCofins) / 100)) + i_valorIva + i_valorIpi;
        return Util.round(custoSemImposto, 4);
    }
}
