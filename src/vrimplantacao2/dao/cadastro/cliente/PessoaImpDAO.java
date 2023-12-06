/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.cadastro.cliente;

import java.sql.Statement;
import java.util.LinkedHashMap;
import java.util.Map;
import vr.core.collection.Properties;
import vr.implantacao.main.App;
import vrframework.classe.Conexao;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.vo.importacao.PessoaImp;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Michael-Oliveira
 */
public class PessoaImpDAO {
    
    ConexaoPostgres con = new ConexaoPostgres();
    Properties prop = App.properties();
    private String ip = prop.get("database.ip");
    private int porta = Integer.parseInt(prop.get("database.porta"));

    void salvar(PessoaImp cliente) throws Exception {
        abrirConexao();
        try (Statement stm = con.getConexao().createStatement()) {
            SQLBuilder sql = new SQLBuilder();
            sql.setTableName("pessoaimp");
            sql.put("cnpj_loja_mercado", cliente.getCnpjLojaMercado());
            sql.put("id", cliente.getImpid());
            sql.put("cnpj", cliente.getCnpj());
            sql.put("inscricaoestadual", cliente.getInscricaoestadual());
            sql.put("orgaoemissor", cliente.getOrgaoemissor());
            sql.put("razao", cliente.getRazao());
            sql.put("fantasia", cliente.getFantasia());
            sql.put("ativo", cliente.getAtivo());
            sql.put("bloqueado", cliente.getBloqueado());
            sql.put("databloqueio ", cliente.getDatabloqueio());
            sql.put("endereco", cliente.getEndereco());
            sql.put("numero", cliente.getNumero());
            sql.put("complemento", cliente.getComplemento());
            sql.put("bairro", cliente.getBairro());
            sql.put("municipioibge", cliente.getMunicipioibge());
            sql.put("municipio", cliente.getMunicipio());
            sql.put("ufibge", cliente.getUfibge());
            sql.put("uf", cliente.getUf());
            sql.put("cep", cliente.getCep());
            sql.put("estadocivil", cliente.getEstadocivil());
            sql.put("datanascimento", cliente.getDatanascimento());
            sql.put("datacadastro", cliente.getDatacadastro());
            sql.put("sexo", cliente.getSexo());
            sql.put("empresa", cliente.getEmpresa());
            sql.put("empresaendereco", cliente.getEmpresaendereco());
            sql.put("empresanumero", cliente.getEmpresanumero());
            sql.put("empresacomplemento", cliente.getEmpresacomplemento());
            sql.put("empresabairro", cliente.getEmpresabairro());
            sql.put("empresamunicipioibge", cliente.getEmpresamunicipioibge());
            sql.put("empresamunicipio", cliente.getEmpresamunicipio());
            sql.put("empresaufibge", cliente.getEmpresaufibge());
            sql.put("empresauf", cliente.getEmpresauf());
            sql.put("empresacep", cliente.getEmpresacep());
            sql.put("empresatelefone", cliente.getEmpresatelefone());
            sql.put("dataadmissao", cliente.getDataadmissao());
            sql.put("cargo", cliente.getCargo());
            sql.put("salario", cliente.getSalario());
            sql.put("valorlimite", cliente.getValorlimite());
            sql.put("nomeconjuge", cliente.getNomeconjuge());
            sql.put("nomepai", cliente.getNomepai());
            sql.put("nomemae", cliente.getNomemae());
            sql.put("observacao", cliente.getObservacao());
            sql.put("diavencimento", cliente.getDiavencimento());
            sql.put("permitecreditorotativo", cliente.getPermitecreditorotativo());
            sql.put("permitecheque", cliente.getPermitecheque());
            sql.put("telefone", cliente.getTelefone());
            sql.put("celular", cliente.getCelular());
            sql.put("email", cliente.getEmail());
            sql.put("fax", cliente.getFax());
            sql.put("cobrancatelefone", cliente.getCobrancatelefone());
            sql.put("prazopagamento", cliente.getPrazopagamento());
            sql.put("cobrancaendereco", cliente.getCobrancaendereco());
            sql.put("cobrancanumero", cliente.getCobrancanumero());
            sql.put("cobrancacomplemento", cliente.getCobrancacomplemento());
            sql.put("cobrancabairro", cliente.getCobrancabairro());
            sql.put("cobrancamunicipioibge", cliente.getCobrancamunicipioibge());
            sql.put("cobrancamunicipio", cliente.getCobrancamunicipio());
            sql.put("cobrancaufibge", cliente.getCobrancaufibge());
            sql.put("cobrancauf", cliente.getCobrancauf());
            sql.put("cobrancacep", cliente.getCobrancacep());
            sql.put("tipoorgaopublico", cliente.getTipoorgaopublico());
            sql.put("limitecompra", cliente.getLimitecompra());
            sql.put("inscricaomunicipal", cliente.getInscricaomunicipal());
            sql.put("tipoindicadorie", cliente.getTipoindicadorie());
            sql.put("fornecedor", cliente.getFornecedor());
            sql.put("conveniado", cliente.getConveniado());

            try {
                stm.execute(sql.getInsert());
            } catch (Exception e) {
                System.out.println(sql.getInsert());
                e.printStackTrace();
                throw e;
            }            
        }
        fecharConexao();
    }
    
    public void abrirConexao() throws Exception {
        con.abrirConexao(ip, porta, "implantacao", "postgres", "VrPost@Server");
    }

    public void fecharConexao() throws Exception {
        con.close();
    }
}