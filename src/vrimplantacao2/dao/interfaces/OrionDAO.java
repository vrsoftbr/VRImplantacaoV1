/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoDB2;
import vrimplantacao.classe.ConexaoDBF;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author lucasrafael
 */
public class OrionDAO extends InterfaceDAO {

    private static final Logger LOG = Logger.getLogger(OrionDAO.class.getName());
    public String i_arquivo;

    @Override
    public String getSistema() {
        return "Orion";
    }
    
    public void gravar() throws Exception {
        
        String sql = "select "
                + "e.plu, "
                + "e.nome, "
                + "((e.custobase - e.descontos) + e.icmssubstr + e.encargos + e.frete + e.outrasdesp) as custocomimposto, \n"
                + "e.custobase as custosemimposto, e.vendavare "
                + "from estoque e ";
        
        Statement st = null;
        StringBuilder sq = null;
        
        Conexao.begin();
        
        st = Conexao.createStatement();
        
        
        int cont = 0;
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(sql)) {
                while (rst.next()) {
                    
                    sq = new StringBuilder();
                    sq.append("insert into implantacao.preco_custo_orion_loja3 (loja, codigo, nome, custocomimposto, custosemimposto, precovenda) ");
                    sq.append("values (");
                    sq.append("'"+getLojaOrigem()+"', ");
                    sq.append("'"+rst.getString("plu")+"', ");
                    sq.append("'"+Utils.acertarTexto(rst.getString("nome"))+ "', ");
                    sq.append(rst.getDouble("custocomimposto")+ ", ");
                    sq.append(rst.getDouble("custosemimposto") + ", ");
                    sq.append(rst.getDouble("vendavare") + ");");
                    
                    st.execute(sq.toString());
                    
                    cont++;
                    
                    ProgressBar.setStatus("Gravando..." + cont);
                }
            }
            
