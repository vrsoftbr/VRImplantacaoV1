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
import java.util.logging.Logger;
import vr.core.utils.StringUtils;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
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
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Bruno
 */
public class ImperiumMarket2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    public static final Logger LOG = Logger.getLogger(StringUtils.class.getName());

    @Override
    public String getSistema() {
        return "IMPERIUMMARKET";
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
                OpcaoFornecedor.MUNICIPIO,
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
                    OpcaoProduto.VENDA_PDV,
                    OpcaoProduto.PDV_VENDA,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
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
                    //                    "	select distinct \n"
                    //                    + "	concat(icms,'-',substring(tabicmsprod,1,3),'-',redbasevenda)as id,\n"
                    //                    + "	concat(icms, ' %', ' RDZ ', redbasevenda)as descricao,\n"
                    //                    + "	icms as icmsSaida,\n"
                    //                    + "	redbasevenda as reducaovenda,\n"
                    //                    + "	substring(tabicmsprod ,1,3) as cst\n"
                    //                    + "	from produto_tributacao pt "

                    "select  \n"
                    + "	concat(icmscompra,'-',substring(tabicmsprodentrada ,1,3),'-',redbase)as id,\n"
                    + "	concat(icmscompra, ' %', ' RDZ ', redbase)as descricao,\n"
                    + "	icmscompra as icmsSaida,\n"
                    + "	redbase as reducaovenda,\n"
                    + "	case when substring(tabicmsprodentrada ,1,3) = 51 then '40'\n"
                    + "	when substring(tabicmsprodentrada ,1,3) = 70 then '20'\n"
                    + "	when substring(tabicmsprodentrada ,1,3) = 41 then '40'\n"
                    + "	else substring(tabicmsprodentrada ,1,3) end as cst\n"
                    + "	from produto_tributacao pt "
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("icmsSaida"),
                            rs.getDouble("reducaovenda"))
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
                    "select\n"
                    + "  distinct \n"
                    + "	g.IDGRUPO as merc1,\n"
                    + "	g.NOME as desc1,\n"
                    + "	s2.id as merc2,\n"
                    + "	s2.Nome desc2,\n"
                    + "	s.id as merc3,\n"
                    + "	s.nome as desc3\n"
                    + "from\n"
                    + "	subgrupo1 s\n"
                    + "	left join grupo g on g.IDGRUPO = s.idgrupo \n"
                    + "	left join subgrupo s2 on s2.idSubGrupo = s.idsubgrupo \n"
                    + "  where g.IDGRUPO is not null\n"
                    + "  order by 1,3,5"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("desc1"));
                    imp.setMerc2ID(rs.getString("merc2") == null
                            ? rs.getString("merc1")
                            : rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("desc2") == null
                            ? rs.getString("desc1")
                            : rs.getString("desc2"));
                    imp.setMerc3ID(rs.getString("merc3"));
                    imp.setMerc3Descricao(rs.getString("desc3"));

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
                    "select\n"
                    + "idProduto as id_produto,\n"
                    + "CodigoEan as ean,\n"
                    + "qtde_emb as embalagem\n"
                    + "from\n"
                    + "	produto_ean pe "
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("embalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	p.idProduto as id, \n"
                    + "	PesoVariavel as ebalanca,\n"
                    + "	p.Ean,\n"
                    + "	ean1 ,\n"
                    + "	Descricao as descricaocompleta,\n"
                    + "	DescrRed as descricaoreduzida,\n"
                    + "	descricaoetq as descricaogondola,\n"
                    + "	UnidEntra as tipoembalagem,\n"
                    + "	EmbSaida as qtdembalagem,\n"
                    + "	ClassFiscal as ncm,\n"
                    + "	cest as cest,\n"
                    + "	idSituacao as ativo,\n"
                    + "	e.estoque_atual as estoque,\n"
                    + "	p.idGrupo as merc1,\n"
                    + "	p.idSubGrupo as merc2,\n"
                    + "	p.idSubGrupo1  as merc3,\n"
                    + "	pp.MARGEM as margem,\n"
                    + "	pp.CUSTO as custo,\n"
                    + "	validade,\n"
                    + "	pp.VENDA1 as precovenda,\n"
                    + " p.idFamilia as idfamilia, \n"
                    + " concat(icmscompra,'-',substring(tabicmsprodentrada ,1,3),'-',redbase) as idicms,\n"
                    + " pt.icmscompra as aliqEntrada, \n"
                    + " pt.redbase as redEntrada, \n"
                    + " pt.icms as aliqEntrada,\n"
                    + " pt.redbasevenda as redEntrada,\n"
                    + " pt.nat_receita \n"
                    + "from\n"
                    + "	produto p \n"
                    + "join produto_tributacao pt on p.idProduto = pt.idproduto and pt.id_loja  = 1\n"
                    + "join produto_estoque e on e.idProduto  = p.idProduto and e.ID_LOJa = 1\n"
                    + "join produto_preco pp on pp.IDPRODUTO = p.idProduto and pp.ID_LOJA  = 1\n"      
            ))
                {

                    Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                    while (rs.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());

                        imp.setImportId(rs.getString("id"));
                        imp.seteBalanca(rs.getBoolean("ebalanca"));
                        imp.setEan(rs.getString("ean"));

                        ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(rs.getString("ean"), -2));

                        if (bal != null) {
                            imp.seteBalanca(true);
                            imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                            imp.setValidade(bal.getValidade());
                            imp.setEan(String.valueOf(bal.getCodigo()));
                        }

                        imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                        imp.setDescricaoGondola(rs.getString("descricaogondola") == null
                                ? imp.getDescricaoReduzida()
                                : rs.getString("descricaogondola"));

                        imp.setTipoEmbalagem(rs.getString("tipoembalagem"));
                        imp.setTipoEmbalagemCotacao(rs.getString("tipoembalagem"));
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.setValidade(rs.getInt("validade"));
                        imp.setTipoEmbalagemVolume(imp.getTipoEmbalagemCotacao());

                        imp.setNcm(rs.getString("ncm"));
                        imp.setCest(rs.getString("cest"));
                        imp.setSituacaoCadastro(rs.getInt("ativo") == 2 ? SituacaoCadastro.EXCLUIDO : SituacaoCadastro.ATIVO);

                        imp.setEstoque(rs.getDouble("estoque"));

                        imp.setCodMercadologico1(rs.getString("merc1"));
                        imp.setCodMercadologico2(rs.getString("merc2"));
                        imp.setCodMercadologico3(rs.getString("merc3"));
                        imp.setIdFamiliaProduto(rs.getString("idfamilia"));
                        imp.setEan(rs.getString("ean"));

                        imp.setMargem(rs.getDouble("margem"));
                        imp.setCustoComImposto(rs.getDouble("custo"));
                        imp.setPrecovenda(rs.getDouble("precovenda"));

                        String idIcms = rs.getString("idicms");

                        imp.setIcmsDebitoId(idIcms);
                        imp.setIcmsDebitoForaEstadoId(idIcms);
                        imp.setIcmsDebitoForaEstadoNfId(idIcms);
                        imp.setIcmsConsumidorId(idIcms);
                        imp.setIcmsCreditoId(idIcms);
                        imp.setIcmsCreditoForaEstadoId(idIcms);
                        //              imp.setIcmsAliqEntrada(rs.getDouble("aliqEntrada"));
                        //              imp.setIcmsReducaoEntrada(rs.getDouble("redEntrada"));

                        imp.setPiscofinsNaturezaReceita(rs.getString("nat_receita"));

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
                try (ResultSet rst = stm.executeQuery(
                        "select \n"
                        + "1 as id,\n"
                        + "'42384510000150' as cnpj,\n"
                        + "'453.184.067.117' as ie,\n"
                        + "'SUPERMERCADO OLIVIO LTDA' as razao,\n"
                        + "'RUA CAPITAO JOSE JOAQUIM' as endereco,\n"
                        + "'100' as numero,\n"
                        + "'' complemento,\n"
                        + "'MOCOQUINHA' as bairro,\n"
                        + "'MOCOCA' as municipio,\n"
                        + "'SP' as uf,\n"
                        + "'13734140' as cep,\n"
                        + "'' as telefone\n"
                        + "from empresa e "
                )) {
                    while (rst.next()) {
                        ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                        imp.setId(rst.getString("id"));
                        imp.setCnpj(rst.getString("cnpj"));
                        imp.setInscricaoEstadual(rst.getString("ie"));
                        imp.setRazao(rst.getString("razao"));
                        imp.setEndereco(rst.getString("endereco"));
                        imp.setNumero(rst.getString("numero"));
                        imp.setComplemento(rst.getString("complemento"));
                        imp.setBairro(rst.getString("bairro"));
                        imp.setMunicipio(rst.getString("municipio"));
                        imp.setUf(rst.getString("uf"));
                        imp.setCep(rst.getString("cep"));
                        imp.setTelefone(rst.getString("telefone"));

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
                        "select\n"
                        + "	idCliente as id_cliente,\n"
                        + "	nome ,\n"
                        + "	1 as id_empresa,\n"
                        + "	trim(cpf) as cpf_cnpj,\n"
                        + "	limite ,\n"
                        + "	senhacartao as senha,\n"
                        + "	obs \n"
                        + "from\n"
                        + "	cliente c"
                )) {
                    while (rs.next()) {
                        ConveniadoIMP imp = new ConveniadoIMP();
                        imp.setId(rs.getString("id_cliente"));
                        imp.setNome(rs.getString("nome"));
                        imp.setIdEmpresa(rs.getString("id_empresa"));
                        imp.setCnpj(rs.getString("cpf_cnpj"));
                        imp.setConvenioLimite(rs.getDouble("limite"));
                        imp.setSenha(rs.getInt("senha"));
                        imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                        imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);

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
                try (ResultSet rst = stm.executeQuery(
                        "  select\n"
                        + "  IDDEBITO as id,\n"
                        + "  IDCLIENTE  as id_conveniado,\n"
                        + "  ECF,\n"
                        + "  NR_VENDA as documento,\n"
                        + "  DT_VENDA as data_hora,\n"
                        + "  VL_VISTA as valor \n"
                        + "  from debito d \n"
                        + "  where situacao != 'P'"
                )) {
                    while (rst.next()) {
                        ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                        imp.setId(rst.getString("id"));
                        imp.setIdConveniado(rst.getString("id_conveniado"));
                        imp.setEcf(rst.getString("ecf"));
                        imp.setNumeroCupom(rst.getString("documento"));
                        imp.setDataHora(rst.getTimestamp("data_hora"));
                        imp.setValor(rst.getDouble("valor"));

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
                        "select \n"
                        + "	idFamilia as id,\n"
                        + "	nome as descricao\n"
                        + "	from familia f ")) {
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

        @Override
        public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
            List<ProdutoFornecedorIMP> result = new ArrayList<>();

            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	idProduto as produtoid,\n"
                        + "	idFornecedor as fornecedorid,\n"
                        + "	Referencia as codigoexterno,\n"
                        + " embalagem as embalagem \n"
                        + "from\n"
                        + "	itensfornecedor i "
                )) {
                    while (rs.next()) {
                        ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setIdProduto(rs.getString("produtoid"));
                        imp.setIdFornecedor(rs.getString("fornecedorid"));
                        imp.setCodigoExterno(rs.getString("codigoexterno"));
                        imp.setQtdEmbalagem(rs.getDouble("embalagem"));

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
                        + "	IDFORNECEDOR as id, \n"
                        + "	NOME as razao,\n"
                        + "	FANTASIA as fantasia,\n"
                        + "	RG_IE as ie,\n"
                        + "	CPF_CGC as cnpj_cnpf,\n"
                        + "	ENDERECO ,\n"
                        + "	NUMERO ,\n"
                        + "	COMPLEMENTO ,\n"
                        + "	BAIRRO ,\n"
                        + "	CIDADE ,\n"
                        + " codmunicipio as ibge,\n"
                        + "	CEP ,\n"
                        + " condicaofat as condicao, \n"
                        + " DiasVisita as visita, \n"
                        + " entrega, \n"
                        + "	TELEFONE \n"
                        + "from\n"
                        + "	fornecedor f"
                )) {
                    while (rs.next()) {
                        FornecedorIMP imp = new FornecedorIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());

                        imp.setImportId(rs.getString("id"));
                        imp.setRazao(Utils.acertarTexto(rs.getString("razao")));
                        imp.setFantasia(Utils.acertarTexto(rs.getString("fantasia")));
                        imp.setIe_rg(rs.getString("ie"));
                        imp.setCnpj_cpf(rs.getString("cnpj_cnpf"));
                        imp.setEndereco(rs.getString("endereco"));
                        imp.setNumero(rs.getString("numero"));
                        imp.setComplemento(rs.getString("COMPLEMENTO"));
                        imp.setBairro(rs.getString("bairro"));
                        imp.setMunicipio(rs.getString("CIDADE"));
                        imp.setIbge_municipio(rs.getInt("ibge"));
                        imp.setCep(rs.getString("cep"));
                        imp.setTel_principal(rs.getString("telefone"));
                        imp.setCondicaoPagamento(rs.getInt("condicao"));
                        imp.setPrazoSeguranca(0);
                        imp.setPrazoEntrega(rs.getInt("entrega"));
                        imp.setPrazoVisita(rs.getInt("visita"));

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
                try (ResultSet rs = stm.executeQuery(
                        "select\n"
                        + "	idCliente as id,\n"
                        + "	trim(cpf) as cpf,\n"
                        + "	rg ,\n"
                        + "	nome ,\n"
                        + "	endereco ,\n"
                        + "	numero ,\n"
                        + "	complemento ,\n"
                        + "	bairro ,\n"
                        + "	cidade ,\n"
                        + "	uf ,\n"
                        + "	cep ,\n"
                        + "	celular ,\n"
                        + "	status_cadastro ,\n"
                        + " limite \n"
                        + "from\n"
                        + "	cliente c"
                )) {
                    while (rs.next()) {
                        ClienteIMP imp = new ClienteIMP();

                        imp.setId(rs.getString("id"));
                        imp.setCnpj(rs.getString("cpf"));
                        imp.setInscricaoestadual(rs.getString("rg"));
                        imp.setRazao(rs.getString("nome"));
                        imp.setFantasia(rs.getString("nome"));
                        imp.setEndereco(rs.getString("endereco"));
                        imp.setNumero(rs.getString("numero"));
                        imp.setComplemento(rs.getString("complemento"));
                        imp.setBairro(rs.getString("bairro"));
                        imp.setMunicipio(rs.getString("cidade"));
                        imp.setUf(rs.getString("uf"));
                        imp.setCep(rs.getString("cep"));
                        imp.setTelefone(rs.getString("celular"));
                        imp.setValorLimite(rs.getDouble("limite"));

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
                        "  select\n"
                        + "  IDDEBITO as id,\n"
                        + "  IDCLIENTE  as id_cliente,\n"
                        + "  DT_VENDA as emissao,\n"
                        + "  DT_VENC as vencto,\n"
                        + "  VL_VISTA as valor \n"
                        + "  from debito d \n"
                        + "  where situacao != 'P'"
                )) {
                    while (rs.next()) {
                        CreditoRotativoIMP imp = new CreditoRotativoIMP();

                        imp.setId(rs.getString("id"));
                        imp.setParcela(1);
                        imp.setIdCliente(rs.getString("id_cliente"));
                        imp.setDataEmissao(rs.getDate("emissao"));
                        imp.setDataVencimento(rs.getDate("vencto"));
                        imp.setValor(rs.getDouble("valor"));

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
        return new ImperiumMarket2_5DAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new ImperiumMarket2_5DAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
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
                        String id = rst.getString("idvenda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora_cupom");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora_cupom");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));

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
                    + "distinct \n"
                    + "	REPLACE(concat(i.cupom,i.ecf,i.hora_cupom),':','') as idvenda,\n"
                    + "	i.cupom as numeroCupom,\n"
                    + "	i.ecf ,\n"
                    + "	i.datamov as data,\n"
                    + "	i.hora_cupom ,\n"
                    + "	i.idcliente as id_cliente,\n"
                    + "	c.cpf ,\n"
                    + "	c.nome as nomecliente,\n"
                    + "	sum(i2.valor) as valor \n"
                    + "from\n"
                    + "	itensvenda i\n"
                    + "	left join cliente c on c.idCliente = i.idcliente \n"
                    + "	left join itensvenda i2 on i.iditensvenda = i2.idItensVenda \n"
                    + "	where i.datamov between '" + strDataInicio + "' and '" + strDataTermino + "' \n"
                    + "	group by REPLACE(concat(i.cupom,i.ecf,i.hora_cupom),':','')";

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

                        next.setVenda(rst.getString("idvenda"));
                        next.setId(rst.getString("id_item"));
                        //next.setSequencia(rst.getInt("seq"));
                        next.setProduto(rst.getString("produto"));
                        //next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("ean"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setCancelado(rst.getBoolean("ativo"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));

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
                    + "	REPLACE(concat(i.cupom,i.ecf,i.hora_cupom),':','') as idvenda,\n"
                    + "	idItensVenda as id_item,\n"
                    + "	i.idProduto as produto,\n"
                    + "	codigoEan as ean,\n"
                    + "	p.Descricao ,\n"
                    + "	i.quantidade ,\n"
                    + "	round(i.valor/quantidade,3) as valor,\n"
                    + "	i.acrescimoItem as acrescimo,\n"
                    + "	i.descontoItem as desconto,\n"
                    + " case when i.situacao = 'C' then 1 else 0 end as ativo \n"
                    + "	from\n"
                    + "	itensvenda i\n"
                    + "	left join produto p on p.idProduto = i.idProduto \n"
                    + "	where i.datamov"
                    + " between '" + VendaIterator.FORMAT.format(dataInicio) + "' and '" + VendaIterator.FORMAT.format(dataTermino) + "' \n"
                    + "and i.idproduto != 471"
                    + "	order by datamov , hora_cupom , cupom ";

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
