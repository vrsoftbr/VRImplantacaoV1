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
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class SuperLoja10DAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SuperLoja10";
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select GRUPO, DESCRICAO from GRUPO_ESTOQUE order by GRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("GRUPO"));
                    imp.setMerc1Descricao(rst.getString("DESCRICAO"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(imp.getMerc1Descricao());
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc1Descricao());
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.PRODUTO,\n"
                    + "p.DESCRICAO,\n"
                    + "p.REDUZIDO,\n"
                    + "p.UNIDADEMEDIDA,\n"
                    + "p.GRUPOESTOQUE,\n"
                    + "p.NCM,\n"
                    + "p.TIPOTRIBUTACAO,\n"
                    + "p.EAN13,\n"
                    + "p.INATIVO,\n"
                    + "p.PESO_BRUTO,\n"
                    + "p.PESO_LIQUIDO,\n"
                    + "p.EMBALAGEM,\n"
                    + "p.CADASTRO,\n"
                    + "p.TRIBUTACAO_PIS_COFINS,\n"
                    + "pis.PIS_CST,\n"
                    + "pis.ENTRADA_PIS_CST,\n"
                    + "pis.PIS_DETALHEMENTO,\n"
                    + "p.PRECO_CUSTO,\n"
                    + "p.ICMS_ALIQUOTA,\n"
                    + "p.ICMS_CST,\n"
                    + "p.VALIDADE,\n"
                    + "p.ESTOQUE_MINIMO,\n"
                    + "p.PESAVEL,\n"
                    + "p.CEST\n"
                    + "from PRODUTOS p\n"
                    + "left join TRIBUTACAO_PIS_COFINS pis on pis.TIPO = p.TRIBUTACAO_PIS_COFINS\n"
                    + "order by p.PRODUTO;"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.FORNECEDOR as ID,  \n"
                    + "p.INSCRICAO as CNPJ,\n"
                    + "p.NOME,\n"
                    + "p.LOGRADOURO,\n"
                    + "p.ENDERECO,\n"
                    + "p.NUM,\n"
                    + "p.BAIRRO,\n"
                    + "p.CODMUNICIPIO,\n"
                    + "p.CIDADE,\n"
                    + "p.UF,\n"
                    + "p.CEP,\n"
                    + "p.FONE,\n"
                    + "p.EMAIL,\n"
                    + "p.COMPLEMENTO,\n"
                    + "p.IE,\n"
                    + "p.IDENTIDADE_NUMERO,\n"
                    + "p.IDENTIDADE_ORGAO,\n"
                    + "p.IDENTIDADE_EMISSAO,\n"
                    + "p.IDENTIDADE_UF,\n"
                    + "p.CADASTRO,\n"
                    + "p.INSCRICAO_RURAL,\n"
                    + "p.INSCRICAO_MUNICIPAL,\n"
                    + "p.PAIS,\n"
                    + "p.CELULAR,\n"
                    + "p.RAZAO_SOCIAL,\n"
                    + "pt.TELEFONE,\n"
                    + "pt.CONTATO_NOME,\n"
                    + "f.INATIVO,\n"
                    + "f.DIA_FIXO_PAGAMENTO,\n"
                    + "f.PRODUTOR_RURAL\n"
                    + "from pessoas p\n"
                    + "inner JOIN FORNECEDORES f on f.INSCRICAO = p.INSCRICAO\n"
                    + "left join PESSOAS_TELEFONE pt on pt.INSCRICAO = p.INSCRICAO\n"
                    + "order by p.INSCRICAO;"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "FORNECEDOR,\n"
                    + "PRODUTO,\n"
                    + "CODIGO as CODIGOEXTERNO\n"
                    + "from FORNECEDORES_PRODUTO"
            )) {

            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "c.CLIENTE as ID,\n"
                    + "p.INSCRICAO as CNPJ,\n"
                    + "p.NOME,\n"
                    + "p.LOGRADOURO,\n"
                    + "p.ENDERECO,\n"
                    + "p.NUM,\n"
                    + "p.BAIRRO,\n"
                    + "p.CODMUNICIPIO,\n"
                    + "p.CIDADE,\n"
                    + "p.UF,\n"
                    + "p.CEP,\n"
                    + "p.FONE,\n"
                    + "p.EMAIL,\n"
                    + "p.COMPLEMENTO,\n"
                    + "p.IE,\n"
                    + "p.IDENTIDADE_NUMERO,\n"
                    + "p.IDENTIDADE_ORGAO,\n"
                    + "p.IDENTIDADE_EMISSAO,\n"
                    + "p.IDENTIDADE_UF,\n"
                    + "p.CADASTRO,\n"
                    + "p.INSCRICAO_RURAL,\n"
                    + "p.INSCRICAO_MUNICIPAL,\n"
                    + "p.ESTADO_CIVIL,\n"
                    + "p.NOME_MAE,\n"
                    + "p.NOME_PAI,\n"
                    + "p.PAIS,\n"
                    + "p.NASCIMENTO,\n"
                    + "p.CELULAR,\n"
                    + "p.RAZAO_SOCIAL,\n"
                    + "pt.TELEFONE,\n"
                    + "pt.CONTATO_NOME,\n"
                    + "c.INATIVO,\n"
                    + "c.LIMITE_CREDITO,\n"
                    + "c.OBSERVACOES,\n"
                    + "c.DIA_FIXO_PAGAMENTO\n"
                    + "from pessoas p\n"
                    + "inner JOIN CLIENTES c on c.INSCRICAO = p.INSCRICAO\n"
                    + "left join PESSOAS_TELEFONE pt on pt.INSCRICAO = p.INSCRICAO\n"
                    + "order by p.INSCRICAO;"
            )) {
                while (rst.next()) {

                }
            }
        }
        return null;
    }
}
