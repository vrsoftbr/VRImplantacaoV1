package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class LogTECDAO extends InterfaceDAO implements MapaTributoProvider {

    String telefonesCliente = "";
    String array[];
    
    private boolean importarSomenteBalanca = false;
    private String complemento = "";
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim().toUpperCase();
    }

    public void setImportarSomenteBalanca(boolean importarSomenteBalanca) {
        this.importarSomenteBalanca = importarSomenteBalanca;
    }
    

    @Override
    public String getSistema() {
        if (this.complemento.equals("")) {
            return "LogTEC";
        } else {
            return "LogTEC - " + this.complemento;
        }
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select distinct\n" +
                    "	'SAIDA' tipo,\n" +
                    "	cod_taxa id,\n" +
                    "	cod_taxa descricao,\n" +
                    "	cod_sit_tributaria cst,\n" +
                    "	aliq_ecf aliquota,\n" +
                    "	0 reduzido\n" +
                    "from\n" +
                    "	c_produto cp\n" +
                    "union\n" +
                    "select distinct\n" +
                    "	'ENTRADA' tipo,\n" +
                    "	'' id,\n" +
                    "	'' descricao,\n" +
                    "	ni.cod_situacao cst,\n" +
                    "	ni.aliq_icms aliquota,\n" +
                    "	ni.per_reducao reduzido\n" +
                    "from\n" +
                    "	nfcompraitem ni\n" +
                    "order by\n" +
                    "	1,2,3 "
            )) {
                while (rs.next()) {
                    if ("SAIDA".equals(rs.getString("tipo"))) {
                        result.add(new MapaTributoIMP(
                            rs.getString("id"), 
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reduzido")
                        ));
                    } else {
                        String id = formatTributacaoID(
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reduzido")
                        );
                        result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reduzido")                                
                        ));
                    }
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select\n"
                    + "     cod_empresa id,\n"
                    + "     cgc_empresa cnpj,\n"
                    + "     fan_empresa fantasia\n"
                    + " from empresa ")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("fantasia")));
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
                    " select \n"
                    + "     m1.cod_grupo m1,\n"
                    + "     m1.des_grupo m1_desc,\n"
                    + "     m2.cod_subgrupo m2,\n"
                    + "     m2.des_subgrupo m2_desc\n"
                    + " from \n"
                    + "     grupo_produto m1\n"
                    + " left join subgrupo_produto m2\n"
                    + "     on m1.cod_grupo = m2.cod_grupo\n"
                    + " order by m1.cod_grupo,m2.cod_subgrupo")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(rs.getString("m1_desc"));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(rs.getString("m2_desc"));

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
                "with trib as (\n" +
                "	select\n" +
                "		distinct on(ni.cod_produto)\n" +
                "		ni.cod_produto,\n" +
                "		n.dat_cadastro,\n" +
                "		ni.cod_situacao,\n" +
                "		ni.aliq_icms,\n" +
                "		ni.per_reducao\n" +
                "	from\n" +
                "		nfcompraitem ni\n" +
                "		join nfcompra n on\n" +
                "			ni.cod_empresa = n.cod_empresa and\n" +
                "			ni.cod_interno = n.cod_interno\n" +
                "	order by\n" +
                "		ni.cod_produto, n.dat_cadastro desc\n" +
                ")\n" +
                "select \n" +
                "	p.cod_produto importId,\n" +
                "	p.dat_cadastro dataCadastro,\n" +
                "	p.dat_alteracao dataAlteracao,\n" +
                "	p.cod_barra ean,\n" +
                "	cp.und_produto unidade,\n" +
                "	case when cp.flg_balanca = 'S' then 1 else 0 end pesavel,\n" +
                "	cp.balanca_dias_validade validade,\n" +
                "	cp.des_completa descricaoCompleta,\n" +
                "	p.des_resumida descricaoReduzida,\n" +
                "	p.des_resumida descricaoGondola,\n" +
                "	p.cod_grupo m1,\n" +
                "	p.cod_subgrupo m2,\n" +
                "	p.pes_bruto pesoBruto,\n" +
                "	p.pes_liquido pesoLiquido,\n" +
                "	p.min_regulador estoqueminimo,\n" +
                "	e.saldo_estoque estoque,\n" +
                "	cp.vlr_custo_liquido custoSemImposto,\n" +
                "	cp.vlr_custo_bruto custoComImposto,    \n" +
                "	cp.vlr_unitario1 precovenda,\n" +
                "	case when cp.flg_ativo = 'S' then 0 else 1 end situacao,\n" +
                "	cp.des_ncm ncm,\n" +
                "	cp.des_cest cest,\n" +
                "	pis.cod_st_saida piscofinsCstDebito,\n" +
                "	pis.cod_st_entrada piscofinsCstCredito,\n" +
                "	pis.nat_receita	natrec,\n" +
                "	cp.cod_taxa id_icms_s,\n" +
                "	cp.cod_sit_tributaria icms_cst_s,\n" +
                "	cp.aliq_ecf icms_aliq_s,\n" +
                "	0 icms_red_s,\n" +
                "	coalesce(trib.cod_situacao, cp.cod_sit_tributaria) icms_cst_e,\n" +
                "	coalesce(trib.aliq_icms, cp.aliq_ecf) icms_aliq_e,\n" +
                "	coalesce(trib.per_reducao, 0) icms_red_e,\n" +
                "	p.und_referencia unidadevolume,\n" +
                "	p.qtd_referencia volume\n" +
                "from\n" +
                "	c_produto cp\n" +
                "	join produto p\n" +
                "		on p.cod_produto = cp.cod_produto\n" +
                "		and p.cod_empresa = cp.cod_empresa\n" +
                "	join pis_cofins pis\n" +
                "		on pis.codigo = p.cod_pis_cofins\n" +
                "	join produto_estoque e\n" +
                "		on e.cod_produto = p.cod_produto\n" +
                "		and p.cod_empresa = e.cod_empresa\n" +
                "	left join trib on\n" +
                "		trib.cod_produto = p.cod_produto\n" +
                "where\n" +
                "	cp.cod_empresa = " + getLojaOrigem()
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setEan(rs.getString("ean"));
                    imp.setImportId(rs.getString("importId"));

                        ProdutoBalancaVO bal = balanca.get(Utils.stringToInt(imp.getEan(), -2));
                if (bal != null) {
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagem("U".equals(bal.getPesavel()) ? "UN" : "KG");
                    imp.seteBalanca(true);
                    imp.setValidade(bal.getValidade());
                } else {
                    if (this.importarSomenteBalanca) {
                        continue;
                    }
                    //imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.seteBalanca(rs.getBoolean("pesavel"));
                    imp.setValidade(rs.getInt("validade"));
                }

                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    imp.setDescricaoCompleta(rs.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaoGondola"));
                    imp.setCodMercadologico1(rs.getString("m1"));
                    imp.setCodMercadologico2(rs.getString("m2"));
                    imp.setPesoBruto(rs.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoLiquido"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));

                    imp.setCustoComImposto(rs.getDouble("custoComImposto"));
                    imp.setCustoSemImposto(rs.getDouble("custoSemImposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacao") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setIcmsDebitoId(rs.getString("id_icms_s"));
                    imp.setIcmsDebitoForaEstadoId(rs.getString("id_icms_s"));
                    imp.setIcmsDebitoForaEstadoNfId(rs.getString("id_icms_s"));
                    imp.setIcmsConsumidorId(rs.getString("id_icms_s"));
                    String icmsEntradaId = formatTributacaoID(
                            rs.getInt("icms_cst_e"),
                            rs.getDouble("icms_aliq_e"),
                            rs.getDouble("icms_red_e")
                    );
                    imp.setIcmsCreditoId(icmsEntradaId);
                    imp.setIcmsCreditoForaEstadoId(icmsEntradaId);

                    imp.setPiscofinsCstDebito(rs.getInt("piscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rs.getInt("piscofinsCstCredito"));
                    imp.setPiscofinsNaturezaReceita(rs.getInt("natrec"));
                    
                    imp.setVolume(rs.getDouble("volume"));
                    imp.setTipoEmbalagemVolume(rs.getString("unidadevolume"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private String formatTributacaoID(int cst, double aliquota, double reducao) {
        return String.format("%d-%.2f-%.2f",
                cst,
                aliquota,
                reducao
        );
    }
    

        @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select\n"
                    + "     pf.cod_fornece fornecedor,\n"
                    + "     p.cod_produto produto,\n"
                    + "     pf.cod_barras codexterno\n"
                    + " from\n"
                    + "     produto_fornecedor pf\n"
                    + "     left join produto p\n"
                    + "     on p.cod_produto = pf.cod_produto\n"
                    + "     and p.cod_empresa = pf.cod_empresa\n"
                    + " where pf.cod_empresa = " + getLojaOrigem() + "\n"
                    + " order by 1,2")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setIdProduto(rs.getString("produto"));
                    imp.setCodigoExterno(rs.getString("codexterno"));

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
                    " select\n"
                    + "     cod_fornece id,\n"
                    + "     raz_fornece razao,\n"
                    + "     fan_fornece fantasia,\n"
                    + "     cgc_fornece cnpj,\n"
                    + "     ins_fornece ie,\n"
                    + "     end_fornece endereco,\n"
                    + "     nroend_fornecedor numero,\n"
                    + "     end_complemento complemento,\n"
                    + "     bai_fornece bairro,\n"
                    + "     codigofederal ibge_municipio,\n"
                    + "     nom_cidade municipio,\n"
                    + "     codestadual ibge_uf,\n"
                    + "     est_fornece uf,\n"
                    + "     cep_fornece cep,\n"
                    + "     fon_fornece tel_principal,\n"
                    + "     dat_cadastro datacadastro,\n"
                    + "     observacao,\n"
                    + "     prazo_medio_entrega prazoentrega\n"
                    + " from fornecedor f\n"
                    + "		left join cidade cid\n"
                    + "		  on cid.cod_cidade = f.cod_cidade\n"
                    + " order by cod_fornece")) {
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
                    imp.setIbge_municipio(rs.getInt("ibge_municipio"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setIbge_uf(rs.getInt("ibge_uf"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("tel_principal"));
                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setPrazoEntrega(rs.getInt("prazoentrega"));

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
                    "select	\n"
                    + "	 cod_empresa,\n"
                    + "	 cod_cliente id,\n"
                    + "	 cpf_cgc cnpj,\n"
                    + "	 rg_inscr ie,\n"
                    + "	 raz_cliente razao,\n"
                    + "	 fan_cliente fantasia,\n"
                    + "	 case when flg_ativo='S' then 1 else 0 end ativo,\n"
                    + "	 flg_bloq_liberado bloqueado,\n"
                    + "	 end_cliente endereco,\n"
                    + "	 nroend_cliente numero,\n"
                    + "	 end_complemento complemento,\n"
                    + "	 bai_cliente bairro,\n"
                    + "	 cid.codigofederal municipioIBGE,\n"
                    + "	 cid.nom_cidade municipio,\n"
                    + "	 cid.codestadual ufIBGE,\n"
                    + "	 cid.cod_estado estado,\n"
                    + "	 cep_cliente cep,\n"
                    + "	 est_civil TipoEstadoCivil,\n"
                    + "	 dat_nascto dataNascimento,\n"
                    + "	 dat_cadastro dataCadastro,\n"
                    + "	 coalesce(sexo,'M') sexo,\n"
                    + "	 local_trab empresa,\n"
                    + "	 end_trab empresaEndereco,\n"
                    + "	 fon_trab empresaTelefone,\n"
                    + "	 nom_cargo cargo,\n"
                    + "	 vlr_renda salario,\n"
                    + "	 vlr_limite_aberto valorLimite,\n"
                    + "	 nom_conjuge nomeConjuge,\n"
                    + "	 cpf_conjuge cpfConjuge,\n"
                    + "	 nom_pai nomePai,\n"
                    + "	 nom_mae nomeMae,\n"
                    + "	 observacao,\n"
                    + "	 num_fone telefone,\n"
                    + "      num_celular celular,\n"
                    + "	 email,\n"
                    + "	 endereco_cobranca cobrancaEndereco,\n"
                    + "	 bairro_cobranca cobrancaBairro,\n"
                    + "	 cidade_cobranca cobrancaMunicipio,\n"
                    + "	 cep_cobranca cobrancaCep\n"
                    + "from cliente cli\n"
                    + "	 left join cidade cid\n"
                    + "	   on cid.cod_cidade = cli.cod_cidade\n"
                    //+ "where cod_empresa = " + getLojaOrigem() + "\n"
                    + "order by cod_cliente")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("cod_empresa") + "-" + rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoestadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipioIBGE(rs.getInt("municipioibge"));
                    imp.setMunicipio(rs.getString("municipio"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataNascimento(rs.getDate("datanascimento"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setSexo("F".equals(rs.getString("sexo").trim()) ? TipoSexo.FEMININO : TipoSexo.MASCULINO);

                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaEndereco(rs.getString("empresaendereco"));
                    imp.setEmpresaTelefone(rs.getString("empresatelefone"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("valorlimite"));
                    imp.setNomeConjuge(rs.getString("nomeconjuge"));
                    imp.setCpfConjuge(rs.getString("cpfconjuge"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setNomeMae(rs.getString("nomemae"));

                    if (rs.getString("observacao") != null && !"".equals(rs.getString("observacao"))) {
                        imp.setObservacao(rs.getString("observacao"));
                    }

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setCobrancaEndereco(rs.getString("cobrancaendereco"));
                    imp.setCobrancaBairro(rs.getString("cobrancabairro"));
                    imp.setCobrancaMunicipio(rs.getString("cobrancamunicipio"));
                    imp.setCobrancaCep(rs.getString("cobrancacep"));

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
                    "select\n" +
                    "	r.cod_empresa,\n" +
                    "	cod_receber id,\n" +
                    "	dat_emissao dataemissao,\n" +
                    "	num_doc numeroCupom,\n" +
                    "	vlr_doc valor,\n" +
                    "	desc_doc observacao,\n" +
                    "	r.cod_empresa_cliente,\n" +
                    "	r.cod_cliente idCliente,\n" +
                    "	dat_vencto dataVencimento,\n" +
                    "	vlr_juros juros,\n" +
                    "	c.cpf_cgc cnpjCliente\n" +
                    "from\n" +
                    "	receber r\n" +
                    "	left join cliente c on \n" +
                    "		r.cod_cliente = c.cod_cliente and\n" +
                    "		r.cod_empresa = c.cod_empresa\n" +
                    "where\n" +
                    "	flg_aberto = 'S'\n" +
                    //"	and r.cod_empresa = " + getLojaOrigem() + "\n" +
                    "order by\n" +
                    "	1,2")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));

                    imp.setDataEmissao(rs.getDate("dataemissao"));
                    imp.setNumeroCupom(rs.getString("numerocupom"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setIdCliente(String.format(
                            "%s-%s",
                            rs.getString("cod_empresa_cliente"),
                            rs.getString("idcliente")
                    ));
                    imp.setDataVencimento(rs.getDate("datavencimento"));
                    imp.setJuros(rs.getDouble("juros"));
                    imp.setCnpjCliente(rs.getString("cnpjCliente"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
