/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.openide.util.Exceptions;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.arquivo.Arquivo;
import vrimplantacao2.utils.arquivo.ArquivoFactory;
import vrimplantacao2.utils.arquivo.LinhaArquivo;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class SicDAO extends InterfaceDAO {

    private String arquivo;
    private String sistema = "Sic";
    private Map<String, String> opcoes = new LinkedHashMap<>();
    private SimpleDateFormat formatData = new SimpleDateFormat(Parametros.get().getWithNull("yyyy-MM-dd", "IMPORTACAO", "PLANILHA", "FORMATO_DATA"));

    @Override
    public String getSistema() {
        return sistema;
    }

    public Map<String, String> getOpcoes() {
        return opcoes;
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
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
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

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        Arquivo produtos = ArquivoFactory.getArquivo(this.arquivo, getOpcoes());
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

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
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
}
