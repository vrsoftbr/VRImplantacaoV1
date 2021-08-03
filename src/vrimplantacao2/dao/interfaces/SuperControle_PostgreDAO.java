package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoPostgres;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

public class SuperControle_PostgreDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "SuperControle";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                new OpcaoProduto[]{
                    OpcaoProduto.FAMILIA,
                    OpcaoProduto.FAMILIA_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO_PRODUTO,
                    OpcaoProduto.MERCADOLOGICO,
                    OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                    OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                    OpcaoProduto.PRODUTOS,
                    OpcaoProduto.EAN,
                    OpcaoProduto.EAN_EM_BRANCO,
                    OpcaoProduto.DATA_CADASTRO,
                    OpcaoProduto.TIPO_EMBALAGEM_EAN,
                    OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                    OpcaoProduto.PESAVEL,
                    OpcaoProduto.VALIDADE,
                    OpcaoProduto.DESC_COMPLETA,
                    OpcaoProduto.DESC_GONDOLA,
                    OpcaoProduto.DESC_REDUZIDA,
                    OpcaoProduto.ESTOQUE_MAXIMO,
                    OpcaoProduto.ESTOQUE_MINIMO,
                    OpcaoProduto.PRECO,
                    OpcaoProduto.CUSTO,
                    OpcaoProduto.ESTOQUE,
                    OpcaoProduto.ATIVO,
                    OpcaoProduto.NCM,
                    OpcaoProduto.CEST,
                    OpcaoProduto.PIS_COFINS,
                    OpcaoProduto.NATUREZA_RECEITA,
                    OpcaoProduto.ICMS,
                    OpcaoProduto.ICMS_SAIDA,
                    OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                    OpcaoProduto.ICMS_SAIDA_NF,
                    OpcaoProduto.ICMS_ENTRADA,
                    OpcaoProduto.ICMS_CONSUMIDOR,
                    OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                    OpcaoProduto.PAUTA_FISCAL,
                    OpcaoProduto.PAUTA_FISCAL_PRODUTO,
                    OpcaoProduto.EXCECAO,
                    OpcaoProduto.MARGEM
                }
        ));
    }
    
    public List<Estabelecimento> getLojaCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	\"Id\" as id,\n"
                    + "	\"Descricao\" as descricao\n"
                    + "from dbo.\"Loja\"\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("id"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	m1.\"Id\" as merc1,\n"
                    + "	m1.\"Descricao\" as merc1_descricao,\n"
                    + "	m2.\"Id\" as merc2,\n"
                    + "	m2.\"Descricao\" as merc2_descricao,\n"
                    + "	m3.\"Id\" as merc3,\n"
                    + "	m3.\"Descricao\" as merc3_descricao\n"
                    + "from dbo.\"Departamento\" m1\n"
                    + "join dbo.\"Secao\" m2 on m2.\"Departamento_Id\" = m1.\"Id\"\n"
                    + "join dbo.\"Categoria\" m3 on m3.\"Secao_Id\" = m2.\"Id\"\n"
                    + "order by 1, 3, 5"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_descricao"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_descricao"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	\"Id\" as id,\n"
                    + "	\"Descricao\" as descricao\n"
                    + "from dbo.\"Familia\"\n"
                    + "order by 1"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	p.\"Id\" as id,\n"
                    + "	p.\"EAN\" as ean,\n"
                    + "	p.\"Descricao\" as descricaocompleta,\n"
                    + "	p.\"DescricaoReduzida\" as descricaoreduzida,\n"
                    + "	p.\"Unidade\" as tipoembalagem,\n"
                    + "	p.\"DeBalanca\" as balanca,\n"
                    + "	p.\"BalancaValidade\" as validade,\n"
                    + "	p.\"Unitario\",\n"
                    + "	p.\"Volume\",\n"
                    + "	p.\"Peso\" as peso,\n"
                    + "	p.\"FkDepartamento\" as mercadologico1,\n"
                    + "	p.\"FkSecao\" as mercadologico2,\n"
                    + "	p.\"FkCategoria\" as mercadologico3,\n"
                    + " p.\"FkFamilia\" as idfamiliaproduto,\n"
                    + "	p.\"DtCadastro\" as datacadastro,\n"
                    + "	p.\"Ativo\" as situacaocadastro,\n"
                    + "	p.\"NCM\" as ncm,\n"
                    + "	p.\"Cest\" as cest,\n"
                    + "	p.\"TribPIS\" as cstpiscofinssaida,\n"
                    + "	p.\"TribPISEntrada\" as cstpiscofinsentrada,\n"
                    + "	p.\"TribICMS\" as csticms,\n"
                    + "	p.\"AliqICMS\" as aliquotaicms,\n"
                    + "	p.\"ReducaoBC\" as reducaoicms,\n"
                    + "	p.\"Pauta\" as pauta,\n"
                    + "	p.\"MVA\" as mva\n"
                    + "from dbo.\"Produto\" p\n"
                    + "order by 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.seteBalanca(rst.getBoolean("balanca"));
                    imp.setValidade(rst.getInt("validade"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setIdFamiliaProduto(rst.getString("idfamiliaproduto"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSituacaoCadastro(rst.getBoolean("situacaocadastro") ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cstpiscofinssaida"));
                    imp.setPiscofinsCstCredito(rst.getString("cstpiscofinsentrada"));
                    imp.setIcmsCstSaida(rst.getInt("csticms"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliquotaicms"));
                    imp.setIcmsReducaoSaida(rst.getDouble("reducaoicms"));
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("csticms"));
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("aliquotaicms"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("reducaoicms"));
                    imp.setIcmsCstSaidaForaEstadoNF(rst.getInt("csticms"));
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("aliquotaicms"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("reducaoicms"));                    
                    imp.setIcmsCstEntrada(rst.getInt("csticms"));
                    imp.setIcmsAliqEntrada(rst.getDouble("aliquotaicms"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("reducaoicms"));
                    imp.setIcmsCstEntradaForaEstado(rst.getInt("csticms"));
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("aliquotaicms"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("reducaoicms"));
                    imp.setIcmsCstConsumidor(rst.getInt("csticms"));
                    imp.setIcmsAliqConsumidor(rst.getDouble("aliquotaicms"));
                    imp.setIcmsReducaoConsumidor(rst.getDouble("reducaoicms"));
                    
                    result.add(imp);                    
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	\"Produto_Id\" as idproduto,\n"
                    + "	\"EAN\" as ean,\n"
                    + "	\"Quantidade\" as qtdembalagem\n"
                    + "from dbo.\"EanAfiliado\"\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	f.\"Id\" as id,\n"
                    + "	f.\"RazaoSocial\" as razao,\n"
                    + "	f.\"NomeFantasia\" as fantasia,\n"
                    + "	f.\"Cnpj\" as cnpj,\n"
                    + "	f.\"IE\" as inscricaoestadual,\n"
                    + "	f.\"IM\" as inscricaomunicipal,\n"
                    + "	f.\"DtCadastro\" as datacadastro,\n"
                    + "	f.\"Ativo\" as ativo,\n"
                    + "	f.\"Observacao\" as observacao,\n"
                    + "	e.\"Logradouro\" as endereco,\n"
                    + "	e.\"Numero\" as numero,\n"
                    + "	e.\"Complemento\" as complemento,\n"
                    + "	e.\"Bairro\" as bairro,\n"
                    + "	e.\"Municipio\" as municipio,\n"
                    + "	e.\"MunicipioCodigo\" as municipioibge,\n"
                    + "	e.\"UF\" as uf,\n"
                    + "	trim(t.\"DDD\"||' '||t.\"Numero\") as telefone,\n"
                    + "	t.\"Contato\" as contato,\n"
                    + "	em.email\n"
                    + "from dbo.\"Entidade\" f\n"
                    + "left join dbo.\"Endereco\" e on e.\"Entidade_Id\" = f.\"Id\"\n"
                    + "left join dbo.\"Fone\" t on t.\"Entidade_Id\" = f.\"Id\"\n"
                    + "left join dbo.\"Email\" em on em.\"Entidade_Id\" = f.\"Id\"\n"
                    + "where f.\"TipoEntidade\" = 2\n"
                    + "order by 1"
            )) {

            }
        }
        return null;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	c.\"Id\" as id,\n"
                    + "	c.\"RazaoSocial\" as razao,\n"
                    + "	c.\"NomeFantasia\" as fantasia,\n"
                    + "	c.\"Cnpj\" as cnpj,\n"
                    + "	c.\"IE\" as inscricaoestadual,\n"
                    + "	c.\"IM\" as inscricaomunicipal,\n"
                    + "	c.\"DtCadastro\" as datacadastro,\n"
                    + "	c.\"DtNascimento\" as datanascimento,\n"
                    + "	c.\"Ativo\" as ativo,\n"
                    + "	c.\"Observacao\" as observacao,\n"
                    + "	e.\"Logradouro\" as endereco,\n"
                    + "	e.\"Numero\" as numero,\n"
                    + "	e.\"Complemento\" as complemento,\n"
                    + "	e.\"Bairro\" as bairro,\n"
                    + "	e.\"Municipio\" as municipio,\n"
                    + "	e.\"MunicipioCodigo\" as municipioibge,\n"
                    + "	e.\"UF\" as uf,\n"
                    + "	trim(t.\"DDD\"||' '||t.\"Numero\") as telefone,\n"
                    + "	t.\"Contato\" as contato,\n"
                    + "	em.email		\n"
                    + "from dbo.\"Entidade\" c\n"
                    + "left join dbo.\"Endereco\" e on e.\"Entidade_Id\" = c.\"Id\"\n"
                    + "left join dbo.\"Fone\" t on t.\"Entidade_Id\" = c.\"Id\"\n"
                    + "left join dbo.\"Email\" em on em.\"Entidade_Id\" = c.\"Id\"\n"
                    + "where c.\"TipoEntidade\" = 1\n"
                    + "order by 1"
            )) {

            }
        }
        return null;
    }
}
