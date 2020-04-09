package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
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

    public boolean v_usar_arquivoBalanca = false;

    String telefonesCliente = "";
    String array[];

    @Override
    public String getSistema() {
        return "LogTEC";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select id, descritivo from tributacao"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descritivo")));
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
                    "select\n"
                    + "    p.id,\n"
                    + "    ean.ean,\n"
                    + "    p.descritivo,\n"
                    + "    p.reduzido,\n"
                    + "    coalesce_varchar(p.descritivocompleto, p.descritivo) descricaocompleta,\n"
                    + "    pe.estoque_atual,\n"
                    + "    pe.estoque_minimo,\n"
                    + "    pe.estoque_padrao,\n"
                    + "    coalesce(p.pesob, 0::numeric) AS pesob,\n"
                    + "    coalesce(p.pesol, 0::numeric) AS pesol,\n"
                    + "    pl.custo,\n"
                    + "    pl.custo_liquido,\n"
                    + "    pl.custo_medio,\n"
                    + "    pl.custo_sem_imposto,\n"
                    + "    pl.margemcad,\n"
                    + "    pl.margemreal,\n"
                    + "    pl.venda1,\n"
                    + "    p.depto merc1,\n"
                    + "    case when \n"
                    + "	   p.secao = 0 then\n"
                    + "	   1 else\n"
                    + "	   p.secao end as merc2,\n"
                    + "    case when \n"
                    + "	   coalesce(p.grupo, coalesce(p.secao, p.depto)) = 0 then\n"
                    + "	   1 else \n"
                    + "	   coalesce(p.grupo, coalesce(p.secao, p.depto)) end as merc3,\n"
                    + "    case when \n"
                    + "	   coalesce(p.subgrupo, coalesce(p.grupo, p.secao)) = 0 then\n"
                    + "	   1 else\n"
                    + "	   coalesce(p.subgrupo, coalesce(p.grupo, p.secao)) end as merc4,\n"
                    + "    p.unidade_venda,\n"
                    + "    p.unidade_compra,\n"
                    + "    p.embalagem_venda,\n"
                    + "    p.embalagem_compra,\n"
                    + "    p.datahorac,\n"
                    + "    p.envia_balanca balanca,\n"
                    + "    p.situacao,\n"
                    + "    p.classificacao_fiscal ncm,\n"
                    + "    coalesce_varchar(pc.valor, nc.valor) AS cest,\n"
                    + "    (select t.icms from tributacao t where t.id = pl.idtributacao) icmsdebito,\n"
                    + "    (select t.situacao_tributaria from tributacao t where t.id = pl.idtributacao) cstdebito,\n"
                    + "    (select t.reducao from tributacao t where t.id = pl.idtributacao) reducaoicms,\n"
                    + "    (select pt.idpis from produto_tributacao pt where pt.idproduto = p.id) cstpis,\n"
                    + "    p.validade,\n"
                    + "    vw.idgrupo_tributacao tribgrupo\n"
                    + "from\n"
                    + "    produto p\n"
                    + "join\n"
                    + "	  vw_produto_tributacao vw on p.id = vw.idproduto\n"
                    + "join \n"
                    + "    produto_loja pl on p.id = pl.produto\n"
                    + "left join \n"
                    + "    produto_ean ean on p.id = ean.produto\n"
                    + "join \n"
                    + "    produto_estoque pe on p.id = pe.produto\n"
                    + "inner join \n"
                    + "    empresa e on pl.empresa = e.id\n"
                    + "join \n"
                    + "    produto_config pc on p.id = pc.idproduto and pc.id::text = 'PRODUTO.CODIGO.CEST'::text\n"
                    + "left join \n"
                    + "    ncm n on p.classificacao_fiscal::text = n.id::text and\n"
                    + "    p.extipi::text = n.extipi::text\n"
                    + "left join \n"
                    + "    ncm_config nc on n.id::text = nc.idncm::text and nc.id::text = 'NCM.CODIGO.CEST'::text\n"
                    + "where\n"
                    + "    pe.estoque = 1 and\n"
                    + "    e.id = " + getLojaOrigem() + "\n"
                    + "    --p.situacao in (0) 0: Normal; 1: Excluído; 2: Fora de Linha\n"
                    + "order by\n"
                    + "	 p.id")) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descritivo"));
                    imp.setDescricaoReduzida(rs.getString("reduzido"));
                    imp.setEstoque(rs.getDouble("estoque_atual"));
                    imp.setEstoqueMinimo(rs.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoque_padrao"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setMargem(rs.getDouble("margemreal"));
                    imp.setPrecovenda(rs.getDouble("venda1"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setCodMercadologico4(rs.getString("merc4"));
                    imp.setTipoEmbalagem(rs.getString("unidade_venda"));
                    imp.setQtdEmbalagem(rs.getInt("embalagem_venda"));
                    imp.setDataCadastro(rs.getDate("datahorac"));
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("cstpis"));

                    if (rs.getInt("tribgrupo") == 1) {
                        imp.setIcmsAliqSaida(18);
                        imp.setIcmsCstSaida(10);
                        imp.setIcmsReducaoSaida(0);
                    } else {
                        imp.setIcmsAliqSaida(18);
                        imp.setIcmsCstSaida(0);
                        imp.setIcmsReducaoSaida(0);
                    }

                    imp.setValidade(rs.getInt("validade"));

                    if (v_usar_arquivoBalanca) {
                        if ((rs.getInt("balanca") == 1) || (rs.getInt("balanca") == 2)) {
                            ProdutoBalancaVO produtoBalanca;
                            long codigoProduto;
                            codigoProduto = Long.parseLong(imp.getImportId().trim());

                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }
                            if (produtoBalanca != null) {
                                imp.setEan(imp.getImportId());
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : rs.getInt("validade"));
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }
                        } else {
                            imp.seteBalanca(true);
                            imp.setValidade(rs.getInt("validade"));
                        }
                    }
                    imp.setSituacaoCadastro(rs.getInt("situacao") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);

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
                    + "     pf.cod_produto produto,\n"
                    + "     p.cod_referencia referencia\n"
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
                    imp.setCodigoExterno(rs.getString("referencia"));

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
                    + "     prazo_medio_entrega prazoEntrega\n"
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
                    imp.setPrazoEntrega(rs.getInt("prazo_entrega"));

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
                    + "	 sexo,\n"
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
                    + "  num_celular celular,\n"
                    + "	 email,\n"
                    + "	 endereco_cobranca cobrancaEndereco,\n"
                    + "	 bairro_cobranca cobrancaBairro,\n"
                    + "	 cidade_cobranca cobrancaMunicipio,\n"
                    + "	 cep_cobranca cobrancaCep\n"
                    + "from cliente cli\n"
                    + "	 left join cidade cid\n"
                    + "	   on cid.cod_cidade = cli.cod_cidade\n"
                    + "where cod_empresa = 1\n"
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

                    /*
                     if (rs.getString("telefones") != null && !"".equals(rs.getString("telefones"))) {
                     array = new String[3];
                     telefonesCliente = rs.getString("telefones");
                     array = telefonesCliente.split(" / ");
                     int c = 1;
                     for (String telefones : array) {
                     imp.addContato(String.valueOf(c), "TELEFONE " + c, telefones.trim(), null, null);
                     c++;
                     }
                     }
                     if (rs.getString("contatoemail") != null & !"".equals(rs.getString("contatoemail"))) {
                     imp.addContato("EMAIL", rs.getString("contatoemail"), null, null, rs.getString("email"));
                     }*/
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
                    + "	c.id, \n"
                    + "	c.idreferencia,\n"
                    + "	cf.cpf,\n"
                    + "	cj.cnpj,\n"
                    + "	c.emissao, \n"
                    + "	c.cadid idcliente, \n"
                    + "	c.vencimento, \n"
                    + "	c.valor, \n"
                    + "	c.parcela, \n"
                    + "	c.ecf \n"
                    + "from \n"
                    + "	contasr c \n"
                    + "left join\n"
                    + "	clientef cf on c.cadid = cf.id\n"
                    + "left join\n"
                    + "	clientej cj on c.cadid = cj.id\n"
                    + "where \n"
                    + "	c.pagamento is null\n"
                    + "order by \n"
                    + "	c.emissao")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    if (rs.getString("cnpj") == null) {
                        imp.setCnpjCliente(rs.getString("cpf"));
                    } else {
                        imp.setCnpjCliente(rs.getString("cnpj"));
                    }
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setEcf(rs.getString("ecf"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
