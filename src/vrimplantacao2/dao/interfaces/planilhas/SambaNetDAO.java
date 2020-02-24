package vrimplantacao2.dao.interfaces.planilhas;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Logger;
import jxl.Cell;
import jxl.Sheet;
import jxl.Workbook;
import jxl.WorkbookSettings;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class SambaNetDAO extends InterfaceDAO implements MapaTributoProvider {
    
    private static final Logger LOG = Logger.getLogger(SambaNetDAO.class.getName());
    
    private String planilhaFamilia;
    private String planilhaBalanca;
    private String planilhaProdutos;
    private String planilhaProdutosContador;
    private String planilhaFornecedor;
    private String planilhaClientes;
    private boolean inativacao = false;

    @Override
    public String getSistema() {
        return "SambaNet";
    }

    public void setPlanilhaFamiliaProduto(String planilhaFamilia) {
        this.planilhaFamilia = planilhaFamilia;
    }

    public void setPlanilhaProdutos(String planilhaProdutos) {
        this.planilhaProdutos = planilhaProdutos;
    }

    public void setPlanilhaBalanca(String planilhaBalanca) {
        this.planilhaBalanca = planilhaBalanca;
    }

    public void setPlanilhaProdutosContator(String planilhaProdutosContador) {
        this.planilhaProdutosContador = planilhaProdutosContador;
    }

    public void setPlanilhaFornecedor(String planilhaFornecedor) {
        this.planilhaFornecedor = planilhaFornecedor;
    }

    public void setPlanilhaClientes(String planilhaClientes) {
        this.planilhaClientes = planilhaClientes;
    }

    public void setInativacao(boolean inativacao) {
        this.inativacao = inativacao;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.FAMILIA_PRODUTO,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
            OpcaoProduto.IMPORTAR_GERAR_SUBNIVEL_MERC,
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO, 
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.CUSTO,
            OpcaoProduto.PRECO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,
            OpcaoProduto.FAMILIA,
            OpcaoProduto.ATIVO,
            OpcaoProduto.QTD_EMBALAGEM_COTACAO,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.VALIDADE
        }));
    }
    
    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        File file = new File(this.planilhaFamilia);
        
        if (file.exists()) {        
            List<FamiliaProdutoIMP> result = new ArrayList<>();

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);
            
            ProgressBar.setStatus("Analisando Planilha de Família de Produtos");
            ProgressBar.setMaximum(sheet.getRows());
            for (int i = 1; i < sheet.getRows(); i++) {
                Cell[] cells = sheet.getRow(i);
                //Se for uma linha vazia ou não numérica, pula
                if (
                        sheet.getCell(0, i) == null || !Utils.acertarTexto(sheet.getCell(0, i).getContents()).matches("[0-9]+")
                ) {
                    ProgressBar.next();
                    continue;
                }
                
                //Caso contrário verifica se a coluna C é uma descrição
                if (!"".equals(Utils.acertarTexto(cells[2].getContents()))) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(cells[0].getContents());
                    imp.setDescricao(cells[2].getContents());
                    
                    result.add(imp);
                }                
                
                ProgressBar.next();
            }

            return result;
        } else {
            throw new IOException("Planilha de Família de Produtos não encontrada");
        }
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        
        File file = new File(this.planilhaProdutosContador);        
        if (file.exists()) {

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);
            
            int linha = 0;
            try {
                Set<String> tributos = new HashSet<>();
                for (int i = 1; i < sheet.getRows(); i++) {
                    if (
                            val(sheet, 0, i).matches("[0-9]+") &&
                            val(sheet, 1, i).matches("[0-9]+") &&
                            !val(sheet, 5, i).equals("")
                    ) {
                        if (tributos.add(val(sheet, 10, i))) {
                            LOG.fine("Tributo '" + val(sheet, 10, i) + "' incluso!");
                        }
                    }
                }
                
                List<MapaTributoIMP> result = new ArrayList<>();
                
                for (String trib: tributos) {
                    result.add(new MapaTributoIMP(trib, trib));
                }
                
                return result;
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }
        }
        return null;
    }
    
    private String val(Sheet sheet, int col, int row) {
        String str = sheet.getCell(col, row).getContents();
        str = str == null ? "" : str.trim();
        return str;
    }

    private Map<String, Integer> getProdutoBalanca() throws Exception {
        File file = new File(this.planilhaBalanca);        
        if (file.exists()) {        
            Map<String, Integer> result = new HashMap<>();

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sh = planilha.getSheet(0);
            
            ProgressBar.setStatus("Analisando Planilha de Balança");
            ProgressBar.setMaximum(sh.getRows());
           
            
            int linha = 0;
            try {
                for (int i = 1; i < sh.getRows(); i++) {
                    linha++;
                    if (
                            val(sh, 1, i).matches("[0-9]+") &&
                            !val(sh, 6, i).equals("") &&
                            val(sh, 8, i).matches("[0-9]+") &&
                            !val(sh, 10, i).equals("")
                    ) {
                        result.put(val(sh, 1, i), Utils.stringToInt(val(sh, 8, i)));
                    }
                    
                    ProgressBar.next();
                }
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }

            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }
    
    private static class Mercadologico {
        String id;
        String descricao;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        File file = new File(this.planilhaProdutos);        
        if (file.exists()) {        
            List<MercadologicoIMP> result = new ArrayList<>();

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);
            
            ProgressBar.setStatus("Analisando Planilha de Mercadológicos");
            ProgressBar.setMaximum(sheet.getRows());
            
            Mercadologico centroReceita = null;
            Mercadologico grupo = null;
            Mercadologico categoria;
            
            int linha = 0;
            try {
                for (int i = 1; i < sheet.getRows(); i++) {
                    linha++;
                    if (
                            sheet.getCell(0, i) != null &&
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CENTRO DE RECEITA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            centroReceita = new Mercadologico();
                            centroReceita.id =  Utils.acertarTexto(strs[0].substring(17).trim());
                            centroReceita.descricao =  Utils.acertarTexto(strs[1]);
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("GRUPO")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            grupo = new Mercadologico();
                            grupo.id =  Utils.acertarTexto(strs[0].substring(5).trim());
                            grupo.descricao =  Utils.acertarTexto(strs[1]);
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CATEGORIA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            categoria = new Mercadologico();
                            categoria.id =  Utils.acertarTexto(strs[0].substring(9).trim());
                            categoria.descricao =  Utils.acertarTexto(strs[1]);

                            MercadologicoIMP imp = new MercadologicoIMP();
                            imp.setImportSistema(getSistema());
                            imp.setImportLoja(getLojaOrigem());
                            imp.setMerc1ID(centroReceita.id);
                            imp.setMerc1Descricao(centroReceita.descricao);
                            imp.setMerc2ID(grupo.id);
                            imp.setMerc2Descricao(grupo.descricao);
                            imp.setMerc3ID(categoria.id);
                            imp.setMerc3Descricao(categoria.descricao);
                            result.add(imp);
                        }
                    }

                    ProgressBar.next();
                }
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }

            return result;
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        File file = new File(this.planilhaProdutos);        
        if (file.exists()) {

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sheet = planilha.getSheet(0);

            int linha = 0;
            
            
            MultiMap<String, ProdutoIMP> produtos = new MultiMap<>();
            Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
            
            try {

                String centroReceita = "", grupo = "", categoria = "";
                
                Map<String, String> familia = getFamilia();

                ProgressBar.setStatus("Analisando Planilha de Produtos");
                ProgressBar.setMaximum(sheet.getRows());
                for (int i = 1; i < sheet.getRows(); i++) {
                    linha++;
                    if (
                            sheet.getCell(0, i) != null &&
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).matches("[0-9]+") &&
                            Utils.acertarTexto(sheet.getCell(1, i).getContents()).matches("[0-9]")
                    ) {
                        
                        ProdutoIMP imp = new ProdutoIMP();
                        if (inativacao) {
                            imp.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                        }
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(sheet.getCell(0, i).getContents());
                        imp.setEan(sheet.getCell(2, i).getContents());
                        imp.setDescricaoCompleta(sheet.getCell(7, i).getContents());
                        imp.setDescricaoReduzida(sheet.getCell(7, i).getContents());
                        imp.setDescricaoGondola(sheet.getCell(7, i).getContents());
                        imp.setQtdEmbalagemCotacao(Utils.stringToInt(sheet.getCell(10, i).getContents(), 1));
                        imp.setEstoque(Utils.stringToDouble(sheet.getCell(13, i).getContents()));
                        imp.setCustoSemImposto(Utils.stringToDouble(sheet.getCell(15, i).getContents()));
                        imp.setCustoComImposto(Utils.stringToDouble(sheet.getCell(15, i).getContents()));
                        imp.setPrecovenda(Utils.stringToDouble(sheet.getCell(18, i).getContents()));
                        imp.setCodMercadologico1(centroReceita);
                        imp.setCodMercadologico2(grupo);
                        imp.setCodMercadologico3(categoria);
                        imp.setIdFamiliaProduto(familia.get(imp.getImportId()));
                        
                        ProdutoBalancaVO prod = produtosBalanca.get(Utils.stringToInt(imp.getEan()));
                        if (prod != null) {
                            imp.seteBalanca(true);
                            imp.setValidade(prod.getValidade());
                            if ("U".equals(prod.getPesavel())) {
                                imp.setTipoEmbalagem("UN");
                            } else {
                                imp.setTipoEmbalagem("KG");
                            }
                            imp.setEan(String.valueOf(prod.getCodigo()));
                        } else {
                            imp.seteBalanca(false);
                            imp.setValidade(0);
                            imp.setTipoEmbalagem("UN");
                        }
                        
                        produtos.put(imp, imp.getImportId(), sheet.getCell(2, i).getContents());
                        
                    } else if (
                            sheet.getCell(0, i) != null &&
                            Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CENTRO DE RECEITA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            centroReceita = Utils.acertarTexto(strs[0].substring(17));
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("GRUPO")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            grupo = Utils.acertarTexto(strs[0].substring(5));
                        }
                    } else if (
                            sheet.getCell(0, i) != null && Utils.acertarTexto(sheet.getCell(0, i).getContents()).contains("CATEGORIA")
                    ) {
                        String[] strs = Utils.acertarTexto(sheet.getCell(0, i).getContents()).split("\\-");
                        if (strs.length == 2) {
                            categoria = Utils.acertarTexto(strs[0].substring(9));
                        }                    
                    }

                    ProgressBar.next();
                }
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }
            
            vincularTributos(produtos);
           
            return new ArrayList<>(produtos.values());
        } else {
            throw new IOException("Planilha(s) não encontrada");
        }
    }

    private Map<String, String> getFamilia() throws Exception {
        File file = new File(this.planilhaFamilia);        
        if (!file.exists()) {
            throw new Exception("");
        }

        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        settings.setIgnoreBlanks(false);

        Workbook planilha = Workbook.getWorkbook(file, settings);            
        Sheet sheet = planilha.getSheet(0);

        ProgressBar.setStatus("Analisando Planilha de Produtos (Família de Produtos)");
        ProgressBar.setMaximum(sheet.getRows());

        int linha = 0;

        try {
            Map<String, String> result = new HashMap<>();
            
            String familia = null;
            for (int i = 1; i < sheet.getRows(); i++) {
                //Se a coluna 2 for um número e a coluna 3 for texto, então é um produto.
                if (
                        Utils.acertarTexto(sheet.getCell(1, i).getContents()).matches("[0-9]+") &&
                        !Utils.acertarTexto(sheet.getCell(2, i).getContents()).equals("")
                ) {
                    result.put(sheet.getCell(1, i).getContents(), familia);
                    LOG.finer(String.format("Família '%s' vinculado ao produto '%s'-'%s'",
                            familia,
                            sheet.getCell(1, i),
                            sheet.getCell(2, i)
                    ));
                } else if (
                        //Se a primeira coluna é numérica, se a segunda é vazia e se a terceira for um texto
                        //então é uma família.
                        Utils.acertarTexto(sheet.getCell(0, i).getContents()).matches("[0-9]+") &&
                        Utils.acertarTexto(sheet.getCell(1, i).getContents()).equals("") &&
                        !Utils.acertarTexto(sheet.getCell(2, i).getContents()).equals("")
                ) {
                    familia = Utils.acertarTexto(sheet.getCell(0, i).getContents());
                }
                ProgressBar.next();
            }
            
            return result;
        } catch (Exception ex) {
            System.out.println(linha);
            throw ex;
        }        
    }

    private void vincularTributos(MultiMap<String, ProdutoIMP> produtos) throws Exception {
        File file = new File(this.planilhaProdutosContador);        
        if (file.exists()) {

            WorkbookSettings settings = new WorkbookSettings();
            settings.setEncoding("CP1250");
            settings.setIgnoreBlanks(false);

            Workbook planilha = Workbook.getWorkbook(file, settings);            
            Sheet sh = planilha.getSheet(0);

            ProgressBar.setStatus("Analisando Planilha de Produtos (Tributação)");
            ProgressBar.setMaximum(sh.getRows());

            int linha = 0;
            
            try {
                for (int i = 1; i < sh.getRows(); i++) {
                    //Se a coluna 2 for um número e a coluna 3 for texto, então é um produto.
                    if (
                            val(sh, 0, i).matches("[0-9]+") &&
                            val(sh, 1, i).matches("[0-9]+") &&
                            !val(sh, 5, i).equals("")
                    ) {
                        ProdutoIMP imp = produtos.get(val(sh, 0, i),val(sh, 1, i));
                    
                        if (!imp.isBalanca()) {
                            imp.setTipoEmbalagem(val(sh,6,i));
                        }
                        imp.setDescricaoCompleta(val(sh,5,i));
                        imp.setNcm(val(sh,7,i));
                        imp.setIcmsDebitoId(val(sh,10, i));
                        imp.setIcmsCreditoId(val(sh,10, i));
                        imp.setPiscofinsCstCredito(val(sh,11,i));
                        imp.setPiscofinsCstDebito(val(sh,12,i));
                        imp.setPiscofinsNaturezaReceita(val(sh,16,i));
                        imp.setCest(val(sh,17,i));
                    }
                    
                    ProgressBar.next();
                }
                
            } catch (Exception ex) {
                System.out.println(linha);
                throw ex;
            }
        }
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {

        List<FornecedorIMP> result = new ArrayList<>();
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        settings.setIgnoreBlanks(false);

        Workbook planilha = Workbook.getWorkbook(new File(this.planilhaFornecedor), settings);            
        Sheet sh = planilha.getSheet(0);

        ProgressBar.setStatus("Analisando Planilha de Fornecedores");
        ProgressBar.setMaximum(sh.getRows());

        int linha = 0;

        try {
            FornecedorIMP imp = null;
            for (int i = 1; i < sh.getRows(); i++) {
                //Se a coluna 2 for um número e a coluna 3 for texto, então é um produto.
                if (
                        val(sh, 0, i).equals("Cód.") &&
                        val(sh, 3, i).equals("Razão Social:")                             
                ) {
                    if (imp != null) {
                        result.add(imp);
                    }
                    imp = new FornecedorIMP();
                    if (inativacao) {
                        imp.setAtivo(false);
                    }
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(val(sh, 1, i));
                    imp.setRazao(val(sh, 8, i));
                    imp.setCnpj_cpf(val(sh, 12, i));
                    imp.setIe_rg(val(sh, 16, i));
                    if (!val(sh, 20, i).equals("")) {
                        imp.addContato(val(sh, 20, i), val(sh, 20, i), "", TipoContato.COMERCIAL, "");
                    }
                    if (!val(sh, 23, i).equals("")) {
                        imp.setTel_principal(val(sh, 23, i));
                    }
                } else if (
                        val(sh, 0, i).equals("") &&
                        !val(sh, 17, i).equals("") &&
                        !val(sh, 20, i).equals("")
                ) {
                    imp.setBairro(val(sh, 17, i));
                    imp.setMunicipio(val(sh, 20, i));
                } else if (
                        val(sh, 0, i).equals("Fantasia:") &&
                        val(sh, 9, i).equals("Endereço:")
                ) {
                    imp.setFantasia(val(sh, 2, i));
                    if (imp.getFantasia().equals("")) {
                        imp.setFantasia(imp.getRazao());
                    }
                    imp.setEndereco(val(sh, 9, i));
                    imp.setComplemento(val(sh, 12, i));

                    if (
                        !val(sh, 17, i).equals("") &&
                        !val(sh, 20, i).equals("")
                    ) {
                        imp.setBairro(val(sh, 17, i));
                        imp.setMunicipio(val(sh, 20, i));
                    }
                    imp.setUf(val(sh, 25, i));
                    
                    if (
                        val(sh, 9, i).equals("Endereço:")) {
                        imp.setObservacao(val(sh, 10, i));
}
                    
                    // imp.setObservacao(val(sh, 10, i));
                }

                ProgressBar.next();
            }            
            
            if (imp != null) {
                result.add(imp);
            }

        } catch (Exception ex) {
            System.out.println(linha);
            throw ex;
        }
     
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        WorkbookSettings settings = new WorkbookSettings();
        settings.setEncoding("CP1250");
        settings.setIgnoreBlanks(false);

        Workbook planilha = Workbook.getWorkbook(new File(this.planilhaClientes), settings);            
        Sheet sh = planilha.getSheet(0);

        ProgressBar.setStatus("Analisando Planilha de Fornecedores");
        ProgressBar.setMaximum(sh.getRows());

        int linha = 0;

        try {
            ClienteIMP imp = null;
            for (int i = 1; i < sh.getRows(); i++) {
                //Se a coluna 2 for um número e a coluna 3 for texto, então é um produto.
                if (
                        val(sh, 0, i).equals("Cód.") &&
                        val(sh, 1, i).equals("Razão Social:")                             
                ) {
                    if (imp != null) {
                        result.add(imp);
                    }
                    imp = new ClienteIMP();
                    if (inativacao) {
                        imp.setAtivo(false);
                    }
                    imp.setId(val(sh, 0, i));
                    imp.setRazao(val(sh, 8, i));
                    imp.setCnpj(val(sh, 12, i));
                    imp.setInscricaoestadual(val(sh, 7, i));
                    if (!val(sh, 10, i).equals("")) {
                        imp.addContato(val(sh, 10, i), val(sh, 1018, i), val(sh, 24, i), "", "");
                    }
                    if (!val(sh, 8, i).equals("")) {
                        imp.setTelefone(val(sh, 8, i));
                    imp.setObservacao(val(sh, 11, i));
                    /*}
                } else if (
                        val(sh, 0, i).equals("") //&&
                        //!val(sh, 19, i).equals("") &&
                        //!val(sh, 25, i).equals("")
                ) {
                    imp.setBairro(val(sh, 19, i));
                    imp.setMunicipio(val(sh, 25, i));
                } else if (
                        val(sh, 0, i).equals("Fantasia:") &&
                        val(sh, 9, i).equals("Endereço:")
                ) {
                    imp.setFantasia(val(sh, 1, i));
                    if (imp.getFantasia().equals("")) {
                        imp.setFantasia(imp.getRazao());
                    }
                    imp.setEndereco(val(sh, 11, i));

                    if (!val(sh, 11, i).equals("")) {                    
                        imp.setBairro(val(sh, 17, i));
                    }                    
                    if (
                        !val(sh, 25, i).equals("")
                    ) {                        
                        imp.setUf(val(sh, 25, i));*/
                    }
                }
                if (imp != null) {
                    result.add(imp);
                }
                ProgressBar.next();
            }            
            
            

        } catch (Exception ex) {
            System.out.println(linha);
            throw ex;
        }
        
        return result;
    }
    
}
