package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class InovaDAO extends InterfaceDAO {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento != null ? complemento.trim() : "";
    }
    
    
    @Override
    public String getSistema() {
        if ("".equals(complemento)) {
            return "Inova";
        } else {
            return "Inova - " + complemento;
        }
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS
        ));
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.produtoid id,\n" +
                    "	p.produtodatacadastro datacadastro,\n" +
                    "	p.produtodataultimaalteracao dataalteracao,\n" +
                    "	p.produtocodigobarra ean,\n" +
                    "	p.produtopesavel pesavel,\n" +
                    "	p.produtotipo,\n" +
                    "	p.produtounitprodutoqtd qtdembalagem,\n" +
                    "	p.produtounidademedida unidade,\n" +
                    "	p.produtodiasvalidade validade,\n" +
                    "	coalesce(nullif(trim(p.produtodescricaodetalhada),''), p.produtodescricao) descricaocompleta,\n" +
                    "	p.produtodescricao descricaoreduzida,\n" +
                    "	p.produtocategoriaid mercadologico1,\n" +
                    "	p.produtofamiliaid id_familia,\n" +
                    "	p.produtoqtdestoquemax estoquemaximo,\n" +
                    "	p.produtoqtdestoquemin estoqueminimo,\n" +
                    "	p.produtoqtdestoque estoque,\n" +
                    "	p.produtolucroporcento margem,\n" +
                    "	p.produtovalorcompra custosemimposto,\n" +
                    "	p.produtovalorfinal custocomimposto,\n" +
                    "	p.produtovalorvenda precovenda,\n" +
                    "	p.produtostatus ativo,\n" +
                    "	p.produtoncm ncm,\n" +
                    "	p.produtocest cest,\n" +
                    "	p.produtopiscst pis_cst,\n" +
                    "	p.produtoimcscst icms_cst,\n" +
                    "	p.produtoicms icms_aliquota,\n" +
                    "	p.produtoicmsredbcalc icms_reduzido\n" +
                    "from\n" +
                    "	produtos p\n" +
                    "order by\n" +
                    "	1"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
                    
                    ProdutoBalancaVO bal = balanca.get(Utils.stringToInt(rst.getString("ean"), -2));
                    if (bal != null) {
                        imp.setEan(bal.getCodigo() + "");
                        imp.setQtdEmbalagem(1);
                        imp.seteBalanca(true);
                        imp.setValidade(imp.getValidade());
                        switch (bal.getPesavel()) {
                            case "U": imp.setTipoEmbalagem("UN"); break;
                            default : imp.setTipoEmbalagem("KG"); break;
                        }
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                        imp.seteBalanca(rst.getBoolean("pesavel"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                    }
                    
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(rst.getBoolean("ativo") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(rst.getString("pis_cst"));
                    imp.setPiscofinsCstDebito(rst.getString("pis_cst"));
                    imp.setIcmsCst(rst.getString("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojas() {
        return Arrays.asList(new Estabelecimento("1", "LOJA"));
    }
    
}
