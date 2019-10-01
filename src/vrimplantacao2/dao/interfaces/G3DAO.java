/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class G3DAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "G3";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet(Arrays.asList(new OpcaoProduto[]{
            OpcaoProduto.IMPORTAR_MANTER_BALANCA,
            OpcaoProduto.PRODUTOS,
            OpcaoProduto.ATIVO,
            OpcaoProduto.DESC_COMPLETA,
            OpcaoProduto.DESC_GONDOLA,
            OpcaoProduto.DESC_REDUZIDA,
            OpcaoProduto.DATA_CADASTRO,
            OpcaoProduto.EAN,
            OpcaoProduto.EAN_EM_BRANCO,
            OpcaoProduto.TIPO_EMBALAGEM_EAN,
            OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
            OpcaoProduto.CUSTO,
            OpcaoProduto.MARGEM,
            OpcaoProduto.PRECO,
            OpcaoProduto.ESTOQUE,
            OpcaoProduto.PESAVEL,
            OpcaoProduto.NCM,
            OpcaoProduto.CEST,
            OpcaoProduto.ICMS,
            OpcaoProduto.PIS_COFINS,
            OpcaoProduto.NATUREZA_RECEITA,
            OpcaoProduto.ATACADO,
            OpcaoProduto.VALIDADE,
            OpcaoProduto.MERCADOLOGICO,
            OpcaoProduto.MERCADOLOGICO_PRODUTO,}));
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	id, descricao, codigo \n"
                    + "FROM grupo\n"
                    + "ORDER BY id"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("id"));
                    imp.setMerc1Descricao(rst.getString("descricao"));
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
                    "SELECT \n"
                    + "	p.ID AS id,\n"
                    + "	p.descricao AS descricaocompleta,\n"
                    + "	p.DESCRICAO_PDV AS descricaoreduzida,\n"
                    + "	p.ID_GRUPO AS mercadologico,\n"
                    + "	p.lucro AS margem,\n"
                    + "	p.valor_compra AS custosemimposto,\n"
                    + "	p.valor_custo AS custocomimposto,\n"
                    + "	p.VALOR_VENDA AS precovenda,\n"
                    + "	p.DATA_CADASTRO AS datacadastro,\n"
                    + "	p.ECF_ICMS_ST AS aliquotaconsumidor,\n"
                    + "	p.ESTOQUE_MAX AS estoquemaximo,\n"
                    + "	p.ESTOQUE_MIN AS estoqueminimo,\n"
                    + "	p.QTD_ESTOQUE AS estoque,\n"
                    + "	p.GTIN,\n"
                    + "	p.EAN,\n"
                    + "	u.NOME AS tipoembalagem,\n"
                    + "	p.NCM AS ncm,\n"
                    + "	p.CEST AS cest,\n"
                    + "	p.CST_PIS_SAIDA,\n"
                    + "	p.CST_PIS_ENTRADA,\n"
                    + "	p.CST_COFINS_SAIDA,\n"
                    + "	p.CST_COFINS_ENTRADA,\n"
                    + "	p.cod_nat_rec AS naturezareceita,\n"
                    + "	p.COD_CST_DENTRO,\n"
                    + "	p.COD_CST_FORA,\n"
                    + "	p.ALIQUOTA_ICMS_DENTRO,\n"
                    + "	p.ALIQUOTA_ICMS_FORA,\n"
                    + "	p.ALIQUOTA_ICMS_ST_DENTRO,\n"
                    + "	p.ALIQUOTA_ICMS_ST_FORA,\n"
                    + "	p.REDUCAO_BC_DENTRO,\n"
                    + "	p.REDUCAO_BC_FORA,\n"
                    + "	p.REDUCAO_BC_ST_DENTRO,\n"
                    + "	p.REDUCAO_BC_ST_FORA,\n"
                    + "	case p.EXCLUIDO when 0 then 'ATIVO' ELSE 'EXCLUIDO' end situacaocadastro\n"
                    + "FROM produto p\n"
                    + "LEFT JOIN unidade_produto u ON u.ID = p.ID_UNIDADE_PRODUTO\n"
                    + "ORDER BY p.ID"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                }
            }
        }
        return null;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opt) throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        if (opt == OpcaoProduto.ATACADO) {
            try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "SELECT \n"
                        + "	id, \n"
                        + "	qtd_atacado,\n"
                        + "	valor_venda_atacado,\n"
                        + "	valor_venda\n"
                        + "FROM produto \n"
                        + "WHERE qtd_atacado > 1\n"
                        + "AND coalesce(valor_venda_atacado, 0) > 0"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("id"));
                        imp.setQtdEmbalagem(rst.getInt("qtd_atacado"));
                        imp.setPrecovenda(rst.getDouble("valor_venda"));
                        imp.setAtacadoPreco(rst.getDouble("valor_venda_atacado"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + "	f.id_fornecedor,\n"
                    + "	f.razao_social,\n"
                    + "	f.nome_fantasia,\n"
                    + "	f.numero_documento AS cnpj,\n"
                    + "	f.ie,	\n"
                    + "	f.endereco,\n"
                    + "	f.numero,\n"
                    + "	f.complemento,\n"
                    + "	f.bairro,\n"
                    + "	f.cep,\n"
                    + "	f.municipio,\n"
                    + "	f.codigo_municipio,\n"
                    + "	f.uf,\n"
                    + "	f.contato,\n"
                    + "	f.email,\n"
                    + "	f.fax,\n"
                    + "	f.telefone,\n"
                    + "	f.ativo,\n"
                    + "	f.data_cadastro\n"
                    + "FROM fornecedor f\n"
                    + "ORDER BY f.id_fornecedor"
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
                    "SELECT \n"
                    + "	id_fornecedor,\n"
                    + "	id_produto,\n"
                    + "	codigo_produto,\n"
                    + "	codigo_produto_fornecedor\n"
                    + "FROM fornecedor_produto\n"
                    + "ORDER BY id_fornecedor, id_produto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigo_produto_fornecedor"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
