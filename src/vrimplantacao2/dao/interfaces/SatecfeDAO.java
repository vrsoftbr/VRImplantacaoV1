/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SatecfeDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Satecfe";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigo, codigo ean, descricao, unidade, \n"
                    + "	saldosem, venda, custo, taxa, maximo, minimo, \n"
                    + "	st, ncm, peso\n"
                    + "from \n"
                    + "	dbmercado.produto\n"
                    + "union all	\n"
                    + "select \n"
                    + "	codigo, barra ean, descricao, unidade, \n"
                    + "	saldosem, venda, custo, taxa, maximo, minimo, \n"
                    + "	st, ncm, peso\n"
                    + "from \n"
                    + "	dbmercado.produto"
            )) {
                ProdutoIMP imp = new ProdutoIMP();
                imp.setImportSistema(getSistema());
                imp.setImportLoja(getLojaOrigem());
                imp.setImportId(rst.getString("codigo"));
                imp.setEan(rst.getString("ean"));
                imp.setDescricaoCompleta(rst.getString("descricao"));
                imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                imp.setDescricaoGondola(imp.getDescricaoCompleta());
                imp.setTipoEmbalagem(rst.getString("unidade"));
                imp.setNcm(rst.getString("ncm"));
                imp.setMargem(rst.getDouble("taxa"));
                imp.setCustoComImposto(rst.getDouble("custo"));
                imp.setCustoSemImposto(imp.getCustoComImposto());
                imp.setPrecovenda(rst.getDouble("venda"));
                imp.setPiscofinsCstDebito(7);
                imp.setPiscofinsCstCredito(71);
                imp.setPiscofinsNaturezaReceita(999);
                imp.setIcmsCst(60);
                imp.setIcmsAliq(0);
                imp.setIcmsReducao(0);
                vResult.add(imp);
            }
        }
        return vResult;
    }
    
    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigo, nome, fantasia, datacad, endereco,\n"
                    + "	bairro, cidade, cep, estado, cnpj, ie, cic, ie,\n"
                    + "	contato, fone, tipo, fone1, tipo1, fone2, tipo2,\n"
                    + "	email, pagina, obs1\n"
                    + "from \n"
                    + "	dbmercado.fornece"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return null;
    }
    
    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> vResult = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	codigo, nome, fantasia, dataniver, datacad, ecivil,\n"
                    + "	sexo, naciona, endereco, bairro, cidade, cep, estado, \n"
                    + "	cnpj, ie, cic, rg, contato, fone, tipo, fone1, tipo1,\n"
                    + "	fone2, tipo2, email, pagina, pai, mae, obs1, obs, limite,\n"
                    + "	refe, obs2 \n"
                    + " from \n"
                    + " 	dbmercado.cliente"
            )) {
                while (rst.next()) {
                    
                }
            }
        }
        return null;
    }
}
