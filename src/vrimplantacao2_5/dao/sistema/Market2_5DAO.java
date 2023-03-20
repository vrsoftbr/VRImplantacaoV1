package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Bruno
 */
public class Market2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Market";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_PRODUTO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.RECEITA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CLIENTE_EVENTUAL
        ));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	distinct trim(ds_icms) as id,\n"
                    + "	coalesce (ds_icms, 'NULO') descricao,\n"
                    + "	case\n"
                    + "		when ds_icms = 'IS' then '40'\n"
                    + "		when ds_icms = '07' then '00'\n"
                    + "		when ds_icms = '12' then '00'\n"
                    + "		when ds_icms = '17' then '00'\n"
                    + "		when ds_icms = '25' then '00'\n"
                    + "		when TRIM(ds_icms) = 'ST' then '60'\n"
                    + "		when ds_icms = 'NT' then '41'\n"
                    + "		when ds_icms is null then '40'\n"
                    + "		else '40'\n"
                    + "	end as cst,\n"
                    + "	case ds_icms\n"
                    + "		when 'IS' then 0\n"
                    + "		when '07' then 7\n"
                    + "		when '12' then 12\n"
                    + "		when '17' then 17\n"
                    + "		when '25' then 25\n"
                    + "		when 'ST' then 0\n"
                    + "		when 'NT' then 0\n"
                    + "		else 0\n"
                    + "	end as aliq,\n"
                    + "		0 as red\n"
                    + "from\n"
                    + "	produto.tb_produto_loja tpl\n"
                    + "where\n"
                    + "	cd_loja = " + getLojaOrigem() + "\n"
                    + "order by 1 "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliq"),
                            rs.getDouble("red"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	dep.cd_depto as merc1,\n"
                    + "	dep.nm_depto as descmerc1,\n"
                    + "	sec.cd_depto_secao as merc2,\n"
                    + "	sec.nm_depto_secao as descmerc2,\n"
                    + "	coalesce(gru.cd_depto_grupo, 1) as merc3,\n"
                    + "	coalesce(gru.nm_depto_grupo, sec.nm_depto_secao) as descmerc3\n"
                    + "from\n"
                    + "	produto.tb_depto dep\n"
                    + "left join\n"
                    + "	produto.tb_depto_secao sec on sec.cd_depto = dep.cd_depto\n"
                    + "left join\n"
                    + "	produto.tb_depto_grupo gru on gru.cd_depto_secao = sec.cd_depto_secao\n"
                    + "order by\n"
                    + "	dep.cd_depto, sec.cd_depto_secao")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cd_produto_semelhante as id,\n"
                    + "	nm_produto_semelhante as nomeproduto,\n"
                    + "	dt_inc as dtcadastro\n"
                    + "from \n"
                    + "	produto.tb_produto_semelhante \n"
                    + "order by \n"
                    + "	nm_produto_semelhante")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("nomeproduto"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	ean.cd_produto id_produto,\n"
                    + "	ean.cd_codbarra ean,\n"
                    + "	p.tp_embalagem tipo_emb,\n"
                    + "	p.qt_embalagem  / p.qt_fracionado qtd_emb \n"
                    + "from\n"
                    + "	produto.tb_produto_codbarra ean\n"
                    + "	join produto.tb_produto p on p.cd_produto = ean.cd_produto\n"
                    + " where ean.is_padrao = 'S' "
                    + "order by ean.cd_produto"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtd_emb"));
                    imp.setTipoEmbalagem(rs.getString("tipo_emb"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select  \n"
                    + "	tp.nr_produto id,\n"
                    + "	tp.nm_produto_longo desc_completa,\n"
                    + "	tp.nm_reduzido desc_reduzida,\n"
                    + "	cd.cd_codbarra ean,\n"
                    + "	custo.vl_custo_faturado custosemimposto,\n"
                    + "	custo.vl_custo custocomimposto,\n"
                    + "	custo.vl_venda precovenda,\n"
                    + "	custo.per_margem_venda margem,\n"
                    + " custo.qt_minimo est_min,\n"
                    + "	est.qt_saldo estoque,\n"
                    + "	tp.cd_depto codmerc1,\n"
                    + " tp.cd_produto_semelhante as idfamilia,\n"
                    + "	tp.cd_depto_secao codmerc2,\n"
                    + "	tp.cd_depto_grupo codmerc3,\n"
                    + "	tp.tp_embalagem tipo_embalagem,\n"
                    + "	case \n"
                    + "	when tp.tp_unidade_medida = 'M2' then 'MT'	\n"
                    + "	when tp.tp_unidade_medida = 'CE' then 'MT'\n"
                    + "	when tp.tp_unidade_medida = 'GR' then 'KG'\n"
                    + "	when tp.tp_unidade_medida = 'ML' then 'LT'\n"
                    + "	else 'UN' end as tipo_unidade_medida,\n"
                    + "	tp.qt_embalagem qtde_emb,\n"
                    + "	coalesce (pb.qt_dias_validade_balanca, '0') validadebalanca,\n"
                    + "	case when tp.is_excluido = 'N' then 1 else 0\n"
                    + "	end as ativo,\n"
                    + "	tp.dt_inc data_cadastro,\n"
                    + "	tp.vl_peso_liquido pesoliquido,\n"
                    + "	tp.vl_peso_bruto pesobruto,\n"
                    + "	(select \n"
                    + "		f.nr_ncm\n"
                    + "	from\n"
                    + "		produto.tb_ncm_figura_vigencia_federal f\n"
                    + "	where \n"
                    + "		f.cd_ncm_figura_mva = tp.cd_ncm_figura_mva limit 1) as ncm,\n"
                    + "		(select \n"
                    + "		f.nr_cest\n"
                    + "	from\n"
                    + "		produto.tb_ncm_figura_vigencia_federal f\n"
                    + "	where \n"
                    + "		f.cd_ncm_figura_mva = tp.cd_ncm_figura_mva limit 1) as cest,\n"
                    + "	trim(custo.ds_icms) as id_icms,\n"
                    + "	tp_unidade_medida  tipovolume,\n"
                    + "	qt_unidade_medida  volume,\n"
                    + "	case \n"
                    + "	when tp.tp_venda = 'B' then 1 else 0 end is_balanca \n"
                    + "from\n"
                    + "	produto.tb_produto tp\n"
                    + "left join produto.tb_produto_codbarra cd on cd.cd_produto = tp.cd_produto\n"
                    + "left join produto.tb_produto_loja custo on custo.cd_produto = tp.cd_produto and custo.cd_loja = 1\n"
                    + "left join saldo.vw_saldo_loja est on est.nr_produto = tp.nr_produto and nr_loja = 1\n"
                    + "left join produto.tb_produto_balanca pb on pb.cd_produto = tp.cd_produto  and  pb.cd_loja = 1\n"
                    + "where cd.is_padrao = 'S'"
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));

                    imp.seteBalanca(rs.getBoolean("is_balanca"));
                    imp.setEan(rs.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade() > 1
                                ? bal.getValidade() : rs.getInt("validadebalanca"));
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rs.getString("desc_completa"));
                    imp.setDescricaoReduzida(rs.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setTipoEmbalagem(rs.getString("tipo_embalagem"));
//                    imp.setTipoEmbalagemCotacao(rs.getString("emb_compra"));
                    imp.setQtdEmbalagem(rs.getInt("qtde_emb"));
                    imp.setVolume(rs.getDouble("volume") / 1000);
                    imp.setTipoEmbalagemVolume(rs.getString("tipovolume"));

                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));

                    imp.setEstoqueMinimo(rs.getDouble("est_min"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setIdFamiliaProduto(rs.getString("idfamilia"));

                    imp.setCodMercadologico1(rs.getString("codmerc1"));
                    imp.setCodMercadologico2(rs.getString("codmerc2"));
                    imp.setCodMercadologico3(rs.getString("codmerc3"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    String idIcms;

                    idIcms = rs.getString("id_icms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    //          imp.setPiscofinsCstCredito(rs.getString("piscofins"));
                    //          imp.setPiscofinsCstDebito(rs.getString("piscofins"));
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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	c.cd_base id,\n"
                    + "	nm_base razao,\n"
                    + "	case when nm_fantasia is null then nm_base else nm_fantasia end fantasia,\n"
                    + "	nr_cpf_cnpj cnpj,\n"
                    + "	ie.ds_documento ie,\n"
                    + "	e.nm_logradouro endereco,\n"
                    + "	nr_endereco numero,\n"
                    + "	ds_complemento complemento,\n"
                    + "	b.nm_bairro bairro,\n"
                    + "	m.nm_cidade cidade,\n"
                    + "	m.cd_uf uf,\n"
                    + "	nr_cep cep,\n"
                    + "	c.dt_inc data_cadastro,\n"
                    + "	ds_email_nfe email,\n"
                    + "	case when is_ativo = 'S' then 1 else 0	end ativo,\n"
                    + "	replace (coalesce (t1.sg_ddd,'') || t1.ds_valor,'-', '') as tel_principal,\n"
                    + "	t1.nm_contato contato1,\n"
                    + "	replace (coalesce (t1.sg_ddd,'') || t1.ds_valor,'-', '') as fone1,\n"
                    + "	t2.nm_contato contato2,\n"
                    + "	replace(replace(coalesce (t2.sg_ddd, '') || t2.ds_valor, '-', ''), '*', '') fone2\n"
                    + "from\n"
                    + "	cadastro.tb_base c\n"
                    + "left join cadastro.tb_logradouro e on e.cd_logradouro = c.cd_logradouro\n"
                    + "left join cadastro.tb_bairro b on b.cd_bairro = c.cd_bairro\n"
                    + "left join cadastro.tb_cidade m on m.cd_cidade = c.cd_cidade\n"
                    + "left join cadastro.tb_base_documento ie on 	ie.cd_base = c.cd_base\n"
                    + "left join cadastro.tb_base_contato t1 on t1.cd_base = c.cd_base and t1.tp_principal = 'S'\n"
                    + "left join cadastro.tb_base_contato t2 on t2.cd_base = c.cd_base and t2.tp_principal = 'N'\n"
                    + "join cadastro.tb_base_tipo tipo on tipo.cd_base = c.cd_base and tipo.cd_base_tipo_flag = 2\n"
                    + "join cadastro.tb_cliente n on n.cd_base_cliente = c.cd_base and n.cd_loja = " + getLojaOrigem()
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(Utils.acertarTexto(rs.getString("tel_principal")));

                    if ((rs.getString("contato1") != null)
                            && (!rs.getString("contato1").trim().isEmpty())) {
                        imp.addContato(
                                rs.getString("contato1"),
                                rs.getString("fone1"),
                                null,
                                TipoContato.COMERCIAL,
                                rs.getString("email")
                        );
                    }

                    if ((rs.getString("contato2") != null)
                            && (!rs.getString("contato2").trim().isEmpty())) {
                        imp.addContato(
                                rs.getString("contato2"),
                                rs.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
//                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_pagar as id, \n"
                    + "	cp.cd_base_fornecedor as id_fornecedor,\n"
                    + "	nr_nota_fiscal as documento, \n"
                    + "	cp.dt_emissao as emissao,\n"
                    + "	cp.dt_inc as entrada, \n"
                    + "	dt_vcto as vencimento,\n"
                    + "	vl_valor as valor,\n"
                    + "	cp.ds_obs as observacao\n"
                    + " from\n"
                    + "	pagar.tb_pagar cp\n"
                    + " where\n"
                    + "	cd_loja = " + getLojaOrigem() + "\n"
                    + "	and cd_titulo_carteira = 7\n"
                    + "	and dt_ultima_baixa is null"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataEntrada(rst.getDate("entrada"));
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"), rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_cheque_item as id, \n"
                    + "	ch.dt_inc as emissao,\n"
                    + "	dt_vcto as vencimento,\n"
                    + "	nr_cheque as numcheque,\n"
                    + "	vl_valor as valor,\n"
                    + "	doc.nr_cpf_cnpj cpfcnpj,\n"
                    + "	z.nr_banco as banco,\n"
                    + "	z.nr_ag as  agencia ,\n"
                    + "	z.nr_conta as conta,\n"
                    + "	tdoc.nr_telefone as telefone,\n"
                    + "	tdoc.nm_titular as titular,\n"
                    + "	ch.ds_obs obs\n"
                    + "from\n"
                    + "	predatado.tb_cheque_item ch \n"
                    + "	left join predatado.tb_cheque t on t.cd_cheque = ch.cd_cheque 	\n"
                    + "	left join predatado.tb_cheque_cmc7_cpf_cnpj doc on doc.cd_cheque_cmc7_cpf_cnpj = ch.cd_cheque_cmc7_cpf_cnpj \n"
                    + "	left join predatado.tb_cheque_cpf_cnpj tdoc on tdoc.nr_cpf_cnpj = doc.nr_cpf_cnpj \n"
                    + "	left join predatado.tb_cheque_cmc7 z on z.cd_cheque_cmc7 = doc.cd_cheque_cmc7\n"
                    + "	where  tp_baixa is null and t.cd_loja = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setDataDeposito(rst.getDate("vencimento"));
                    imp.setNumeroCheque(rst.getString("numcheque"));
                    imp.setNome(rst.getString("titular"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setCpf(rst.getString("cpfcnpj"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setValor(rst.getDouble("valor"));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	pf.cd_base_fornecedor id_fornecedor,\n"
                    + "	nr_produto id_produto,	\n"
                    + "	nr_produto_externo codexterno,\n"
                    + "	tp.qt_embalagem qtde_embalagem\n"
                    + "from\n"
                    + "	produto.tb_produto tp \n"
                    + "	join produto.tb_produto_loja_forn pf on pf.cd_produto = tp.cd_produto and pf.cd_loja = " + getLojaOrigem() + "\n"
                    + "	order by 1,2"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setCodigoExterno(rs.getString("codexterno"));
                    imp.setQtdEmbalagem(rs.getDouble("qtde_embalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "with pai as \n"
                    + "(\n"
                    + "select\n"
                    + "	distinct \n"
                    + "	rec.cd_produto_receita as id_receita,\n"
                    + "	prod.nr_produto as id_produtopai,\n"
                    + "	prod.nm_produto as descricao\n"
                    + "from\n"
                    + "	produto.tb_produto_receita rec\n"
                    + "join produto.tb_produto_receita_item ri on\n"
                    + "	rec.cd_produto_receita = ri.cd_produto_receita\n"
                    + "join produto.tb_produto prod on\n"
                    + "	prod.cd_produto = rec.cd_produto\n"
                    + "order by 1,2)\n"
                    + "     select \n"
                    + "         ri.cd_produto_receita id_receita,\n"
                    + "		pai.id_produtopai,\n"
                    + "		pai.descricao descricao,\n"
                    + "		p.nr_produto id_produtofilho,\n"
                    + "		p.nm_produto desc_filho,\n"
                    + "		r.qt_rendimento_peso as rendimento_peso,\n"
                    + "		ri.qt_utilizado as qtde\n"
                    + "from\n"
                    + "	produto.tb_produto_receita_item ri\n"
                    + "join produto.tb_produto p on\n"
                    + "	p.cd_produto = ri.cd_produto\n"
                    + "join pai on\n"
                    + "	pai.id_receita = ri.cd_produto_receita \n"
                    + "join produto.tb_produto_receita r on r.cd_produto_receita = ri.cd_produto_receita "
            //                   + "where pai.id_receita = 4109"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());

                    imp.setImportid(rst.getString("id_receita"));
                    imp.setIdproduto(rst.getString("id_produtopai"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setRendimento(rst.getDouble("rendimento_peso"));
                    imp.setQtdembalagemreceita(rst.getInt("qtde"));
                    imp.setQtdembalagemproduto(1000);
                    imp.setFator(1);
                    imp.setFichatecnica("");
                    imp.getProdutos().add(rst.getString("id_produtofilho"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	c.cd_base id,\n"
                    + "	nr_cpf_cnpj cpf_cnpj,\n"
                    + "	ie.ds_documento rg_ie,\n"
                    + "	nm_base razao,\n"
                    + "	case when nm_fantasia is null then nm_base else nm_fantasia end fantasia ,\n"
                    + "	e.nm_logradouro endereco,\n"
                    + "	nr_endereco numero,\n"
                    + "	ds_complemento complemento,\n"
                    + "	b.nm_bairro bairro,\n"
                    + "	m.nm_cidade cidade,\n"
                    + "	m.cd_uf estado,\n"
                    + "	nr_cep cep,\n"
                    + "	c.dt_inc data_cadastro,\n"
                    + "	n.dt_nasc data_nasc,\n"
                    + "	n.nm_pai nomepai,\n"
                    + "	n.nm_mae nomemae,\n"
                    + "	n.vl_limite limite,\n"
                    + "	ds_email_nfe email,\n"
                    + "	coalesce (n.tp_sexo, 'M') sexo,\n"
                    + "	case when is_ativo = 'S' then 1 else 0 end ativo,\n"
                    + "	t2.tp_principal tipo_tel,\n"
                    + "	replace (coalesce (t1.sg_ddd, '') || t1.ds_valor, '-','') as telefone,\n"
                    + "	t1.nm_contato contato1,\n"
                    + "	replace(replace(coalesce (t2.sg_ddd, '') || t2.ds_valor,'-',''),'*','') celular,\n"
                    + "	t2.nm_contato contato2\n"
                    + "from\n"
                    + "	cadastro.tb_base c\n"
                    + "	left join cadastro.tb_logradouro e on e.cd_logradouro = c.cd_logradouro \n"
                    + "	left join cadastro.tb_bairro b on b.cd_bairro = c.cd_bairro \n"
                    + "	left join cadastro.tb_cidade m on m.cd_cidade = c.cd_cidade \n"
                    + "	left join cadastro.tb_base_documento ie on ie.cd_base = c.cd_base \n"
                    + "	left join cadastro.tb_base_contato t1 on t1.cd_base = c.cd_base and t1.tp_principal = 'S'\n"
                    + "	left join cadastro.tb_base_contato t2 on t2.cd_base = c.cd_base and t2.tp_principal = 'N'\n"
                    + "	join cadastro.tb_base_tipo tipo on tipo.cd_base  = c.cd_base and tipo.cd_base_tipo_flag = 1\n"
                    + "	join cadastro.tb_cliente n on n.cd_base_cliente = c.cd_base and n.cd_loja = " + getLojaOrigem() + "\n"
                    + "	order by 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setObservacao(rs.getString("contato1"));
                    imp.setObservacao(rs.getString("contato2"));

                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));

                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setValorLimite(rs.getDouble("limite"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	r.cd_receber as id,\n"
                    + "	r.cd_base_cliente as idcliente,\n"
                    + "	ba.nr_cpf_cnpj as cnpj,\n"
                    + "	ba.nm_base as razao,\n"
                    + "	r.cd_caixa as ecf,\n"
                    + "	r.nr_titulo,\n"
                    + "	r.nr_digito,\n"
                    + "	r.ds_obs,\n"
                    + "	r.vl_valor,\n"
                    + "	r.dt_emissao,\n"
                    + "	r.dt_vcto,\n"
                    + "	r.nr_coo as coo,\n"
                    + "	r.nr_serie_ecf as serieecf\n"
                    + "from \n"
                    + "	receber.tb_receber r \n"
                    + "join\n"
                    + "	cadastro.tb_titulo_tipo rt on rt.cd_titulo_tipo = r.cd_titulo_tipo\n"
                    + "join\n"
                    + "	cadastro.tb_base ba on ba.cd_base = r.cd_base_cliente\n"
                    + "where \n"
                    + "	dt_ultima_baixa is null and r.cd_loja = " + getLojaOrigem() + "\n "
                    + "and r.cd_titulo_tipo = 15\n"
                    + "order by dt_emissao"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setCnpjCliente(rs.getString("cnpj"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("dt_emissao"));
                    imp.setDataVencimento(rs.getDate("dt_vcto"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setObservacao(rs.getString("ds_obs"));
                    imp.setValor(rs.getDouble("vl_valor"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_convenio as id,\n"
                    + "	b.nr_cpf_cnpj as cnpj, \n"
                    + "	b.nm_base as razao,\n"
                    + "	doc.ds_documento as ie,\n"
                    + "	e.nm_logradouro as endereco,\n"
                    + "	nr_endereco as numero,\n"
                    + " ds_complemento as complemento,\n"
                    + " bairro.nm_bairro as bairro,\n"
                    + " m.nm_cidade as cidade,\n"
                    + " m.cd_uf as uf,\n"
                    + " nr_cep as cep,\n"
                    + " replace (coalesce (cont.sg_ddd,'') || cont.ds_valor,'-', '') as telefone,\n"
                    + " ce.ds_obs as observacao\n"
                    + "from\n"
                    + "	receber.tb_convenio ce \n"
                    + "	left join cadastro.tb_cliente c on c.cd_base_cliente = ce.cd_base_cliente and c.cd_loja = " + getLojaOrigem() + "\n"
                    + "	left join cadastro.tb_base b on b.cd_base = c.cd_base_cliente \n"
                    + "	left join cadastro.tb_base_documento doc on doc.cd_base = b.cd_base \n"
                    + "	left join cadastro.tb_bairro bairro on bairro.cd_bairro = b.cd_bairro\n"
                    + "	left join cadastro.tb_cidade m on m.cd_cidade = c.cd_cidade\n"
                    + "	left join cadastro.tb_logradouro e on e.cd_logradouro = b.cd_logradouro\n"
                    + "	left join cadastro.tb_base_contato cont on cont.cd_base_contato = b.cd_base and cont.tp_principal = 'S'"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacoes(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	tc.cd_convenio||'-'||nr_conveniado as id_cliente,\n"
                    + "	nm_conveniado as nome,\n"
                    + "	tc.cd_convenio as id_empresa,\n"
                    + "	tc.vl_limite as limite,\n"
                    + "	dt_desativacao ,\n"
                    + " tc.nr_cpf_cnpj as cpf_cnpj, \n "
                    + "	case when dt_desativacao is null then 1 else 0 	end ativo,\n"
                    + "	tc.ds_obs as observacao\n"
                    + "from\n"
                    + "	receber.tb_conveniado tc\n"
                    + "	left join receber.tb_convenio ce on ce.cd_convenio = tc.cd_convenio \n"
                    + "	left join cadastro.tb_cliente c on c.cd_base_cliente = ce.cd_base_cliente and c.cd_loja = " + getLojaOrigem() + "\n"
                    + "	left join cadastro.tb_base b on b.cd_base = c.cd_base_cliente \n"
                    + "	left join cadastro.tb_base_documento doc on doc.cd_base = b.cd_base"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id_cliente"));
                    imp.setNome(rs.getString("nome"));
                    imp.setIdEmpresa(rs.getString("id_empresa"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rs.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_receber  as id,\n"
                    + "	tr.cd_convenio||'-'||nr_conveniado as id_conveniado,\n"
                    + "	nr_titulo as documento,\n"
                    + "	nr_serie_ecf ecf,\n"
                    + "	dt_emissao as datamovimento,\n"
                    + "	dt_inc as data_hora,\n"
                    + "	vl_valor as valor,\n"
                    + "	ds_obs as observacao\n"
                    + "from\n"
                    + "	receber.tb_receber tr\n"
                    + "where\n"
                    + "	cd_loja = " + getLojaOrigem() + "\n"
                    + "	and nr_conveniado is not null \n"
                    + "	and dt_ultima_baixa is null"
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("id_conveniado"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    //imp.setDataMovimento(rst.getDate("datamovimento"));
                    imp.setDataHora(rst.getTimestamp("data_hora"));
                    imp.setValor(rst.getDouble("valor"));
//                    imp.setObservacao(rst.getString("observacao"));

                    /*
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("cliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataHora(rst.getTimestamp("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                     */
                    result.add(imp);
                }
            }
        }
        return result;
    }

}
