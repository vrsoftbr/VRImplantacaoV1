/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces.unificacao.primeiropreco;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrframework.classe.Conexao;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class PrimeiroPrecoDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Primeiro Preco";
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct "
                    + "loja as id, "
                    + "'PRIMEIRO PRECO LJ01' as descricao "
                    + "from implantacao.produtos_primeiro_preco_loja01")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ATIVO,
                OpcaoProduto.ICMS,
                OpcaoProduto.NCM,
                OpcaoProduto.MAPA_TRIBUTACAO
        ));
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ok.codigo_interno as codigo_interno_ok,\n"
                    + "	ok.codigo_barras as codigo_barras_ok,\n"
                    + "	ok.descricao_produto descricao_produto_ok,\n"
                    + "	lj01.codigo as codigo_lj01,\n"
                    + "	lj01.descricao_produto as descricao_produto_lj01,\n"
                    + "	lj01.custo_unitario as custo,\n"
                    + "	lj01.pct_margem as margem,\n"
                    + "	lj01.preco_venda,\n"
                    + "	lj01.produto_ativo,\n"
                    + "	case lj01.produto_ativo when 'S'then 1 \n"
                    + "		else 0 end situacao_cadastro,\n"
                    + "	icm_s.ncm as ncm,\n"
                    + "    icm_s.cst_sac,\n"
                    + "    icm_s.aliquota_sac,\n"
                    + "    icm_s.reducao_sac,\n"
                    + "    icm_s.cst_sas,\n"
                    + "    icm_s.aliquota_sas,\n"
                    + "    icm_s.reducao_sas,\n"
                    + "    icm_s.cst_svc,\n"
                    + "    icm_s.aliquota_svc,\n"
                    + "    icm_s.reducao_svc,\n"
                    + "    icm_s.cst_snc,\n"
                    + "    icm_s.aliquota_snc,\n"
                    + "    icm_s.reducao_snc,\n"
                    + "    icm_s.trib_pdv,\n"
                    + "    icm_s.descricao_produto	\n"
                    + "from implantacao.posicao_estoque_ok	ok\n"
                    + "join implantacao.produtos_primeiro_preco_loja01 lj01\n"
                    + "	on lj01.codigo = ok.codigo_interno\n"
                    + "left join implantacao.produtos_primeiropreco_icmssaida_loja01 icm_s\n"
                    + "	on ok.codigo_barras = icm_s.ean\n"
                    + "where lj01.loja = '" + getLojaOrigem() + "' \n"
                    + "and lj01.produto_ativo = 'S'\n"        
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo_interno_ok"));
                    imp.setEan(rst.getString("codigo_barras_ok"));
                    imp.setDescricaoCompleta(rst.getString("descricao_produto_ok"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setSituacaoCadastro(rst.getInt("situacao_cadastro"));
                    imp.setMargem(Double.parseDouble(rst.getString("margem").replace(".", "").replace(",", ".")));
                    imp.setCustoComImposto(Double.parseDouble(rst.getString("custo").replace(".", "").replace(",", ".")));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(Double.parseDouble(rst.getString("preco_venda").replace(".", "").replace(",", ".")));
                    imp.setNcm(rst.getString("ncm"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ESTOQUE) {
            try (Statement stm = Conexao.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select "
                        + "codigo_interno, "
                        + "quantidade "
                        + "from implantacao.posicao_estoque"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codigo_interno"));
                        imp.setEstoque(Double.parseDouble(rst.getString("quantidade").replace(".", "").replace(",", ".")));
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        return null;
    }
}
