/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Desenvolvimento
 */
public class GatewaySistemasDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Gateway Sistemas";
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	e.CODIGO  AS id,\n"
                    + "	e.BARRAS  AS ean,\n"
                    + "	e.PADRAO_BARRAS AS tipo,\n"
                    + "	e.UND_C  AS tipoembalagemcotacao,\n"
                    + "	e.UND_V  AS tipoembalagem,\n"
                    + "	e.ATIVO AS situacaocadastro,\n"
                    + "	e.NOME AS descricaocompleta,\n"
                    + "	e.QTD AS estoque,\n"
                    + "	e.QTD_MAXIMA AS estoquemaxima,\n"
                    + "	e.QTD_MINIMA AS estoqueminima,\n"
                    + "	e.PRECO_CUSTO AS custo,\n"
                    + "	e.PRECO_VENDA AS precovenda,\n"
                    + "	e.PESO_BRUTO AS pesobruto,\n"
                    + "	e.PESO_LIQUIDO AS pesoliquido,\n"
                    + "	e.NCM,\n"
                    + "	et.CEST AS cest,	\n"
                    + "	et.TIPO_TRIBUTACAO,\n"
                    + "	et.ST AS cst,\n"
                    + "	et.ICMS AS icms,\n"
                    + "	et.REDUCAO AS reducao,\n"
                    + "	et.MVA AS mva,\n"
                    + "	et.PIS_ST AS cstpis,\n"
                    + "	et.COFINS_ST AS cstcofins,	\n"
                    + "	et.ALIQ_FCP AS fcp\n"
                    + "FROM ESTOQUE e\n"
                    + "LEFT JOIN EST_TRIBUTACAO et ON et.CODIGO = e.CODIGO\n"
                    + "WHERE e.BARRAS = '7891000100103'\n"
                    + "ORDER BY 1"
            )) {

            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.CODIGO AS id,\n"
                    + "	f.nome AS razao,\n"
                    + "	COALESCE(f.FANTASIA, f.NOME) AS fantasia,\n"
                    + "	f.CNPJ AS cnpj,\n"
                    + "	f.IE AS ie_rg,\n"
                    + "	f.CPF AS cpf,\n"
                    + "	f.RG AS rg,\n"
                    + "	f.ENDERECO AS enderenco,\n"
                    + "	f.NUMERO AS numero,\n"
                    + "	f.COMPLEMENTO AS complemento,\n"
                    + "	f.BAIRRO AS bairro,\n"
                    + "	f.CEP AS cep,\n"
                    + "	f.UF AS uf,\n"
                    + "	f.CIDADE AS municipio,\n"
                    + "	f.COD_CIDADE AS muinicioibge,\n"
                    + "	f.TELEFONE AS telefone,\n"
                    + "	f.CELULAR AS celular,\n"
                    + "	f.FAX AS fax,\n"
                    + "	f.EMAIL AS email,\n"
                    + "	f.SITE AS site\n"
                    + "FROM FORNECEDORES f \n"
                    + "ORDER BY 1"
            )) {

            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	c.CODIGO AS id,\n"
                    + "	c.NOME AS razao,\n"
                    + "	COALESCE(c.FANTASIA, c.NOME) AS fantasia,\n"
                    + "	c.CNPJ AS cnpj,\n"
                    + "	c.IE AS inscricaoestadual,\n"
                    + "	c.CPF AS cpf,\n"
                    + "	c.RG AS rg,\n"
                    + "	c.ATIVO AS ativo,\n"
                    + "	c.ENDERECO AS endereco,\n"
                    + "	c.NUMERO AS numero,\n"
                    + "	c.COMPLEMENTO AS complemento,\n"
                    + "	c.BAIRRO AS bairro,\n"
                    + "	c.CEP AS cep,\n"
                    + "	c.UF AS uf,\n"
                    + "	c.CIDADE AS municipio,\n"
                    + "	c.COD_CIDADE AS municipioibge,\n"
                    + "	c.TELEFONE AS telefone,\n"
                    + "	c.CELULAR AS celular,\n"
                    + "	c.FAX AS fax,\n"
                    + "	c.EMAIL AS email,\n"
                    + "	c.OBSERVACOES AS obs,\n"
                    + "	c.NOME_MAE AS nomemae,\n"
                    + "	c.NOME_PAI AS nomepai,\n"
                    + "	c.CONJUGUE AS nomeconjuge,\n"
                    + "	c.PROFISSAO AS cargo,\n"
                    + "	c.NASCIMENTO AS datanascimento,\n"
                    + "	c.LIMITE_CREDITO AS valorlimite,\n"
                    + "	c.DATA_CADASTRO AS datacadastro,\n"
                    + "	c.BLOQUEADO AS bloqueado\n"
                    + "FROM CLIENTES c \n"
                    + "ORDER BY 1"
            )) {

            }
        }
        return result;
    }

}
