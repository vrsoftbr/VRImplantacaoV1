/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Lucas
 */
public class VisualMixDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "VisualMix";
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	al.CODIGO as id, \n"
                    + "	al.DESCRICAO as descricao, "
                    + "	al.SITUACAOTRIBUTARIA as cst,\n"
                    + " al.PERCENTUAL as aliquota, \n"
                    + "	al.REDUCAO as reducao \n"
                    + "from dbo.Aliquotas_NF al\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliquota"),
                            rst.getDouble("reducao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "f.Codigo, "
                    + "f.Descricao "
                    + "from dbo.Grupo_Precos f "
                    + "order by Codigo"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("Codigo"));
                    imp.setDescricao(rst.getString("Descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {

            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "Mercadologico1 as merc1, "
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 1\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();

                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("descricao"));

                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "Mercadologico1 as merc1, \n"
                    + "Mercadologico2 as merc2, \n"
                    + "Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 2\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc2 = merc.get(rst.getString("merc1"));
                    if (merc2 != null) {
                        merc2.addFilho(
                                rst.getString("merc2"),
                                rst.getString("descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	Mercadologico1 as merc1,\n"
                    + "	Mercadologico2 as merc2, \n"
                    + "	Mercadologico3 as merc3, \n"
                    + "	Descricao as descricao \n"
                    + "from dbo.Mercadologicos\n"
                    + "where Nivel = 3\n"
                    + "order by 1, 2, 3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("descricao")
                            );
                        }
                    }
                }
            }
        }
        return new ArrayList<>(merc.values());
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	p.Produto_Id as id,\n"
                    + " (cast(ean.Codigo_Automacao as varchar) + cast(ean.Digito_Automacao as varchar)) as ean,\n"
                    + " p.Peso_Variavel,\n"
                    + " p.Pre_Pesado,\n"
                    + " p.Qtd_Decimal,\n"
                    + " p.ProdutoPai,\n"
                    + "	p.Descricao_Completa as descricaocompleta, \n"
                    + " p.Descricao_Reduzida as descricaoreduzida, \n"
                    + " p.Descricao_Balanca,\n"
                    + "	est.Custo_Ultima_Entrada_Com_Icms as custocomimposto,\n"
                    + " est.Custo_Ultima_Entrada_Sem_Icms as custosemimposto,\n"
                    + "	pre.preco_venda as precovenda,\n"
                    + " p.Margem_Atacado, \n"
                    + " p.Margem_Teorica, \n"
                    + " p.MargemFixa, \n"
                    + " p.Aliquota, \n"
                    + " p.Aliquota_FCP, \n"
                    + " p.Aliquota_Interna, \n"
                    + " p.Aliquota_NF,\n"
                    + " f.Codigo as idfamiliaproduto,\n"
                    + "	p.Mercadologico1, \n"
                    + " p.Mercadologico2, \n"
                    + " p.Mercadologico3, \n"
                    + " p.Mercadologico4, \n"
                    + " p.Mercadologico5, \n"
                    + " p.Situacao as situacaocadastro,\n"
                    + "	p.SituacaoTributaria as csticms, \n"
                    + " est.EstoqueInicial as estoque, \n"
                    + " p.Estoque_Minimo, \n"
                    + " p.Estoque_Maximo, \n"
                    + " p.EspecUnitariaTipo as tipoembalagem, \n"
                    + " p.EspecUnitariaQtde as qtdembalagem,\n"
                    + "	p.TipoProduto, \n"
                    + " p.Codigo_NCM as ncm, \n"
                    + " p.CEST as cest, \n"
                    + " p.TipoCodMercad as tipomercadoria,\n"
                    + "	p.CstPisCofinsEntrada, \n"
                    + " p.CstPisCofinsSaida, \n"
                    + " p.NaturezaReceita,\n"
                    + " p.Fabricante as idfabricante\n"
                    + "from dbo.Produtos p\n"
                    + "left join dbo.Precos_Loja pre on pre.produto_id = p.Produto_Id\n"
                    + "	and pre.loja = " + getLojaOrigem() + " and pre.sequencia = 1\n"
                    + "left join dbo.Produtos_Estoque est on est.Produto_Id = p.Produto_Id\n"
                    + "	and est.Loja = " + getLojaOrigem() + "\n"
                    + "left join dbo.Automacao ean on ean.Produto_Id = p.Produto_Id\n"
                    + "left join dbo.Grupo_Precos_Produtos f on f.Produto_Id = p.Produto_Id\n"
                    + "order by p.Produto_Id"
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.Codigo as id, "
                    + " f.Tipo, "
                    + " tf.Descricao as tipofornecedor, "
                    + " f.RazaoSocial as razao, "
                    + " f.NomeFantasia as fantasia,\n"
                    + "	f.TipoLogradouro, "
                    + " f.Endereco, "
                    + " f.NumeroEnd as numero, "
                    + " f.Complemento, "
                    + " f.Bairro, "
                    + " f.Cidade as municipio, "
                    + " f.Estado as uf, \n"
                    + "	f.Cep, "
                    + " f.CxPostal as caixapostal, "
                    + " f.Telefone, "
                    + " f.Fax, "
                    + " f.Telex, "
                    + " f.TeleContato, "
                    + " f.Contato, "
                    + " f.TeleContato, \n"
                    + "	f.CGC as cnpj, "
                    + " f.InscricaoEstadual as ie, "
                    + " f.PrazoEntrega, "
                    + " f.DataCadastro, \n"
                    + "	f.CondicaoPagto, "
                    + " cp.Descricao as condicaopagamento, "
                    + " cp.Qtd_Parcelas, "
                    + " f.Observacao,  \n"
                    + "	f.Supervisor, "
                    + " f.CelSupervisor, "
                    + " f.EmailSupervisor, "
                    + " f.TelSupervisor, "
                    + " f.Email, "
                    + " f.Vendedor, "
                    + " f.TelVendedor, "
                    + " f.CelVendedor,\n"
                    + "	f.EmailVendedor, "
                    + " f.Gerente, "
                    + " f.TelGerente, "
                    + " f.CelGerente, "
                    + " f.EmailGerente,\n"
                    + "	f.Situacao, "
                    + " f.Status\n"
                    + "from dbo.Fornecedores f\n"
                    + "left join dbo.Condicoes_Pagto cp on cp.Codigo = f.CondicaoPagto\n"
                    + "left join dbo.TipoFornecedor tf on tf.Tipo = f.Tipo\n"
                    + "order by f.Codigo"
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

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	pf.Fornecedor as idfornecedor,\n"
                    + "	pf.Produto_Id as idproduto,\n"
                    + "	pf.Referencia as codigoexterno,\n"
                    + "	pf.Qtde_Emb as qtdembalagem,\n"
                    + "	pf.Preco_Tabela as custo\n"
                    + "from dbo.Produtos_Fornecedor pf\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setIdFornecedor(rst.getString("idfornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
                    imp.setQtdEmbalagem(rst.getDouble("qtdembalagem"));
                    imp.setCustoTabela(rst.getDouble("custo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
