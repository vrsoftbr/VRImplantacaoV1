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
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class ScefDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(ScefDAO.class.getName());

    @Override
    public String getSistema() {
        return "Scef";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "e.empcodigo codigo,\n"
                    + "(trim(e.empnome)||' - LOCAL '||l.loccodigo||' '||l.locnome) descricao,\n"
                    + "l.loccodigo,\n"
                    + "l.locnome\n"
                    + "from local l\n"
                    + "inner join empresa e on e.empcodigo = l.empcodigo\n"
                    + "order by loccodigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("loccodigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.procodigo,\n"
                    + "b.codbarra,\n"
                    + "e.prequantidade,\n"
                    + "p.prodescricao,\n"
                    + "p.prodesc_imp_fiscal,\n"
                    + "p.propeso_unidade,\n"
                    + "p.propreco_custo_sem_icms custocomimposto,\n"
                    + "p.propreco_custo_com_icms custosemimposto,\n"
                    + "p.propreco_custo_medio_cicms,\n"
                    + "p.propreco_custo_medio_sicms,\n"
                    + "p.propreco_venda precovenda,\n"
                    + "p.promargem,\n"
                    + "p.proinclusao,\n"
                    + "p.probalanca,\n"
                    + "case p.status when 'A' then '1' else '0' end status,\n"
                    + "p.embcodigo,\n"
                    + "p.embcodigo_unidade,\n"
                    + "p.proicms,\n"
                    + "p.proreducao_base_icms,\n"
                    + "p.prodiasvencimento,\n"
                    + "p.pro_sit_trib_nf,\n"
                    + "p.proexclusao,\n"
                    + "p.proncm,\n"
                    + "p.stpcodigo,\n"
                    + "p.stpcodigo_entrada,\n"
                    + "p.stccodigo,\n"
                    + "p.stccodigo_entrada,\n"
                    + "c.cestchave,\n"
                    + "s.prosit_tributaria sit_trib,\n"
                    + "p.prosit_tributaria sit_prod,\n"
                    + "s.pstdescricao sit_descricao\n"
                    + "from produto p\n"
                    + "left join cest c on c.cestcodigo = p.cestcodigo\n"
                    + "left join prosituacao_tributaria s on s.prosit_tributaria = p.prosit_tributaria\n"
                    + "left join proembala e on e.procodigo = p.procodigo\n"
                    + "left join procodbarra b on b.precodigo = e.precodigo\n"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("procodigo"));
                    imp.setEan(rst.getString("codbarra"));
                    imp.seteBalanca((!"N".equals(rst.getString("probalanca"))));
                    imp.setSituacaoCadastro(rst.getDate("proexclusao") != null ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setTipoEmbalagem(rst.getString("embcodigo"));
                    imp.setQtdEmbalagem(rst.getInt("prequantidade"));
                    imp.setDataCadastro(rst.getDate("proinclusao"));
                    imp.setDescricaoCompleta(rst.getString("prodescricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setMargem(rst.getDouble("promargem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setPiscofinsCstDebito(rst.getString("stpcodigo"));
                    imp.setPiscofinsCstCredito(rst.getString("stpcodigo_entrada"));
                    imp.setNcm(rst.getString("proncm"));
                    imp.setCest(rst.getString("cestchave"));
                    imp.setIcmsCst(rst.getInt("pro_sit_trib_nf"));
                    imp.setIcmsAliq(rst.getDouble("proicms"));
                    imp.setIcmsReducao(rst.getDouble("proreducao_base_icms"));
                    result.add(imp);
                }
            }
        }
        LOG.getName();
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "procodigo, \n"
                        + "estatual\n"
                        + "from estoque\n"
                        + "where loccodigo = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("procodigo"));
                        imp.setEstoque(rst.getDouble("estatual"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.pescodigo,\n"
                    + "f.pesnome,\n"
                    + "f.pesapelido,\n"
                    + "f.pesendereco,\n"
                    + "f.pesendereco_numero,\n"
                    + "f.pesendereco_complemento,\n"
                    + "f.pesbairro,\n"
                    + "f.pescidade,\n"
                    + "f.pesuf, \n"
                    + "f.pesfone, \n"
                    + "f.pesfax, \n"
                    + "f.pesemail,\n"
                    + "f.pescep,\n"
                    + "f.pesobservacao,\n"
                    + "f.pesdatainclusao\n"
                    + "FROM PESSOA f\n"
                    + "WHERE f.PESFORNECEDOR = 'S'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("pescodigo"));
                    imp.setRazao(rst.getString("pesnome"));
                    imp.setFantasia(rst.getString("pesapelido"));
                    imp.setEndereco(rst.getString("pesendereco"));
                    imp.setNumero(rst.getString("pesendereco_numero"));
                    imp.setComplemento(rst.getString("pesendereco_complemento"));
                    imp.setBairro(rst.getString("pesbairro"));
                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "pescodigo,\n"
                    + "procodigo,\n"
                    + "ultatualizacao,\n"
                    + "forxproreferencia\n"
                    + "from fornecedorxproduto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("procodigo"));
                    imp.setIdFornecedor(rst.getString("pescodigo"));
                    imp.setCodigoExterno(rst.getString("forxproreferencia"));
                    imp.setDataAlteracao(rst.getDate("ultatualizacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
