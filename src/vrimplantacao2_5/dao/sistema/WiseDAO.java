/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javax.swing.JOptionPane;
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
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoFirebird;

/**
 *
 * @author Wagner
 */
public class WiseDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "WISE";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
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
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATACADO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.MARGEM_MAXIMA,
                OpcaoProduto.MARGEM_MINIMA,
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
                OpcaoFornecedor.CONDICAO_PAGAMENTO,
                OpcaoFornecedor.CONDICAO_PAGAMENTO2,
                OpcaoFornecedor.TELEFONE
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
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_CONVENIADO,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	ALIQUOTA id,\n"
                    + "	PORC_ICMS aliquota,\n"
                    + "	DESCRICAO\n"
                    + "FROM\n"
                    + "	ALIQ_BEMA ab "
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "  SELECT DISTINCT \n"
                    + "	DEPAR mercid1,\n"
                    + "	DEPAR descmerc1,\n"
                    + "	DEPAR mercid2,\n"
                    + "	DEPAR descmerc2,\n"
                    + "	CASE\n"
                    + "		WHEN NOMEGRUPO IS NULL THEN CAST('GENERICO' AS varchar(100) character set WIN1251) ELSE NOMEGRUPO \n"
                    + "	END mercid3,\n"
                    + "	CASE\n"
                    + "		WHEN NOMEGRUPO IS NULL THEN CAST('GENERICO' AS varchar(100) character set WIN1251) ELSE NOMEGRUPO \n"
                    + "	END descmerc3\n"
                    + "FROM \n"
                    + "	PRODUTOS p\n"
                    + "WHERE DEPAR IS NOT NULL "
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("descmerc1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("descmerc2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
                    imp.setMerc3Descricao(rst.getString("descmerc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {

        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.ORDEM Id,\n"
                    + "	p.DTCAD dataCadastro,\n"
                    + "	p.ULTALTERA dataAlteracao,\n"
                    + "	p.BARRA ean,\n"
                    + "	p.ULTQTDE qtdEmbalagem,\n"
                    + "	p.EMBALAGEM embEan,\n"
                    + "	p.UNIDADE tipoEmbalagem,\n"
                    + "	p.UNIDADE tipoEmbalagemCotacao,\n"
                    + "	CASE WHEN P.BALANCA = 'Y' THEN 1 ELSE 0 end e_balanca,\n"
                    + "	p.DESCRICAO  descricaoCompleta,\n"
                    + "	CASE WHEN p.APDO IS NULL OR p.APDO = '' THEN p.DESCRICAO ELSE p.APDO END descricaoReduzida,\n"
                    + "	p.DEPAR merc1,\n"
                    + "	p.DEPAR merc2,\n"
                    + "	p.NOMEGRUPO merc3,\n"
                    + "	p.PESOB pesoBruto,\n"
                    + "	p.PESOL pesoLiquido,\n"
                    + "	p.ESTOQUE estoque,\n"
                    + "	p.MARCKUP margem,\n"
                    + "	p.CUSTO custoSemImposto,\n"
                    + "	p.CUSTO custoComImposto,\n"
                    + "	p.VISTA precovenda,\n"
                    + "	CASE WHEN p.INATIVO = 'Y' THEN 0 ELSE 1 end situacaoCadastro,\n"
                    + "	p.NCM ncm,\n"
                    + "	p.CEST cest,\n"
                    + "	p.PIS piscofinsCstDebito,\n"
                    + "	p.COFINS piscofinsCstCredito,\n"
                    + "	null piscofinsNaturezaReceita,\n"
                    + "	p.CST Cst,\n"
                    + "	p.ALIQ idicmsAliqSaida,\n"
                    + "	p.REDUCAO_ICMS icmsReducao\n"
                    + "from\n"
                    + "	PRODUTOS p "
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setTipoEmbalagemVolume(rst.getString("tipoEmbalagem"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsCstCredito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsCstDebito"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setDataAlteracao(rst.getDate("dataAlteracao"));
                    imp.setEan(rst.getString("ean"));

                    imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoEmbalagemCotacao"));
                    imp.setQtdEmbalagem(rst.getInt("embEan"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdEmbalagem"));
                    imp.setPesoBruto(rst.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoLiquido"));
                    imp.setSituacaoCadastro(rst.getInt("situacaoCadastro"));
                    //imp.setSituacaoCadastro(rst.getInt("situacaoCadastro") == 0 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);
                    imp.setPiscofinsNaturezaReceita(rst.getInt("piscofinsNaturezaReceita"));
                    //imp.setFornecedorFabricante(rst.getString("fornec"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    imp.setIcmsDebitoId(rst.getString("idicmsAliqSaida"));
                    imp.setIcmsDebitoForaEstadoId(rst.getString("idicmsAliqSaida"));
                    imp.setIcmsDebitoForaEstadoNfId(rst.getString("idicmsAliqSaida"));
                    imp.setIcmsCreditoId(rst.getString("idicmsAliqSaida"));
                    imp.setIcmsCreditoForaEstadoId(rst.getString("idicmsAliqSaida"));
                    imp.setIcmsConsumidorId(rst.getString("idicmsAliqSaida"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO id, DESCRI descricao FROM FAMILIA f"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	f.CODIGO id,\n"
                    + "	f.RAZAO  razao,\n"
                    + "	f.ENDE  endereco,\n"
                    + "	f.BAIRRO bairro,\n"
                    + "	f.cep cep,\n"
                    + "	f.CIDADE municipio,\n"
                    + "	f.UF uf,\n"
                    + "	f.END_NUM numero,\n"
                    + "	f.IE inscestadual ,\n"
                    + "	f.CNPJ cpfcnpj,\n"
                    + "	f.FANTASIA  fantasia,\n"
                    + "	f.EMAIL email,\n"
                    + "	'1' ATIVO,\n"
                    + "	f.FONE1 fone,\n"
                    + "	f.FONE2 fone2,\n"
                    + "	f.FAX,\n"
                    + "	f.CELULAR\n"
                    + "from\n"
                    + "	FORNECEDORES f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("inscestadual"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(Utils.acertarTexto(rst.getString("fone")));

                    String fax = (rst.getString("fax"));
                    if (!"".equals(fax)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("1");
                        cont.setImportId("1");
                        cont.setNome("FAX");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(fax);
                    }

                    String email = (rst.getString("email"));
                    if (!"".equals(email)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("2");
                        cont.setImportId("2");
                        cont.setNome("EMAIL");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
                    }

                    String fone2 = (Utils.acertarTexto(rst.getString("fone2")));
                    if (!"".equals(fone2)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("3");
                        cont.setImportId("3");
                        cont.setNome("TELEFONE 2");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
                    }

                    String cel = (Utils.acertarTexto(rst.getString("celular")));
                    if (!"".equals(cel)) {
                        FornecedorContatoIMP cont = imp.getContatos().make("4");
                        cont.setImportId("4");
                        cont.setNome("CELULAR");
                        cont.setTipoContato(TipoContato.COMERCIAL);
                        cont.setTelefone(email);
                    }
                    imp.setAtivo((rst.getInt("ATIVO") == 1));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "  SELECT\n"
                    + "	p.ORDEMINT externo,\n"
                    + "	e.CODFORN||'.0' idfornec ,\n"
                    + "	p.ORDEM idprod\n"
                    + "FROM\n"
                    + "	EQUIV e\n"
                    + "JOIN PRODUTOS p ON\n"
                    + "	e.PARA = p.BARRA "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("idfornec"));
                    imp.setIdProduto(rst.getString("idprod"));
                    imp.setCodigoExterno(rst.getString("externo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT ORDEM id, BARRA ean, UNIDADE emb FROM PRODUTOS p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("emb"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "  	SELECT distinct\n"
                    + "	c.CODIGO id,\n"
                    + "    c.CGC cpfcnpj,\n"
                    + "    c.RAZAO razao,\n"
                    + "    c.NOME fantasia,\n"
                    + "    CASE WHEN c.BLOQ = 'True' THEN 0 ELSE 1 end ativo,\n"
                    + "    c.ENDERECO endereco,\n"
                    + "    c.END_NUM numero,\n"
                    + "    c.BAIRRO bairro,\n"
                    + "    c.CIDADE municipio,\n"
                    + "    c.UF uf,\n"
                    + "    c.CEP cep,\n"
                    + "    c.DATACADASTRO dataCadastro,\n"
                    + "    c.NASC dtnasc,\n"
                    + "    c.FONE,\n"
                    + "    c.FONE2 ,\n"
                    + "    c.EMAIL email,\n"
                    + "    c.CREDITO limite,\n"
                    + "    c.CELULAR celular\n"
                    + "FROM\n"
                    + "	CLIENTES c "
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setCelular(Utils.acertarTexto(rst.getString("celular")));
                    imp.setTelefone(Utils.acertarTexto(rst.getString("fone")));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEmail(rst.getString("email"));

                    if ((rst.getString("FONE2") != null)
                            && (!rst.getString("FONE2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FONE 2",
                                Utils.stringLong(rst.getString("FONE2")),
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
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "  	SELECT distinct\n"
                    + "	c.CODIGO id,\n"
                    + "    c.CGC cpfcnpj,\n"
                    + "    c.IE ie,\n"
                    + "    c.RAZAO razao,\n"
                    + "    c.NOME fantasia,\n"
                    + "    CASE WHEN c.BLOQ = 'True' THEN 0 ELSE 1 end ativo,\n"
                    + "    c.ENDERECO endereco,\n"
                    + "    c.END_NUM numero,\n"
                    + "    c.BAIRRO bairro,\n"
                    + "    c.CIDADE municipio,\n"
                    + "    c.UF uf,\n"
                    + "    c.CEP cep,\n"
                    + "    c.DATACADASTRO dataCadastro,\n"
                    + "    c.NASC dtnasc,\n"
                    + "    c.FONE,\n"
                    + "    c.FONE2 ,\n"
                    + "    c.EMAIL email,\n"
                    + "    c.CREDITO limite,\n"
                    + "    c.CELULAR celular\n"
                    + "FROM\n"
                    + "	CLIENTES c \n"
                    + "WHERE c.CONV = 'True'"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rst.getString("id"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoEstadual(rst.getString("ie"));
                    imp.setRazao(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(Utils.acertarTexto(rst.getString("FONE")));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "  	SELECT distinct\n"
                    + "	c.CODIGO id_cliente,\n"
                    + "    c.CGC cpfcnpj,\n"
                    + "    c.IE ie,\n"
                    + "    c.RAZAO razao,\n"
                    + "    c.NOME fantasia,\n"
                    + "    CASE WHEN c.BLOQ = 'True' THEN 0 ELSE 1 end ativo,\n"
                    + "    c.ENDERECO endereco,\n"
                    + "    c.END_NUM numero,\n"
                    + "    c.BAIRRO bairro,\n"
                    + "    c.CIDADE municipio,\n"
                    + "    c.UF uf,\n"
                    + "    c.CEP cep,\n"
                    + "    c.DATACADASTRO dataCadastro,\n"
                    + "    c.NASC dtnasc,\n"
                    + "    c.FONE,\n"
                    + "    c.FONE2 ,\n"
                    + "    c.EMAIL email,\n"
                    + "    c.CREDITO limite,\n"
                    + "    c.CELULAR celular\n"
                    + "FROM\n"
                    + "	CLIENTES c \n"
                    + "WHERE c.CONV = 'True'"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id_cliente"));
                    imp.setNome(rs.getString("fantasia"));
                    imp.setIdEmpresa(rs.getString("id_cliente"));
                    imp.setCnpj(rs.getString("cpfcnpj"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rs.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT\n"
                    + "	DISTINCT \n"
                    + "	f.CONT id,\n"
                    + "	c.CODIGO id_conveniado,\n"
                    + "	f.DATA  dataemissao,\n"
                    + "	f.VENC datavencimento,\n"
                    + "	f.CUPOM numerodocumento,\n"
                    + "	f.VALOR valororig,\n"
                    + "	(f.VALOR - COALESCE(f.VLRPAGO, 0)) valor,\n"
                    + "	COALESCE(f.VLRPAGO, 0) VLRPAGO \n"
                    + "FROM\n"
                    + "	FIADO f\n"
                    + "LEFT JOIN CLIENTES c ON\n"
                    + "	f.NOME = c.NOME\n"
                    + "WHERE\n"
                    + "	pago != 'S'\n"
                    + "	AND c.CONV = 'True'"
            )) {
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("id_conveniado"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setDataHora(rst.getTimestamp("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT\n"
                    + "	DISTINCT \n"
                    + "	f.CONT id,\n"
                    + "	c.CODIGO idcliente,\n"
                    + "	f.DATA  dataemissao,\n"
                    + "	f.VENC datavencimento,\n"
                    + "	f.CUPOM numerodocumento,\n"
                    + "	f.VALOR valororig,\n"
                    + "	(f.VALOR - COALESCE(f.VLRPAGO, 0)) valor,\n"
                    + "	COALESCE(f.VLRPAGO, 0) VLRPAGO,\n"
                    + "	c.CONV \n"
                    + "FROM\n"
                    + "	FIADO f\n"
                    + "LEFT JOIN CLIENTES c ON\n"
                    + "	f.NOME = c.NOME AND c.CONV NOT IN ('True')\n"
                    + "WHERE\n"
                    + "	f.pago != 'S'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

//    private String removerAcentos(String texto) {
//        texto = texto != null ? Normalizer.normalize(texto, Normalizer.Form.NFD) : "";
//        texto = texto != null ? texto.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "") : "";
//        texto = texto != null ? texto.replaceAll("�", "C") : "";
//        texto = texto != null ? texto.replaceAll("[^\\p{ASCII}]", "") : "";
//
//        return texto;
//    }
    private int gerarCodigoAtacado() {
        Object[] options = {"ean atacado", "ean13", "ean14", "Cancelar"};
        int decisao = JOptionPane.showOptionDialog(null, "Escolha uma opção de ean",
                "Gerar eans", JOptionPane.DEFAULT_OPTION, JOptionPane.QUESTION_MESSAGE, null, options, options[0]);
        return decisao;
    }
}
