package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Bruno
 */
public class STI32_5DAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "STI3";
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.CNPJ,
                OpcaoCliente.INSCRICAO_ESTADUAL,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_EMPRESA,
                OpcaoCliente.CONVENIO_TRANSACAO,
                OpcaoCliente.RECEBER_CHEQUE,
                OpcaoCliente.CONVENIO_CONVENIADO));
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
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
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.MARGEM_MAXIMA,
                    OpcaoProduto.MARGEM_MINIMA,
                    OpcaoProduto.OFERTA,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO
                }
        ));
    }

     @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " codigo as id,\n"
                    + " aliquota,\n"
                    + " reducao,\n"
                    + " descricao \n"
                    + "from aliquotas_icms"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("id"),
                            rs.getDouble("aliquota"),
                            rs.getDouble("reducao"))
                    );
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + " p.codGrupo idmerc1,\n"
                    + " g.descricao descmerc1,\n"
                    + " p.codGrupo2 idmerc2,\n"
                    + " g2.descricao descmerc2,\n"
                    + " p.codGrupo2 idmerc3,\n"
                    + " g2.descricao descmerc3\n"
                    + " /*Grupo3_idGrupo3,\n"
                    + " Grupo4_idGrupo4,\n"
                    + " Grupo5_idGrupo5 */\n"
                    + "from produtos p\n"
                    + "left join grupos g on g.codigo = p.codGrupo\n"
                    + "left join grupos2 g2 on g2.codigo = p.codGrupo2\n"
                    + "/*left join grupo3 g3 on g3.idGrupo3 = p.Grupo3_idGrupo3\n"
                    + "left join grupo4 g4 on g4.idGrupo4 = p.Grupo4_idGrupo4\n"
                    + "left join grupo5 g5 on g5.idGrupo5 = p.Grupo5_idGrupo5*/\n"
                    + "order by 1,3,5 "
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("idmerc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("idmerc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(rs.getString("idmerc3"));
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT\n"
                    + "	tab_cod id,\n"
                    + "	tab_desc descricao\n"
                    + "FROM\n"
                    + "	st_semelhante"
            )) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setDescricao(rs.getString("descricao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
     */
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " p.codPro id,\n"
                    + " p.descricao descricaocompleta,\n"
                    + " p.unidade,\n"
                    + " p.exportarBalanca ebalanca,\n"
                    + " p.utilizarbalanca,\n"
                    + " p.codMarca,\n"
                    + " p.codFabricante,\n"
                    + " p.clasFiscal,\n"
                    + " p.codigoCEST,\n"
                    + " p.dataCadastro,\n"
                    + " p.qtdPorCaixa,\n"
                    + " case when p.inativo = 0 then 1 else 0 end situacao,\n"
                    + " p.codGrupo idmerc1,\n"
                    + " p.codGrupo2 idmerc2,\n"
                    + " p.codGrupo2 idmerc3,\n"
                    + " /*p.Grupo3_idGrupo3,\n"
                    + " p.Grupo4_idGrupo4,\n"
                    + " p.Grupo5_idGrupo5,*/\n"
                    + " p.aliquotaPIS,\n"
                    + " p.aliquotaCofins,\n"
                    + " p.precoCaixa,\n"
                    + " p.diasValidade,\n"
                    + " p.tipoItem  tipoproduto,\n"
                    + " p.unidadeMedida_idUnidadeMedida,\n"
                    + " e.quantidade estoque,\n"
                    + " e.precoAVista precovenda,\n"
                    + " e.precoCusto precocusto,\n"
                    + " e.margemLucro margem,\n"
                    + " substring(e.codigoBarras,7,length(e.codigoBarras)) codigo_interno,\n"
                    + " case when e.codigoBarrasFornecedor = 'SEM GTIN' then p.codPro \n"
                    + "         else e.codigoBarrasFornecedor end codigobarras,\n"
                    + " t.aliqDentroEstado icms_id\n"
                    + "from produtos p\n"
                    + "left join estoque e on e.codProd = p.codPro\n"
                    + "left join produtos_empresas t on t.codProd = p.codPro and t.codEmp = " + getLojaOrigem() + "\n"
                    + "where \n"
                    + " e.codEmp = " + getLojaOrigem()
            )) {

                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));

                    imp.seteBalanca(rs.getBoolean("utilizarbalanca"));
                    imp.setEan(rs.getString("codigobarras"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("id"), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade());
                        imp.setEan(String.valueOf(bal.getCodigo()));
                    }

                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoReduzida());

                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setTipoEmbalagemCotacao(rs.getString("qtdPorCaixa"));
                    imp.setQtdEmbalagem(1);
                    imp.setTipoEmbalagemVolume(rs.getString("unidade"));

                    imp.setNcm(rs.getString("clasFiscal"));
                    imp.setCest(rs.getString("codigoCEST"));
                    imp.setDataCadastro(rs.getDate("dataCadastro"));
                    imp.setSituacaoCadastro(rs.getInt("situacao"));

                    imp.setEstoque(rs.getDouble("estoque"));

                    imp.setCodMercadologico1(rs.getString("idmerc1"));
                    imp.setCodMercadologico2(rs.getString("idmerc2"));
                    imp.setCodMercadologico3(rs.getString("idmerc3"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("precocusto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    String idIcms;

                    idIcms = rs.getString("icms_id");

                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);

                    result.add(imp);
                }
            }
        }

        return result;
    }


    /*    @Override
    public List<PautaFiscalIMP> getPautasFiscais(Set<OpcaoFiscal> opcoes) throws Exception {
        List<PautaFiscalIMP> result = new ArrayList<>();

        try (
                Statement st = ConexaoMySQL.getConexao().createStatement();
                ResultSet rs = st.executeQuery(
                        ""
                )) {
            while (rs.next()) {
                PautaFiscalIMP imp = new PautaFiscalIMP();

                imp.setId(buildPautaKey(rs.getString("ncm"), rs.getDouble("iva"), rs.getString("idicmspauta")));
                imp.setNcm(rs.getString("ncm"));
                imp.setIva(rs.getDouble("iva"));
                imp.setIvaAjustado(rs.getDouble("iva"));
                imp.setAliquotaDebitoId(rs.getString("idicmspauta"));
                imp.setAliquotaDebitoForaEstadoId(rs.getString("idicmspauta"));
                imp.setAliquotaCreditoId(rs.getString("idicmspauta"));
                imp.setAliquotaCreditoForaEstadoId(rs.getString("idicmspauta"));

                result.add(imp);
            }
        }

        return result;
    }

    private String buildPautaKey(String ncm, double iva, String idIcmsPauta) {
        return String.format("%s-%.2f-%s", ncm, iva, idIcmsPauta);
    }
     */
    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + " pf.codProduto produtoid,\n"
                    + " pf.codFornecedor fornecedorid,\n"
                    + " pf.codProdutoFornecedor codigoexterno,\n"
                    + " um.un_medida unidade\n"
                    + "from prod_fornec pf\n"
                    + "left join unidades_medidas um on um.codigo = pf.unidadeMedida_idUnidadeMedida" 
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rs.getString("produtoid"));
                    imp.setIdFornecedor(rs.getString("fornecedorid"));
                    imp.setCodigoExterno(rs.getString("codigoexterno"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo as id,\n"
                    + "	razao,\n"
                    + "	razao as fantasia,\n"
                    + "	cnpj,\n"
                    + "	insc_est as ie ,\n"
                    + "	endereco ,\n"
                    + "	numero ,\n"
                    + "	complemento ,\n"
                    + "	bairro ,\n"
                    + "	cidade ,\n"
                    + "	cep, \n"
                    + "	contato \n"
                    + "from\n"
                    + "	fornecedores f"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                    imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("contato"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*    
    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo id,\n"
                    + "	nome razao,\n"
                    + "	prazo,\n"
                    + "	diavence diapagamento,\n"
                    + "	desconto,\n"
                    + "	bloquear,\n"
                    + "	multa,\n"
                    + "	ativo\n"
                    + "from\n"
                    + "	carteirasconvenio"
            )) {
                while (rs.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setDiaPagamento(rs.getInt("diapagamento"));
                    imp.setDesconto(rs.getDouble("desconto"));

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
                    + "   cg1_cod id,\n"
                    + "   cg1_nome razao,\n"
                    + "   cg1_cpf cnpj,\n"
                    + "   cg1_convenio idconvenio,\n"
                    + "   cg1_limite limite,\n"
                    + "   cg1_bloqueadovp,\n"
                    + "   cg1_observacao \n"
                    + "  from cg1\n"
                    + "   where cg1_convenio <> 0;"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setIdEmpresa(rs.getString("idconvenio"));
                    imp.setNome(rs.getString("razao"));
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
                    "SELECT \n"
                    + "	f.FN1_NUM id,\n"
                    + "	f.FN1_PARC parcela,\n"
                    + "	f.CG1_COD idcliente,\n"
                    + "	f.fn1_doc documento,\n"
                    + "	f.caixa,\n"
                    + "	f.cupom,\n"
                    + "	f.FN1_EMISSAO emissao,\n"
                    + "	f.FN1_VENC vencimento,\n"
                    + "	f.fn1_hist observacao,\n"
                    + "	f.FN1_JUROS juros,\n"
                    + "	f.FN1_MULTA multa,\n"
                    + "	f.FN1_VALOR valor\n"
                    + "FROM \n"
                    + "	fn1 f\n"
                    + "	JOIN cg1 c ON c.cg1_cod = f.cg1_cod\n"
                    + "WHERE \n"
                    + " c.cg1_convenio <> 0 and \n"
                    + "	f.fn1_dtbaixa IS null AND\n"
                    + "	f.fn1_empresa = " + getLojaOrigem() + " AND\n"
                    + "	f.fn1_tipo NOT IN (37, 62, 64)"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

                while (rs.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setEcf(rs.getString("caixa"));
                    imp.setIdConveniado(rs.getString("idcliente"));
                    imp.setDataMovimento(rs.getDate("emissao"));
                    imp.setValor(rs.getDouble("valor"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.setDataHora(new Timestamp(format.parse(imp.getDataMovimento() + " 00:00:00").getTime()));

                    result.add(imp);
                }
            }
        }

        return result;
    }
     */
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codigo_cliente as id, \n"
                    + "	cnpj_cpf ,\n"
                    + "	insc_rg as rg_ie,\n"
                    + "	nome as razao,\n"
                    + "	fantasia,\n"
                    + "	logradouro as endereco,\n"
                    + "	numero ,\n"
                    + "	complemento ,\n"
                    + "	bairro ,\n"
                    + "	cidade ,\n"
                    + "	uf as estado ,\n"
                    + "	cep ,\n"
                    + "	contato ,\n"
                    + "	celular ,\n"
                    + "	email ,\n"
                    + "	emailNfe ,\n"
                    + "	dataCadastro as data_cadastro,\n"
                    + "	inativo as ativo,\n"
                    + "	limiteCredito as limite \n"
                    + "from\n"
                    + "	clientes c"
            )) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("contato"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setLimiteCompra(rs.getDouble("limite"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	idtempClientes as id,\n"
                    + "	idCliente as id_cliente,\n"
                    + "	c.cnpj_cpf ,\n"
                    + "	v.numCaixa as ecf,\n"
                    + "	v.numDocto as coo ,\n"
                    + "	v.numECF ,\n"
                    + "	v.dataEmissao as dt_emissao,\n"
                    + "	vencimento as dt_vcto,\n"
                    + "	valor as vl_valor\n"
                    + "from\n"
                    + "	tempclientes t\n"
                    + "	join clientes c on c.codigo_cliente = idCliente \n"
                    + "	join vendas v on v.codigo = idCondPagto \n"
                    + "where\n"
                    + "	dataPagto is null"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setParcela(1);
                    imp.setIdCliente(rs.getString("id_cliente"));
                    imp.setNumeroCupom(rs.getString("coo"));
                    imp.setDataEmissao(rs.getDate("dt_emissao"));
                    imp.setDataVencimento(rs.getDate("dt_vcto"));
                    imp.setValor(rs.getDouble("vl_valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	f.FN1_NUM id,\n"
                    + "	f.FN1_PARC parcela,\n"
                    + "	f.CG1_COD idcliente,\n"
                    + "	c.cg1_nome razao,\n"
                    + "	c.cg1_fone telefone,\n"
                    + "	c.cg1_cpf cpf,\n"
                    + "	c.cg1_cgc cnpj,\n"
                    + "	c.cg1_rg rg,\n"
                    + "	c.cg1_inscestadual ie,\n"
                    + "	f.fn1_doc documento,\n"
                    + "	f.fn1_cmc7 cmc7,\n"
                    + "	f.fn1_cheque cheque,\n"
                    + "	f.FN1_DTCHEQUE datacheque,\n"
                    + "	bc.cg1_banco banco,\n"
                    + "	bc.cg1_agencia agencia,\n"
                    + "	bc.cg1_conta conta,\n"
                    + "	f.caixa,\n"
                    + "	f.cupom,\n"
                    + "	f.FN1_EMISSAO emissao,\n"
                    + "	f.FN1_VENC vencimento,\n"
                    + "	f.fn1_hist observacao,\n"
                    + "	f.FN1_JUROS juros,\n"
                    + "	f.FN1_MULTA multa,\n"
                    + "	f.FN1_VALOR valor\n"
                    + "FROM \n"
                    + "	fn1 f\n"
                    + "JOIN cg1 c ON f.CG1_COD = c.cg1_cod\n"
                    + "LEFT JOIN cg1_banco bc ON f.cg1_banco_num = bc.cg1_banco_num\n"
                    + "WHERE \n"
                    + "	f.fn1_dtbaixa IS null\n"
                    + "	AND f.fn1_empresa = " + getLojaOrigem() + " \n"
                    + "	AND fn1_tipo IN (2,3)\n"
                    + "ORDER BY \n"
                    + "	f.FN1_VENC"
            )) {
                while (rs.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rs.getString("id"));
                    imp.setDataDeposito(rs.getDate("vencimento"));
                    imp.setNumeroCheque(rs.getString("documento"));
                    imp.setDate(rs.getDate("emissao"));
                    imp.setCmc7(rs.getString("cmc7"));
                    imp.setBanco(rs.getInt("banco"));
                    imp.setAgencia(rs.getString("agencia"));
                    imp.setConta(rs.getString("conta"));
                    imp.setNome(rs.getString("razao"));
                    imp.setTelefone(rs.getString("telefone"));

                    String cpf = rs.getString("cpf"), cnpj = rs.getString("cnpj"),
                            ie = rs.getString("ie"), rg = rs.getString("rg");
                    if (cpf != null && !"".equals(cpf)) {
                        imp.setCpf(cpf);
                    } else {
                        imp.setCpf(cnpj);
                    }

                    if (rg != null && !"".equals(rg)) {
                        imp.setRg(rg);
                    } else {
                        imp.setRg(ie);
                    }

                    imp.setValor(rs.getDouble("valor"));
                    imp.setNumeroCupom(rs.getString("cupom"));
                    imp.setEcf(rs.getString("caixa"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
     */
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codProd as id_produto,\n"
                    + "	dataPromocaoInicio as datainicio,\n"
                    + "	dataPromocaoFim as datafim,\n"
                    + "	precoAVista as preconormal,\n"
                    + "	precoPromocao as precooferta\n"
                    + "from\n"
                    + "	estoque e\n"
                    + "	where dataPromocaoInicio is not null \n"
                    + "	and dataPromocaoFim is not null \n"
                    + "	order by 2,3 "
            )) {
                while (rs.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setDataInicio(rs.getDate("datainicio"));
                    imp.setDataFim(rs.getDate("datafim"));
                    imp.setPrecoOferta(rs.getDouble("precooferta"));
                    imp.setPrecoNormal(rs.getDouble("preconormal"));

                    result.add(imp);
                }
            }
        }
        return result;
    }
}
