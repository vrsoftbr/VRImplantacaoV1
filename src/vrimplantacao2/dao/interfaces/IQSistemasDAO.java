/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEstadoCivil;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class IQSistemasDAO extends InterfaceDAO {

    public boolean arquivoBalanca = false;
    
    @Override
    public String getSistema() {
        return "IQSistemas";
    }

    public List<Estabelecimento> getLojasCliente() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "CodigoFilial, \n"
                    + "empresa, \n"
                    + "cnpj \n"
                    + "FROM filiais\n"
                    + "ORDER BY CodigoFilial"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("CodigoFilial"), rst.getString("cnpj") + " - " + rst.getString("empresa")));
                }
            }
        }
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MAPA_TRIBUTACAO,
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.IMPORTAR_RESETAR_BALANCA,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.PESO_BRUTO,
            OpcaoProduto.PESO_LIQUIDO,
            OpcaoProduto.ESTOQUE_MINIMO,
            OpcaoProduto.ESTOQUE_MAXIMO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ICMS,
            OpcaoProduto.MARGEM,
            OpcaoProduto.ATIVO
        }));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "g.codigo,\n"
                    + "g.grupo AS descgrupo,\n"
                    + "sg.codigosubgrupo,\n"
                    + "sg.subgrupo AS descsubgrupo\n"
                    + "FROM grupos g\n"
                    + "INNER JOIN subgrupos sg ON sg.grupo = g.grupo\n"
                    + "ORDER BY g.codigo, sg.codigosubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("codigo"));
                    imp.setMerc1Descricao(rst.getString("descgrupo"));
                    imp.setMerc2ID(rst.getString("codigosubgrupo"));
                    imp.setMerc2Descricao(rst.getString("descsubgrupo"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
                    "SELECT \n"
                    + "p.codigo AS id,\n"
                    + "p.codigobarras,\n"
                    + "p.descricao,\n"
                    + "CASE p.unidade \n"
                    + "WHEN 'GR' THEN 'KG'\n"
                    + "WHEN 'KG' THEN 'KG'\n"
                    + "WHEN 'FR' THEN 'KG'\n"
                    + "ELSE 'UN'\n"
                    + "END tipoembalagem,\n"
                    + "p.embalagem AS qtdembalagem,\n"
                    + "p.datacadastro AS datacadastro,\n"
                    + "p.tributacao AS csticms,\n"
                    + "p.icms AS aliqicms,\n"
                    + "p.percentualRedICMsST AS redIcms,\n"
                    + "p.ncm,\n"
                    + "p.cest,\n"
                    + "p.tributacaoPIS,\n"
                    + "p.tributacaoCOFINS,\n"
                    + "p.cstpisEntrada,\n"
                    + "p.cstcofinsEntrada,\n"
                    + "p.codigosuspensaopis as naturezareceita,\n"
                    + "g.codigo as codigogrupo,\n"
                    + "p.grupo,\n"
                    + "s.codigosubgrupo,\n"
                    + "p.subgrupo,\n"
                    + "p.custofornecedor as custo,\n"
                    + "p.margemlucro,\n"
                    + "p.precovenda,\n"
                    + "p.estminimo,\n"
                    + "p.quantidade AS estoque,\n"
                    + "p.validade,\n"
                    + "p.pesobruto,\n"
                    + "p.pesoliquido,\n"
                    + "p.situacao,\n"
                    + "p.unidade\n"
                    + "FROM produtos p\n"
                    + "LEFT JOIN grupos g ON g.grupo = p.grupo\n"
                    + "LEFT JOIN subgrupos s ON s.subgrupo = p.subgrupo\n"
                    + "WHERE p.CodigoFilial = '" + getLojaOrigem() + "'\n"
                    //+ "AND p.situacao LIKE '%Balança%'\n"
                    //+ "AND p.codigo NOT LIKE '0000%'\n"
                    //+ "AND CHAR_LENGTH(p.codigo) < 7\n"
                    + "ORDER BY p.codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(Utils.formataNumero(rst.getString("codigobarras")));
                    imp.seteBalanca(rst.getString("situacao").contains("Item da Bala"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.setCodMercadologico1(rst.getString("codigogrupo"));
                    imp.setCodMercadologico2(rst.getString("codigosubgrupo"));
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("margemlucro"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setSituacaoCadastro(rst.getString("situacao").contains("Inativo") ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("tributacaoPIS"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpisEntrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));
                    imp.setIcmsCst(rst.getInt("cstIcms"));
                    imp.setIcmsAliq(rst.getDouble("aliqicms"));
                    imp.setIcmsReducao(rst.getDouble("redIcms"));
                    result.add(imp);
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
                    + "f.razaosocial AS razao,\n"
                    + "f.empresa AS fantasia,\n"
                    + "f.CGC AS cnpj,\n"
                    + "f.INSCRICAO AS ie_rg,\n"
                    + "f.CPF,\n"
                    + "f.ENDERECO,\n"
                    + "f.numero,\n"
                    + "f.CEP,\n"
                    + "f.BAIRRO,\n"
                    + "f.CIDADE,\n"
                    + "f.ESTADO,\n"
                    + "f.TELEFONE,\n"
                    + "f.TELEFONE2,\n"
                    + "f.TELEFONE3,\n"
                    + "f.FAX,\n"
                    + "f.FAX2,\n"
                    + "f.FAX3,\n"
                    + "f.EMAIL,\n"
                    + "f.DATACAD,\n"
                    + "f.situacao,\n"
                    + "f.OBSERVACAO\n"
                    + "FROM fornecedores f\n"
                    + "WHERE f.fornecedor = 'S'\n"
                    + "AND f.CodigoFilial = '" + getLojaOrigem() + "'\n"
                    + "ORDER BY f.Codigo;"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("ESTADO"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setDatacadastro(rst.getDate("DATACAD"));
                    imp.setAtivo(rst.getString("situacao").contains("Ativo"));
                    imp.setObservacao(rst.getString("OBSERVACAO"));

                    if ((rst.getString("TELEFONE2") != null)
                            && (!rst.getString("TELEFONE2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("TELEFONE2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("TELEFONE3") != null)
                            && (!rst.getString("TELEFONE3").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 3",
                                rst.getString("TELEFONE3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("FAX"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("FAX2") != null)
                            && (!rst.getString("FAX2").trim().isEmpty())) {
                        imp.addContato(
                                "FAX2",
                                rst.getString("FAX2"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("FAX3") != null)
                            && (!rst.getString("FAX3").trim().isEmpty())) {
                        imp.addContato(
                                "FAX3",
                                rst.getString("FAX3"),
                                null,
                                TipoContato.NFE,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("EMAIL").toLowerCase()
                        );
                    }
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT codigo as idproduto, idfornecedor, custo, documento,\n"
                    + "  MAX(dataultent) AS dataalteracao\n"
                    + " FROM produtosinventario \n"
                    + "WHERE codigo IS NOT NULL\n"
                    + "  AND idfornecedor IS NOT NULL\n"
                    + "GROUP BY codigo, idfornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("documento"));
                    imp.setCustoTabela(rst.getDouble("custo"));
                    imp.setDataAlteracao(rst.getDate("dataalteracao"));
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
                    + "c.Codigo,\n"
                    + "c.Nome,\n"
                    + "c.apelido,\n"
                    + "c.cnpj,\n"
                    + "c.inscricao,\n"
                    + "c.cpf,\n"
                    + "c.identidade AS rg,\n"
                    + "c.endereco,\n"
                    + "c.numero,\n"
                    + "c.cep,\n"
                    + "c.bairro,\n"
                    + "c.cidade,\n"
                    + "c.estado,\n"
                    + "c.telefone,\n"
                    + "c.telefone2,\n"
                    + "c.telefone3,\n"
                    + "c.fax,\n"
                    + "c.celular,\n"
                    + "c.email,\n"
                    + "c.datacadastro,\n"
                    + "c.nascimento,\n"
                    + "c.localtrabalho,\n"
                    + "c.profissao,\n"
                    + "c.salario,\n"
                    + "c.enderecotrab,\n"
                    + "c.bairrotrab,\n"
                    + "c.ceptrab,\n"
                    + "c.cidadetrab,\n"
                    + "c.estadotrab,\n"
                    + "c.telefonetrab,\n"
                    + "c.cnpjtrab,\n"
                    + "c.credito,\n"
                    + "c.situacao AS situacaocadastro,\n"
                    + "c.sexo,\n"
                    + "c.estadocivil,\n"
                    + "c.conjuge,\n"
                    + "c.cpfconj,\n"
                    + "c.identidadeconj,\n"
                    + "c.nascimentoconj,\n"
                    + "c.pai,\n"
                    + "c.mae,\n"
                    + "c.ultcompra,\n"
                    + "c.ultpagamento,\n"
                    + "c.ultvrpago,\n"
                    + "c.observacao\n"
                    + "FROM clientes c;"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("Codigo"));
                    imp.setRazao(rst.getString("Nome"));
                    imp.setFantasia(rst.getString("apelido"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    //imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setFax(rst.getString("fax"));
                    imp.setEmail(rst.getString("email") != null ? rst.getString("email").toLowerCase() : null);

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                rst.getString("telefone3"),
                                null,
                                null
                        );
                    }

                    imp.setEmpresa(rst.getString("localtrabalho"));
                    imp.setCargo(rst.getString("profissao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setEmpresaEndereco(rst.getString("enderecotrab"));
                    imp.setEmpresaCep(rst.getString("ceptrab"));
                    imp.setEmpresaBairro(rst.getString("bairrotrab"));
                    imp.setEmpresaMunicipio(rst.getString("cidadetrab"));
                    imp.setEmpresaUf(rst.getString("estadotrab"));
                    imp.setEmpresaTelefone(rst.getString("telefonetrab"));
                    imp.setValorLimite(rst.getDouble("credito"));

                    if ((rst.getString("situacaocadastro") != null)
                            && (!rst.getString("situacaocadastro").trim().isEmpty())) {

                        if (rst.getString("situacaocadastro").contains("CANCEL")) {
                            imp.setAtivo(false);
                            imp.setBloqueado(true);
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                        } else if (rst.getString("situacaocadastro").contains("EXCLU")) {
                            imp.setAtivo(false);
                            imp.setBloqueado(true);
                            imp.setPermiteCheque(false);
                            imp.setPermiteCreditoRotativo(false);
                        } else if (rst.getString("situacaocadastro").contains("LIBERA")) {
                            imp.setAtivo(true);
                            imp.setBloqueado(false);
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                        } else if (rst.getString("situacaocadastro").contains("BLOQUE")) {
                            imp.setAtivo(true);
                            imp.setBloqueado(true);
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                        } else {
                            imp.setAtivo(true);
                            imp.setBloqueado(false);
                            imp.setPermiteCheque(true);
                            imp.setPermiteCreditoRotativo(true);
                        }
                    } else {
                        imp.setAtivo(false);
                        imp.setBloqueado(true);
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                    }

                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        imp.setSexo(rst.getString("sexo").contains("F") ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    }

                    if ((rst.getString("estadocivil") != null)
                            && (!rst.getString("estadocivil").trim().isEmpty())) {

                        if (rst.getString("estadocivil").contains("Solte")) {
                            imp.setEstadoCivil(TipoEstadoCivil.SOLTEIRO);
                        } else if (rst.getString("estadocivil").contains("casa")) {
                            imp.setEstadoCivil(TipoEstadoCivil.CASADO);
                        } else if (rst.getString("estadocivil").contains("viú")) {
                            imp.setEstadoCivil(TipoEstadoCivil.VIUVO);
                        } else if (rst.getString("estadocivil").contains("Sepera")) {
                            imp.setEstadoCivil(TipoEstadoCivil.DIVORCIADO);
                        } else {
                            imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                        }
                    } else {
                        imp.setEstadoCivil(TipoEstadoCivil.NAO_INFORMADO);
                    }

                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setObservacao(rst.getString("observacao"));

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
                    + "sequenciainc AS id, \n"
                    + "codigo AS codcliente,\n"
                    + "documento AS cupom,\n"
                    + "datacompra AS emissao,\n"
                    + "vencimento,\n"
                    + "parcela,\n"
                    + "valor,\n"
                    + "valoratual\n"
                    + "FROM crmovclientes \n"
                    + "WHERE CodigoFilial = '" + getLojaOrigem() + "'\n"
                    + "AND quitado = 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("codcliente"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    
                    if (rst.getString("vencimento").contains("0000-00-00")) {
                        imp.setDataVencimento(rst.getDate("emissao"));
                    } else {
                        imp.setDataVencimento(rst.getDate("vencimento"));
                    }
                    
                    imp.setValor(rst.getDouble("valoratual"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setParcela(rst.getInt("parcela"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
