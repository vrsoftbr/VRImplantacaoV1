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
import vrimplantacao2.vo.enums.TipoContato;
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
                    "select id, descricao from aliquotas_icms"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descricao")));
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
                    "select \n"
                    + "	p.cod_produto importId,\n"
                    + "	dat_cadastro dataCadastro,\n"
                    + "	dat_alteracao dataAlteracao,\n"
                    + "	cod_barra ean,\n"
                    + "	und_produto unidade,\n"
                    + "	case when cp.flg_balanca = 'S' then 1 else 0 end pesavel,\n"
                    + "	cp.balanca_dias_validade validade,\n"
                    + "	des_completa descricaoCompleta,\n"
                    + "	des_resumida descricaoReduzida,\n"
                    + "	des_resumida descricaoGondola,\n"
                    + "	cod_grupo m1,\n"
                    + "	cod_subgrupo m2,\n"
                    + "	pes_bruto pesoBruto,\n"
                    + "	pes_liquido pesoLiquido,\n"
                    + "	min_regulador estoqueminimo,\n"
                    + "	saldo_estoque estoque,\n"
                    + "	vlr_custo_liquido custoSemImposto,\n"
                    + "	cp.vlr_custo_bruto custoComImposto,    \n"
                    + "	vlr_unitario1 precovenda,\n"
                    + "	case when cp.flg_ativo = 'S' then 0 else 1 end situacao,\n"
                    + "	des_ncm ncm,\n"
                    + "	des_cest cest,\n"
                    + "	cod_sit_tributaria icmsCstSaida,\n"
                    + "	cp.aliq_ecf icmsAliqSaida,\n"
                    + "	pis.cod_st_saida piscofinsCstDebito,\n"
                    + "	pis.cod_st_entrada piscofinsCstCredito,\n"
                    + "	pis.nat_receita	natrec\n"
                    + "from	c_produto cp\n"
                    + "	join produto p\n"
                    + "		on p.cod_produto = cp.cod_produto\n"
                    + "		and p.cod_empresa = cp.cod_empresa\n"
                    + "	join pis_cofins pis\n"
                    + "		on pis.codigo = p.cod_pis_cofins\n"
                    + "	join produto_estoque e\n"
                    + "		on e.cod_produto = p.cod_produto\n"
                    + "		and p.cod_empresa = e.cod_empresa\n"
                    + "where cp.cod_empresa = " + getLojaOrigem() + " \n"
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

                        imp.setIcmsCstSaida(rs.getInt("icmsCstSaida"));
                        imp.setIcmsAliqSaida(rs.getDouble("icmsAliqSaida"));
                        imp.setPiscofinsCstDebito(rs.getInt("piscofinsCstDebito"));
                        imp.setPiscofinsCstCredito(rs.getInt("piscofinsCstCredito"));
                        imp.setPiscofinsNaturezaReceita(rs.getInt("natrec"));
                        
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
                        + "where cod_empresa = " + getLojaOrigem() + "\n"
                        + "order by cod_cliente")) {
                    while (rs.next()) {
                        ClienteIMP imp = new ClienteIMP();
                        imp.setId(rs.getString("id"));
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
                        "select \n"
                        + "	  distinct on (cod_receber)\n"
                        + "	  cod_receber id,\n"
                        + "	  dat_emissao dataemissao,\n"
                        + "	  num_doc numeroCupom,\n"
                        + "	  vlr_doc valor,\n"
                        + "	  desc_doc observacao,\n"
                        + "	  r.cod_cliente idCliente,\n"
                        + "	  dat_vencto dataVencimento,\n"
                        + "	  vlr_juros juros,\n"
                        + "	  c.cpf_cgc cnpjCliente\n"
                        + "from receber r\n"
                        + "	  left join cliente c\n"
                        + "		on r.cod_cliente = c.cod_cliente\n"
                        + "	  where flg_aberto = 'S'\n"
                        + "		and r.cod_empresa = " + getLojaOrigem() + "\n"
                        + "	order by 1,2")) {
                    while (rs.next()) {
                        CreditoRotativoIMP imp = new CreditoRotativoIMP();
                        imp.setId(rs.getString("id"));

                        imp.setDataEmissao(rs.getDate("dataemissao"));
                        imp.setNumeroCupom(rs.getString("numerocupom"));
                        imp.setValor(rs.getDouble("valor"));
                        imp.setObservacao(rs.getString("observacao"));
                        imp.setIdCliente(rs.getString("idcliente"));
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
