package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoFornecedor;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Guilherme
 */
public class CervantesDAO extends InterfaceDAO implements MapaTributoProvider {

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	id_dados_empresa id,\n" +
                    "	nome_fantasia fantasia \n" +
                    "from \n" +
                    "	dados_empresa"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("id"), 
                            rst.getString("fantasia")));
                }
            }
        }

        return result;
    }

    @Override
    public String getSistema() {
        return "Cervantes";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.OFERTA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.CEST,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MARGEM,
            OpcaoProduto.QTD_EMBALAGEM_EAN,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.NCM,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.DESCONTINUADO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.RECEITA,
            OpcaoProduto.SECAO,
            OpcaoProduto.PRATELEIRA,
            OpcaoProduto.OFERTA,
            OpcaoProduto.FABRICANTE
        }));
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	p.produto_codigo,\n" +
                    "	ean.codigo_barra,\n" +
                    "	un.descricao unidade\n" +
                    "from \n" +
                    "	prod_cod_barras ean \n" +
                    "join produto p using (id_produto)\n" +
                    "join prod_unidade un on p.id_prod_unidade = un.id_prod_unidade"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produto_codigo"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	distinct coalesce(sit_trib_icms_simples, '0') || ' ' || coalesce(aliquota_icms, 0) id,\n" +
                    "	'cst -> ' || coalesce(aliquota_icms, '0') descricao\n" +
                    "from \n" +
                    "	produto \n" +
                    "where \n" +
                    "	sit_trib_icms_simples is not null"
            )) {
                while (rst.next()) {
                    result.add(
                            new MapaTributoIMP(
                                    rst.getString("id"),
                                    rst.getString("descricao")
                            )
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	p.id_produto,\n" +
                    "	p.produto_codigo,\n" +
                    "	p.produto_balanca,\n" +
                    "	p.descricao,\n" +
                    "	p.descricao_detalhada,\n" +
                    "	p.descricao_resumida,\n" +
                    "	ean.codigo_barra,\n" +
                    "	p.cean ean,\n" +
                    "	p.cean_trib eantrib,\n" +
                    "	ps.id_prod_grupo merc1,\n" +
                    "	p.id_prod_subgrupo merc2,\n" +
                    "	un.descricao unidade,\n" +
                    "	p.inativo,\n" +
                    "	p.qtd_estoque,\n" +
                    "	p.qtd_estoque_minimo,\n" +
                    "	p.cod_ncm ncm,\n" +
                    "	p.cest,\n" +
                    "	p.preco_custo,\n" +
                    "	p.preco_venda,\n" +
                    "	p.sit_trib_pis pisdebito,\n" +
                    "	p.sit_trib_pis_entrada piscredito,\n" +
                    "	p.sit_trib_cofins cofinsdebito,\n" +
                    "	p.sit_trib_cofins_entrada cofinscredito,\n" +
                    "	p.aliquota_icms,\n" +
                    "	p.aliquota_icms_entrada,\n" +
                    "	p.sit_trib_icms,\n" +
                    "	p.sit_trib_icms_entrada,\n" +
                    "	coalesce(sit_trib_icms_simples, '0') || ' ' || coalesce(aliquota_icms, 0) idaliquota,\n" +
                    "	p.sit_trib_icms_simples_entrada\n" +
                    "from \n" +
                    "	produto p\n" +
                    "left join prod_unidade un on p.id_prod_unidade = un.id_prod_unidade\n" +
                    "left join prod_cod_barras ean on p.id_produto = ean.id_produto\n" +
                    "left join prod_subgrupo ps on p.id_prod_subgrupo = ps.id_prod_subgrupo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("produto_codigo"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoGondola(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("descricao_resumida"));
                    
                    if(imp.getDescricaoReduzida() == null || "".equals(imp.getDescricaoReduzida().trim())) {
                        imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    }
                    
                    imp.seteBalanca(rst.getBoolean("produto_balanca"));
                    imp.setEan(rst.getString("codigo_barra"));
                    imp.setEstoqueMinimo(rst.getDouble("qtd_estoque_minimo"));
                    imp.setEstoque(rst.getDouble("qtd_estoque"));

                    imp.setCustoSemImposto(rst.getDouble("preco_custo"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    //imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setSituacaoCadastro(rst.getBoolean("inativo") == true ? 0 : 1);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("pisdebito"));
                    imp.setPiscofinsCstCredito(rst.getString("cofinscredito"));

                    imp.setIcmsConsumidorId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoId(rst.getString("idaliquota"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idaliquota"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	f.id_fornecedor,\n" +
                    "	f.fornecedor_codigo,\n" +
                    "	f.cpf_cnpj,\n" +
                    "	f.insc_estadual,\n" +
                    "	f.inativo,\n" +
                    "	f.razao_social,\n" +
                    "	f.nome_fantasia,\n" +
                    "	f.logradouro,\n" +
                    "	f.bairro,\n" +
                    "	c.descricao cidade,\n" +
                    "	c.sigla_estado uf,\n" +
                    "	f.cep,\n" +
                    "	f.numero,\n" +
                    "	f.complemento,\n" +
                    "	f.obs,\n" +
                    "	f.email,\n" +
                    "	f.fone,\n" +
                    "	f.contato,\n" +
                    "	f.suframa\n" +
                    "from \n" +
                    "	fornecedor f\n" +
                    "left join cidade c using (id_cidade)"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("fornecedor_codigo"));
                    imp.setRazao(rst.getString("razao_social"));
                    imp.setFantasia(rst.getString("nome_fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpf_cnpj"));
                    imp.setIe_rg(rst.getString("insc_estadual"));
                    imp.setAtivo(rst.getBoolean("inativo") == false);
                    imp.setEndereco(rst.getString("logradouro"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone"));
                    imp.addEmail("EMAIL", rst.getString("email"), TipoContato.NFE);
                    imp.addContato(rst.getString("contato"), null, null, TipoContato.NFE, null);
                    imp.setObservacao(rst.getString("obs"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	p.produto_codigo idproduto,\n" +
                    "	f.fornecedor_codigo idfornecedor,\n" +
                    "	pf.codigo codigoexterno,\n" +
                    "	pf.unidade,\n" +
                    "	pf.qtd_produto qtd\n" +
                    "from \n" +
                    "	produto_fornecedor pf \n" +
                    "join produto p on pf.id_produto = p.id_produto\n" +
                    "join fornecedor f on pf.id_fornecedor = f.id_fornecedor\n" +
                    "order by \n" +
                    "	2, 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtd"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
