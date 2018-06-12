package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.InventarioIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class AvanceDAO extends InterfaceDAO implements MapaTributoProvider {

    private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");
    
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
                    imp.setSituacaoCadastro((rst.getInt("situacaocadastro") == 1 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO));
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
                    "SELECT aliquota id, CONCAT(descricao,'  |cst:' ,cst) descricao FROM aliquota ORDER BY 1"
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
                                (rst.getString("VENDEDOR").length() > 30 ? rst.getString("VENDEDOR").substring(0, 30) : rst.getString("VENDEDOR").trim()),
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
                                (rst.getString("supervisor").length() > 30 ? rst.getString("supervisor").substring(0, 30) : rst.getString("supervisor").trim()),
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
                    + "c.natureza_juridica,\n"
                    + "e.endereco\n"
                    + "FROM clientes c\n"
                    + "LEFT JOIN clientes_enderecos e on e.cliente = c.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
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
                        imp.setInscricaoestadual(rst.getString("rg").trim().replace("'", ""));
                    } else if ((rst.getString("inscr") != null)
                            && (!rst.getString("inscr").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("inscr").trim().replace("'", ""));
                    } else {
                        imp.setInscricaoMunicipal("ISENTO");
                    }

                    imp.setOrgaoemissor(rst.getString("orgemissor").replace("'", ""));
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

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "id,\n"
                    + "emissao,\n"
                    + "vencimento,\n"
                    + "documento,\n"
                    + "codcli,\n"
                    + "cupom,\n"
                    + "valor_original,\n"
                    + "valor,\n"
                    + "valorpago,\n"
                    + "(valor - valorpago) valorconta,\n"
                    + "historico,\n"
                    + "caixa\n"
                    + "FROM receb\n"
                    + "WHERE pagamento IS NULL\n"
                    + "AND codcli IS NOT NULL\n"
                    + "AND id_conta <> 2"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("codcli"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valorconta"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setObservacao(rst.getString("historico"));
                    if (rst.getDouble("valor_original") > 0) {
                        imp.setObservacao(imp.getObservacao() + " VALOR ORIGINAL DA CONTA " + rst.getDouble("valor_original"));
                    }
                    if (rst.getDouble("valorpago") > 0) {
                        imp.setObservacao(imp.getObservacao() + " VALOR PAGO " + rst.getDouble("valorpago"));
                    }
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "ch.id, \n"
                    + "ch.emissao, \n"
                    + "ch.documento, \n"
                    + "ch.vencimento,\n"
                    + "ch.cupom,\n"
                    + "ch.valor,\n"
                    + "ch.valorpago,\n"
                    + "(ch.valor - ch.valorpago) valorconta, \n"
                    + "ch.historico,\n"
                    + "ch.caixa,\n"
                    + "b.codigo,\n"
                    + "ch.ncheque,\n"
                    + "ch.numbanco,\n"
                    + "ch.agencia,\n"
                    + "ch.cpfcgc,\n"
                    + "ch.caixa,\n"
                    + "ch.datahora_alteracao,\n"
                    + "ch.cpfcgc AS cpfcnpj_cheque,\n"
                    + "c.cpf AS cpf_cliente,\n"
                    + "c.rg AS rg_cliente,\n"
                    + "c.nome AS nome_cliente,\n"
                    + "c.cgc AS cgc_cliente,\n"
                    + "c.inscr AS inscr_cliente,\n"
                    + "c.telefone AS telefone_cliente\n"
                    + "FROM receb ch\n"
                    + "LEFT JOIN bancos b ON b.id = ch.id_banco\n"
                    + "LEFT JOIN clientes c ON c.codigo = ch.codcli\n"
                    + "WHERE ch.id_conta = 2\n"
                    + "AND pagamento IS NULL"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpfcnpj_cheque"));
                    imp.setNumeroCheque(rst.getString("ncheque"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setBanco(rst.getInt("codigo"));
                    imp.setNumeroCupom(rst.getString("documento"));
                    imp.setEcf(rst.getString("caixa"));
                    imp.setValor(rst.getDouble("valorconta"));
                    imp.setObservacao(rst.getString("historico"));
                    imp.setNome(rst.getString("nome_cliente"));
                    imp.setTelefone(rst.getString("telefone_cliente"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setDataDeposito(rst.getDate("vencimento"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("datahora_alteracao"));
                    imp.setAlinea(0);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<InventarioIMP> getInventario(Date dataInventario) throws Exception {
        List<InventarioIMP> result = new ArrayList<>();
        String nomeTable = "";

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT nome_inventario as nomeTabela "
                    + "FROM inventarios_cab "
                    + "WHERE data_inventario = '" + FORMAT.format(dataInventario) + "' \n"
                    + "AND nome_inventario NOT LIKE '%_1'\n"
                    + "AND id_loja = " + getLojaOrigem()
            )) {
                if (rst.next()) {
                    nomeTable = rst.getString("nomeTabela");
                    System.out.println("Tabela: " + rst.getString("nomeTabela"));
                }
            }

            if ((nomeTable != null) && (!nomeTable.trim().isEmpty())) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "i.Codigo, "
                        + "i.DESCRICAO, "
                        + "i.ALIQUOTA,\n"
                        + "i.CUSTO, "
                        + "i.VENDA, "
                        + "i.LOJAEST, "
                        + "i.CUSMEDIO,\n"
                        + "i.dt_inv, "
                        + "i.aliq_pis_sai, "
                        + "i.aliq_cofins_ent\n"
                        + "FROM " + nomeTable + " i"
                )) {
                    while (rst.next()) {
                        InventarioIMP imp = new InventarioIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setId(getLojaOrigem() + "-" + rst.getString("dt_inv") + "-" + rst.getString("Codigo"));
                        imp.setData(dataInventario);
                        imp.setDataGeracao(dataInventario);
                        imp.setIdProduto(rst.getString("Codigo"));
                        imp.setDescricao(rst.getString("DESCRICAO"));
                        imp.setCustoComImposto(rst.getDouble("CUSTO"));
                        imp.setCustoSemImposto(imp.getCustoComImposto());
                        imp.setCustoMedioComImposto(rst.getDouble("CUSMEDIO"));
                        imp.setCustoMedioSemImposto(imp.getCustoMedioComImposto());
                        imp.setPrecoVenda(rst.getDouble("VENDA"));
                        imp.setQuantidade(rst.getDouble("LOJAEST"));
                        imp.setPis(rst.getDouble("aliq_pis_sai"));
                        imp.setCofins(rst.getDouble("aliq_cofins_ent"));
                        imp.setIdAliquotaDebito(rst.getString("ALIQUOTA"));
                        imp.setIdAliquotaCredito(rst.getString("ALIQUOTA"));
                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }
}
