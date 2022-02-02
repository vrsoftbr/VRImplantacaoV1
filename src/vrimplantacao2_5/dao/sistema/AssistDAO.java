package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
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
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author guilhermegomes
 */
public class AssistDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Assist";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.ATIVO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.MARGEM,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS
        ));
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
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO,
                OpcaoCliente.CONVENIO_CONVENIADO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	cficodigo id,\n"
                    + "	cficodigo || ' - ' || (case when cfiecfaliq::varchar = '0.00' then cfiecfpos else cfiecfaliq::varchar end) descricao,\n"
                    + "	cfiecfaliq icms,\n"
                    + "	case when cfiecfpos = 'FF' then 60\n"
                    + "	         when cfiecfpos = 'II' then 40 else 0 end as cst,\n"
                    + "	0 reducao\n"
                    + "from \n"
                    + "	public.file059")) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getDouble("icms"),
                            rs.getDouble("reducao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	distinct \n"
                    + "	gr.grumcodigo merc1,\n"
                    + "	gr.grumdescri descmerc1,\n"
                    + "	case when sg.sbgrucodig = '' then gr.grumcodigo else sg.sbgrucodig end as merc2,\n"
                    + "	case when sg.sbgrudescr = '' then gr.grumdescri else sg.sbgrudescr end as descmerc2\n"
                    + "from \n"
                    + "	public.file005 pr \n"
                    + "join public.file004 gr on pr.procategor = gr.grumcodigo\n"
                    + "join public.file002 sg on pr.prosbgrcod = sg.sbgrucodig\n"
                    + "order by \n"
                    + "	merc1, merc2")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID(imp.getMerc2ID());
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
        Map<Integer, vrimplantacao2.vo.cadastro.ProdutoBalancaVO> produtosBalanca
                = new ProdutoBalancaDAO().getProdutosBalanca();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	p.procodigo id,\n"
                    + "	p.procodbar1 ean,\n"
                    + "	p.prodesampl descricaocompleta,\n"
                    + "	p.prodesatac descricaoatacado,\n"
                    + "	p.procategor merc1,\n"
                    + "	p.prosbgrcod merc2,\n"
                    + "	p.prouniabre unidade,\n"
                    + "	p.provendeci balanca,\n"
                    + "	p.proestsupe estoque,\n"
                    + "	p.prominsupe estoquemin,\n"
                    + "	p.promaxsupe estoquemax,\n"
                    + "	p.procustsm custo,\n"
                    + "	p.prouprcomp custocompra,\n"
                    + "	p.promargsm margem,\n"
                    + "	p.promargsm2 margem2,\n"
                    + "	p.promargsm3 margem3,\n"
                    + "	p.promargat margematacado,\n"
                    + "	p.proprcvare precovenda,\n"
                    + "	p.propreco2 preco2,\n"
                    + "	p.propreco3 preco3,\n"
                    + "	p.propreco4 preco4,\n"
                    + "	p.propreco5 preco5,\n"
                    + "	p.propreco6 preco6,\n"
                    + "	p.probalanca isbalanca,\n"
                    + "	p.provalidad validade,\n"
                    + "	p.proposiecf icms_ecf,\n"
                    + "	p.procst cst,\n"
                    + "	p.proassoccf idaliquota,\n"
                    + "	p.prodatacad cadastro,\n"
                    + "	p.proclfisc ncm,\n"
                    + "	p.proncmsh cest,\n"
                    + "	p.prosittrib situacaotributaria\n"
                    + "from \n"
                    + "	public.file005 p WHERE proultvend >= '2019-04-01'")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());

                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setCustoComImposto(rs.getDouble("custo"));
                    imp.setCustoSemImposto(rs.getDouble("custo"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setMargem(rs.getDouble("margem"));

                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));

                    imp.setIcmsConsumidorId(rs.getString("idaliquota"));
                    imp.setIcmsDebitoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoId(imp.getIcmsConsumidorId());
                    imp.setIcmsDebitoForaEstadoNfId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoId(imp.getIcmsConsumidorId());
                    imp.setIcmsCreditoForaEstadoId(imp.getIcmsConsumidorId());

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    ProdutoBalancaVO balanca = produtosBalanca.get(Utils.stringToInt(imp.getImportId(), -2));

                    if (balanca != null) {
                        imp.setEan(String.valueOf(balanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("P".equals(balanca.getPesavel()) ? "KG" : "UN");
                        imp.setValidade(balanca.getValidade() > 1
                                ? balanca.getValidade() : 0);
                    } else {
                        imp.setValidade(Utils.stringToInt(rs.getString("validade")));
                        imp.seteBalanca(rs.getString("isbalanca").trim().equals("S"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	procodigo id,\n"
                    + "	procodbar1 ean\n"
                    + "from \n"
                    + "	public.file005\n"
                    + "where \n"
                    + "	procodbar1 != ''\n"
                    + "union \n"
                    + "select \n"
                    + "	procodigo id,\n"
                    + "	procodbar2 ean\n"
                    + "from \n"
                    + "	public.file005\n"
                    + "where \n"
                    + "	procodbar2 != ''\n"
                    + "union\n"
                    + "select \n"
                    + "	procodigo id,\n"
                    + "	procodbar3 ean\n"
                    + "from \n"
                    + "	public.file005\n"
                    + "where \n"
                    + "	procodbar3 != ''\n"
                    + "union \n"
                    + "select \n"
                    + "	procodigo id,\n"
                    + "	procodbar4 ean\n"
                    + "from \n"
                    + "	public.file005\n"
                    + "where \n"
                    + "	procodbar4 != ''")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	f.forcgc id, \n"
                    + "	f.forie ie,\n"
                    + "	f.forcpf cnpj,\n"
                    + "	f.forfantasi fantasia,\n"
                    + "	f.forazao razao,\n"
                    + "	f.forenderec endereco,\n"
                    + "	f.forbairro bairro,\n"
                    + "	f.forcidade cidade,\n"
                    + "	f.forendnum numero,\n"
                    + "	f.forendcomp complemento,\n"
                    + "	f.forcep cep,\n"
                    + "	f.foruf uf,\n"
                    + "	f.forfone1 telefone,\n"
                    + "	f.forfone2 telefone2,\n"
                    + "   f.forfax fax,\n"
                    + "	f.forcontato contato,\n"
                    + "	f.forobserv1 observacao,\n"
                    + "	f.forobserv2 obs2\n"
                    + "from \n"
                    + "	public.file037 f")) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("id"));
                    imp.setIe_rg(rs.getString("ie"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTel_principal(rs.getString("telefone"));

                    String tel2 = rs.getString("telefone2"),
                            fax = rs.getString("fax"),
                            contato = rs.getString("contato"),
                            observacao = rs.getString("observacao"),
                            observacao2 = rs.getString("obs2");

                    if (tel2 != null && !tel2.isEmpty()) {
                        imp.addContato("1", "FONE2", tel2, null, TipoContato.NFE, null);
                    }

                    if (fax != null && !fax.isEmpty()) {
                        imp.addContato("2", "FAX", fax, null, TipoContato.NFE, null);
                    }

                    if (contato != null && !contato.isEmpty()) {
                        imp.addContato("3", contato, null, null, TipoContato.NFE, null);
                    }

                    if (observacao != null && !observacao.isEmpty()) {
                        imp.setObservacao(observacao);
                    }

                    if (observacao2 != null && !observacao2.isEmpty()) {
                        imp.addContato("4", observacao2, null, null, TipoContato.NFE, null);
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	pr.procodigo idproduto,\n"
                    + "	pf.forcgcpf idfornecedor,\n"
                    + "	pf.forcodigox referencia\n"
                    + "from \n"
                    + "	public.file035 pf \n"
                    + "join \n"
                    + "	public.file005 pr on pf.procodigo = pr.procodigo")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setIdFornecedor(rs.getString("idfornecedor"));
                    imp.setCodigoExterno(rs.getString("referencia"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	c.convcgc id,\n"
                    + "	c.convcgc cnpj,\n"
                    + "	c.convie ie,\n"
                    + "	c.convrazao razao,\n"
                    + "	c.convfantas fantasia,\n"
                    + "	c.convend endereco,\n"
                    + "	c.convbairro bairro,\n"
                    + "	c.convcidade cidade,\n"
                    + "	c.convuf uf,\n"
                    + "	c.convcep cep,\n"
                    + "	c.convfone1 telefone,\n"
                    + "	c.convemail email\n"
                    + "from \n"
                    + "	public.file006 c")) {
                while (rs.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));
                    imp.setInscricaoEstadual(rs.getString("ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTelefone(rs.getString("telefone"));

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
                    "select \n"
                    + "	substring(coalesce(clicpf, clicgc), 0, 4) ||\n"
                    + "	substring(coalesce(clicpf, clicgc), 9, 4) id,\n"
                    + "	c.clicpf cnpj,\n"
                    + " c.clicgc cpf, \n"
                    + "	c.clistatus status,\n"
                    + "	c.clirg rgie,\n"
                    + "	c.clifantasi fantasia,\n"
                    + "	c.clirazao razao,\n"
                    + "	c.clienderec endereco,\n"
                    + "	c.clibairro bairro,\n"
                    + "	c.clicidcod ibgecidade,\n"
                    + "	c.clicidade cidade,\n"
                    + "	c.cliendnum numero,\n"
                    + "	c.cliendcomp complemento,\n"
                    + "	c.clicep cep,\n"
                    + "	c.cliuf uf,\n"
                    + "	c.clifone1 telefone,\n"
                    + "	c.clifone2 telefone2,\n"
                    + "	c.clifax fax,\n"
                    + "	c.clicadata cadastro,\n"
                    + "	c.cliconvcgc idempresa,\n"
                    + "	c.clidatnasc nascimento,\n"
                    + "	c.clidiavenc vencimento,\n"
                    + "	c.clihistor1 observacao,\n"
                    + "	c.clilimite limite\n"
                    + "from \n"
                    + "	cliente c")) {
                while (rs.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));

                    if (imp.getCnpj() == null) {
                        imp.setCnpj(rs.getString("cpf"));
                    }

                    imp.setIdEmpresa(rs.getString("idempresa"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	distinct\n"
                    + "	tmvdata lancamento,\n"
                    + "	substring(tmvorcgcpf, 0, 4) ||\n"
                    + "	substring(tmvorcgcpf, 9, 4) idconveniado,\n"
                    + "	tmvorclien razao,\n"
                    + "	tmvnumseq documento,\n"
                    + "	tmvhorario hora,\n"
                    + "	substring(tmvtermina, 5, 2) ecf,\n"
                    + "	tmvlrtotal total \n"
                    + "from \n"
                    + "	public.back025 \n"
                    + "where \n"
                    + "	tmvorclien != ''")) {

                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");

                while (rs.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();

                    imp.setId(rs.getString("documento"));
                    imp.setNumeroCupom(imp.getId());
                    imp.setEcf(rs.getString("ecf"));
                    imp.setIdConveniado(rs.getString("idconveniado"));
                    imp.setDataMovimento(rs.getDate("lancamento"));
                    imp.setValor(rs.getDouble("total"));

                    String hora = timestampDate.format(rs.getDate("lancamento")) + " "
                            + rs.getString("hora");

                    imp.setDataHora(new Timestamp(format.parse(hora).getTime()));

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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	substring(coalesce(clicpf, clicgc), 0, 4) ||\n"
                    + "	substring(coalesce(clicpf, clicgc), 9, 4) id,\n"
                    + "	c.clicpf cnpj,\n"
                    + " c.clicgc cpf, \n"
                    + "	c.clistatus status,\n"
                    + "	c.clirg rgie,\n"
                    + "	c.clifantasi fantasia,\n"
                    + "	c.clirazao razao,\n"
                    + "	c.clienderec endereco,\n"
                    + "	c.clibairro bairro,\n"
                    + "	c.clicidcod ibgecidade,\n"
                    + "	c.clicidade cidade,\n"
                    + "	c.cliendnum numero,\n"
                    + "	c.cliendcomp complemento,\n"
                    + "	c.clicep cep,\n"
                    + "	c.cliuf uf,\n"
                    + "	c.clifone1 telefone,\n"
                    + "	c.clifone2 telefone2,\n"
                    + "	c.clifax fax,\n"
                    + "	c.clicadata::varchar cadastro,\n"
                    + "	c.cliconvcgc idempresa,\n"
                    + "	c.clidatnasc nascimento,\n"
                    + "	c.clidiavenc vencimento,\n"
                    + "	c.clihistor1 observacao,\n"
                    + "	c.clilimite limite\n"
                    + "from \n"
                    + "	cliente c")) {

                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj"));

                    if (imp.getCnpj() == null) {
                        imp.setCnpj(rs.getString("cpf"));
                    }

                    imp.setInscricaoestadual(Utils.formataNumero(rs.getString("rgie")));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setCep(rs.getString("cep"));
                    imp.setUf(rs.getString("uf"));
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setDataCadastro(rs.getDate("cadastro"));
                    imp.setDataNascimento(rs.getDate("nascimento"));

                    imp.setObservacao(rs.getString("observacao"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	distinct\n"
                    + "	tmvdata lancamento,\n"
                    + "	substring(tmvorcgcpf, 0, 4) ||\n"
                    + "	substring(tmvorcgcpf, 9, 4) idcliente,\n"
                    + "	tmvorclien razao,\n"
                    + "	tmvnumseq documento,\n"
                    + "	tmvhorario hora,\n"
                    + "	substring(tmvtermina, 5, 2) ecf,\n"
                    + "	tmvlrtotal total \n"
                    + "from \n"
                    + "	public.back025 \n"
                    + "where \n"
                    + "	tmvorclien != '' and \n"
                    + "	tmvdata between '2021-12-01' and '2021-12-31'")) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("documento"));
                    imp.setNumeroCupom(imp.getId());
                    imp.setEcf(rs.getString("ecf"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setDataEmissao(rs.getDate("lancamento"));
                    imp.setValor(rs.getDouble("total"));

                    result.add(imp);
                }
            }
        }

        return result;
    }
}
