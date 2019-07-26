/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.sql.Date;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.file.ArquivoLeitura;
import vrimplantacao.utils.Utils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SIMSDAO extends InterfaceDAO {

    private String MERFile;
    private String CBMERFile;
    private String NIVE1File;
    private String NIVE2File;
    private String NIVE3File;
    private String NIVE4File;
    private String CLIENTEFile;
    private String FORNECEDORFile;

    public void setMERFile(String MERFile) {
        this.MERFile = MERFile;
    }

    public void setCBMERFile(String CBMERFile) {
        this.CBMERFile = CBMERFile;
    }

    public void setNIVE1File(String NIVE1File) {
        this.NIVE1File = NIVE1File;
    }

    public void setNIVE2File(String NIVE2File) {
        this.NIVE2File = NIVE2File;
    }

    public void setNIVE3File(String NIVE3File) {
        this.NIVE3File = NIVE3File;
    }

    public void setNIVE4File(String NIVE4File) {
        this.NIVE4File = NIVE4File;
    }

    public void setCLIENTEFile(String CLIENTEFile) {
        this.CLIENTEFile = CLIENTEFile;
    }

    public void setFORNECEDORFile(String FORNECEDORFile) {
        this.FORNECEDORFile = FORNECEDORFile;
    }

    @Override
    public String getSistema() {
        return "SIMS";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        MultiMap<String, MercadologicoIMP> temp = new MultiMap<>(5);

        for (List<String> nivel : carregarNIVE(1)) {
            MercadologicoIMP imp = new MercadologicoIMP();
            imp.setImportSistema(getSistema());
            imp.setImportLoja(getLojaOrigem());
            imp.setMerc1ID(nivel.get(0));
            imp.setMerc1Descricao(nivel.get(4));
            result.add(imp);
            temp.put(imp, imp.getMerc1ID(), "0", "0", "0", "0");
        }
        for (List<String> nivel : carregarNIVE(2)) {
            MercadologicoIMP get = temp.get(nivel.get(0), "0", "0", "0", "0");
            if (get != null) {
                MercadologicoIMP imp = new MercadologicoIMP();
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(get.getMerc1ID());
                imp.setMerc1Descricao(get.getMerc1Descricao());
                imp.setMerc2ID(nivel.get(1));
                imp.setMerc2Descricao(nivel.get(4));
                result.add(imp);
                temp.put(imp, imp.getMerc1ID(), imp.getMerc2ID(), "0", "0", "0");
            }
        }
        for (List<String> nivel : carregarNIVE(3)) {
            MercadologicoIMP get = temp.get(nivel.get(0), nivel.get(1), "0", "0", "0");
            if (get != null) {
                MercadologicoIMP imp = new MercadologicoIMP();
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(get.getMerc1ID());
                imp.setMerc1Descricao(get.getMerc1Descricao());
                imp.setMerc2ID(get.getMerc2ID());
                imp.setMerc2Descricao(get.getMerc2Descricao());
                imp.setMerc3ID(nivel.get(2));
                imp.setMerc3Descricao(nivel.get(4));
                result.add(imp);
                temp.put(imp, imp.getMerc1ID(), imp.getMerc2ID(), imp.getMerc3ID(), "0", "0");
            }
        }
        for (List<String> nivel : carregarNIVE(4)) {
            MercadologicoIMP get = temp.get(nivel.get(0), nivel.get(1), nivel.get(2), "0", "0");
            if (get != null) {
                MercadologicoIMP imp = new MercadologicoIMP();
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setMerc1ID(get.getMerc1ID());
                imp.setMerc1Descricao(get.getMerc1Descricao());
                imp.setMerc2ID(get.getMerc2ID());
                imp.setMerc2Descricao(get.getMerc2Descricao());
                imp.setMerc3ID(get.getMerc3ID());
                imp.setMerc3Descricao(get.getMerc3Descricao());
                imp.setMerc4ID(nivel.get(3));
                imp.setMerc4Descricao(nivel.get(4));
                result.add(imp);
                temp.put(imp, imp.getMerc1ID(), imp.getMerc2ID(), imp.getMerc3ID(), imp.getMerc4ID(), "0");
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        for (List<String> prod : carregarMER()) {

            String id = prod.get(0);
            String descricao = prod.get(1);
            String dataCadastro = null;
            if (prod.size() - 1 > 69) {
                dataCadastro = prod.get(70);
                if (dataCadastro.length() < 8) {
                    dataCadastro = null;
                }
            }
            String ncm = Utils.formataNumero(prod.get(30));
            String tipoEmbalagem = prod.get(3);
            double peso = Utils.truncar2(Utils.stringToDouble(prod.get(10)), 2);
            double preco = Utils.truncar2(converterValor("".equals(prod.get(105)) ? prod.get(12) : prod.get(105)), 2);
            double custoComImposto = Utils.truncar2(converterValor(prod.get(11)), 2);
            double custoSemImposto = custoComImposto;//Utils.truncar2(converterValor(prod.get(26)), 2);
            double estoque = prod.get(13) == null || prod.get(13).trim().isEmpty() ? 0 : Double.parseDouble(prod.get(13));
            String piscofins = prod.get(65);
            int aliqPdv = prod.get(7) != null || !prod.get(7).trim().isEmpty() ? Utils.stringToInt(prod.get(7)) : -2;
            String merc1 = prod.get(4);
            String merc2 = prod.get(68);
            String merc3 = prod.get(69);
            String merc4 = prod.get(71);
            boolean eBalanca = false;
            String descricaoReduzida = descricao;
            int validade = 0;
            String ean = prod.get(101);
            int idSituacaoCadastral = "".equals(prod.get(100)) ? 0 : 1;
            int qtdembalagem = Utils.stringToInt(prod.get(104));
            qtdembalagem = qtdembalagem < 1 ? 1 : qtdembalagem;

            if (prod.get(100) != null && !prod.get(100).equals("")) {
                descricaoReduzida = prod.get(102);
                validade = Utils.stringToInt(prod.get(106));
                ean = prod.get(101);
                eBalanca = (Long.parseLong(ean) > 0 || Long.parseLong(ean) <= 999999) && (Utils.stringToBool(prod.get(107)));
            }

            ProdutoIMP imp = new ProdutoIMP();
            imp.setImportLoja(getLojaOrigem());
            imp.setImportSistema(getSistema());
            imp.setImportId(id);
            imp.setEan(ean);
            imp.seteBalanca(eBalanca);
            imp.setTipoEmbalagem(tipoEmbalagem);
            imp.setValidade(validade);
            imp.setDescricaoCompleta(descricao);
            imp.setDescricaoReduzida(descricaoReduzida);
            imp.setDescricaoGondola(descricao);
            imp.setCodMercadologico1(merc1);
            imp.setCodMercadologico2(merc2);
            imp.setCodMercadologico3(merc3);
            imp.setCodMercadologico4(merc4);
            imp.setPesoBruto(peso);
            imp.setPesoLiquido(peso);
            imp.setPrecovenda(preco);
            imp.setCustoComImposto(custoComImposto);
            imp.setCustoSemImposto(custoSemImposto);
            imp.setEstoque(estoque);
            imp.setSituacaoCadastro(idSituacaoCadastral);
            imp.setNcm(ncm);
            result.add(imp);
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        for (List<String> rst : carregarFORNECEDOR()) {

            Date datacadastro;
            try {
                datacadastro = new Date(new SimpleDateFormat("dd/MM/yyyy").parse(rst.get(26)).getTime());
            } catch (ParseException e) {
                datacadastro = new Date(new java.util.Date().getTime());
            }

            FornecedorIMP imp = new FornecedorIMP();
            imp.setImportLoja(getLojaOrigem());
            imp.setImportSistema(getSistema());
            imp.setImportId(rst.get(0));
            imp.setRazao(rst.get(1));
            imp.setFantasia(rst.get(2));
            imp.setCnpj_cpf(rst.get(3));
            imp.setIe_rg(rst.get(4));
            imp.setDatacadastro(datacadastro);
            imp.setEndereco((rst.get(6) + " " + rst.get(7)).trim());
            imp.setComplemento(rst.get(8));
            imp.setCep(rst.get(12));
            imp.setBairro(rst.get(9));
            imp.setMunicipio(rst.get(10));
            imp.setUf(rst.get(11));
            imp.setTel_principal(rst.get(13));
            imp.setObservacao("IMPORTADO VR" + (!"".equals(rst.get(25)) ? " - TIPOEMPRESA " + rst.get(15) : ""));

            if ((rst.get(14) != null)
                    && (!rst.get(14).trim().isEmpty())) {

                imp.addContato(
                        "TELEFONE 2",
                        rst.get(14),
                        null,
                        TipoContato.COMERCIAL,
                        null
                );
            }
            if ((rst.get(24) != null)
                    && (!rst.get(24).trim().isEmpty())) {

                imp.addContato(
                        "TELEFONE 3",
                        rst.get(24),
                        null,
                        TipoContato.COMERCIAL,
                        null
                );
            }
            if ((rst.get(25) != null)
                    && (!rst.get(25).trim().isEmpty())
                    && (rst.get(25).contains("@"))) {

                imp.addContato(
                        "EMAIL",
                        null,
                        null,
                        TipoContato.NFE,
                        rst.get(25).trim().toLowerCase()
                );
            }
            result.add(imp);
        }

        return result;
    }

    private List<List<String>> carregarNIVE(int level) throws Exception {
        List<List<String>> result = new ArrayList<>();

        File f;
        switch (level) {
            case 1:
                f = new File(this.NIVE1File);
                break;
            case 2:
                f = new File(this.NIVE2File);
                break;
            case 3:
                f = new File(this.NIVE3File);
                break;
            default:
                f = new File(this.NIVE4File);
                break;
        }
        if (f.exists() && !f.isDirectory()) {
            try (FileReader fr = new FileReader(f)) {
                try (BufferedReader br = new BufferedReader(fr)) {
                    //Salta as duas primeiras linhas que é o cabeçalho.
                    br.readLine();
                    br.readLine();
                    String[] recordId = null;

                    for (String linha = br.readLine(); linha != null; linha = br.readLine()) {
                        if (linha.startsWith("^")) {
                            String ids = linha.trim().substring(7, linha.trim().length() - 1);
                            recordId = ids.split(",");
                        } else {
                            List<String> nivel = new ArrayList<>(Arrays.asList(linha.split("\\^")));

                            nivel.set(0, Utils.acertarTexto(nivel.get(0)));

                            String nivel1Id = recordId[0];
                            String nivel2Id = "0";
                            String nivel3Id = "0";
                            String nivel4Id = "0";

                            if (level > 1) {
                                nivel2Id = recordId[1];
                            }
                            if (level > 2) {
                                nivel3Id = recordId[2];
                            }
                            if (level > 3) {
                                nivel4Id = recordId[3];
                            }

                            nivel.add(0, nivel1Id);
                            nivel.add(1, nivel2Id);
                            nivel.add(2, nivel3Id);
                            nivel.add(3, nivel4Id);

                            if (nivel.size() < 6) {
                                int cont = 6 - nivel.size();
                                while (cont > 0) {
                                    nivel.add("");
                                    cont--;
                                }
                            }
                            result.add(nivel);
                        }
                    }
                }
            }
        }
        return result;
    }

    private List<List<String>> carregarMER() throws Exception {
        List<List<String>> result = new ArrayList<>();

        File f = new File(this.MERFile);
        try (FileReader fr = new FileReader(f)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                //Salta as duas primeiras linhas que é o cabeçalho.
                br.readLine();
                br.readLine();
                Long recordId = null;

                Map<Long, Map<Long, List<String>>> cb = carregarCBMER();
                for (String linha = br.readLine(); linha != null; linha = br.readLine()) {
                    if (linha.startsWith("^MER")) {
                        recordId = Long.parseLong(getId(linha)[0]);
                    } else {
                        Map<Long, List<String>> get = cb.get(recordId);
                        if (get != null) {
                            for (List<String> ean : get.values()) {
                                List<String> produto = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                                produto.add(0, String.valueOf(recordId));
                                if (produto.size() < 100) {
                                    int cont = 100 - produto.size();
                                    while (cont > 0) {
                                        produto.add("");
                                        cont--;
                                    }
                                }
                                produto.addAll(ean);
                                long barra = Utils.stringToLong(produto.get(101), -2);
                                if (/*Utils.stringToBool(produto.get(107)) && */barra > 0 && barra < 999999) {
                                    produto.set(107, "S");//, linha)get(107) 
                                } else {
                                    produto.set(107, "N");
                                }
                                result.add(produto);
                            }
                        } else {
                            List<String> produto = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                            produto.add(0, String.valueOf(recordId));
                            if (produto.size() < 100) {
                                int cont = 100 - produto.size();
                                while (cont > 0) {
                                    produto.add("");
                                    cont--;
                                }
                            }
                            int cont = 15;
                            while (cont > 0) {
                                produto.add("");
                                cont--;
                            }
                            result.add(produto);
                        }
                    }
                }
            }
        }
        return result;
    }

    private Map<Long, Map<Long, List<String>>> carregarCBMER() throws Exception {
        Map<Long, Map<Long, List<String>>> result = new LinkedHashMap<>();
        String erro = "";
        File f = new File(this.CBMERFile);
        try (FileReader fr = new FileReader(f)) {
            try (BufferedReader br = new BufferedReader(fr)) {
                //Salta as duas primeiras linhas que é o cabeçalho.
                br.readLine();
                br.readLine();
                long recordId = -1;
                long ean = -2;
                for (String linha = br.readLine(); linha != null; linha = br.readLine()) {
                    if (linha.startsWith("^CBMER")) {
                        erro = linha;
                        String[] ids = getId(linha);
                        recordId = Long.parseLong(ids[1]);
                        ean = Long.parseLong(ids[0]);
                    } else {
                        List<String> produto = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                        produto.add(0, String.valueOf(recordId));
                        produto.add(1, String.valueOf(ean));
                        if (produto.size() < 20) {
                            int cont = 20 - produto.size();
                            while (cont > 0) {
                                produto.add("");
                                cont--;
                            }
                        }
                        Map<Long, List<String>> eans = result.get(recordId);
                        if (eans == null) {
                            eans = new HashMap<>();
                            eans.put(Long.parseLong(produto.get(1)), produto);
                            result.put(recordId, eans);
                        } else {
                            eans.put(Long.parseLong(produto.get(1)), produto);
                            result.put(recordId, eans);
                        }
                    }
                }
            } catch (Exception e) {
                System.out.println(erro);
                throw e;
            }
        }

        return result;
    }

    private String[] getId(String linha) {
        linha = linha.trim();
        linha = linha.replaceAll("\\^.*\\(", "");
        linha = linha.replaceAll("\\)", "");
        return linha.split(",");
    }

    private double converterValor(String valor) {
        if (valor == null) {
            return 0;
        } else {
            valor = Utils.formataNumero(valor);
            valor = "000" + valor;
            if (valor.length() >= 3) {
                String inteiro = valor.substring(0, valor.length() - 2);
                String decimal = valor.substring(valor.length() - 2, valor.length());
                return Double.parseDouble(inteiro + "." + decimal);
            } else {
                return Double.parseDouble(valor);
            }
        }
    }

    private List<List<String>> carregarFORNECEDOR() throws IOException {
        List<List<String>> result = new ArrayList<>();

        try (ArquivoLeitura arquivo = new ArquivoLeitura(this.FORNECEDORFile)) {
            //Salta as duas primeiras linhas que é o cabeçalho.
            arquivo.remove(0);
            arquivo.remove(0);
            String recordId = null;

            for (String linha : arquivo) {
                if (linha.startsWith("^")) {
                    recordId = getId(linha)[0];
                } else {
                    List<String> fornecedor = new ArrayList<>(Arrays.asList(linha.split("\\^")));
                    fornecedor.add(0, recordId);

                    if (fornecedor.size() < 55) {
                        int cont = 55 - fornecedor.size();
                        while (cont > 0) {
                            fornecedor.add("");
                            cont--;
                        }
                    }
                    result.add(fornecedor);
                }
            }
        }

        return result;
    }
}