            st.close();
            Conexao.commit();
        }
        
        
    }
    
    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
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
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.MARGEM,
                    OpcaoProduto.OFERTA
                }
        ));
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        Map<String, Estabelecimento> result = new LinkedHashMap<>();
        
        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select firma as nome, cgc as id from config"
            )) {
                while (rst.next()) {
                    result.put(rst.getString("id"), new Estabelecimento(rst.getString("id"), rst.getString("nome")));
                }
            }
        }

        return new ArrayList<>(result.values());
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct "
                    + "codsub as id, "
                    + "titulograd as descricao "
                    + "from ESTOQUE "
                    + "where codsub is not null "
                    + "or trim(codsub) <> ''"
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

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "distinct "
                    + "m1.codsetor cod_m1, "
                    + "m1.setor desc_m1, "
                    + "m2.codgrupo cod_m2, "
                    + "m2.grupo desc_m2, "
                    + "m3.codigo cod_m3, "
                    + "m3.subgrupo desc_m3 "
                    + "from setor m1 "
                    + "left join grupo m2 on m2.codsetor = m1.codsetor "
                    + "left join subgrupo m3 on m3.codgrupo = m2.codgrupo "
                    + "order by m1.codsetor, m2.codgrupo, m3.codigo"
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "e.plu id_produto, "
                    + "l.codigo ean, "
                    + "e.codsetor mercadologico1, "
                    + "e.codgru mercadologico2,"
                    + "e.codsubgru mercadologico3, "
                    + "e.nome descricaocompleta, "
                    + "e.descricao descricaoreduzida, "
                    + "e.gondola descricaogondola, "
                    + "e.custo, "
                    + "e.classfis ncm, "
                    + "e.cest, "
                    + "e.sittribut, "
                    + "e.icms, "
                    + "e.reducao, "
                    + "e.unidade, "
                    + "e.inclusao, "
                    + "e.piscst, "
                    + "e.cofinscst, "
                    + "e.vendavare, "
                    + "e.lucrovare margem, "
                    + "l.qtde, "
                    + "e.quantfisc, "
                    + "e.custobase, "
                    + "e.gradeum, "
                    + "e.gradedois, "
                    + "e.codsub, "
                    + "e.custobase custosemimposto, "
                    + "e.custobase, "        
                    + "((e.custobase - e.descontos) + e.icmssubstr + e.encargos + e.frete + e.outrasdesp) as custocomimposto "
                    + "from estoque e "
                    + "left join ligplu l on e.plu = l.plu  "
                    + "where e.plu is not null"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                int cont = 0;
                while (rst.next()) {
                    
                    
                    System.out.println(getLojaOrigem() + " - " + getSistema() + " - " + rst.getString("id_produto"));
                    
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setIdFamiliaProduto(rst.getString("codsub"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setQtdEmbalagem(rst.getInt("qtde"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("vendavare"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setEstoque(rst.getDouble("quantfisc"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscst"));
                    imp.setPiscofinsCstCredito(rst.getString("cofinscst"));
                    
                    imp.setIcmsCstSaida(rst.getInt("sittribut"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaida(rst.getDouble("reducao"));
                    
                    imp.setIcmsCstConsumidor(imp.getIcmsCstSaida());
                    imp.setIcmsAliqConsumidor(imp.getIcmsAliqSaida());
                    imp.setIcmsReducaoConsumidor(imp.getIcmsReducaoSaida());

                    imp.setIcmsCstSaidaForaEstado(rst.getInt("sittribut"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("reducao"));

                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("sittribut"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("reducao"));

                    imp.setIcmsCstEntrada(rst.getInt("sittribut"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("reducao"));
                    
                    imp.setIcmsCstEntradaForaEstado(rst.getInt("sittribut"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("reducao"));
                    
                    imp.setDataCadastro(rst.getDate("inclusao"));

                    long codigoProduto;
                    if ((rst.getString("ean") != null)
                            && (!rst.getString("ean").trim().isEmpty())) {

                        if (Long.parseLong(Utils.formataNumero(rst.getString("ean").trim())) <= 999999) {

                            codigoProduto = Long.parseLong(rst.getString("ean").trim());
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                            }

                            imp.setEan(rst.getString("ean"));

                        } else {
                            imp.seteBalanca(false);
                            imp.setEan(rst.getString("ean"));
                        }
                    } else {
                        imp.seteBalanca(false);
                        imp.setEan(rst.getString("ean"));
                    }

                    result.add(imp);
                    
                    cont++;
                    ProgressBar.setStatus("Carregando produtos..." + cont);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "codigo, "
                    + "nome, "
                    + "razao, "
                    + "inscest, "
                    + "cgc, "
                    + "rua, "
                    + "casa, "
                    + "edificio, "
                    + "sala, "
                    + "cidade, "
                    + "bairro, "
                    + "cep, "
                    + "estado, "
                    + "inclusao, "
                    + "email, "
                    + "contato, "
                    + "contatcom, "
                    + "telefone1, "
                    + "telefone2, "
                    + "telefone3, "
                    + "obs "
                    + "FROM FORNECE "
                    + "order by codigo "
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    
                    if ((rst.getString("codigo") != null)
                            && (!rst.getString("codigo").trim().isEmpty())) {
                        imp.setImportId(rst.getString("codigo"));
                    } else {
                        imp.setImportId(rst.getString("cgc"));
                    }
                    
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setCnpj_cpf(rst.getString("cgc"));
                    imp.setIe_rg(rst.getString("inscest"));
                    imp.setEndereco(rst.getString("rua"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setComplemento(rst.getString("casa") + " " + rst.getString("edificio") + " " + rst.getString("sala"));
                    imp.setDatacadastro(rst.getDate("inclusao"));
                    imp.setObservacao(rst.getString("obs"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("contato"));
                    }
                    if ((rst.getString("contatcom") != null)
                            && (!rst.getString("contatcom").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATOCOM " + rst.getString("contatcom"));
                    }

                    imp.setTel_principal(rst.getString("telefone1"));

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE2",
                                rst.getString("telefone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "TELEFONE 3",
                                rst.getString("telefone3"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("email").toLowerCase()
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

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT "
                    + "CODFOR, "
                    + "CODINT, "
                    + "PLU, "
                    + "QTDE "
                    + "FROM LIGFAB"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdFornecedor(rst.getString("CODFOR"));
                    imp.setIdProduto(rst.getString("PLU"));
                    imp.setCodigoExterno(rst.getString("CODINT"));
                    imp.setQtdEmbalagem(rst.getInt("QTDE"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "codigo, "
                    + "nome, "
                    + "razao, "
                    + "nascimento, "
                    + "inscest, "
                    + "cgc, "
                    + "cic, "
                    + "firma, "
                    + "cargo, "
                    + "salario, "
                    + "saldo, "
                    + "pai, "
                    + "mae, "
                    + "rua, "
                    + "casa, "
                    + "edificio, "
                    + "apto, "
                    + "cidade, "
                    + "bairro, "
                    + "cep, "
                    + "estado, "
                    + "email, "
                    + "abertura, "
                    + "contato, "
                    + "telefone1, "
                    + "telefone2, "
                    + "telefone3, "
                    + "contatcom, "
                    + "rg "
                    + "from cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("nome"));
                    imp.setDataNascimento(rst.getDate("nascimento"));
                    imp.setEmpresa(rst.getString("firma"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("saldo"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setEndereco(rst.getString("rua"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setTelefone(rst.getString("telefone1"));
                    imp.setEmail(rst.getString("email"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao("CONTATO " + rst.getString("contato"));
                    }
                    if ((rst.getString("contatcom") != null)
                            && (!rst.getString("contatcom").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATOCOM " + rst.getString("contatcom"));
                    }

                    if ((rst.getString("cic") != null)
                            && (!rst.getString("cic").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cic"));
                    } else if ((rst.getString("cgc") != null)
                            && (!rst.getString("cgc").trim().isEmpty())) {
                        imp.setCnpj(rst.getString("cgc"));
                    } else {
                        imp.setCnpj("");
                    }

                    if ((rst.getString("rg") != null)
                            && (!rst.getString("rg").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("rg"));
                    } else if ((rst.getString("inscest") != null)
                            && (!rst.getString("inscest").trim().isEmpty())) {
                        imp.setInscricaoestadual(rst.getString("inscest"));
                    } else {
                        imp.setInscricaoestadual("");
                    }

                    if ((rst.getString("telefone2") != null)
                            && (!rst.getString("telefone2").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "TELEFONE 2",
                                rst.getString("telefone2"),
                                null,
                                null
                        );
                    }
                    if ((rst.getString("telefone3") != null)
                            && (!rst.getString("telefone3").trim().isEmpty())) {
                        imp.addContato(
                                "3",
                                "TELEFONE 3",
                                rst.getString("telefone3"),
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

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "codigo, "
                    + "vencimento, "
                    + "dlanca, "
                    + "valorreceb, "
                    + "codigocli, "
                    + "terminal "
                    + "from receber "
                    + "where transacao is null\n"
                    + "and codigocli is not null"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setIdCliente(rst.getString("codigocli"));
                    imp.setNumeroCupom(rst.getString("codigo"));
                    imp.setEcf(rst.getString("terminal"));
                    imp.setDataEmissao(rst.getDate("dlanca"));
                    imp.setDataVencimento(rst.getDate("vencimento"));
                    imp.setValor(rst.getDouble("valorreceb"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoDBF.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	e.plu as idproduto, \n"
                    + "	e.proinivare as datainicio, \n"
                    + "	e.profimvare as datatermino, \n"
                    + "	e.vendavare as precovenda, \n"
                    + "	e.promovare as precooferta \n"
                    + "from estoque e\n"
                    + "order by e.proinivare"
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datatermino"));
                    imp.setPrecoNormal(rst.getDouble("precovenda"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setTipoOferta(TipoOfertaVO.CAPA);
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    private String dataInicioVenda;
    private String dataTerminoVenda;

    public void setDataInicioVenda(String dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(String dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(dataInicioVenda, dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(dataInicioVenda, dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoDBF.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id") + rst.getString("ecf") + rst.getString("datavenda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }

                        next.setId(id);
                        
                        next.setNumeroCupom(rst.getString("numerocupom") == null
                                ? Utils.stringToInt(rst.getString("id"))
                                : Utils.stringToInt(rst.getString("numerocupom")));
                        
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("datavenda"));
                        next.setIdClientePreferencial(rst.getString("idcliente"));

                        /*String horaInicio = timestampDate.format(rst.getDate("datavenda"))
                                + " "
                                + rst.getString("horainicio") == null ? "00:00:00" : rst.getString("horainicio");

                        String horaTermino = timestampDate.format(rst.getDate("datavenda"))
                                + " "
                                + rst.getString("horafim") == null ? "00:00:00" : rst.getString("horafim");*/
                        
                        String horaInicio = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        String horaTermino = timestampDate.format(rst.getDate("datavenda")) + " 00:00:00";
                        
                        next.setCancelado("Cancelado".equals(rst.getString("status")));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("totalvenda"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("seriesat"));
                        next.setChaveCfe(rst.getString("chavesat"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String dataInicio, String dataTermino) throws Exception {
            this.sql
                    = "select \n"
                    + "	v.codigo as id,\n"
                    + "	v.codcli as idcliente,\n"
                    + "	v.cupom as numerocupom,\n"
                    + "	v.terminal as ecf,\n"
                    + "	v.operador,\n"
                    + "	v.data as datavenda,\n"
                    + "	v.horainicio,\n"
                    + "	v.horafim,\n"
                    + "	v.estado as status,\n"
                    + "	v.desconto,\n"
                    + "	v.acrescimo,\n"
                    + "	v.total,\n"
                    + "	v.totalvenda,\n"
                    + "	v.chavesat,\n"
                    + "	v.chasatcanc,\n"
                    + "	v.seriesat,\n"
                    + "	v.numcfe\n"
                    + "from vendas v\n"
                    + "where v.data between '" + dataInicio + "' and '" + dataTermino + "'\n"
                    //+ " and v.numcfe = '000126'\n"
                    + "order by v.data";

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

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("dd/MM/yyyy");

        private Statement stm = ConexaoDBF.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String idVenda = rst.getString("idvenda") + rst.getString("ecf") + rst.getString("datavenda");
                        String id = rst.getString("idvenda")
                                + rst.getString("ecf")
                                + rst.getString("datavenda")
                                + rst.getString("idproduto")
                                + rst.getString("sequencia")
                                + rst.getString("qtdembalagem");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("idproduto"));
                        next.setSequencia(rst.getInt("sequencia"));
                        next.setDescricaoReduzida(rst.getString("descricaoproduto"));
                        next.setQuantidade(rst.getDouble("qtdembalagem"));
                        next.setTotalBruto(rst.getDouble("valortotal"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setCancelado("Cancelado".equals(rst.getString("status")));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setUnidadeMedida(rst.getString("tipoembalagem"));
                        
                        String trib = "";
                        
                        if (rst.getInt("cst") == 40) {
                            trib = "F";
                        } else if (rst.getInt("cst") == 41) {
                            trib = "N";
                        } else if (rst.getInt("cst") == 60) {
                            trib = "F";
                        } else if (rst.getInt("cst") == 0) {

                            if (rst.getDouble("aliquota") == 7) {
                                trib = "0700";
                            } else if (rst.getDouble("aliquota") == 11) {
                                trib = "1100";
                            } else if (rst.getDouble("aliquota") == 4.5) {
                                trib = "0450";
                            } else if (rst.getDouble("aliquota") == 12) {
                                trib = "1200";
                            } else if (rst.getDouble("aliquota") == 18) {
                                trib = "1800";
                            } else if (rst.getDouble("aliquota") == 25) {
                                trib = "2500";
                            } else if (rst.getDouble("aliquota") == 27) {
                                trib = "2700";
                            } else if (rst.getDouble("aliquota") == 17) {
                                trib = "1700";
                            } else {
                                trib = "0";
                            }
                        }

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "0450":
                    cst = 0;
                    aliq = 4.5;
                    break;
                case "0700":
                    cst = 0;
                    aliq = 7;
                    break;
                case "1100":
                    cst = 0;
                    aliq = 11;
                    break;
                case "1200":
                    cst = 0;
                    aliq = 12;
                    break;
                case "1800":
                    cst = 0;
                    aliq = 18;
                    break;
                case "2500":
                    cst = 0;
                    aliq = 25;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
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

        public VendaItemIterator(String dataInicio, String dataTermino) throws Exception {
            this.sql
                    = "select distinct\n"
                    + "	i.codvenda as idvenda,\n"
                    + "	i.terminal as ecf,\n"
                    + "	i.item as sequencia,\n"
                    + "	i.codplu as idproduto,\n"
                    + "	i.codestoque as codigobarras,\n"
                    + "	i.descricao as descricaoproduto,\n"
                    + "	upper(i.unidade) as tipoembalagem,\n"
                    + "	i.quantpeso as qtdembalagem,\n"
                    + "	i.custo,\n"
                    + "	i.venda as precovenda,\n"
                    + "	i.desconto,\n"
                    + "	i.total as valortotal,\n"
                    + "	i.datavenda,\n"
                    + "	i.icms as aliquota,\n"
                    + "	i.sittribut as cst,\n"
                    + "	i.estado as status\n"
                    + "from detaven i\n"
                    + "where i.datavenda between '" + dataInicio + "' and '" + dataTermino + "'"
                    + "and i.codplu is not null\n"
                    + "order by i.codvenda, i.terminal, i.item";

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
