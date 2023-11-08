/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
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
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;

/**
 *
 * @author Michael
 */
public class G3DAO2_5 extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "G3";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "     m1.idgrupo as m1grupo,\n"
                    + "     m1.nome as m1desc,\n"
                    + "     m2.idSubGrupo as m2subgrupo,\n"
                    + "     m2.Nome as m2desc,\n"
                    + "     m3.idsubgrupo1 as m3subgrupo2,\n"
                    + "     m3.nome as m3desc\n"
                    + "from grupo m1 \n"
                    + "	left join subgrupo m2\n"
                    + "		on m2.idGrupo = m1.idgrupo \n"
                    + "	left join subgrupo1 m3\n"
                    + "		on m3.idsubgrupo = m2.idSubGrupo and m3.idsubgrupo = m2.idSubGrupo \n"
                    + "order by m1grupo, m2subgrupo, m3subgrupo2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1grupo"));
                    imp.setMerc1Descricao(rst.getString("m1desc"));
                    imp.setMerc2ID(rst.getString("m2subgrupo"));
                    imp.setMerc2Descricao(rst.getString("m2desc"));
                    imp.setMerc3ID(rst.getString("m3subgrupo2"));
                    imp.setMerc3Descricao(rst.getString("m3desc"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.idproduto AS id,\n"
                    + "	p.descricao,\n"
                    + "	p.descrred reduzida,\n"
                    + "	descricaoetq gondola,\n"
                    + "	p.idgrupo AS mercadologico1,\n"
                    + "	p.idsubgrupo as mercadologico2,\n"
                    + "	p.idsubgrupo1 as mercadologico3,\n"
                    + "	pp.margem,\n"
                    + "	p.idFamilia as id_familia,\n"
                    + "	pp.custo custosemimposto,\n"
                    + "	pp.custo custocomimposto,\n"
                    + "	pp.venda1 precovenda,\n"
                    + "	dtcadastro datacadastro,\n"
                    + "	coalesce(pesoproduto,0) pesobruto,\n"
                    + "	coalesce(pesovariavel,0) pesoliquido,\n"
                    + "	estmax estoquemaximo,\n"
                    + "	estmin estoqueminimo,\n"
                    + "	estoque_atual AS estoque,\n"
                    + "	ean,\n"
                    + "	unidsaida tipoembalagem,\n"
                    + "	classfiscal AS ncm,\n"
                    + "	p.cest as cest,\n"
                    + "	p.idsituacao,\n"
                    + "	concat('0', substr(p.tabIcmsProdEntrada, 1, 3)) as icms_cst_e,\n"
                    + "	p.IcmsCompra as icms_alqt_e,\n"
                    + "	p.RedBase as icms_rbc_e,\n"
                    + "	concat('0', substr(p.TabIcmsProd, 1, 3)) as icms_cst_s,\n"
                    + "	p.Icms as icms_alqt_s,\n"
                    + "	p.RedBaseVenda as icms_rbc_s,\n"
                    + "	substr(p.CST_PIS,1,2) as piscofins_cst_e,\n"
                    + "	substr(p.CST_PIS_SAIDA,1,2) as piscofins_cst_s,\n"
                    + " p.icmscompra aliquota,\n"
                    + " p.RedBase reducao,\n"
                    + " substring(p.tabicmsprodentrada,1,2) cst,\n"
                    + "	coalesce(nat_receita,'') naturezareceita,\n"
                    + " p.sittrib as icms \n"
                    + "FROM produto p \n"
                    + "	left join produto_estoque pe\n"
                    + "		on pe.idproduto = p.idproduto\n"
                    + "	left join produto_preco pp\n"
                    + "		on pp.idproduto = p.idproduto and pe.id_loja = pp.id_loja\n"
                    + " left join cadtributacao ct\n"
                    + "		on p.SitTrib = ct.idCadTributacao"
            )) {
                while (rst.next()) {
                    Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    //                  imp.seteBalanca("1".equals(rst.getString("PesoVariavel")));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(rst.getString("reduzida"));
                    if (rst.getString("gondola") == null || rst.getString("gondola").isEmpty()) {
                        imp.setDescricaoGondola("reduzida");
                    } else {
                        imp.setDescricaoGondola("gondola");
                    }
                    //imp.setDescricaoGondola(rst.getString("gondola") == null ? rst.getString("reduzida") : rst.getString("gondola"));
                    imp.setSituacaoCadastro(rst.getInt("idsituacao") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setTipoEmbalagem(Utils.acertarTexto(rst.getString("tipoembalagem")));
//                    imp.setValidade(rst.getInt("dias"));
                    imp.setIdFamiliaProduto(rst.getString("id_familia"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins_cst_s"));
                    imp.setPiscofinsCstCredito(rst.getInt("piscofins_cst_e"));
                    imp.setPiscofinsNaturezaReceita(rst.getInt("naturezareceita"));

//                    imp.setIcmsCst(rst.getInt("cst"));
//                    imp.setIcmsAliq(rst.getDouble("aliquota"));
//                    imp.setIcmsReducao(rst.getDouble("reducao"));
                    imp.setIcmsConsumidorId(rst.getString("icms"));
                    imp.setIcmsDebitoId(rst.getString("icms"));
                    imp.setIcmsCreditoId(rst.getString("icms"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("icms"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("icms"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("icms"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(Utils.acertarTexto(rst.getString("tipoembalagem")));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "idProduto as id_produto,\n"
                    + "CodigoEan as ean,\n"
                    + "qtde_emb as emb \n"
                    + "from produto_ean "
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("emb"));

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
            try (ResultSet rs = stm.executeQuery(
                    "	select  idFamilia , nome from familia f ")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("idFamilia"));
                    imp.setDescricao(rs.getString("nome"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.idfornecedor,\n"
                    + "	f.nome,\n"
                    + "	f.fantasia,\n"
                    + "	f.CPF_CGC AS cnpj,\n"
                    + "	f.RG_IE ie,\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.cep,\n"
                    + "	f.CIDADE,\n"
                    + "	f.codmunicipio,\n"
                    + "	f.uf,\n"
                    + "	f.contato,\n"
                    + "	f.email,\n"
                    + "	f.fax,\n"
                    + "	f.telefone,\n"
                    + "	f.DTCADASTRO,\n"
                    + " f.obs\n"
                    + "FROM fornecedor f\n"
                    + "ORDER BY f.idfornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idfornecedor"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));

                    imp.setTel_principal(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "CELULAR",
                                null,
                                rst.getString("contato"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "  idFornecedor,\n"
                    + "  idProduto,\n"
                    + "  Referencia codexterno,\n"
                    + "  Embalagem qtdembalagem\n"
                    + "from\n"
                    + "  itensfornecedor\n"
                    + "order by \n"
                    + "	idfornecedor "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setCodigoExterno(rst.getString("codexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	idcliente id,\n"
                    + "	cpf,\n"
                    + " rg,\n"
                    + "	nome razao,\n"
                    + "	nome fantasia,\n"
                    + "	status_cliente ativo,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	codmunicipio municipioIBGE,\n"
                    + "	cidade,\n"
                    + "	cUf ufIBGE,\n"
                    + "	uf estado,\n"
                    + "	cep,\n"
                    + "	tipo,\n"
                    + "	dt_nasc dataNascimento,\n"
                    + "	dtabertura dataCadastro,\n"
                    + "	coalesce(empresa,'') empresa,\n"
                    + "	coalesce(fone_emp,'') empresaTelefone,\n"
                    + "	salario,\n"
                    + "	limite valorLimite,\n"
                    + "	coalesce(conjuge,'') nomeConjuge,\n"
                    + "	obs observacao,\n"
                    + "	coalesce(vencimento,'') diaVencimento,\n"
                    + "	fone telefone,\n"
                    + "	celular,\n"
                    + "	coalesce(email,'') email,\n"
                    + "	enderecocob cobrancaEndereco,\n"
                    + "	numerocob cobrancaNumero,\n"
                    + "	complementocob cobrancaComplemento,\n"
                    + "	bairrocob cobrancaBairro,\n"
                    + "	cidadecob cobrancaMunicipio,\n"
                    + "	ufcob cobrancaUf,\n"
                    + "	cepcob cobrancaCep\n"
                    + "from cliente c  "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rst.getString("razao")));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setFantasia(Utils.acertarTexto(rst.getString("fantasia")));
                    imp.setEndereco(Utils.acertarTexto(rst.getString("endereco")));
                    imp.setBairro(Utils.acertarTexto(rst.getString("bairro")));
                    imp.setMunicipio(Utils.acertarTexto(rst.getString("cidade")));
                    imp.setUf(Utils.acertarTexto(rst.getString("estado")));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setDataNascimento(rst.getDate("dataNascimento"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setCnpj(rst.getString("cpf"));

                    if (rst.getString("tipo") == null) {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                    } else if (rst.getString("tipo").equals("TT")) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteCreditoRotativo(true);
                    } else if (rst.getString("tipo").equals("CH")) {
                        imp.setPermiteCheque(true);
                        imp.setPermiteChequeAVista(true);
                        imp.setPermiteCreditoRotativo(false);
                    } else if (rst.getString("tipo").equals("CR")) {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(true);
                    } else if (rst.getString("tipo") == null) {
                        imp.setPermiteCheque(false);
                        imp.setPermiteCreditoRotativo(false);
                    }

                    imp.setTelefone(Utils.formataNumero(rst.getString("telefone")));
                    imp.setCelular(Utils.formataNumero(rst.getString("celular")));
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.setEmail(Utils.acertarTexto(rst.getString("email")).toLowerCase());
                    } else {
                        imp.setEmail("");
                    }

                    imp.setObservacao(rst.getString("observacao"));

                    imp.setValorLimite(rst.getDouble("valorlimite"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    imp.setCobrancaEndereco(Utils.acertarTexto(rst.getString("cobrancaendereco")));
                    imp.setCobrancaBairro(Utils.acertarTexto(rst.getString("cobrancabairro")));
                    imp.setCobrancaMunicipio(Utils.acertarTexto(rst.getString("cobrancamunicipio")));
                    imp.setCobrancaUf(Utils.acertarTexto(rst.getString("cobrancauf")));
                    imp.setCobrancaCep(Utils.acertarTexto(rst.getString("cobrancacep")));

                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	r.iddebito id,\n"
                    + "	dt_venda dataEmissao,\n"
                    + "	nr_venda numeroCupom,\n"
                    + "	ecf,\n"
                    + "	vl_vista valor,\n"
                    + "	dr.Vl_Recebido ,\n"
                    + "	coalesce ((Vl_Vista - dr.Vl_Recebido),Vl_Vista)as total,\n"
                    + "	observacao,\n"
                    + "	r.idCliente,\n"
                    + "	cpf cnpjCliente,\n"
                    + "	dt_venc dataVencimento\n"
                    + "from debito r \n"
                    + "left join cliente c on r.IDCLIENTE = c.idCliente \n"
                    + "left join debito_recebido dr on r.IDDebito = dr.IDDebito \n"
                    + "where SITUACAO != 'P'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));

                    imp.setValor(rst.getDouble("total"));
                    //                   imp.setParcela(rst.getInt("recparc"));
                    imp.setObservacao(rst.getString("observacao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    " select distinct\n"
                    + "	 idCadTributacao id,\n"
                    + "	 c.descricao,\n"
                    + "	 aliquotaIcms,\n"
                    + "	 coalesce(RedBase,0) reducao,\n"
                    + "	 v.icms_cst_e cst\n"
                    + "from\n"
                    + "	 cadtributacao c \n"
                    + "	   left join produto p on c.idCadTributacao = p.SitTrib\n"
                    + "	   join mxf_vw_produtos v on v.codigo_produto = p.idProduto "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            Utils.stringToInt(rs.getString("cst")),
                            rs.getInt("aliquotaIcms"),
                            rs.getInt("reducao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	idchequepre id,\n"
                    + "	cgc_cpf cpf,\n"
                    + "	cheque numerocheque,\n"
                    + "	banco,\n"
                    + "	ch.agencia,\n"
                    + "	ch.conta,\n"
                    + "	emissao,\n"
                    + "	dt_baixa datadeposito,\n"
                    + "	cupom numerocupom,\n"
                    + "	Venc as vencimento, \n"
                    + "	ecf,\n"
                    + "	valor,\n"
                    + "	c.rg,\n"
                    + "	c.fone telefone,\n"
                    + "	c.nome,\n"
                    + "	ch.obs observacao,\n"
                    + "	situacao situacaocheque,\n"
                    + "	datahora_alteracao alteracao\n"
                    + "from\n"
                    + "	chequepre ch\n"
                    + "	left join cliente c\n"
                    + "		on c.idCliente = ch.idCliente \n"
                    + "where Situacao != 'P'"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setConta(rst.getString("conta"));
                    imp.setNumeroCheque(rst.getString("numerocheque"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNome(rst.getString("nome"));
                    imp.setBanco(Integer.parseInt(Utils.formataNumero(rst.getString("banco"))));
                    imp.setCpf(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setDataDeposito(rst.getDate("vencimento"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }
    /*
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new G3DAO2_5.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new G3DAO2_5.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();
        int cont = 1;

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        //String id = rst.getString("id") + "-" + rst.getString("numerocupom") + "-" + rst.getString("ecf");
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        if (cont == 18) {
                            cont = 1;
                        }
                        next.setEcf(Utils.stringToInt(rst.getString(cont++)));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));
                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setCpf(rst.getString("cpf"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                        if (rst.getString("nomecliente") != null
                                && !rst.getString("nomecliente").trim().isEmpty()
                                && rst.getString("nomecliente").trim().length() > 45) {

                            next.setNomeCliente(rst.getString("nomecliente").substring(0, 45));
                        } else {
                            next.setNomeCliente(rst.getString("nomecliente"));
                        }

                        String endereco
                                = Utils.acertarTexto(rst.getString("endereco")) + ","
                                + Utils.acertarTexto(rst.getString("numero")) + ","
                                + Utils.acertarTexto(rst.getString("comple")) + ","
                                + Utils.acertarTexto(rst.getString("bairro")) + ","
                                + Utils.acertarTexto(rst.getString("cidade")) + "-"
                                + Utils.acertarTexto(rst.getString("estado"));
                        next.setEnderecoCliente(endereco);
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "select\n"
                    //+ "	concat(seq,cupom,data) id,\n"
                    + "	v.seq id_venda,\n"
                    + "	v.seq numerocupom,\n"
                    + "	codcli idcliente,\n"
                    + "	case micro\n"
                    + "		when 'PDV01' then 1\n"
                    + "		when 'PDV02' then 2\n"
                    + "		when 'PDV03' then 3\n"
                    + "		when 'PDV04' then 4\n"
                    + "	end ecf,\n"
                    + "	data,\n"
                    + "	horaini horainicio,\n"
                    + "	horafim horatermino,\n"
                    + "	bruto subtotalimpressora,\n"
                    + "	cpf,\n"
                    + "	desconto,\n"
                    + "	acrescim,\n"
                    + "	nome nomecliente,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	comple,\n"
                    + "	bairro,\n"
                    + "	cidade,\n"
                    + "	uf estado\n"
                    + "from\n"
                    + "	tslv010 v\n"
                    + "where\n"
                    + "	empresa = substring('" + idLojaCliente + "',1,1)\n"
                    + "	and exclui != 'S'\n"
                    + "	and data between '" + strDataInicio + "' and '" + strDataTermino + "'\n"
                    + "order by seq,data,horaini";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }

    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        //String id = rst.getString("id_venda");
                        //String idItem = rst.getString("id_item");

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setProduto(rst.getString("produto"));
                        //next.setSequencia(rst.getInt("nroitem"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        //next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "select\n"
                    + "	i.cupom id_venda,\n"
                    + "	i.seq id_item,\n"
                    + "	i.cupom numerocupom,\n"
                    + "	codprod produto,\n"
                    + "	codbarras codigobarras,\n"
                    + "	unidade,\n"
                    + "	descricao,\n"
                    + "	quant quantidade,\n"
                    + "	unit precovenda,\n"
                    + "	i.total,\n"
                    + "	i.desconto,\n"
                    + "	case when i.exclui = 'S' then 1 else 0 end cancelado,\n"
                    + "	case micro\n"
                    + "		when 'PDV01' then 1\n"
                    + "		when 'PDV02' then 2\n"
                    + "		when 'PDV03' then 3\n"
                    + "		when 'PDV04' then 4\n"
                    + "	end ecf,\n"
                    + "	v.data data\n"
                    + "from\n"
                    + "	tslv011 i\n"
                    + "	join tslv010 v on v.seq = i.cupom\n"
                    + "where\n"
                    + "	empresa = substring('" + idLojaCliente + "',1,1)\n"
                    + "	and i.exclui != 'S'\n"
                    + "	and data between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by i.cupom,i.seq";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }*/

}
