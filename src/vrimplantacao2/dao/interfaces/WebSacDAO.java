package vrimplantacao2.dao.interfaces;

import java.io.File;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
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
import vrimplantacao2.vo.importacao.ReceitaIMP;

/**
 *
 * @author lucasrafael
 */
public class WebSacDAO extends InterfaceDAO implements MapaTributoProvider {

    public String v_arquivo = "";

    @Override
    public String getSistema() {
        return "WebSac";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codcf codigo,\n"
                    + "('NOME: '||descricao||' CST:'||codcst||' ALIQ: '||aliqicms||' REDU: '||aliqredicms) as descricao, \n"
                    + "aliqicms, \n"
                    + "aliqredicms, \n"
                    + "codcst\n"
                    + "from classfiscal\n"
                    + "order by codcf"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " m1.coddepto cod_m1,\n"
                    + " m1.nome desc_m1,\n"
                    + " m2.codgrupo cod_m2,\n"
                    + " m2.descricao desc_m2,\n"
                    + " m3.codsubgrupo cod_m3,\n"
                    + " m3.descricao desc_m3\n"
                    + "from departamento m1\n"
                    + " inner join grupoprod m2 on m2.coddepto = m1.coddepto\n"
                    + " inner join subgrupo m3 on m3.codgrupo = m2.codgrupo\n"
                    + "order by m1.coddepto, m2.codgrupo, m3.codsubgrupo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cod_m1"));
                    imp.setMerc1Descricao(rst.getString("desc_m1"));
                    imp.setMerc2ID(rst.getString("cod_m2"));
                    imp.setMerc2Descricao(rst.getString("desc_m2"));
                    imp.setMerc3ID(rst.getString("cod_m3"));
                    imp.setMerc3Descricao(rst.getString("desc_m3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	codsimilar,\n"
                    + "	descricao\n"
                    + "from \n"
                    + "	simprod")) {
                while (rs.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("codsimilar"));
                    imp.setDescricao(rs.getString("descricao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codproduto, \n"
                    + "codean, \n"
                    + "quantidade \n"
                    + "from produtoean \n"
                    + "order by codproduto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codproduto"));
                    imp.setEan(rst.getString("codean"));
                    imp.setQtdEmbalagem(rst.getInt("quantidade"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.codproduto,\n"
                    + "p.descricao,\n"
                    + "p.descricaofiscal,\n"
                    + "pe.codean ean,\n"
                    + "p.coddepto,\n"
                    + "p.codgrupo,\n"
                    + "p.codsubgrupo,\n"
                    + "p.codsimilar,\n"
                    + "p.estminimo,\n"
                    + "p.estmaximo,\n"
                    + "p.pesoliq,\n"
                    + "p.pesobruto,\n"
                    + "p.pesado,\n"
                    + "p.foralinha,\n"
                    + "p.qtdeetiq,\n"
                    + "p.diasvalidade,\n"
                    + "p.pesounid,\n"
                    + "p.vasilhame,\n"
                    + "p.codvasilhame,\n"
                    + "p.codfamilia,\n"
                    + "p.custotab,\n"
                    + "p.precoatc,\n"
                    + "p.precovrj,\n"
                    + "p.margematc,\n"
                    + "p.margemvrj,\n"
                    + "p.datainclusao,\n"
                    + "p.custorep,\n"
                    + "p.altura,\n"
                    + "p.largura,\n"
                    + "p.enviarecommerce,\n"
                    + "p.comprimento,\n"
                    + "p.cest,\n"
                    + "case when p.pesado = 'S' and p.pesounid = 'P' then 'KG' else u.sigla end as embalagem,\n"
                    + "e.quantidade as qtdembalagem,\n"
                    + "pcs.codcst cstpiscofinssaida,\n"
                    + "pce.codcst cstpiscofinsentrada,\n"
                    + "p.natreceita,\n"
                    + "ncm.codigoncm,\n"
                    + "p.codcfpdv,\n"
                    + "cf.descricao icmsdesc,\n"
                    + "cf.codcst as icmscst,\n"
                    + "cf.aliqicms as icmsaliq,\n"
                    + "cf.aliqredicms as icmsred\n"
                    + "from produto p \n"
                    + "left join produtoean pe on p.codproduto = pe.codproduto\n"
                    + "left join embalagem e on e.codembal = p.codembalvda\n"
                    + "left join unidade u on u.codunidade = e.codunidade\n"
                    + "left join piscofins pcs on pcs.codpiscofins = p.codpiscofinssai\n"
                    + "left join piscofins pce on pce.codpiscofins = p.codpiscofinsent\n"
                    + "left join ncm on ncm.idncm = p.idncm\n"
                    + "left join classfiscal cf on cf.codcf = p.codcfpdv\n"
                    + "order by p.codproduto"
            )) {

                Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();

                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codproduto"));
                    imp.seteBalanca("S".equals(rst.getString("pesado")));

                    imp.setEan(rst.getString("ean"));
                    imp.setValidade(rst.getInt("diasvalidade"));

                    ProdutoBalancaVO bal = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (bal != null) {
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(bal.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(bal.getValidade() > 1
                                ? bal.getValidade() : rst.getInt("diasvalidade"));
                        imp.setEan(imp.getImportId());
                    }

                    imp.setTipoEmbalagem(rst.getString("embalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setDescricaoCompleta(rst.getString("descricaofiscal"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("coddepto"));
                    imp.setCodMercadologico2(rst.getString("codgrupo"));
                    imp.setCodMercadologico3(rst.getString("codsubgrupo"));
                    imp.setIdFamiliaProduto(rst.getString("codsimilar"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estmaximo"));
                    imp.setPesoLiquido(rst.getDouble("pesoliq"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPrecovenda(rst.getDouble("precovrj"));
                    imp.setCustoComImposto(rst.getDouble("custorep"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setMargem(rst.getDouble("margemvrj"));
                    imp.setDataCadastro(rst.getDate("datainclusao"));
                    imp.setNcm(rst.getString("codigoncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpiscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("natreceita"));
                    imp.setIcmsDebitoId(rst.getString("codcfpdv"));
                    imp.setIcmsCreditoId(rst.getString("codcfpdv"));
                    imp.setIcmsConsumidorId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsDebitoId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsDebitoId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsDebitoId());

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        if (opcao == OpcaoProduto.ESTOQUE) {
            try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "with estoque as\n"
                        + "(\n"
                        + "  select max(data) as data, codproduto from produtoestabsaldo where codestabelec = " + getLojaOrigem() + " group by codproduto\n"
                        + ")\n"
                        + "select pe.codproduto, pe.saldo, pe.data \n"
                        + "from produtoestabsaldo pe\n"
                        + "inner join estoque e on e.codproduto = pe.codproduto and pe.data = e.data\n"
                        + "where codestabelec = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("codproduto"));
                        imp.setEstoque(rst.getDouble("saldo"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.id, \n"
                    + "p.descritivo,\n"
                    + "p.receita,\n"
                    + "c.rendimento,\n"
                    + "pc.produto,\n"
                    + "pc.qtde,\n"
                    + "pc.qtdeemb\n"
                    + "from produtos p\n"
                    + "inner join composicao c on c.produto_base = p.id\n"
                    + "inner join produtos_composicao pc on pc.produto_base = p.id\n"
                    + "where p.composto = 2\n"
                    + "order by p.id"
            )) {
                while (rst.next()) {
                    ReceitaIMP imp = new ReceitaIMP();

                    double qtdEmbUtilizado = 0;
                    qtdEmbUtilizado = rst.getDouble("qtde");

                    imp.setImportsistema(getSistema());
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportid(rst.getString("id"));
                    imp.setIdproduto(rst.getString("id"));
                    imp.setDescricao(rst.getString("descritivo"));
                    imp.setRendimento(rst.getDouble("rendimento"));
                    imp.setQtdembalagemreceita((int) qtdEmbUtilizado);
                    imp.setQtdembalagemproduto(rst.getInt("qtdeemb"));
                    imp.setFator(1);
                    imp.getProdutos().add(rst.getString("produto"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
    
    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codcliente id,\n"
                    + "	cpfcnpj cnpj,\n"
                    + "	rgie ie,\n"
                    + "	razaosocial razao,\n"
                    + "	enderfat endereco,\n"
                    + "	numerofat numero,\n"
                    + "	complementofat complemento,\n"
                    + "	bairrofat bairro,\n"
                    + "	codcidadefat cidade,\n"
                    + "	uffat uf,\n"
                    + "	cepfat cep,\n"
                    + "	fonefat telefone,\n"
                    + "	codstatus ativo,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	cliente c\n"
                    + "where\n"
                    + "	convenio = 'S'"
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
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setSituacaoCadastro(rst.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacoes(rst.getString("observacao"));

                    result.add(imp);

                }
            }
        }

        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	codcliente id_cliente,\n"
                    + "	nome,\n"
                    + "	codempresa id_empresa,\n"
                    + "	cpfcnpj cpf_cnpj,\n"
                    + "	limite1 limite,\n"
                    + "	case when codstatus = 1 then 1 else 0 end status,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	cliente\n"
                    + "where\n"
                    + "	codempresa is not null\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rs.getString("id_cliente"));
                    imp.setNome(rs.getString("nome"));
                    imp.setIdEmpresa(rs.getString("id_empresa"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setConvenioLimite(rs.getDouble("limite"));
                    imp.setLojaCadastro(Integer.parseInt(getLojaOrigem()));
                    imp.setSituacaoCadastro(rs.getInt("status") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	cr.id,\n"
                    + "	ID_CADASTRO id_convenio,\n"
                    + " cli.ID id_conveniado,\n"
                    + "	CAIXA ecf,\n"
                    + "	NF cupom,\n"
                    + "	cr.DATAHORA_CADASTRO dataemissao,\n"
                    + "	VALOR,\n"
                    + "	cr.OBSERVACAO\n"
                    + "FROM\n"
                    + "	VW_CONTAS cr\n"
                    + "	JOIN CONVENIADAS cv ON cv.id = cr.ID_CADASTRO \n"
                    + " JOIN CLIENTES cli ON cli.CNPJ_CPF = cr.CPF_CNPJ\n"
                    + "WHERE\n"
                    + "	cr.EMPRESA = " + getLojaOrigem() + "\n"
                    + "	AND PLANO_CONTA = 71000\n"
                    + "	AND PAGAMENTO IS NULL\n"
                    + "ORDER BY 1"
            )) {
                while (rst.next()) {

                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdConveniado(rst.getString("id_conveniado"));
                    imp.setEcf(rst.getString("ecf"));
                    imp.setNumeroCupom(rst.getString("cupom"));
                    imp.setDataHora(rst.getTimestamp("dataemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    f.codfornec,\n"
                    + "    regexp_replace(f.nome,'[^A-z 0-9]','','g') as razao,\n"
                    + "    regexp_replace(f.endereco,'[^A-z 0-9]','','g') endereco,\n"
                    + "    regexp_replace(f.bairro,'[^A-z 0-9]','','g') bairro,\n"
                    + "    f.cep,\n"
                    + "    f.codcidade,\n"
                    + "    regexp_replace(c.nome,'[^A-z 0-9]','','g') as nomecidade,\n"
                    + "    c.codoficial cidadeibge,\n"
                    + "    f.uf,\n"
                    + "    regexp_replace(f.contato1,'[^A-z 0-9]','','g') as contato1,\n"
                    + "    f.fone1,\n"
                    + "    f.fone2,\n"
                    + "    f.fone3,\n"
                    + "    f.site,\n"
                    + "    f.email,\n"
                    + "    f.tppessoa,\n"
                    + "    f.cpfcnpj,\n"
                    + "    f.rgie,\n"
                    + "    f.codatividade,\n"
                    + "    f.codbanco,\n"
                    + "    f.agencia,\n"
                    + "    f.contacorrente,\n"
                    + "    regexp_replace(f.observacao,'[^A-z 0-9]','','g') as observacao,\n"
                    + "    f.contato2,\n"
                    + "    f.contato3,\n"
                    + "    f.email1,\n"
                    + "    f.email2,\n"
                    + "    f.email3,\n"
                    + "    f.fone,\n"
                    + "    f.fax,\n"
                    + "    f.numero,\n"
                    + "    f.complemento,\n"
                    + "    f.suframa,\n"
                    + "    f.datainclusao,\n"
                    + "    f.tipocompra,\n"
                    + "    f.inscmunicipal,\n"
                    + "    f.status\n"
                    + "from fornecedor f\n"
                    + "left join cidade c on c.codcidade = f.codcidade\n"
                    + "order by \n"
                    + "	codfornec"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codfornec"));
                    imp.setCnpj_cpf(rst.getString("cpfcnpj"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setIe_rg(rst.getString("rgie"));
                    imp.setInsc_municipal(rst.getString("inscmunicipal"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("nomecidade"));
                    imp.setIbge_municipio(rst.getInt("cidadeibge"));
                    imp.setUf(rst.getString("uf"));
                    imp.setDatacadastro(rst.getDate("datainclusao"));

                    if ((rst.getString("contato1") != null)
                            && (!rst.getString("contato1").trim().isEmpty())) {
                        imp.setObservacao("CONTATO - " + rst.getString("contato1") + " ");
                    }

                    imp.setObservacao(imp.getObservacao() + rst.getString("observacao"));
                    imp.setTel_principal(rst.getString("fone"));

                    if ((rst.getString("fone1") != null)
                            && (!rst.getString("fone1").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 1",
                                rst.getString("fone1"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 2",
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fone3") != null)
                            && (!rst.getString("fone3").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "TELEFONE 3",
                                rst.getString("fone3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "4",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("email1") != null)
                            && (!rst.getString("email1").trim().isEmpty())) {
                        imp.addContato(
                                "5",
                                "EMAIL 1",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email1").toLowerCase()
                        );
                    }
                    if ((rst.getString("email2") != null)
                            && (!rst.getString("email2").trim().isEmpty())) {
                        imp.addContato(
                                "6",
                                "EMAIL 2",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email2").toLowerCase()
                        );
                    }
                    if ((rst.getString("email3") != null)
                            && (!rst.getString("email3").trim().isEmpty())) {
                        imp.addContato(
                                "6",
                                "EMAIL 3",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email3").toLowerCase()
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores(OpcaoFornecedor opcao) throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        WorkbookSettings settings = new WorkbookSettings();
        Workbook arquivo = Workbook.getWorkbook(new File(v_arquivo), settings);
        Sheet[] sheets = arquivo.getSheets();
        int linha;
        if (opcao == OpcaoFornecedor.RAZAO_SOCIAL) {

            linha = 0;
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                for (int i = 0; i < sheet.getRows(); i++) {

                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellRazao = sheet.getCell(2, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setRazao(cellRazao.getContents());
                    result.add(imp);
                }
            }
            return result;
        }
        if (opcao == OpcaoFornecedor.NOME_FANTASIA) {

            linha = 0;
            for (int sh = 0; sh < sheets.length; sh++) {
                Sheet sheet = arquivo.getSheet(sh);
                for (int i = 0; i < sheet.getRows(); i++) {

                    linha++;
                    if (linha == 1) {
                        continue;
                    }

                    Cell cellCodigo = sheet.getCell(0, i);
                    Cell cellFantasia = sheet.getCell(1, i);

                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(cellCodigo.getContents());
                    imp.setFantasia(cellFantasia.getContents());
                    result.add(imp);
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codproduto, \n"
                    + "codfornec, \n"
                    + "reffornec,\n"
                    + "principal \n"
                    + "from prodfornec\n"
                    + "order by principal desc"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("codproduto"));
                    imp.setIdFornecedor(rst.getString("codfornec"));
                    imp.setCodigoExterno(rst.getString("reffornec"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    ""
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("dataalteracao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setVencimento(rst.getDate("vencimento"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	c.codcliente,\n"
                    + "	regexp_replace(c.nome,'[^A-z 0-9]','','g') nome,\n"
                    + "	regexp_replace(c.razaosocial,'[^A-z 0-9]','','g') razaosocial,\n"
                    + "	regexp_replace(c.enderfat,'[^A-z 0-9]','','g') enderfat,\n"
                    + "	regexp_replace(c.bairrofat,'[^A-z 0-9]','','g') bairrofat,\n"
                    + "	c.cepfat,\n"
                    + "	c.codcidadefat,\n"
                    + "	c.uffat,\n"
                    + "	regexp_replace(c.enderent,'[^A-z 0-9]','','g') enderent,\n"
                    + "	regexp_replace(c.bairroent,'[^A-z 0-9]','','g') bairroent,\n"
                    + "	c.cepent,\n"
                    + "	c.codcidadeent,\n"
                    + "	c.ufent,\n"
                    + "	regexp_replace(c.contato,'[^A-z 0-9]','','g') contato,\n"
                    + "	c.site,\n"
                    + "	c.email,\n"
                    + "	c.tppessoa,\n"
                    + "	c.cpfcnpj,\n"
                    + "	c.rgie,\n"
                    + "	regexp_replace(c.observacao,'[^A-z 0-9]','','g') observacao,\n"
                    + "	c.dtnascto,\n"
                    + "	c.sexo,\n"
                    + "	c.estcivil,\n"
                    + "	c.tipomoradia,\n"
                    + "	c.dtmoradia,\n"
                    + "	regexp_replace(c.enderres,'[^A-z 0-9]','','g') enderres,\n"
                    + "	regexp_replace(c.bairrores,'[^A-z 0-9]','','g') bairrores,\n"
                    + "	c.cepres,\n"
                    + "	c.codcidaderes,\n"
                    + "	regexp_replace(cid.nome,'[^A-z 0-9]','','g') nomecidade,\n"
                    + "	cid.codoficial as cidadeibge,\n"
                    + "	c.ufres,\n"
                    + "	regexp_replace(c.nomeconj,'[^A-z 0-9]','','g') nomeconj,\n"
                    + "	c.cpfconj,\n"
                    + "	c.rgconj,\n"
                    + "	c.salarioconj,\n"
                    + "	c.foneres,\n"
                    + "	c.celular,\n"
                    + "	c.fonefat,\n"
                    + "	c.faxfat,\n"
                    + "	c.foneent,\n"
                    + "	c.faxent,\n"
                    + "	c.dtinclusao,\n"
                    + "	c.salario,\n"
                    + "	c.senha,\n"
                    + "	c.numerofat,\n"
                    + "	c.complementofat,\n"
                    + "	c.numeroent,\n"
                    + "	c.complementoent,\n"
                    + "	c.numerores,\n"
                    + "	c.complementores,\n"
                    + "	(coalesce(c.limite1, 0) + coalesce(c.limite2) - coalesce(c.debito1, 0) - coalesce(debito2, 0)) as valorlimite,\n"
                    + "	c.limite1,\n"
                    + "	c.emailnfe,\n"
                    + "	c.rgemissor,\n"
                    + "	c.codstatus,\n"
                    + "	regexp_replace(s.descricao,'[^A-z 0-9]','','g') descricao,\n"
                    + "	s.bloqueado\n"
                    + "from\n"
                    + "	cliente c\n"
                    + "inner join statuscliente s on s.codstatus = c.codstatus\n"
                    + "left join cidade cid on cid.codcidade = c.codcidaderes\n"
                    + "order by\n"
                    + "	c.codcliente"
            /*"select \n"
                    + "c.codcliente,\n"
                    + "c.nome,\n"
                    + "c.razaosocial,\n"
                    + "c.enderfat,\n"
                    + "c.bairrofat,\n"
                    + "c.cepfat,\n"
                    + "c.codcidadefat,\n"
                    + "c.uffat,\n"
                    + "c.enderent,\n"
                    + "c.bairroent,\n"
                    + "c.cepent,\n"
                    + "c.codcidadeent,\n"
                    + "c.ufent,\n"
                    + "c.contato,\n"
                    + "c.site,\n"
                    + "c.email,\n"
                    + "c.tppessoa,\n"
                    + "c.cpfcnpj,\n"
                    + "c.rgie,\n"
                    + "c.observacao,\n"
                    + "c.dtnascto,\n"
                    + "c.sexo,\n"
                    + "c.estcivil,\n"
                    + "c.tipomoradia,\n"
                    + "c.dtmoradia,\n"
                    + "c.enderres,\n"
                    + "c.bairrores,\n"
                    + "c.cepres,\n"
                    + "c.codcidaderes,\n"
                    + "cid.nome as nomecidade,\n"
                    + "cid.codoficial as cidadeibge,\n"
                    + "c.ufres,\n"
                    + "c.nomeconj,\n"
                    + "c.cpfconj,\n"
                    + "c.rgconj,\n"
                    + "c.salarioconj,\n"
                    + "c.foneres,\n"
                    + "c.celular,\n"
                    + "c.fonefat,\n"
                    + "c.faxfat,\n"
                    + "c.foneent,\n"
                    + "c.faxent,\n"
                    + "c.dtinclusao,\n"
                    + "c.salario,\n"
                    + "c.senha,\n"
                    + "c.numerofat,\n"
                    + "c.complementofat,\n"
                    + "c.numeroent,\n"
                    + "c.complementoent,\n"
                    + "c.numerores,\n"
                    + "c.complementores,\n"
                    + "(coalesce(c.limite1, 0) + coalesce(c.limite2) - coalesce(c.debito1, 0) - coalesce(debito2, 0)) as valorlimite,\n"
                    + "c.limite1,\n"
                    + "c.emailnfe,\n"
                    + "c.rgemissor,\n"
                    + "c.codstatus,\n"
                    + "s.descricao,\n"
                    + "s.bloqueado\n"
                    + "from cliente c\n"
                    + "inner join statuscliente s on s.codstatus = c.codstatus\n"
                    + "left join cidade cid on cid.codcidade = c.codcidaderes\n"
                    + "order by c.codcliente"*/
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codcliente"));
                    imp.setRazao(rst.getString("razaosocial"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("rgie"));
                    imp.setOrgaoemissor(rst.getString("rgemissor"));
                    imp.setEndereco(rst.getString("enderres"));
                    imp.setNumero(rst.getString("numerores"));
                    imp.setComplemento(rst.getString("complementores"));
                    imp.setBairro(rst.getString("bairrores"));
                    imp.setCep(rst.getString("cepres"));
                    imp.setMunicipioIBGE(rst.getInt("cidadeibge"));
                    imp.setUf(rst.getString("ufres"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setDataNascimento(rst.getDate("dtnascto"));
                    if ((rst.getString("sexo") != null)
                            && (!rst.getString("sexo").trim().isEmpty())) {
                        if ("M".equals(rst.getString("sexo"))) {
                            imp.setSexo(TipoSexo.MASCULINO);
                        } else {
                            imp.setSexo(TipoSexo.FEMININO);
                        }
                    } else {
                        imp.setSexo(TipoSexo.MASCULINO);
                    }

                    imp.setTelefone(rst.getString("foneres"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setNomeConjuge(rst.getString("nomeconj"));

                    imp.setDataCadastro(rst.getDate("dtinclusao"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("limite1"));
                    imp.setBloqueado("S".equals(rst.getString("bloqueado")));
                    imp.setPermiteCreditoRotativo(imp.isBloqueado());
                    imp.setPermiteCheque(imp.isBloqueado());
                    imp.setAtivo("N".equals(rst.getString("bloqueado")));

                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "SITE",
                                null,
                                null,
                                rst.getString("site").toLowerCase()
                        );
                    }
                    if ((rst.getString("faxfat") != null)
                            && (!rst.getString("faxfat").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "FAXFAT",
                                rst.getString("faxfat"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("faxent") != null)
                            && (!rst.getString("faxent").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "FAXENT",
                                rst.getString("faxent"),
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
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "l.codlancto as id,\n"
                    + "l.parcela,\n"
                    + "l.codparceiro as codcliente,\n"
                    + "l.valorparcela as valor,\n"
                    + "l.valorliquido,\n"
                    + "l.valorjuros,\n"
                    + "l.valordescto,\n"
                    + "l.valoracresc,\n"
                    + "l.dtemissao as dataemissao,\n"
                    + "l.dtvencto as datavencimento,\n"
                    + "l.numnotafis as numerocupom\n"
                    + "from lancamento l\n"
                    + "where l.pagrec = 'R' \n"
                    + "and l.status = 'A' \n"
                    + "and l.tipoparceiro = 'C'\n"
                    + "and l.codestabelec = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("codcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setJuros(rst.getDouble("valorjuros"));

                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	codcheque id,\n"
                    + "	dtemissao emissao,\n"
                    + "	valorcheque valor,\n"
                    + "	numcheque nro_cheque,\n"
                    + "	b.codoficial banco,\n"
                    + "	b.agencia,\n"
                    + "	f.rgie,\n"
                    + "	f.cpfcnpj,\n"
                    + "	nominal nome,\n"
                    + "	f.fone1 telefone,\n"
                    + "	c.observacao\n"
                    + "from\n"
                    + "	cheque c\n"
                    + "	join banco b on b.codbanco = c.codbanco\n"
                    + "	left join fornecedor f on f.razaosocial like c.nominal\n"
                    + "where\n"
                    + "	c.codestabelec = " + getLojaOrigem()
            //                  + "	--and dtpagto is null"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDate(rst.getDate("emissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setNumeroCheque(rst.getString("nro_cheque"));
                    imp.setBanco(rst.getInt("banco"));
                    imp.setAgencia(rst.getString("agencia"));
                    imp.setRg(rst.getString("ierg"));
                    imp.setCpf(rst.getString("cpfcnpj"));
                    imp.setNome(rst.getString("nome"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "codestabelec codigo, \n"
                    + "razaosocial descricao \n"
                    + "from estabelecimento\n"
                    + "order by codestabelec"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }
}
