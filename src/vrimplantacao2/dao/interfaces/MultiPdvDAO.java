/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class MultiPdvDAO extends InterfaceDAO {

    public String v_arquivo = "";
    private BufferedReader br = null;

    @Override
    public String getSistema() {
        return "MultiPdv";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        String linha = "";
        int cont = 0;
        try {

            br = new BufferedReader(new InputStreamReader(new FileInputStream(v_arquivo), "UTF-8"));
            Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
            while ((linha = br.readLine()) != null) {

                cont++;
                if (cont == 1) {
                    continue;
                }

                ProdutoIMP imp = new ProdutoIMP();
                ProdutoBalancaVO produtoBalanca;
                imp.setImportLoja(getLojaOrigem());
                imp.setImportSistema(getSistema());
                imp.setImportId(linha.substring(0, 7));
                imp.setEan(linha.substring(7, 20));
                imp.setDescricaoCompleta(linha.substring(20, 60));
                imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                imp.setDescricaoGondola(imp.getDescricaoCompleta());

                long codigoProduto;
                codigoProduto = Long.parseLong(imp.getImportId());
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

                result.add(imp);
                
                
            }
            return result;

        } catch (Exception ex) {
            throw ex;
        }
    }
}
