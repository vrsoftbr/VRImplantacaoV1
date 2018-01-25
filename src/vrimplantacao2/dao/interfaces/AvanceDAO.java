package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AvanceDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Avance";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id, fantasia FROM adm_empresas_estab ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("fantasia")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT codigo, nome FROM depto ORDER BY 1"
            )) {
                while (rst.next()) {

                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("nome"));

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id, descricao FROM familia ORDER BY id"
            )) {
                while (rst.next()) {

                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	p.codigo id,\n"
                    + "	p.cadastro datacadastro,\n"
                    + "	p.embalagem qtdcotacao,\n"
                    + "	CASE WHEN p.codbalanca != 0 THEN p.codbalanca ELSE ean.codbarra END ean,\n"
                    + "	CASE WHEN p.codbalanca != 0 THEN 1 ELSE ean.qtd_embalagem END qtdembalagem,\n"
                    + "	p.unidade,\n"
                    + "	CASE WHEN p.codbalanca != 0 THEN 1 ELSE 0 END ebalanca,\n"
                    + "	p.validade,\n"
                    + "	p.descricao descricaocompleta,\n"
                    + "	p.descecf descricaoreduzida,\n"
                    + "	p.depart mercadologico1,\n"
                    + "	p.id_familia,\n"
                    + "	p.peso_bruto,\n"
                    + "	p.peso_liquido,\n"
                    + "	est.lojaestmin estoqueminimo,\n"
                    + "	est.lojaestmax estoquemaximo,\n"
                    + "	est.lojaest estoque,\n"
                    + "	p.dentrouf margem,\n"
                    + "	p.custo custosemimposto,\n"
                    + "	p.custofinal custocomimposto,\n"
                    + "	p.atualvenda precovenda,\n"
                    + "	p.inativo situacaocadastro,\n"
                    + "	ncm.ncm,\n"
                    + "	p.cest,\n"
                    + "	p.cst_pis_ent piscofins_entrada,\n"
                    + "	p.cst_pis_sai piscofins_saida,\n"
                    + "	p.cod_nat_receita piscofins_nat_receita,\n"
                    + "	p.cst icms_cst,\n"
                    + "	p.aliquota\n"
                    + "FROM\n"
                    + "	cadmer p\n"
                    + "	LEFT JOIN codbarra ean ON p.codigo = ean.codigo\n"
                    + "	LEFT JOIN cadmer_estoque est ON p.codigo = est.codigo AND est.id_loja = " + getLojaOrigem() + "\n"
                    + "	LEFT JOIN ncm ON ncm.id = p.id_ncm\n"
                    + "WHERE\n"
                    + "	NOT ean.codbarra IS NULL\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdcotacao"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca(rst.getBoolean("ebalanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("situacaocadastro")));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("piscofins_entrada")));
                    imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("piscofins_saida")));
                    imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("piscofins_nat_receita")));
                    imp.setIcmsDebitoId(rst.getString("aliquota"));
                    imp.setIcmsCreditoId(rst.getString("aliquota"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id, CONCAT(descricao,'  |cst:' ,cst) descricao FROM aliquota ORDER BY 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("id"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "f.Codigo,\n"
                    + "f.RAZAO,\n"
                    + "f.FANTASIA,\n"
                    + "f.ENDERECO,\n"
                    + "f.BAIRRO,\n"
                    + "f.CIDADE,\n"
                    + "f.ESTADO,\n"
                    + "f.CEP,\n"
                    + "f.TELEFONE,\n"
                    + "f.FAX,\n"
                    + "f.CGC,\n"
                    + "f.inscmun,\n"
                    + "f.inscr,\n"
                    + "f.OBS,\n"
                    + "f.DATA,\n"
                    + "f.EMAIL,\n"
                    + "f.HTTP,\n"
                    + "f.VENDEDOR,\n"
                    + "f.situacao_empresa,\n"
                    + "f.anotacoes,\n"
                    + "f.senha,\n"
                    + "f.cod_autorizado,\n"
                    + "f.banco1,\n"
                    + "f.banco2,\n"
                    + "f.agencia1,\n"
                    + "f.agencia2,\n"
                    + "f.conta1,\n"
                    + "f.conta2,\n"
                    + "f.prest_serv,\n"
                    + "f.nomeconta1,\n"
                    + "f.nomeconta2,\n"
                    + "f.cod_mun,\n"
                    + "f.suframa,\n"
                    + "f.fonevend,\n"
                    + "f.supervisor,\n"
                    + "f.fonesup,\n"
                    + "f.emailsup,\n"
                    + "f.logr,\n"
                    + "f.numero,\n"
                    + "f.regime_tributario,\n"
                    + "f.id_pais,\n"
                    + "f.inativo,\n"
                    + "f.status,\n"
                    + "f.prev_entrega,\n"
                    + "f.ind_ie,\n"
                    + "f.natureza_juridica,\n"
                    + "f.id_cidade,\n"
                    + "f.reg_trib,\n"
                    + "UPPER(c.nome) NOMECIDADE,\n"
                    + "c.codibge,\n"
                    + "UPPER(u.uf) UFSIGLA,\n"
                    + "UPPER(u.descricao) NOMEESTADO\n"
                    + "FROM fornece f\n"
                    + "LEFT JOIN cidade c ON c.id = f.id_cidade\n"
                    + "LEFT JOIN uf u ON u.id = c.id_uf"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("RAZAO"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setCnpj_cpf(rst.getString("CGC"));
                    imp.setInsc_municipal(rst.getString("inscmun"));
                    imp.setIe_rg(rst.getString("inscr"));
                    imp.setObservacao(rst.getString("OBS") + " " + rst.getString("anotacoes"));
                    imp.setAtivo(rst.getInt("inativo") == 0 ? true : false);
                    imp.setDatacadastro(rst.getDate("DATA"));

                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("FAX").trim()),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("EMAIL").toLowerCase()
                        );
                    }
                    if ((rst.getString("VENDEDOR") != null)
                            && (!rst.getString("VENDEDOR").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                rst.getString("VENDEDOR").substring(0, 30),
                                (rst.getString("fonevend") == null ? "" : rst.getString("fonevend").trim()),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("supervisor") != null)
                            && (!rst.getString("supervisor").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                rst.getString("supervisor").substring(0, 30),
                                (rst.getString("fonesup") == null ? "" : rst.getString("fonesup").trim()),
                                null,
                                TipoContato.COMERCIAL,
                                (rst.getString("emailsup") == null ? "" : rst.getString("emailsup").trim())
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "pf.CODPROD,\n"
                    + "pf.CODFOR,\n"
                    + "pf.datahora,\n"
                    + "pf.valor,\n"
                    + "merc.referencia\n"
                    + "FROM forprod pf\n"
                    + "LEFT JOIN (SELECT id_cadmer, referencia, codfor\n"
                    + "       FROM cadmer_referencia) merc ON merc.id_cadmer = pf.CODPROD\n"
                    + "                                    AND merc.codfor = pf.CODFOR"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("CODPROD"));
                    imp.setIdFornecedor(rst.getString("CODFOR"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    imp.setCustoTabela(rst.getDouble("valor"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "c.codigo,\n"
                    + "c.nome,\n"
                    + "c.nascimento,\n"
                    + "c.estcivil,\n"
                    + "c.sexo,\n"
                    + "c.rg,\n"
                    + "c.orgemissor,\n"
                    + "c.cpf,\n"
                    + "c.natura,\n"
                    + "c.nacional,\n"
                    + "c.pai,\n"
                    + "c.mae,\n"
                    + "c.conjuge,\n"
                    + "c.conjnasc,\n"
                    + "c.conjcpf,\n"
                    + "c.conjrg,\n"
                    + "c.conjrgorg,\n"
                    + "c.casamento,\n"
                    + "c.numero,\n"
                    + "c.compl,\n"
                    + "c.logr,\n"
                    + "c.bairro,\n"
                    + "c.cidade,\n"
                    + "c.uf,\n"
                    + "c.cep,\n"
                    + "c.telefone,\n"
                    + "c.empresa,\n"
                    + "c.funcao,\n"
                    + "c.empnum,\n"
                    + "c.empend,\n"
                    + "c.empendnum,\n"
                    + "c.empcompl,\n"
                    + "c.empbairro,\n"
                    + "c.empcid,\n"
                    + "c.empuf,\n"
                    + "c.empfone,\n"
                    + "c.admissao,\n"
                    + "c.refpess,\n"
                    + "c.refpessfon,\n"
                    + "c.empramal,\n"
                    + "c.banco1,\n"
                    + "c.agencia1,\n"
                    + "c.conta1,\n"
                    + "c.banco2,\n"
                    + "c.agencia2,\n"
                    + "c.conta2,\n"
                    + "c.email,\n"
                    + "c.renda,\n"
                    + "c.obs,\n"
                    + "c.limite,\n"
                    + "c.situacao,\n"
                    + "c.bloqueado,\n"
                    + "c.motivobloq,\n"
                    + "c.cgc,\n"
                    + "c.inscr,\n"
                    + "c.fax,\n"
                    + "c.tipo,\n"
                    + "c.fantasia,\n"
                    + "c.anotacoes,\n"
                    + "c.cod_mun,\n"
                    + "c.id_pais,\n"
                    + "c.natureza_juridica\n"
                    + "FROM clientes c"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("compl"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataNascimento(rst.getDate("nascimento"));

                    if ((rst.getString("cpf") != null)
                            && (!rst.getString("cpf").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cpf").trim());
                    } else if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cgc").trim());
                    } else {
                        imp.setCnpj("");
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("rg").trim());
                    } else if ((rst.getString("inscr") != null)
                            && (!rst.getString("inscr").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("inscr").trim());
                    } else {
                        imp.setInscricaoMunicipal("ISENTO");
                    }

                    imp.setOrgaoemissor(rst.getString("orgemissor"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setCargo(rst.getString("funcao"));
                    imp.setEmpresaEndereco(rst.getString("empend"));
                    imp.setEmpresaNumero(rst.getString("empendnum"));
                    imp.setEmpresaComplemento(rst.getString("empcompl"));
                    imp.setEmpresaBairro(rst.getString("empbairro"));
                    imp.setEmpresaMunicipio(rst.getString("empcid"));
                    imp.setEmpresaUf(rst.getString("empuf"));
                    imp.setEmpresaTelefone(rst.getString("empfone"));
                    imp.setDataAdmissao(rst.getDate("admissao"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setObservacao(rst.getString("obs") + " " + rst.getString("anotacoes"));

                    imp.setBloqueado(rst.getInt("bloqueado") == 0 ? false : true);

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                Utils.formataNumero(rst.getString("fax").trim()),
                                null,
                                null
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
