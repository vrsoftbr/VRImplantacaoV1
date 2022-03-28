/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import com.sun.imageio.plugins.jpeg.JPEG;
import vrimplantacao2.dao.interfaces.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import vr.core.utils.StringUtils;
import vrframework.classe.Util;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Wagner
 */
public class GZProdadosDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(GZProdadosDAO.class.getName());

    @Override
    public String getSistema() {
        return "GZProdados";
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
            OpcaoProduto.ICMS_SAIDA,
            OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
            OpcaoProduto.ICMS_ENTRADA,
            OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
            OpcaoProduto.MARGEM
        }));
    }
    
    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.CONVENIO_CONVENIADO));
    }

    public ArrayList<Estabelecimento> getLojasCliente() throws Exception {
        ArrayList<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select IdEmpresa codigo, RazaoSocial, CGC_CPF from empresa;"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("RazaoSocial") + " - " + rst.getString("CGC_CPF")));
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
                    "select distinct\n"
                    + " concat(id,'.',aliquotaIcms,'.',redbase) id,\n"
                    + " descricao,\n"
                    + " aliquotaIcms,\n"
                    + " redbase,\n"
                    + " cst\n"
                    + "from (\n"
                    + "select distinct\n"
                    + "t.idCadTributacao id,\n"
                    + "t.descricao,\n"
                    + "t.aliquotaIcms,\n"
                    + "case when p.Icms = 0 then 0\n"
                    + "else p.RedBase end RedBase,\n"
                    + "lpad(p.TabIcmsProd,2,2) cst,\n"
                    + "t.sittrib\n"
                    + "from produto p\n"
                    + "join cadtributacao t on t.idCadTributacao = p.SitTrib\n"
                    + ") mapa;"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquotaIcms"),
                            rst.getDouble("redbase")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        ProdutoParaFamiliaHelper helper = new ProdutoParaFamiliaHelper(result);

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " idFamilia id,\n"
                    + " nome descricao\n"
                    + "from familia"
            )) {
                while (rst.next()) {
                    helper.gerarFamilia(rst.getString("id"), rst.getString("descricao"));
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
                    "select distinct\n"
                    + " p.idGrupo mercid1,\n"
                    + " g.NOME desc1,\n"
                    + " p.idSubGrupo mercid2,\n"
                    + " sb.Nome desc2,\n"
                    + " p.idSubGrupo1 mercid3,\n"
                    + " sb1.nome desc3\n"
                    + "from produto p\n"
                    + "join grupo g on g.IDGRUPO = p.idGrupo\n"
                    + "join subgrupo sb on sb.idSubGrupo = p.idSubGrupo \n"
                    + "join subgrupo1 sb1 on sb1.idsubgrupo1 = p.idSubGrupo1\n"
                    + "order by 1,3,5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID("mercid3");
                    imp.setMerc3Descricao(rst.getString("desc3"));
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
                    "select \n"
                    + " p.idProduto produtoid,\n"
                    + " p.Descricao descricaocompleta,\n"
                    + " p.DescrRed descricaoreduzida,\n"
                    + " p.descricaoetq descricaogondola,\n"
                    + " p.pesovariavel,\n"
                    + " p.UnidSaida embalagem,\n"
                    + " p.EmbSaida qtde,\n"
                    + " pr.VENDA1 precovenda,\n"
                    + " pr.CUSTO precocusto,\n"
                    + " pr.CUSTO_MEDIO precocustomedio,\n"
                    + " p.Margem margem,\n"
                    + " p.Validade validade,\n"
                    + " p.idGrupo mercid1,\n"
                    + " p.idSubGrupo mercid2,\n"
                    + " p.idSubGrupo1 mercid3,\n"
                    + " case when p.idSituacao = 2 then 0\n"
                    + " else 1 end as situacao,\n"
                    + " p.DtCadastro datacadastro,\n"
                    + " e.estoque_atual estoque,\n"
                    + " p.EstMin estoquemin,\n"
                    + " p.EstMax estoquemax,\n"
                    + " p.Tipo balanca,\n"
                    + " case when length(p.Ean) > 14 then substr(p.Ean,2,14)\n"
                    + " else p.Ean end ean,\n"
                    + " p.ClassFiscal ncm,\n"
                    + " p.Pis,\n"
                    + " p.pisEntrada,\n"
                    + " lpad(p.CST_PIS,2,2) cst_pis_entrada,\n"
                    + " p.Cofins,\n"
                    + " p.cofinsEntrada,\n"
                    + " case when p.TipoPisCofins = 'M' then 4\n"
                    + "      when p.TipoPisCofins = 'N' then 8\n"
                    + "      when p.TipoPisCofins = 'S' then 5\n"
                    + "      when p.TipoPisCofins = 'T' then 0\n"
                    + " else 6 end TipoPisCofins_saida,\n"
                    + " p.idFamilia,\n"
                    + " p.nat_receita,\n"
                    + " p.cest,\n"
                    + " p.dun14,\n"
                    + " concat(t.idCadTributacao,'.',t.aliquotaIcms,'.',p.redbase) id_aliquota\n"
                    + "from produto p\n"
                    + "join produto_estoque e on p.idProduto = e.idProduto\n"
                    + "join produto_preco pr on pr.IDPRODUTO = p.idProduto\n"
                    + "join cadtributacao t on t.idCadTributacao = p.SitTrib\n"
                    + "order by 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produtoid"));
                    //imp.setTipoEmbalagem(rst.getString("embalagem"));
                    //imp.setQtdEmbalagem(rst.getInt("qtde"));

                    int codigoProduto = Utils.stringToInt(rst.getString("ean"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(rst.getBoolean("pesovariavel"));
                        imp.setTipoEmbalagem(rst.getString("embalagem"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setQtdEmbalagem(rst.getInt("qtde"));
                    }

                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));

                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));

                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setCustoComImposto(rst.getDouble("precocusto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemax"));
                    imp.setSituacaoCadastro(rst.getInt("situacao"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("TipoPisCofins_saida"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("nat_receita"));

                    imp.setIcmsDebitoId(rst.getString("id_aliquota"));
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " idProduto codigo,\n"
                    + " case when length(CodigoEan) > 14 then substr(CodigoEan,2,14)\n"
                    + " else CodigoEan end ean,\n"
                    + " qtde_emb,\n"
                    + " valor_venda\n"
                    + "from produto_ean"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtde_emb"));
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
                    "select \n"
                    + " IDFORNECEDOR,\n"
                    + " NOME,\n"
                    + " FANTASIA,\n"
                    + " TELEFONE,\n"
                    + " FAX,\n"
                    + " ENDERECO,\n"
                    + " BAIRRO,\n"
                    + " CIDADE,\n"
                    + " UF,\n"
                    + " CEP,\n"
                    + " NUMERO,\n"
                    + " DTCADASTRO,\n"
                    + " CPF_CGC,\n"
                    + " RG_IE,\n"
                    + " EMAIL\n"
                    + "from fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("IDFORNECEDOR"));
                    imp.setRazao(rst.getString("NOME"));
                    imp.setFantasia(rst.getString("FANTASIA"));
                    imp.setCnpj_cpf(rst.getString("CPF_CGC"));
                    imp.setIe_rg(rst.getString("RG_IE"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("UF"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setDatacadastro(rst.getDate("DTCADASTRO"));

                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("FAX").trim(),
                                null,
                                TipoContato.COMERCIAL,
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
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " idFornecedor,\n"
                    + " idProduto,\n"
                    + " Referencia,\n"
                    + " Embalagem\n"
                    + "from itensfornecedor "
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idProduto"));
                    imp.setIdFornecedor(rst.getString("idFornecedor"));
                    imp.setCodigoExterno(rst.getString("Referencia"));
                    imp.setQtdEmbalagem(rst.getInt("Embalagem"));
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
                    "select \n"
                    + " idCliente,\n"
                    + " nome,\n"
                    + " endereco,\n"
                    + " bairro,\n"
                    + " cidade,\n"
                    + " uf,\n"
                    + " cep,\n"
                    + " numero,\n"
                    + " cpf,\n"
                    + " rg,\n"
                    + " fone,\n"
                    + " celular,\n"
                    + " limite,\n"
                    + " replace(dt_nasc,'/','-') dt_nasc,\n"
                    + " est_civil,\n"
                    + " dtAbertura,\n"
                    + " complemento,\n"
                    + " email,\n"
                    + " numerocartao,\n"
                    + " NomeEntrega,\n"
                    + " EnderecoEntrega,\n"
                    + " NumeroEntrega,\n"
                    + " CepEntrega,\n"
                    + " bairroEntrega,\n"
                    + " cidadeentrega,\n"
                    + " ufentrega,\n"
                    + " nomecob,\n"
                    + " enderecocob,\n"
                    + " numerocob,\n"
                    + " cepcob,\n"
                    + " bairrocob,\n"
                    + " cidadecob,\n"
                    + " ufcob,\n"
                    + " status_cadastro,\n"
                    + " status_cliente\n"
                    + "from cliente \n"
                    + "where \n"
                    + " idtipocliente <> 1;"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("idCliente"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setTelefone(rst.getString("fone"));
                    imp.setEmail(rst.getString("email"));

                    SimpleDateFormat formatar = new SimpleDateFormat("yyyy-MM-dd");
                    Date data = formatar.parse(rst.getString("dt_nasc"));

                    imp.setDataNascimento(data);
                    imp.setCelular(rst.getString("celular"));

                    imp.setCobrancaEndereco(rst.getString("enderecocob"));
                    imp.setCobrancaNumero(rst.getString("numerocob"));
                    imp.setCobrancaBairro(rst.getString("bairrocob"));
                    imp.setCobrancaMunicipio(rst.getString("cidadecob"));
                    imp.setCobrancaUf(rst.getString("ufcob"));
                    imp.setCobrancaCep(rst.getString("cepcob"));

                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setBloqueado(rst.getBoolean("status_cliente"));
                    imp.setAtivo(rst.getBoolean("status_cadastro"));

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
                    "select \n"
                    + " r.idReceber,\n"
                    + " r.idCliente,\n"
                    + " r.Doc,\n"
                    + " r.Valor,\n"
                    + " r.emissao,\n"
                    + " p.venc,\n"
                    + " r.obs\n"
                    + "from receber r\n"
                    + "join parcelareceber p on p.idReceber = r.idReceber\n"
                    + "join cliente c on c.idCliente = r.idCliente\n"
                    + "where \n"
                    + " c.idtipocliente <> 1;"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("idReceber"));
                    imp.setIdCliente(rst.getString("idCliente"));
                    imp.setNumeroCupom(rst.getString("Doc"));
                    imp.setValor(rst.getDouble("Valor"));
                    imp.setDataEmissao(rst.getDate("emissao"));
                    imp.setDataVencimento(rst.getDate("venc"));
                    imp.setObservacao(rst.getString("obs"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " IdEmpresaConvenio id,\n"
                    + " RazaoSocial razao,\n"
                    + " Fantasia,\n"
                    + " Endereco,\n"
                    + " Numero,\n"
                    + " Bairro,\n"
                    + " Cidade,\n"
                    + " Uf,\n"
                    + " CGC_CPF cnpj,\n"
                    + " InscRG inscricao\n"
                    + "from empresa_convenio")) {
                while (rs.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setUf(rs.getString("uf"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " idCliente,\n"
                    + " nome,\n"
                    + " empresa_convenio,\n"
                    + " cpf,\n"
                    + " limite,\n"
                    + " dtAbertura,\n"
                    + " numerocartao,\n"
                    + " status_cadastro,\n"
                    + " status_cliente\n"
                    + "from cliente \n"
                    + "where \n"
                    + " idtipocliente = 1"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rs.getString("idCliente"));
                    imp.setCnpj(rs.getString("cpf"));
                    imp.setIdEmpresa(rs.getString("empresa_convenio"));
                    imp.setNome(rs.getString("nome"));
                    imp.setConvenioLimite(rs.getDouble("limite"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " r.idReceber,\n"
                    + " r.idCliente,\n"
                    + " r.Doc,\n"
                    + " r.Valor,\n"
                    + " r.emissao,\n"
                    + " p.venc,\n"
                    + " r.obs\n"
                    + "from receber r\n"
                    + "join parcelareceber p on p.idReceber = r.idReceber\n"
                    + "join cliente c on c.idCliente = r.idCliente\n"
                    + "where \n"
                    + " c.idtipocliente = 1;"
            )) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rs.getString("idReceber"));
                    imp.setNumeroCupom(rs.getString("Doc"));
                    imp.setIdConveniado(rs.getString("idCliente"));
                    imp.setDataMovimento(rs.getDate("emissao"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("obs"));

                    imp.setDataHora(new Timestamp(format.parse(imp.getDataMovimento() + " 00:00:00").getTime()));

                    result.add(imp);
                }
            }
        }

        return result;
    }

}
