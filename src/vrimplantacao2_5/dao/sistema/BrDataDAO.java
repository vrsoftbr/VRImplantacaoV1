/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
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
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Wagner
 */
public class BrDataDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "BRDATA";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.CUSTO_COM_IMPOSTO,
                    OpcaoProduto.CUSTO_SEM_IMPOSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.VOLUME_QTD,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.CODIGO_BENEFICIO
                }
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.NUMERO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.PRODUTO_FORNECEDOR));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	  i.C028_CodigoGrupoTributario id,\n"
                    + "	  g.C028_Descricao descricao,\n"
                    + "	  i.C022_SituacaoTributariaEstadual cst,\n"
                    + "	  (i.C029_TaxaICMSEstadual * 100) aliquota,\n"
                    + "	  cast (replace((i.C029_BaseCalculoICMSEstadual * 100),100, 0) as numeric(10,2)) reducao\n"
                    + "	 from C029_CodigoOperacaoItens i\n"
                    + "	 join C028_GrupoTributario g on g.C028_Codigo = i.C028_CodigoGrupoTributario\n"
                    + "	 where i.C015_CodigoOperacao = '000004'"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + " l.C191_Codigo merc1,\n"
                    + " l.C191_Descricao mercdesc1,\n"
                    + " g.C003_Codigo merc2,\n"
                    + " g.C003_Descricao mercdesc2,\n"
                    + " sb.C004_Codigo merc3,\n"
                    + " sb.C004_Descricao mercdesc3\n"
                    + "from C001_Produto p\n"
                    + "join C003_ProdutoGrupo g on g.C003_Codigo  = p.C003_CodigoGrupo\n"
                    + "join C191_LinhaProdutos l on l.C191_Codigo  = g.C190_CodigoLinha \n"
                    + "left join C004_ProdutoSubGrupo sb on sb.C004_Codigo = p.C004_CodigoSubGrupo\n"
                    + "order by 1,3"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("mercdesc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("mercdesc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("mercdesc3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " p.C001_Codigo id,\n"
                    + " p.C001_Descricao descricao,\n"
                    + " p.C028_CodigoGrupoTributario idaliquota,\n"
                    + " l.C191_Codigo merc1,\n"
                    + " g.C003_Codigo merc2,\n"
                    + " sb.C004_Codigo merc3,\n"
                    + " case when e.T030_PrecoVenda > 0 then e.T030_PrecoVenda\n"
                    + "   else  p.C001_PrecoVenda end precovenda,\n"
                    + " p.C001_PrecoVenda,\n"
                    + " p.C001_DataCadastro datacadastro,\n"
                    + " p.C001_ValidadeDias validade,\n"
                    + " p.C001_Obs,\n"
                    + " case when p.C001_Excluido = 0 then 1\n"
                    + "    else 0 end situacao,\n"
                    + " p.C001_ECF,\n"
                    + " p.C002_AliquotaECF,\n"
                    + " p.C001_CodigoNCM ncm,\n"
                    + " p.C001_CodigoCEST cest,\n"
                    + " p.C001_PrecoCusto,\n"
                    + " p.C001_PrecoCompra,\n"
                    + " coalesce(p.C001_GTIN,p.C001_GTIN_Tributavel) ean,\n"
                    + " p.C001_UltimoCustoEntrada custo,\n"
                    + " p.C291_SetorBalanca ebalanca,\n"
                    + " e.T030_Quantidade estoque,\n"
                    + " coalesce(e.C052_UnidadeMedida,p.C001_UnidadeMedida) unidade,\n"
                    + " substring(replace(p.C001_UnidadeAux1,'.',''),1,2) unidadecotacao,\n"
                    + " case when p.C001_Relacao1 = 0 then 1 else p.C001_Relacao1 end qtdecotacao,\n"
                    + " e.T030_PrecoCompra custosemimposto,\n"
                    + " e.T030_UltimoCustoEntrada,\n"
                    + " e.T030_CustoMedio,\n"
                    + " pci.C242_CodigoNaturezaReceita natreceita,\n"
                    + " pci.C073_Codigo piscofins,\n"
                    + " b.C179_CodigoAjusteIcms cbnef\n"
                    + "from C001_Produto p\n"
                    + "left join T030_ControleEstoque e on e.C001_CodigoProduto = p.C001_Codigo \n"
                    + "	and e.C021_CodigoDeposito  = '" + getLojaOrigem() + "' --getloja\n"
                    + "left join C254_ParametroPisCofins pc on pc.C254_Codigo = p.C254_ParametroPIS \n"
                    + "left join C255_ParametroPisCofinsItems pci on pci.C254_Codigo = pc.C254_Codigo\n"
                    + "	and pci.C255_Tipo = 1\n"
                    + "left join C029_CodigoOperacaoItens coi on coi.C028_CodigoGrupoTributario = p.C028_CodigoGrupoTributario \n"
                    + "    and coi.C015_CodigoOperacao = '000004'\n"
                    + "left join C003_ProdutoGrupo g on g.C003_Codigo  = p.C003_CodigoGrupo\n"
                    + "left join C191_LinhaProdutos l on l.C191_Codigo  = g.C190_CodigoLinha \n"
                    + "left join C004_ProdutoSubGrupo sb on sb.C004_Codigo = p.C004_CodigoSubGrupo\n"
                    + "left join C290_GrupoBeneficioTributario b on b.C290_Codigo = p.C290_CodigoGrupoBeneficioTributario\n"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("id"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));

                    imp.setCustoComImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custo"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));

                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setSituacaoCadastro(rst.getInt("situacao"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("piscofins"));
                    imp.setPiscofinsCstCredito(imp.getPiscofinsCstDebito());

                    imp.setIcmsDebitoId(rst.getString("idaliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    
                    imp.setBeneficio(rst.getString("cbnef"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(produtoBalanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        //imp.seteBalanca(rst.getBoolean("ebalanca"));
                        imp.setEan(rst.getString("ean"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setQtdEmbalagem(1);

                        imp.setTipoEmbalagemCotacao(rst.getString("unidadecotacao"));
                        imp.setQtdEmbalagemCotacao(rst.getInt("qtdecotacao"));
                    }

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " C001_CodigoProduto produtoid,\n"
                    + " R055_CodigoBarras ean,\n"
                    + " coalesce(C052_UnidadeMedida, 'UN') unidade\n"
                    + "from R055_CodigoBarrasAlternativo "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produtoid"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " f.C006_Codigo id,\n"
                    + " f.C006_Codigo cpfcnpj,\n"
                    + " f.C006_NomeRazaoSocial razao,\n"
                    + " f.C006_NomeFantasia fantasia,\n"
                    + " f.C006_InscricaoEstadual ie_rg,\n"
                    + " f.C006_TipoPessoa,\n"
                    + " e.C042_Endereco endereco,\n"
                    + " e.C042_Bairro bairro,\n"
                    + " m.C043_Nome cidade,\n"
                    + " e.C042_CEP cep,\n"
                    + " e.C042_Complemento complemento,\n"
                    + " m.C044_UF estado,\n"
                    + " e.C042_Telefone telefone,\n"
                    + " e.C042_Celular,\n"
                    + " e.C042_Contato,\n"
                    + " e.C042_Email,\n"
                    + " e.C042_Numero numero,\n"
                    + " e.C042_Fax fax\n"
                    + "from C006_Pessoa f\n"
                    + "left join C042_Endereco e on e.C006_CodigoPessoa = f.C006_Codigo\n"
                    + "left join C043_Municipio m on m.C043_ID = e.C043_IDMunicipio \n"
                    + "where f.C006_Fornecedor = 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setCep(rst.getString("cep"));
                    imp.setUf(rst.getString("estado"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setTel_principal(rst.getString("telefone"));

                    imp.addContato(
                            rst.getString("C042_Contato"),
                            rst.getString("fax"),
                            rst.getString("C042_Celular"),
                            TipoContato.COMERCIAL,
                            rst.getString("C042_Email"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  C001_CodigoProduto idproduto,\n"
                    + "  C006_CodigoFornecedor idfornecedor,\n"
                    + "  R034_Codigo codigoexterno\n"
                    + "from R034_RelacionaProdutoCodigoFornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " c.C006_Codigo id,\n"
                    + " c.C006_Codigo cpfcnpj,\n"
                    + " c.C006_NomeRazaoSocial razao,\n"
                    + " c.C006_NomeFantasia fantasia,\n"
                    + " c.C006_InscricaoEstadual ie,\n"
                    + " c.C006_RG rg,\n"
                    + " c.C006_LimiteCredito limite,\n"
                    + " c.C006_TipoPessoa,\n"
                    + " c.C006_ValorRenda3 salario,\n"
                    + " e.C042_Endereco endereco,\n"
                    + " e.C042_Bairro bairro,\n"
                    + " m.C043_Nome cidade,\n"
                    + " e.C042_CEP cep,\n"
                    + " e.C042_Complemento complemento,\n"
                    + " m.C044_UF estado,\n"
                    + " e.C042_Telefone telefone,\n"
                    + " e.C042_Celular celular,\n"
                    + " e.C042_Contato,\n"
                    + " e.C042_Email email,\n"
                    + " e.C042_Numero numero,\n"
                    + "case when c.C006_StatusCadastro = 1 then 0 \n"
                    + "      else 1 end situacao,\n"
                    + " case when c.C006_BloqueadoParaVenda = 1 then 0 \n"
                    + "      else 1 end bloqueado\n"
                    + "from C006_Pessoa c\n"
                    + "left join C042_Endereco e on e.C006_CodigoPessoa = c.C006_Codigo\n"
                    + "left join C043_Municipio m on m.C043_ID = e.C043_IDMunicipio \n"
                    + "where c.C006_Cliente = 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setAtivo(rst.getBoolean("situacao"));
                    imp.setBloqueado(rst.getBoolean("bloqueado"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + " T014_ID id,\n"
                    + " T014_Transacao,\n"
                    + " C006_CodigoPessoa fornecedorid,\n"
                    + " C008_CodigoFilial,\n"
                    + " C013_CodigoFormaPagamento,\n"
                    + " T014_NumeroDocumento numerodocumento,\n"
                    + " T014_DataLancamento emissao,\n"
                    + " T014_DataVencimento vencimento,\n"
                    + " T014_ValorSaldoOriginal,\n"
                    + " T014_SaldoTitulo valor,\n"
                    + " T014_Obs obs\n"
                    + "from T014_MovimentacaoContasPagar \n"
                    + "where \n"
                    + " T014_SaldoTitulo <> 0\n"
                    + " and \n"
                    + " C008_CodigoFilial = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setVencimento(rst.getDate("vencimento"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " T012_ID id,\n"
                    + " T012_Transacao,\n"
                    + " C006_CodigoPessoa clienteid,\n"
                    + " C008_CodigoFilial,\n"
                    + " T012_NumeroDocumento numerocupom,\n"
                    + " T012_DataLancamento emissao,\n"
                    + " T012_DataVencimento vencimento,\n"
                    + " DATEDIFF(day,T012_DataVencimento,GETDATE()) AS diasatraso,\n"
                    + " T012_ValorSaldoOriginal,\n"
                    + " T012_SaldoTitulo valor,\n"
                    + " C032_CodigoTabelaJuros,\n"
                    + " T012_Obs obs\n"
                    + "from T012_MovimentacaoContasReceber \n"
                    + "where T012_SaldoTitulo <> 0 \n"
                    + "and C008_CodigoFilial = '" + getLojaOrigem() + "'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setIdCliente(rst.getString("clienteid"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setObservacao(rst.getString("obs"));

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

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new BrDataDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new BrDataDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setNomeCliente(rst.getString("nome_cliente"));
                        next.setCpf(rst.getString("cpf_cnpj"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        String horaInicio = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
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
                    = "";/*"SELECT\n"
                    + "	v.VndNumeroVenda id_venda,\n"
                    + "	d.VndDocNumero numerocupom,\n"
                    + "	VndClienteID id_cliente,\n"
                    + "	c.PessoaNome nome_cliente,\n"
                    + "	v.VndNfpCpfCnpj cpf_cnpj,\n"
                    + "	SUBSTRING(e.EstacaoDescricao, 4, 2) ecf,\n"
                    + "	v.VndDtEmissao emissao,\n"
                    + "	CAST (VndDtAbertura as time) horainicio,\n"
                    + "	CAST (VndDtFechamento as time) horatermino,\n"
                    + "	CASE\n"
                    + "	  when v.VndClienteValor = 0\n"
                    + "   then v.VndConvenioValor\n"
                    + "	  ELSE v.VndClienteValor\n"
                    + "	END subtotalimpressora\n"
                    + "FROM\n"
                    + "	TB_VENDA v\n"
                    + "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID\n"
                    + "LEFT JOIN TB_ESTACAO e on e.EstacaoID = v.VndEstacaoID\n"
                    + "LEFT JOIN TB_PESSOA_PFPJ c on c.PessoaID = v.VndClienteID\n"
                    + "WHERE\n"
                    + " d.VndDocNumero is not NULL \n"
                    + "	and v.VndDtEmissao between '" + strDataInicio + "' and '" + strDataTermino + "'";*/
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

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nro_item"));
                        next.setProduto(rst.getString("produto"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "";/*"select\n"
                    + "	v.VndNumeroVenda id_venda,\n"
                    + "	vi.DocBaseItemID id_item,\n"
                    + "	vi.DocBaseItemSequencia nro_item,\n"
                    + "	vi.DocBaseItemProdID produto,\n"
                    + "	un.UnSigla unidade,\n"
                    + "	case\n"
                    + "	   when p.ProdCodBarras1 is null then p.ProdCodInterno\n"
                    + "	   else p.ProdCodBarras1\n"
                    + "	end as codigobarras,\n"
                    + "	p.ProdDescricao descricao,\n"
                    + "	vi.DocBaseItemQuantidade quantidade,\n"
                    + "	vi.DocBaseItemValorUnitario precovenda,\n"
                    + "	vi.DocBaseItemValorTotal total\n"
                    + "from\n"
                    + "	TB_DOCUMENTO_BASE_ITENS vi\n"
                    + "left join TB_VENDA v on v.VndDocBaseID = vi.DocBaseItemDocBaseID \n"
                    + "left join TB_PRODUTO p on p.ProdID = vi.DocBaseItemProdID \n"
                    + "LEFT JOIN TB_VENDA_DOCUMENTO d on d.VndDocID = v.VndID \n"
                    + "left join TB_UNIDADE_MEDIDA un on un.UnID = vi.DocBaseItemUnidadeID \n"
                    + "WHERE\n"
                    + " d.VndDocNumero is not NULL \n"
                    + "	and v.VndDtEmissao between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "order by 2,1";*/
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
    }
}
