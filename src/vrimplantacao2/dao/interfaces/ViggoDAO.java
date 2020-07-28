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
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class ViggoDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";

    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "VIGGO" + (!complemento.equals("") ? " - " + complemento : "");
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select codigo, nome, cnpj from empresa order by 1"
                )
        ) {
            while (rs.next()) {
                result.add(new Estabelecimento(rs.getString("codigo"), rs.getString("nome") + " - " + rs.getString("cnpj")));
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MAPA_TRIBUTACAO,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.DATA_ALTERACAO,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS
        ));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	distinct\n" +
                        "	g.codigo merc1,\n" +
                        "	g.nome merc1_desc,\n" +
                        "	sg.codigo merc2,\n" +
                        "	sg.nome merc2_desc\n" +
                        "from\n" +
                        "	produto p\n" +
                        "	join grupo g on\n" +
                        "		p.codigo_grupo = g.codigo\n" +
                        "	left join subgrupo sg on\n" +
                        "		p.codigo_subgrupo = sg.codigo\n" +
                        "order by\n" +
                        "	g.codigo"
                )
        ) {
            while (rs.next()) {
                MercadologicoIMP imp = new MercadologicoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(rs.getString("merc1"));
                imp.setMerc1Descricao(rs.getString("merc1_desc"));
                //imp.setMerc2ID(rs.getString("merc2"));
                //imp.setMerc2Descricao(rs.getString("merc2_desc"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "with eans as (\n" +
                        "	select\n" +
                        "		ean.codigo_produto id_produto,\n" +
                        "		nullif(trim(ean.codigo_barra),'') ean,\n" +
                        "		case coalesce(ean.qtd_embalagem, 1)\n" +
                        "			when 0 then 1\n" +
                        "			else coalesce(ean.qtd_embalagem, 1)\n" +
                        "		end qtd_embalagem\n" +
                        "	from\n" +
                        "		produto_codigo_barras ean\n" +
                        "	where\n" +
                        "		not nullif(trim(ean.codigo_barra),'') is null\n" +
                        "),\n" +
                        "prod_eans as (\n" +
                        "	select\n" +
                        "		p.codigo id_produto,\n" +
                        "		nullif(trim(p.codigo_barra),'') ean,\n" +
                        "		case coalesce(p.qtd_embalagem, 1)\n" +
                        "			when 0 then 1\n" +
                        "			else coalesce(p.qtd_embalagem, 1)\n" +
                        "		end qtd_embalagem\n" +
                        "	from\n" +
                        "		produto p\n" +
                        "	where\n" +
                        "		not nullif(trim(p.codigo_barra),'') is null\n" +
                        "),\n" +
                        "u_eans as (\n" +
                        "	select * from eans\n" +
                        "	union\n" +
                        "	select * from prod_eans\n" +
                        ")\n" +
                        "select\n" +
                        "	p.codigo id,\n" +
                        "	coalesce(p.data_cadastro, p.data_zerou, p.data_exportado) data_cadastro,\n" +
                        "	coalesce(p.data_zerou, p.data_exportado) data_alteracao,\n" +
                        "	ean.ean,\n" +
                        "	ean.qtd_embalagem,	\n" +
                        "	p.sigla_unidade unidade,\n" +
                        "	p.fracionado e_pesavel,\n" +
                        "	p.dias_validade	validade,\n" +
                        "	p.descricao descricaocompleta,\n" +
                        "	p.descricao_fiscal descricaoreduzida,\n" +
                        "	p.codigo_grupo merc1,\n" +
                        "	p.codigo_subgrupo merc2,\n" +
                        "	p.peso_bruto,\n" +
                        "	p.peso_liquido,\n" +
                        "	p.qtd_minima estoqueminima,\n" +
                        "	p.qtd_maxima estoquemaximo,\n" +
                        "	p.quantidade estoque,\n" +
                        "	p.margem_lucro,\n" +
                        "	p.preco_custo custocomimposto,\n" +
                        "	p.preco_compra custosemimposto,\n" +
                        "	p.valor precovenda,\n" +
                        "	p.inativo,\n" +
                        "	p.ncm,\n" +
                        "	p.cest,\n" +
                        "	p.cst_pis,\n" +
                        "	p.codigo_aliquota id_icms_saida,\n" +
                        "	p.codigo_aliquota_entrada id_icms_entrada\n" +
                        "from\n" +
                        "	produto p\n" +
                        "	left join u_eans ean on\n" +
                        "		ean.id_produto = p.codigo\n" +
                        "order by\n" +
                        "	id, ean"
                )
        ) {
            while (rs.next()) {
                ProdutoIMP imp = new ProdutoIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setDataCadastro(rs.getDate("data_cadastro"));
                imp.setDataAlteracao(rs.getDate("data_alteracao"));
                imp.setEan(rs.getString("ean"));
                imp.setQtdEmbalagem(rs.getInt("qtd_embalagem"));
                imp.setTipoEmbalagem(rs.getString("unidade"));
                imp.seteBalanca(rs.getBoolean("e_pesavel"));
                imp.setValidade(rs.getInt("validade"));
                imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                imp.setCodMercadologico1(rs.getString("merc1"));
                imp.setCodMercadologico2(rs.getString("merc2"));
                imp.setPesoBruto(rs.getDouble("peso_bruto"));
                imp.setPesoLiquido(rs.getDouble("peso_liquido"));
                imp.setEstoqueMinimo(rs.getDouble("estoqueminima"));
                imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                imp.setEstoque(rs.getDouble("estoque"));
                imp.setMargem(rs.getDouble("margem_lucro"));
                imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                imp.setPrecovenda(rs.getDouble("precovenda"));
                imp.setSituacaoCadastro(rs.getInt("inativo") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                imp.setNcm(rs.getString("ncm"));
                imp.setCest(rs.getString("cest"));
                imp.setPiscofinsCstCredito(rs.getString("cst_pis"));
                imp.setPiscofinsCstDebito(rs.getString("cst_pis"));
                imp.setIcmsDebitoId(rs.getString("id_icms_saida"));
                imp.setIcmsDebitoForaEstadoId(rs.getString("id_icms_saida"));
                imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_icms_saida"));
                imp.setIcmsConsumidorId(rs.getString("id_icms_saida"));
                imp.setIcmsCreditoId(rs.getString("id_icms_entrada"));
                imp.setIcmsCreditoForaEstadoId(rs.getString("id_icms_entrada"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	a.codigo id,\n" +
                        "	a.descricao,\n" +
                        "	a.aliquota_ecf,\n" +
                        "	a.situacao_tributaria cst,\n" +
                        "	a.aliquota_icms_estado aliquota,\n" +
                        "	a.aliquota_reducao reducao\n" +
                        "from\n" +
                        "	aliquota a\n" +
                        "order by\n" +
                        "	a.codigo"
                )
        ) {
            while (rs.next()) {
                result.add(new MapaTributoIMP(
                        rs.getString("id"),
                        rs.getString("descricao") + " - " + rs.getString("aliquota_ecf"),
                        rs.getInt("cst"),
                        rs.getDouble("aliquota"),
                        rs.getDouble("reducao")
                ));
            }
        }
        
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	f.codigo id,\n" +
                        "	f.nome razao,\n" +
                        "	coalesce(nullif(trim(f.nome_fantasia),''), f.nome) fantasia,\n" +
                        "	f.cpf_cnpj cnpj,\n" +
                        "	f.insc_estadual ie,\n" +
                        "	f.insc_suframa suframa,\n" +
                        "	f.inativo,\n" +
                        "	f.endereco,\n" +
                        "	f.numero,\n" +
                        "	f.complemento,\n" +
                        "	f.bairro,\n" +
                        "	c.codigo_municipio ibge_municipio,\n" +
                        "	f.cep,\n" +
                        "	f.data_cadastro,\n" +
                        "	f.observacao,\n" +
                        "	f.opt_simples simples,\n" +
                        "	f.contribuinte_icms\n" +
                        "from\n" +
                        "	participante f\n" +
                        "	left join cidade c on\n" +
                        "		f.codigo_cidade = c.codigo \n" +
                        "where\n" +
                        "	f.tipo_participante = 1\n" +
                        "order by\n" +
                        "	f.codigo "
                )
        ) {
            while (rs.next()) {
                FornecedorIMP imp = new FornecedorIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rs.getString("id"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setCnpj_cpf(rs.getString("cnpj"));
                imp.setIe_rg(rs.getString("ie"));
                imp.setSuframa(rs.getString("suframa"));
                imp.setAtivo(!rs.getBoolean("inativo"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                imp.setCep(rs.getString("cep"));
                imp.setDatacadastro(rs.getDate("data_cadastro"));
                imp.setObservacao(rs.getString("observacao"));
                if (rs.getBoolean("simples")) {
                    imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                } else {
                    if (rs.getBoolean("contribuinte_icms")) {
                        imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ICMS);
                    } else {
                        imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);
                    }                    
                }
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        
        /*try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        ""
                )
        ) {
            while (rs.next()) {
                ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setIdFornecedor(rs.getString(""));
                imp.set(rs.getString(""));
                imp.set(rs.getString(""));
                imp.set(rs.getString(""));
                
                result.add(imp);
            }
        }*/
        
        return result;
    }
    
}
