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
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
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
                OpcaoProduto.MERCADOLOGICO_PRODUTO                
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
                OpcaoCliente.CLIENTE_EVENTUAL));
    }

    /*@Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	aliq.id_grade_trib id,\n"
                    + "	cst.codigo||'-'||aliq.per_icms||'-'||aliq.per_red_icms descricao,\n"
                    + "	cst.codigo cst_saida,\n"
                    + "	aliq.per_icms aliquota_saida,\n"
                    + "	aliq.per_red_icms reducao_saida\n"
                    + "from\n"
                    + "	fs_grade_trib_aliq aliq\n"
                    + "	join tb_cst cst on aliq.id_cst = cst.id_cst and cst.tipo_imposto = 'ICMS'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida"))
                    );
                }
            }
        }

        return result;
    }*/
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
                    "select\n" +
                    "	dep.cd_depto as merc1,\n" +
                    "	dep.nm_depto as descmerc1,\n" +
                    "	sec.cd_depto_secao as merc2,\n" +
                    "	sec.nm_depto_secao as descmerc2,\n" +
                    "	coalesce(gru.cd_depto_grupo, 1) as merc3,\n" +
                    "	coalesce(gru.nm_depto_grupo, sec.nm_depto_secao) as descmerc3\n" +
                    "from\n" +
                    "	produto.tb_depto dep\n" +
                    "left join\n" +
                    "	produto.tb_depto_secao sec on sec.cd_depto = dep.cd_depto\n" +
                    "left join\n" +
                    "	produto.tb_depto_grupo gru on gru.cd_depto_secao = sec.cd_depto_secao\n" +
                    "order by\n" +
                    "	dep.cd_depto, sec.cd_depto_secao")) {
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
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "	tp.nr_produto id,\n"
                    + "	tp.nm_produto_longo desc_completa,\n"
                    + "	tp.nm_reduzido desc_reduzida,\n"
                    + "	cd.cd_codbarra ean,\n"
                    + "	custo.vl_custo_faturado custosemimposto,\n"
                    + "	custo.vl_custo custocomimposto,\n"
                    + "	custo.vl_venda precovenda,\n"
                    + "	custo.per_margem_venda margem,\n"
                    + "	est.qt_saldo estoque,\n"
                    + "	tp.cd_depto codmerc1,\n"
                    + "	tp.cd_depto_secao codmerc2,\n"
                    + "	tp.cd_depto_grupo codmerc3,\n"
                    + "	tp.tp_embalagem tipo_embalagem,\n"
                    + "	tp.qt_embalagem qtde_emb,\n"
                    //+ "	coalesce (pb.qt_dias_validade_balanca, '0') validadebalanca,\n"
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
//                    + "	tp_unidade_medida  tipovolume,\n"
//                    + "	qt_unidade_medida  volume,\n"
                    + "	case \n"
                    + "	when tp.tp_venda = 'B' then 1 else 0 end is_balanca \n"
                    + "from\n"
                    + "	produto.tb_produto tp\n"
                    + "join produto.tb_produto_codbarra cd on cd.cd_produto = tp.cd_produto\n"
                    + "join produto.tb_produto_loja custo on custo.cd_produto = tp.cd_produto and custo.cd_loja = " +getLojaOrigem() + "\n"
                    + "join saldo.vw_saldo_loja est on est.nr_produto = tp.nr_produto and nr_loja = " + getLojaOrigem() + "\n"
                    + "left join produto.tb_produto_balanca pb on pb.cd_produto = tp.cd_produto  and  pb.cd_loja = " + getLojaOrigem()
            )) {
                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    
                    imp.seteBalanca(rst.getBoolean("is_balanca"));
                    imp.setEan(rst.getString("ean"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rst.getString("ean"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade() > 1
                                ? bal.getValidade() : rst.getInt("validadebalanca"));
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rst.getString("desc_completa"));
                    imp.setDescricaoReduzida(rst.getString("desc_reduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setTipoEmbalagem(rst.getString("tipo_embalagem"));
//                    imp.setTipoEmbalagemCotacao(rst.getString("emb_compra"));
                    imp.setQtdEmbalagem(rst.getInt("qtde_emb"));
//                    imp.setVolume(rst.getDouble("volume"));
//                   imp.setTipoEmbalagemVolume(rst.getString("tipovolume"));

                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setDataCadastro(rst.getDate("data_cadastro"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    
                    imp.setCodMercadologico1(rst.getString("codmerc1"));
                    imp.setCodMercadologico2(rst.getString("codmerc2"));
                    imp.setCodMercadologico3(rst.getString("codmerc3"));

                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    String idIcms;

                    idIcms = rst.getString("id_icms");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    //          imp.setPiscofinsCstCredito(rst.getString("piscofins"));
                    //          imp.setPiscofinsCstDebito(rst.getString("piscofins"));
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
                    ""
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
                    imp.setTel_principal(Utils.acertarTexto(rs.getString("telefone")));

                    String email = Utils.acertarTexto(rs.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL,
                                (email.length() > 50 ? email.substring(0, 50) : email));
                    }

                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setObservacao(rs.getString("observacao"));

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

}
