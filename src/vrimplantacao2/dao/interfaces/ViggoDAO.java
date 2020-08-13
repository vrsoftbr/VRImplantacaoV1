package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoItemIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
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
                OpcaoProduto.ICMS,
                OpcaoProduto.OFERTA
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

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (
                Statement st = ConexaoPostgres.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        "select\n" +
                        "	p.codigo id,\n" +
                        "	p.cpf_cnpj cnpj,\n" +
                        "	p.insc_estadual ie,\n" +
                        "	p.nome razao,\n" +
                        "	coalesce(nullif(p.nome_fantasia,''), p.nome) fantasia,\n" +
                        "	p.inativo,\n" +
                        "	p.negativado,\n" +
                        "	p.endereco,\n" +
                        "	p.numero,\n" +
                        "	p.complemento,\n" +
                        "	p.bairro,\n" +
                        "	c.codigo_municipio ibge_municipio,\n" +
                        "	p.cep,\n" +
                        "	p.data_nascimento,\n" +
                        "	p.data_cadastro,\n" +
                        "	p.nome_pai,\n" +
                        "	p.nome_mae,\n" +
                        "	p.observacao,\n" +
                        "	p.contribuinte_icms,\n" +
                        "	p.limite_credito\n" +
                        "from\n" +
                        "	participante p\n" +
                        "	left join cidade c on\n" +
                        "		p.codigo_cidade = c.codigo\n" +
                        "where\n" +
                        "	p.tipo_participante = 0 and\n" +
                        "	not p.codigo in (0, 1)\n" +
                        "order by\n" +
                        "	p.codigo"
                )
        ) {
            while (rs.next()) {
                ClienteIMP imp = new ClienteIMP();
                
                imp.setId(rs.getString("id"));
                imp.setCnpj(rs.getString("cnpj"));
                imp.setInscricaoestadual(rs.getString("ie"));
                imp.setRazao(rs.getString("razao"));
                imp.setFantasia(rs.getString("fantasia"));
                imp.setAtivo(!rs.getBoolean("inativo"));
                imp.setBloqueado(rs.getBoolean("negativado"));
                imp.setEndereco(rs.getString("endereco"));
                imp.setNumero(rs.getString("numero"));
                imp.setComplemento(rs.getString("complemento"));
                imp.setBairro(rs.getString("bairro"));
                imp.setMunicipioIBGE(rs.getInt("ibge_municipio"));
                imp.setCep(rs.getString("cep"));
                imp.setDataNascimento(rs.getDate("data_nascimento"));
                imp.setDataCadastro(rs.getDate("data_cadastro"));
                imp.setNomePai(rs.getString("nome_pai"));
                imp.setNomeMae(rs.getString("nome_mae"));
                imp.setObservacao2(rs.getString("observacao"));
                if (rs.getBoolean("contribuinte_icms")) {
                    imp.setTipoIndicadorIe(TipoIndicadorIE.CONTRIBUINTE_ICMS);
                } else {
                    imp.setTipoIndicadorIe(TipoIndicadorIE.NAO_CONTRIBUINTE);                
                }
                imp.setValorLimite(rs.getDouble("limite_credito"));
                
                result.add(imp);
            }
        }
        
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoPostgres.getConexao().createStatement()) {
            Map<String, List<CreditoRotativoItemIMP>> pagamentos = new HashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	p.numero_conta_receber,\n" +
                    "	p.numero_parcela,\n" +
                    "	p.valor,\n" +
                    "	p.desconto,\n" +
                    "	p.juros,\n" +
                    "	p.data_hora,\n" +
                    "	p.observacao\n" +
                    "from\n" +
                    "	pagto_conta_receber p\n" +
                    "where\n" +
                    "	p.cancelado is null\n" +
                    "order by\n" +
                    "	p.numero_conta_receber,\n" +
                    "	p.numero_parcela"
            )) {
                while (rs.next()) {
                    final String id = rs.getString("numero_conta_receber") + "-" + rs.getString("numero_parcela");
                    List<CreditoRotativoItemIMP> pag = pagamentos.get(id);
                    if (pag == null) {
                        pag = new ArrayList<>();
                        pagamentos.put(id, pag);
                    }
                    CreditoRotativoItemIMP imp = new CreditoRotativoItemIMP();
                    
                    imp.setId(id);
                    imp.setValor(rs.getDouble("valor"));
                    imp.setDesconto(rs.getDouble("desconto"));
                    imp.setMulta(rs.getDouble("juros"));
                    imp.setDataPagamento(rs.getDate("data_hora"));
                    imp.setObservacao(rs.getString("observacao"));
                    
                    pag.add(imp);
                }
            }
            
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	pcr.numero_conta_receber,\n" +
                    "	pcr.numero,\n" +
                    "	cr.\"data\" emissao,\n" +
                    "	cr.numero_cupom,\n" +
                    "	substring(cr.numero_doc,3,1) ecf,\n" +
                    "	pcr.valor,\n" +
                    "	pcr.observacao,\n" +
                    "	cr.codigo_participante id_cliente,\n" +
                    "	pcr.vencimento,\n" +
                    "	pcr.numero parcela,\n" +
                    "	pcr.juros\n" +
                    "from\n" +
                    "	parcela_conta_receber pcr\n" +
                    "	join conta_receber cr on\n" +
                    "		pcr.numero_conta_receber = cr.numero\n" +
                    "order by\n" +
                    "	1, 2"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    final String id = rs.getString("numero_conta_receber") + "-" + rs.getString("numero");
                    
                    imp.setId(id);
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setNumeroCupom(rs.getString("numero_cupom"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setJuros(rs.getDouble("juros"));
                    
                    List<CreditoRotativoItemIMP> pag = pagamentos.get(id);
                    if (pag != null) {
                        for (CreditoRotativoItemIMP pg: pag) {
                            pg.setCreditoRotativo(imp);
                            imp.getPagamentos().add(pg);
                        }
                    }
                    
                    result.add(imp);
                }
            }
        }
        
        return result;        
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	i.codigo_produto id_produto,\n" +
                    "	p.de datainicio,\n" +
                    "	p.ate datafim,\n" +
                    "	i.valor_antigo preconormal,\n" +
                    "	i.valor_novo precooferta\n" +
                    "from\n" +
                    "	item_promocao_produto i\n" +
                    "	join promocao_produto p on\n" +
                    "		i.numero_promocao_produto = p.numero\n" +
                    "where\n" +
                    "	p.cancelada is null"
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    
                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
        
}
