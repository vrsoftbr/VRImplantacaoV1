/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.openide.util.Exceptions;
import vrimplantacao.classe.ConexaoParadox;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class SicDAO extends InterfaceDAO {

    private String sistema = "Sic";
    private SimpleDateFormat formatData = new SimpleDateFormat(Parametros.get().getWithNull("yyyy-MM-dd", "IMPORTACAO", "PLANILHA", "FORMATO_DATA"));

    @Override
    public String getSistema() {
        return sistema;
    }

    private Date getData(String format) {
        if (format != null && !"".equals(format.trim())) {
            try {
                return format == null ? null : formatData.parse(format);
            } catch (ParseException ex) {
                Exceptions.printStackTrace(ex);
            }
        }
        return null;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.MANTER_DESCRICAO_PRODUTO,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                    OpcaoProduto.QTD_EMBALAGEM_EAN,
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
                    OpcaoProduto.MARGEM,

                }
        ));
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	controle,\n"
                    + "	setor\n"
                    + "FROM tabest8"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("controle"));
                    imp.setMerc1Descricao(rst.getString("setor"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc3Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.controle,\n"
                    + "	p.codigo,\n"
                    + "	p.produto,\n"
                    + "	p.precocusto,\n"
                    + "	p.lucro,\n"
                    + "	p.precovenda,\n"
                    + "	p.quantidade,\n"
                    + "	p.estminimo,\n"
                    + "	p.unidade,\n"
                    + "	p.codipi,\n"
                    + "	p.cest,\n"
                    + "	p.cst,\n"
                    + "	p.icms,\n"
                    + "	p.basecalculo,\n"
                    + "	p.inativo,\n"
                    + "	p.pesobruto,\n"
                    + "	p.pesoliq,\n"
                    + "	p.datainc,\n"
                    + " p.lksetor\n"
                    + "FROM tabest1 p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("controle"));
                    imp.setEan(rst.getString("codigo"));
                    imp.setDescricaoCompleta(rst.getString("produto"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setDataCadastro(getData(rst.getString("datainc")));
                    //imp.setPesoBruto(Utils.truncar2(rst.getDouble("pesobruto"), 2));
                    //imp.setPesoLiquido(Utils.truncar2(rst.getDouble("pesoliq"), 2));
                    imp.setCodMercadologico1(rst.getString("lksetor"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setMargem(Utils.truncar2(rst.getDouble("lucro"), 2));
                    imp.setCustoComImposto(Utils.truncar2(rst.getDouble("precocusto"), 2));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(Utils.truncar2(rst.getDouble("precovenda"), 2));
                    imp.setEstoque(Utils.truncar2(rst.getDouble("quantidade"), 2));
                    imp.setEstoqueMinimo(Utils.truncar2(rst.getDouble("estminimo"), 2));
                    imp.setNcm(rst.getString("codipi"));
                    imp.setCest(rst.getString("cest"));

                    imp.setIcmsCstSaida(rst.getInt("cst"));
                    imp.setIcmsAliqSaida(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaida(rst.getDouble("basecalculo"));

                    imp.setIcmsCstSaidaForaEstado(rst.getInt("cst"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("basecalculo"));

                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("cst"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("basecalculo"));

                    imp.setIcmsCstEntrada(rst.getInt("cst"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("basecalculo"));

                    imp.setIcmsCstEntradaForaEstado(rst.getInt("cst"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("basecalculo"));

                    imp.setIcmsCstConsumidor(rst.getInt("cst"));
                    imp.setIcmsAliqConsumidor(rst.getDouble("icms"));
                    imp.setIcmsReducaoConsumidor(rst.getDouble("basecalculo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.controle,\n"
                    + "	cean,\n"
                    + "	p.unidade\n"
                    + "FROM tabest1 p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("controle"));
                    imp.setEan(rst.getString("cean"));
                    result.add(imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	p.controle,\n"
                    + "	ceantrib,\n"
                    + "	p.unidade\n"
                    + "FROM tabest1 p"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("controle"));
                    imp.setEan(rst.getString("ceantrib"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.controle,\n"
                    + "	f.empresa,\n"
                    + "	f.contato,\n"
                    + "	f.endereco,\n"
                    + "	f.bairro,\n"
                    + "	f.cidade,\n"
                    + "	f.estado,\n"
                    + "	f.cep,\n"
                    + "	f.telefone,\n"
                    + "	f.fax,\n"
                    + "	f.cgc,\n"
                    + "	f.insc,\n"
                    + "	f.DATA,\n"
                    + "	f.obs,\n"
                    + "	f.email\n"
                    + "FROM tabfor f"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("controle"));
                    imp.setCnpj_cpf(rst.getString("cgc"));
                    imp.setIe_rg(rst.getString("insc"));
                    imp.setRazao(rst.getString("empresa"));
                    imp.setFantasia(imp.getRazao());
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setDatacadastro(getData(rst.getString("data")));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("contato") != null)
                            && (!rst.getString("contato").trim().isEmpty())) {
                        imp.setObservacao("CONTATO " + rst.getString("contato"));
                    }

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addTelefone("fAX", rst.getString("fax"));
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

        try (Statement stm = ConexaoParadox.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	controle,\n"
                    + "	lkfornec\n"
                    + "FROM tabest1\n"
                    + "WHERE lkfornec <> ''"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("controle"));
                    imp.setIdFornecedor(rst.getString("lkfornec"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    /*
    public List<ProdutoIMP> getProdutos_() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.planilhaProdutos, getOpcoes());
        ProgressBar.setStatus("Carregando produtos...");

        for (LinhaArquivo rst : produtos) {
            String id = rst.getString("Controle");
            if (id != null && !"".equals(id.trim())) {

                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(id);
                imp.setEan(rst.getString("Codigo"));
                imp.setDescricaoCompleta(rst.getString("Produto"));
                imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                imp.setDescricaoGondola(imp.getDescricaoCompleta());
                imp.setTipoEmbalagem(rst.getString("Unidade"));
                imp.setPesoBruto(rst.getDouble("PesoBruto"));
                imp.setPesoLiquido(rst.getDouble("PesoLiq"));
                imp.setDataCadastro(getData(rst.getString("DataInc")));
                imp.setMargem(Utils.truncar2(rst.getDouble("Lucro"), 2));
                imp.setCustoComImposto(rst.getDouble("PrecoCusto"));
                imp.setCustoSemImposto(imp.getCustoComImposto());
                imp.setPrecovenda(rst.getDouble("PrecoVenda"));
                imp.setEstoqueMinimo(rst.getDouble("EstMinimo"));
                imp.setEstoque(rst.getDouble("Quantidade"));
                imp.setNcm(rst.getString("CodIPI"));
                imp.setCest(rst.getString("Cest"));

                imp.setIcmsCstSaida(rst.getInt("CST"));
                imp.setIcmsAliqSaida(rst.getDouble("ICMS"));
                imp.setIcmsReducaoSaida(rst.getDouble("BaseCalculo"));

                imp.setIcmsCstSaidaForaEstado(rst.getInt("CST"));
                imp.setIcmsAliqSaidaForaEstado(rst.getDouble("ICMS"));
                imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("BaseCalculo"));

                imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("CST"));
                imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("ICMS"));
                imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("BaseCalculo"));

                imp.setIcmsCstEntrada(rst.getInt("CST"));
                imp.setIcmsAliqEntrada(rst.getDouble("ICMS"));
                imp.setIcmsReducaoEntrada(rst.getDouble("BaseCalculo"));

                imp.setIcmsCstEntradaForaEstado(rst.getInt("CST"));
                imp.setIcmsAliqEntradaForaEstado(rst.getDouble("ICMS"));
                imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("BaseCalculo"));

                imp.setIcmsCstConsumidor(rst.getInt("CST"));
                imp.setIcmsAliqConsumidor(rst.getDouble("ICMS"));
                imp.setIcmsReducaoConsumidor(rst.getDouble("BaseCalculo"));

                result.add(imp);
            }
        }
        return result;
    }

    public List<ProdutoIMP> getEANs_() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.planilhaFornecedores, getOpcoes());
        ProgressBar.setStatus("Carregando produtos...");

        for (LinhaArquivo rst : produtos) {
            String id = rst.getString("Controle");
            if (id != null && !"".equals(id.trim())) {

                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(id);
                imp.setEan(rst.getString("cEAN"));
                imp.setTipoEmbalagem(rst.getString("Unidade"));

                result.add(imp);
            }
        }
        return result;
    }

    public List<FornecedorIMP> getFornecedores_() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        Arquivo fornecedores = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
        ProgressBar.setStatus("Carregando fornecedores...");

        for (LinhaArquivo rst : fornecedores) {
            String id = rst.getString("Controle");
            if (id != null && !"".equals(id.trim())) {

                FornecedorIMP imp = new FornecedorIMP();
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(id);
                imp.setCnpj_cpf(rst.getString("CGC"));
                imp.setIe_rg(rst.getString("INSC"));
                imp.setRazao(rst.getString("Empresa"));
                imp.setFantasia(imp.getRazao());
                imp.setEndereco(rst.getString("Endereco"));
                imp.setBairro(rst.getString("Bairro"));
                imp.setMunicipio(rst.getString("Cidade"));
                imp.setUf(rst.getString("Estado"));
                imp.setCep(rst.getString("CEP"));
                imp.setDatacadastro(getData(rst.getString("DATA")));
                imp.setTel_principal(rst.getString("Telefone"));

                if ((rst.getString("Contato") != null)
                        && (!rst.getString("Contato").trim().isEmpty())) {
                    imp.setObservacao("CONTATO " + rst.getString("Contato"));
                }

                if ((rst.getString("Fax") != null)
                        && (!rst.getString("Fax").trim().isEmpty())) {
                    imp.addTelefone("FAX", rst.getString("Fax"));
                }

                result.add(imp);
            }
        }
        return result;
    }
    */
}
