package vrimplantacao2_5.dao.sistema;

import vrimplantacao2.dao.interfaces.*;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoAutomacaoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Wagner
 */
public class SISMPlanilhaPgDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "SISMPLANILHAPG";
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
            OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.DATA_ALTERACAO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.VOLUME_TIPO_EMBALAGEM,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.VOLUME_QTD,
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
            OpcaoProduto.MARGEM,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.PDV_VENDA,
            OpcaoProduto.QTD_EMBALAGEM_EAN
        }));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.NUMERO,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.VALOR_LIMITE,
                OpcaoCliente.INSCRICAO_ESTADUAL
        ));
    }

    public ArrayList<Estabelecimento> getLojasCliente() throws Exception {
        ArrayList<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select codigo, descricao from empresa;"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }

        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + " tribicms id,\n"
                    + " tribicms descricao\n"
                    + "from trib_ativos\n"
                    + "union \n"
                    + "select distinct\n"
                    + " tribicms id,\n"
                    + " tribicms descricao\n"
                    + "from trib_inativos;"
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "with mercadologico as (\n"
                    + "select distinct\n"
                    + " regexp_replace(depto,'[A-Z]|-|\\/','','g') mercid1,\n"
                    + " regexp_replace(depto,'[0-9]|-','','g') desc1,\n"
                    + " regexp_replace(secao,'[A-Z]|-|\\/','','g') mercid2,\n"
                    + " regexp_replace(secao,'[0-9]|-','','g') desc2,\n"
                    + " regexp_replace(grupo,'[A-Z]|-|\\/','','g') mercid3,\n"
                    + " regexp_replace(grupo,'[0-9]|-','','g') desc3,\n"
                    + " regexp_replace(subgrupo,'[A-Z]|-|\\/','','g') mercid4,\n"
                    + " regexp_replace(subgrupo,'[0-9]|-','','g') desc4\n"
                    + "from produtos\n"
                    + "order by 1,3,5,7\n"
                    + ")\n"
                    + "select \n"
                    + " mercid1,\n"
                    + " desc1,\n"
                    + " case when mercid2 = '' then mercid1 else mercid2 end,\n"
                    + " case when desc2 = '' then desc1 else desc2 end,\n"
                    + " case when mercid3 = '' and mercid2 = '' then mercid1\n"
                    + "      when mercid3 = '' then mercid2\n"
                    + "      else mercid3 end,\n"
                    + " case when desc3 = '' and desc2 = '' then desc1\n"
                    + "      when desc3 = '' then desc2\n"
                    + "      else desc3 end,\n"
                    + " mercid4,\n"
                    + " desc4\n"
                    + "from mercadologico"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
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

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + " p.codigo,\n"
                    + " p.descricao,\n"
                    + " p.codbarra,\n"
                    + " p.un,\n"
                    + " regexp_replace(p.ebv,'[^0-9]','','g') ebv,\n"
                    + " 1 as ativo,\n"
                    + " replace(p.pcusto,',','.') custo,\n"
                    + " replace(p.pvenda,',','.') venda,\n"
                    + " case when replace(replace(regexp_replace(p.estoque,'[A-z]','','g'),',','.'),' ','') = '' then '0'\n"
                    + "      else replace(replace(regexp_replace(p.estoque,'[A-z]','','g'),',','.'),' ','')\n"
                    + "      end estoque,\n"
                    + " case when p.margem = '' then '0' else p.margem end margem,\n"
                    + " regexp_replace(p.depto,'[A-Z]|-|\\/','','g') mercid1,\n"
                    + " case when  regexp_replace(p.secao,'[A-Z]|-|\\/','','g') = '' \n"
                    + " 		then regexp_replace(p.depto,'[A-Z]|-|\\/','','g') \n"
                    + " 		else regexp_replace(p.secao,'[A-Z]|-|\\/','','g') end mercid2,\n"
                    + " case when regexp_replace(p.grupo,'[A-Z]|-|\\/','','g')  = '' and  regexp_replace(p.secao,'[A-Z]|-|\\/','','g') = '' \n"
                    + " 		then regexp_replace(p.depto,'[A-Z]|-|\\/','','g')\n"
                    + " 	  when regexp_replace(p.grupo,'[A-Z]|-|\\/','','g')  = ''\n"
                    + " 	    then regexp_replace(p.secao,'[A-Z]|-|\\/','','g')\n"
                    + " 	    else regexp_replace(p.grupo,'[A-Z]|-|\\/','','g') end mercid3,\n"
                    + " regexp_replace(p.subgrupo,'[A-Z]|-|\\/','','g') mercid4,\n"
                    + " t.tribicms,\n"
                    + " case when t.piscofins = 'A' then 6\n"
                    + "      when t.piscofins = 'M' then 4\n"
                    + "      when t.piscofins = 'N' then 8\n"
                    + "      when t.piscofins = 'T' then 1\n"
                    + " else 5 end piscofins,\n"
                    + " t.ncm,\n"
                    + " t.cest\n"
                    + "from produtos p\n"
                    + "left join trib_ativos t on t.codigo = p.codigo\n"
                    + "union \n"
                    + "select distinct\n"
                    + " p.codigo,\n"
                    + " p.descricao,\n"
                    + " p.codbarra,\n"
                    + " p.un,\n"
                    + " regexp_replace(p.ebv,'[^0-9]','','g') ebv,\n"
                    + " 0 as ativo,\n"
                    + " replace(p.pcusto,',','.') custo,\n"
                    + " replace(p.pvenda,',','.') venda,\n"
                    + " case when replace(replace(regexp_replace(p.estoque,'[A-z]','','g'),',','.'),' ','') = '' then '0'\n"
                    + "      else replace(replace(regexp_replace(p.estoque,'[A-z]','','g'),',','.'),' ','')\n"
                    + "      end estoque,\n"
                    + " case when p.margem = '' then '0' else p.margem end margem,\n"
                    + " regexp_replace(p.depto,'[A-Z]|-|\\/','','g') mercid1,\n"
                    + " case when  regexp_replace(p.secao,'[A-Z]|-|\\/','','g') = '' \n"
                    + " 		then regexp_replace(p.depto,'[A-Z]|-|\\/','','g') \n"
                    + " 		else regexp_replace(p.secao,'[A-Z]|-|\\/','','g') end mercid2,\n"
                    + " case when regexp_replace(p.grupo,'[A-Z]|-|\\/','','g')  = '' and  regexp_replace(p.secao,'[A-Z]|-|\\/','','g') = '' \n"
                    + " 		then regexp_replace(p.depto,'[A-Z]|-|\\/','','g')\n"
                    + " 	  when regexp_replace(p.grupo,'[A-Z]|-|\\/','','g')  = ''\n"
                    + " 	    then regexp_replace(p.secao,'[A-Z]|-|\\/','','g')\n"
                    + " 	    else regexp_replace(p.grupo,'[A-Z]|-|\\/','','g') end mercid3,\n"
                    + " regexp_replace(p.subgrupo,'[A-Z]|-|\\/','','g') mercid4,\n"
                    + " t.tribicms,\n"
                    + " case when t.piscofins = 'A' then 6\n"
                    + "      when t.piscofins = 'M' then 4\n"
                    + "      when t.piscofins = 'N' then 8\n"
                    + "      when t.piscofins = 'T' then 1\n"
                    + " else 5 end piscofins,\n"
                    + " t.ncm,\n"
                    + " t.cest\n"
                    + "from produtos_inativos p\n"
                    + "left join trib_inativos t on t.codigo = p.codigo\n"
                    + "order by 1"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));

                    int codigoProduto = Utils.stringToInt(rst.getString("codbarra"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("codbarra"));
                        imp.setTipoEmbalagem(rst.getString("un"));
                        imp.setTipoEmbalagemVolume(rst.getString("un"));
                        imp.setTipoEmbalagemCotacao(rst.getString("un"));
                        imp.setQtdEmbalagem(rst.getInt("ebv"));
                        imp.setVolume(1);
                        imp.setQtdEmbalagemCotacao(1);
                    }

                    imp.setSituacaoCadastro(rst.getInt("ativo"));

                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());

                    imp.setCodMercadologico1(rst.getString("mercid1"));
                    imp.setCodMercadologico2(rst.getString("mercid2"));
                    imp.setCodMercadologico3(rst.getString("mercid3"));

                    imp.setMargem(!"".equals(rst.getDouble("margem")) ? rst.getDouble("margem") : 0);
                    imp.setPrecovenda(rst.getDouble("venda"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoque(rst.getDouble("estoque"));

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins"));
                    imp.setPiscofinsCstCredito(imp.getPiscofinsCstDebito());

                    imp.setIcmsDebitoId(rst.getString("tribicms"));
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
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + " p.codigo,\n"
                    + " p.codbarra,\n"
                    + " p.un,\n"
                    + " regexp_replace(p.ebv,'[^0-9]','','g') ebv\n"
                    + "from produtos p\n"
                    + "left join trib_ativos t on t.codigo = p.codigo\n"
                    + "union \n"
                    + "select distinct\n"
                    + " p.codigo,\n"
                    + " p.codbarra,\n"
                    + " p.un,\n"
                    + " regexp_replace(p.ebv,'[^0-9]','','g') ebv\n"
                    + "from produtos_inativos p\n"
                    + "left join trib_inativos t on t.codigo = p.codigo\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setEan(rst.getString("codbarra"));
                    imp.setTipoEmbalagem(rst.getString("un"));
                    imp.setQtdEmbalagem(rst.getInt("ebv"));
                    vResult.add(imp);
                }
            }
        }
        return vResult;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " codigo,\n"
                    + " nome,\n"
                    + " cnpjcpf,\n"
                    + " ie,\n"
                    + " ende,\n"
                    + " compl,\n"
                    + " bairro,\n"
                    + " cidade,\n"
                    + " uf,\n"
                    + " cep,\n"
                    + " tel1,\n"
                    + " tel2 \n"
                    + "from fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(imp.getRazao());
                    imp.setCnpj_cpf(rst.getString("cnpjcpf"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setEndereco(rst.getString("ende"));
                    imp.setComplemento(rst.getString("compl"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTel_principal(rst.getString("tel1"));

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
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  p.codigo produtoid,\n"
                    + "  f.codigo fornecedorid,\n"
                    + "  pf.refe referencia,\n"
                    + "  replace(replace(replace(ebc,',','.'),'''',''),'/','') qtde\n"
                    + "  from produtofornecedor pf\n"
                    + " join produto p on pf.codb = p.codbarra\n"
                    + " join fornecedor2 f on regexp_replace(f.cnpj_cpf,'[.|/|-]','','g') = regexp_replace(pf.cnpj,'[.|/|-]','','g')"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("produtoid"));
                    imp.setIdFornecedor(rst.getString("fornecedorid"));
                    imp.setCodigoExterno(rst.getString("referencia"));
                    imp.setQtdEmbalagem(rst.getDouble("qtde"));
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
                    "select \n"
                    + " codigo,\n"
                    + " nome,\n"
                    + " cnpjcpf,\n"
                    + " case when rg = '' then ie else rg end rg,\n"
                    + " tel1,\n"
                    + " tel2,\n"
                    + " case when situacao = '5-Bloqueado' then 0\n"
                    + "   else 1 end situacao,\n"
                    + "  case when replace(limite,',','.') = '' then '0' else replace(limite,',','.') end limite,\n"
                    + " ende,\n"
                    + " bairro,\n"
                    + " cid,\n"
                    + " uf,\n"
                    + " regexp_replace(ende,'[^0-9]','','g') as numero,\n"
                    + " cep\n"
                    + "from clientes \n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setCnpj(rst.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rst.getString("rg"));
                    imp.setEndereco(rst.getString("ende"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("cep"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cid"));
                    imp.setUf(rst.getString("uf"));
                    imp.setTelefone(rst.getString("tel1"));

                    imp.setBloqueado(rst.getBoolean("situacao"));
                    imp.setAtivo(rst.getBoolean("situacao"));
                    //imp.setLimiteCompra(0);
                    imp.setValorLimite(rst.getDouble("limite"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    private List<ProdutoAutomacaoVO> getDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "  v.id,\n"
                    + "  v.codigobarras,\n"
                    + "  p.id_tipoembalagem \n"
                    + "from implantacao.produto_verificador v\n"
                    + "join produto p on p.id = v.id"
            )) {
                while (rst.next()) {
                    ProdutoAutomacaoVO vo = new ProdutoAutomacaoVO();
                    vo.setIdproduto(rst.getInt("id"));
                    vo.setIdTipoEmbalagem(rst.getInt("id_tipoembalagem"));
                    vo.setCodigoBarras(gerarEan13(Long.parseLong(rst.getString("id")), true));
                    result.add(vo);
                }
            }
        }

        return result;
    }

    public void importarDigitoVerificador() throws Exception {
        List<ProdutoAutomacaoVO> result = new ArrayList<>();
        ProgressBar.setStatus("Carregar Produtos...");
        try {
            result = getDigitoVerificador();

            if (!result.isEmpty()) {
                gravarCodigoBarrasDigitoVerificador(result);
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    private void gravarCodigoBarrasDigitoVerificador(List<ProdutoAutomacaoVO> vo) throws Exception {

        Conexao.begin();
        Statement stm, stm2 = null;
        ResultSet rst = null;

        stm = Conexao.createStatement();
        stm2 = Conexao.createStatement();

        String sql = "";
        ProgressBar.setStatus("Gravando Código de Barras...");
        ProgressBar.setMaximum(vo.size());

        try {

            for (ProdutoAutomacaoVO i_vo : vo) {

                sql = "select codigobarras from produtoautomacao where codigobarras = " + i_vo.getCodigoBarras();
                rst = stm.executeQuery(sql);

                if (!rst.next()) {
                    sql = "insert into produtoautomacao ("
                            + "id_produto, "
                            + "codigobarras, "
                            + "id_tipoembalagem, "
                            + "qtdembalagem) "
                            + "values ("
                            + i_vo.getIdproduto() + ", "
                            + i_vo.getCodigoBarras() + ", "
                            + i_vo.getIdTipoEmbalagem() + ", 1);";
                    stm2.execute(sql);
                } else {
                    sql = "insert into implantacao.produtonaogerado ("
                            + "id_produto, "
                            + "codigobarras) "
                            + "values ("
                            + i_vo.getIdproduto() + ", "
                            + i_vo.getCodigoBarras() + ");";
                    stm2.execute(sql);
                }
                ProgressBar.next();
            }

            stm.close();
            stm2.close();
            Conexao.commit();
        } catch (Exception ex) {
            Conexao.rollback();
            throw ex;
        }
    }

    public long gerarEan13(long i_codigo, boolean i_digito) throws Exception {
        String codigo = String.format("%012d", i_codigo);

        int somaPar = 0;
        int somaImpar = 0;

        for (int i = 0; i < 12; i += 2) {
            somaImpar += Integer.parseInt(String.valueOf(codigo.charAt(i)));
            somaPar += Integer.parseInt(String.valueOf(codigo.charAt(i + 1)));
        }

        int soma = somaImpar + (3 * somaPar);
        int digito = 0;
        boolean verifica = false;
        int calculo = 0;

        do {
            calculo = soma % 10;

            if (calculo != 0) {
                digito += 1;
                soma += 1;
            }
        } while (calculo != 0);

        if (i_digito) {
            return Long.parseLong(codigo + digito);
        } else {
            return Long.parseLong(codigo);
        }
    }

}
