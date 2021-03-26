package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Alan
 */
public class ResultMaisDAO extends InterfaceDAO implements MapaTributoProvider {

    public String complemento = "";

    private static final Logger LOG = Logger.getLogger(ResultMaisDAO.class.getName());

    @Override
    public String getSistema() {
        return "RMSistemas" + complemento;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	valor_taxa || '-' || st || '-' || valor_reducao codigo,\n"
                    + "	valor_taxa || '%' ||\n"
                    + "	case\n"
                    + "	  when st = '20' then ' RED'\n"
                    + "	  when st = '60' then ' SUBS'\n"
                    + "	  else ''\n"
                    + "	end descricao,\n"
                    + "	st cst,\n"
                    + "	valor_taxa aliquota,\n"
                    + "	valor_reducao reducao\n"
                    + "from\n"
                    + "	produto_tributo pt\n"
                    + "order by 1,2"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codigo"),
                            rs.getString("descricao"),
                            rs.getInt("cst"),
                            rs.getInt("aliquota"),
                            rs.getInt("reducao")));
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	cd_empresa id,\n"
                    + " razao_social nome\n"
                    + "from empresa\n"
                    + "	order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getInt("id") + " - " + rst.getString("nome")));
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
                    + "	cd_grupo_produto m1,\n"
                    + "	descricao merc1,\n"
                    + "	cd_grupo_produto m2,\n"
                    + "	descricao merc2,\n"
                    + "	cd_grupo_produto m3,\n"
                    + "	descricao merc3\n"
                    + "from grupo_produto\n"
                    + " order by 1,3,5"
            )) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rs.getString("m1"));
                    imp.setMerc1Descricao(rs.getString("merc1"));
                    imp.setMerc2ID(rs.getString("m2"));
                    imp.setMerc2Descricao(rs.getString("merc2"));
                    imp.setMerc3ID(rs.getString("m3"));
                    imp.setMerc3Descricao(rs.getString("merc3"));

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
                    "select\n"
                    + "	 cd_produto idproduto,\n"
                    + "	 ean codigobarras,\n"
                    + "	 1 qtdembalagem\n"
                    + "from\n"
                    + "	 produto p1\n"
                    + "where ean != 'SEM GTIN'\n"
                    + "	 union\n"
                    + "select\n"
                    + "	 cd_produto idproduto,\n"
                    + "	 ean_trib codigobarras,\n"
                    + "	 1 qtdembalagem\n"
                    + "from\n"
                    + "	 produto p2\n"
                    + "where ean_trib != 'SEM GTIN'\n"
                    + "  order by 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("idproduto"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	p.cd_produto idproduto,\n"
                    + "	codigo codigobarras,\n"
                    + "	upper(p.descricao) descricao,\n"
                    + "	u.simbolo embalagem,\n"
                    + "	case when length(codigo) <= 6  then 1 else 0 end e_balanca,\n"
                    + "	cd_grupo merc1,\n"
                    + "	cd_grupo merc2,\n"
                    + "	cd_grupo merc3,\n"
                    + "	round(perc_lucro,2) margem,\n"
                    + "	pr_compra custosemimposto,\n"
                    + "	pr_custo custocomimposto,\n"
                    + "	pr_venda precovenda,\n"
                    + "	situacao situacaocadastro,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	p.dh_ult_alteracao dataalteracao,\n"
                    + "	est_minimo estoquemin,\n"
                    + "	est_maximo estoquemax,\n"
                    + "	peso,\n"
                    + "	pt.valor_taxa aliqicms,\n"
                    + "	pt.st cst,\n"
                    + "	pt.valor_reducao reducao,\n"
                    + "	p.cod_pis pc_saida,\n"
                    + "	p.cod_pis_ent pc_entrada,\n"
                    + "	ncm,\n"
                    + "	t.cest\n"
                    + "from\n"
                    + "	produto p\n"
                    + "	left join unidade u on u.cd_unidade = p.cd_unidade\n"
                    + "	left join tributo t on p.cd_tributo = t.cd_tributo\n"
                    + "	left join produto_tributo pt on p.cd_tributo = pt.cd_produto\n"
                    + "order by p.cd_produto"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("idproduto"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setDescricaoCompleta(rs.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rs.getString("embalagem"));
                    imp.seteBalanca(rs.getBoolean("e_balanca"));
                    /*
                     if (imp.isBalanca()) {
                     imp.setEan(imp.getImportId());
                     }
                     */
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));

                    imp.setMargem(rs.getDouble("margem"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custosemimposto"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));

                    imp.setSituacaoCadastro(rs.getInt("situacaocadastro") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataAlteracao(rs.getDate("dataalteracao"));

                    //imp.setEstoque(rs.getDouble("estoqueatual"));
                    imp.setEstoqueMinimo(rs.getDouble("estoquemin"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemax"));
                    imp.setPesoBruto(rs.getDouble("peso"));

                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));

                    imp.setPiscofinsCstCredito(rs.getString("pc_entrada"));
                    imp.setPiscofinsCstDebito(rs.getString("pc_saida"));

                    imp.setIcmsCst(rs.getInt("cst"));
                    imp.setIcmsAliqSaida(rs.getInt("aliqicms"));
                    imp.setIcmsReducao(rs.getDouble("reducao"));

                    result.add(imp);
                }
                return result;
            }
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	cd_pessoa id,\n"
                    + "	case when cnpj = '' then cpf else cnpj end cnpj_cpf,\n"
                    + "	inscricao_e inscricaoestadual,\n"
                    + "	inscricao_m inscricaomunicipal,\n"
                    + "	nome razao,\n"
                    + "	n_fantasia fantasia,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	upper(c.descricao) cidade,\n"
                    + "	c.cod_ibge cidade_ibge,\n"
                    + "	c.estado,\n"
                    + "	cep,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	situacao,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	pessoa p\n"
                    + "	join cidade c on c.cd_cidade = p.cd_cidade\n"
                    + "where\n"
                    + "	fornecedor = true\n"
                    + "order by cd_pessoa"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rs.getString("id"));
                    imp.setCnpj_cpf(rs.getString("cnpj_cpf"));
                    imp.setIe_rg(rs.getString("inscricaoestadual"));
                    imp.setInsc_municipal(rs.getString("inscricaomunicipal"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setIbge_municipio(rs.getInt("cidade_ibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDatacadastro(rs.getDate("datacadastro"));
                    imp.setAtivo(rs.getBoolean("situacao"));
                    imp.setObservacao(rs.getString("observacao"));

                    imp.setTel_principal(Utils.formataNumero(rs.getString("telefone")));
                    if (rs.getString("fax") != null && !rs.getString("fax").isEmpty()) {
                        imp.addContato("1", "FAX", rs.getString("fax"), "", TipoContato.COMERCIAL, "");
                    }
                    if (rs.getString("email") != null && !rs.getString("email").isEmpty()) {
                        imp.addContato("2", "EMAIL", "", "", TipoContato.COMERCIAL, rs.getString("email"));
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
                    "select\n"
                    + "	pf.cd_pessoa fornecedor,\n"
                    + "	cd_produto idproduto,\n"
                    + "	cd_fornec_prod codexterno\n"
                    + "from\n"
                    + "	fornec_prod pf\n"
                    + "	join pessoa f on f.cd_pessoa = pf.cd_pessoa\n"
                    + "order by pf.cd_pessoa, cd_produto")) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rs.getString("fornecedor"));
                    imp.setIdProduto(rs.getString("idproduto"));
                    imp.setCodigoExterno(rs.getString("codexterno"));

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
                    "select\n"
                    + "	cd_pessoa id,\n"
                    + "	case when cnpj = '' then cpf else cnpj end cnpj_cpf,\n"
                    + "	inscricao_e inscricaoestadual,\n"
                    + "	inscricao_m inscricaomunicipal,\n"
                    + "	nome razao,\n"
                    + "	n_fantasia fantasia,\n"
                    + "	endereco,\n"
                    + "	numero,\n"
                    + "	complemento,\n"
                    + "	bairro,\n"
                    + "	upper(c.descricao) cidade,\n"
                    + " c.cod_ibge cidade_ibge,\n"
                    + "	c.estado,\n"
                    + "	cep,\n"
                    + "	dt_cadastro datacadastro,\n"
                    + "	dt_nasc datanascimento,\n"
                    + "	situacao,\n"
                    + "	telefone,\n"
                    + "	fax,\n"
                    + "	email,\n"
                    + "	nome_mae nomemae,\n"
                    + "	nome_pai nomepai,\n"
                    + " emp_trab empresa,\n"
                    + " fone_emp empresa_tel,\n"
                    + " cargo,\n"
                    + " remuneracao salario,\n"
                    + " vl_limite limite,\n"
                    + " conjuge,\n"
                    + "	observacao\n"
                    + "from\n"
                    + "	pessoa p\n"
                    + "	join cidade c on c.cd_cidade = p.cd_cidade\n"
                    + "where\n"
                    + "	cliente = true\n"
                    + "order by cd_pessoa")) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rs.getString("inscricaoestadual"));
                    imp.setInscricaoMunicipal(rs.getString("inscricaomunicipal"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setMunicipioIBGE(rs.getInt("cidade_ibge"));
                    imp.setUf(rs.getString("estado"));
                    imp.setCep(rs.getString("cep"));

                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setDataNascimento(rs.getDate("datanascimento"));

                    imp.setAtivo(rs.getInt("situacao") == 1);
                    imp.setTelefone(rs.getString("telefone"));
                    imp.setFax(rs.getString("fax"));
                    imp.setEmail(rs.getString("email"));

                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("empresa_tel"));
                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setObservacao(rs.getString("observacao"));

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
                    + "	codlancto id,\n"
                    + "	codestabelec id_loja,\n"
                    + "   numnotafis documento,\n"
                    + "   (SELECT "
                    + "cupom.numeroecf\n"
                    + "    FROM "
                    + "cupomlancto, cupom\n"
                    + "    WHERE "
                    + "cupomlancto.codlancto = l.codlancto AND\n"
                    + "cupomlancto.idcupom = cupom.idcupom\n"
                    + "   LIMIT 1) AS ecf,\n"
                    + "	dtemissao emissao,\n"
                    + "	dtvencto vencimento,\n"
                    + "	parcela,\n"
                    + "	codparceiro idcliente,\n"
                    + "	valorliquido,\n"
                    + "	valordescto desconto,\n"
                    + "	valorjuros\n"
                    + "from \n"
                    + "	lancamento l \n"
                    + "where \n"
                    + "	pagrec = 'R' and\n"
                    + "	status = 'A' and\n"
                    + "	codestabelec = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "	dtlancto"
            )) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rs.getString("id"));
                    imp.setIdCliente(rs.getString("idcliente"));
                    imp.setNumeroCupom(rs.getString("documento"));
                    imp.setEcf(rs.getString("ecf"));
                    imp.setDataEmissao(rs.getDate("emissao"));
                    imp.setDataVencimento(rs.getDate("vencimento"));
                    imp.setParcela(rs.getInt("parcela"));
                    imp.setValor(rs.getDouble("valorliquido"));
                    imp.setJuros(rs.getDouble("valorjuros"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
     @Override
     public List<ContaPagarIMP> getContasPagar() throws Exception {
     List<ContaPagarIMP> result = new ArrayList<>();
     try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
     try (ResultSet rs = stm.executeQuery(
     "select \n"
     + "	codlancto id,\n"
     + "	codestabelec id_loja,\n"
     + "	numnotafis documento,\n"
     + "	dtemissao emissao,\n"
     + "	dtvencto vencimento,\n"
     + "	parcela,\n"
     + "	codparceiro idfornecedor,\n"
     + "	valorliquido,\n"
     + "	valordescto desconto,\n"
     + "	valorjuros\n"
     + "from \n"
     + "	lancamento l \n"
     + "where \n"
     + "	pagrec = 'P' and\n"
     + "	status = 'A' and\n"
     + "	codestabelec = " + getLojaOrigem() + " and\n"
     + "	codespecie = " + tipoPlanoContaPagar + " and\n"
     + "   tipoparceiro = 'F'\n"
     + "order by\n"
     + "	dtlancto")) {
     while (rs.next()) {
     ContaPagarIMP imp = new ContaPagarIMP();
     imp.setId(rs.getString("id"));
     imp.setNumeroDocumento(rs.getString("documento"));
     imp.setDataEmissao(rs.getDate("emissao"));
     imp.addVencimento(rs.getDate("vencimento"), rs.getDouble("valorliquido"));
     imp.setIdFornecedor(rs.getString("idfornecedor"));

     result.add(imp);
     }
     }
     }
     return result;
     }

     private Date dataInicioVenda;
     private Date dataFimVenda;

     public void setDataInicioVenda(Date dataInicioVenda) {
     this.dataInicioVenda = dataInicioVenda;
     }

     public void setDataFimVenda(Date dataFimVenda) {
     this.dataFimVenda = dataFimVenda;
     }

     @Override
     public Iterator<VendaIMP> getVendaIterator() throws Exception {
     return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataFimVenda);
     }

     @Override
     public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
     return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataFimVenda);
     }

     private static class VendaIterator implements Iterator<VendaIMP> {

     public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

     private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
     String id = rst.getString("idcupom");
     if (!uk.add(id)) {
     LOG.warning("Venda " + id + " já existe na listagem");
     }
     next.setId(id);
     next.setNumeroCupom(Utils.stringToInt(rst.getString("cupom")));
     next.setEcf(Utils.stringToInt(rst.getString("ecf")));
     next.setData(rst.getDate("data"));
     next.setIdClientePreferencial(rst.getString("codcliente"));
     String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
     String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
     next.setHoraInicio(timestamp.parse(horaInicio));
     next.setHoraTermino(timestamp.parse(horaTermino));
     next.setCancelado("C".equals(rst.getString("status")));
     next.setSubTotalImpressora(rst.getDouble("totalliquido"));
     //next.setValorAcrescimo(rst.getDouble("totalacrescimo"));
     next.setCpf(rst.getString("cpfcnpj"));
     next.setNomeCliente(rst.getString("nome"));
     String endereco
     = Utils.acertarTexto(rst.getString("endereco")) + ","
     + Utils.acertarTexto(rst.getString("numero")) + ","
     + Utils.acertarTexto(rst.getString("complemento")) + ","
     + Utils.acertarTexto(rst.getString("bairro")) + ","
     + Utils.acertarTexto(rst.getString("cidade")) + "-"
     + Utils.acertarTexto(rst.getString("estado")) + ","
     + Utils.acertarTexto(rst.getString("cep"));
     next.setEnderecoCliente(endereco);
     }
     }
     } catch (SQLException | ParseException ex) {
     LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
     throw new RuntimeException(ex);
     }
     }

     public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
     this.sql
     = "select\n"
     + "	idcupom,\n"
     + "	dtmovto as data,\n"
     + "	hrmovto hora,\n"
     + "	caixa ecf,\n"
     + "	totalliquido,\n"
     + "	totaldesconto,\n"
     + "	cupom,\n"
     + "	seqecf,\n"
     + "	c.codcliente,\n"
     + "	c.nome,\n"
     + "	cupom.cpfcnpj,\n"
     + "   totalacrescimo,\n"
     + "   status,\n"
     + "	c.enderres endereco,\n"
     + "	c.numerores numero,\n"
     + "	c.complementores complemento,\n"
     + "	c.bairrores bairro,\n"
     + "	cid.nome cidade,\n"
     + "   uf.uf estado,\n"
     + "	c.cepres cep,\n"
     + "	status,\n"
     + "	chavecfe\n"
     + "from\n"
     + "	cupom\n"
     + "left join cliente c on cupom.codcliente = c.codcliente\n"
     + "left join cidade cid on cid.codcidade = c.codcidadeent\n"
     + "left join estado uf on uf.uf = c.ufres\n"
     + "where\n"
     + "	dtmovto between '" + dataInicio + "' and '" + dataTermino + "' and\n"
     + "	codestabelec = " + idLojaCliente + "\n"
     + "order by\n"
     + "	dtmovto";
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

     private Statement stm = ConexaoPostgres.getConexao().createStatement();
     private ResultSet rst;
     private String sql;
     private VendaItemIMP next;

     private void obterNext() {
     try {
     if (next == null) {
     if (rst.next()) {
     next = new VendaItemIMP();
     next.setId(rst.getString("id"));
     next.setVenda(rst.getString("idcupom"));
     next.setProduto(rst.getString("codproduto"));
     next.setDescricaoReduzida(rst.getString("descricao"));
     next.setQuantidade(rst.getDouble("quantidade"));
     next.setPrecoVenda(rst.getDouble("preco"));
     next.setTotalBruto(rst.getDouble("valortotal"));
     //next.setValorAcrescimo(rst.getDouble("acrescimo"));
     //next.setValorDesconto(rst.getDouble("desconto"));
     next.setCancelado("C".equals(rst.getString("status")));
     if ("S".equals(rst.getString("pesado"))) {
     next.setCodigoBarras(rst.getString("codproduto"));
     } else {
     String eanStr = rst.getString("ean");
     if (eanStr != null && eanStr.length() > 14) {
     next.setCodigoBarras(eanStr.substring(1, 14));
     } else {
     next.setCodigoBarras(eanStr);
     }
     }

     next.setUnidadeMedida(rst.getString("embalagem"));

     String trib = rst.getString("tptribicms");
     if (trib == null || "".equals(trib)) {
     trib = "I";
     }

     obterAliquota(next, trib, rst.getDouble("aliqicms"));
     }
     }
     } catch (Exception ex) {
     LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
     throw new RuntimeException(ex);
     }
     }

        
     public void obterAliquota(VendaItemIMP item, String icms, double aliquota) throws SQLException {
            
     //TA	7.00	ALIQUOTA 07%
     //TB	12.00	ALIQUOTA 12%
     //TC	18.00	ALIQUOTA 18%
     //TD	25.00	ALIQUOTA 25%
     //TE	11.00	ALIQUOTA 11%
     //I	0.00	ISENTO
     //F	0.00	SUBST TRIBUTARIA
     //N	0.00	NAO INCIDENTE
       
     int cst;
     double aliq;
     switch (icms) {
     case "I":
     cst = 40;
     aliq = 0;
     break;
     case "F":
     cst = 0;
     aliq = 60;
     break;
     case "T":
     cst = 0;
     aliq = aliquota;
     break;
     case "N":
     cst = 41;
     aliq = 0;
     break;
     default:
     cst = 40;
     aliq = 0;
     break;
     }
     item.setIcmsCst(cst);
     item.setIcmsAliq(aliq);
     }

     public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
     this.sql
     = "select\n"
     + "	(c.caixa || '' || \n"
     + "	i.idcupom || '' || \n"
     + "	i.codproduto || '' || \n"
     + "	i.codprodutopai || '' || \n"
     + "	i.status) as id,\n"
     + "	i.codmovimento,\n"
     + "	i.codproduto,\n"
     + "	(select\n"
     + "		codean\n"
     + "	from\n"
     + "		produtoean ean\n"
     + "	where\n"
     + "		ean.codproduto = p.codproduto\n"
     + "	limit 1) ean,\n"
     + "	p.pesado,\n"
     + "	p.descricao,\n"
     + "	un.sigla embalagem,\n"
     + "	i.idcupom,\n"
     + "	i.quantidade,\n"
     + "	i.preco,\n"
     + "	i.desconto,\n"
     + "   i.acrescimo,\n"
     + "	i.valortotal,\n"
     + "	i.aliqicms,\n"
     + "	i.tptribicms,\n"
     + "	i.status\n"
     + "from\n"
     + "	itcupom i\n"
     + "join cupom c on i.idcupom = c.idcupom\n"
     + "join produto p on p.codproduto = i.codproduto\n"
     + "join embalagem emb on p.codembalvda = emb.codembal\n"
     + "join unidade un on emb.codunidade = un.codunidade\n"
     + "where\n"
     + "	c.dtmovto between '" + dataInicio + "' and '" + dataTermino + "' and\n"
     + "   c.codestabelec = " + idLojaCliente + " and\n"
     + "   i.composicao in ('P', 'N')\n"
     + "order by\n"
     + "	c.dtmovto, idcupom";
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
     */
}
