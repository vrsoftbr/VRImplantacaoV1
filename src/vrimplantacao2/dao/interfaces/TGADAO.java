package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Importacao
 */
public class TGADAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "TGA";
    }

    public List<Estabelecimento> getLojas() throws SQLException {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codempresa,\n"
                    + "    nomefantasia || ' - ' || cgc fantasia\n"
                    + "from\n"
                    + "    gempresa")) {
                while (rs.next()) {
                    result.add(new Estabelecimento(rs.getString("codempresa"), rs.getString("fantasia")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codsit,\n"
                    + "    descricao\n"
                    + "from\n"
                    + "    tstributaria\n"
                    + "where\n"
                    + "    codempresa = " + getLojaOrigem())) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("codsit"), rs.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    distinct\n"
                    + "    g.codgrupo merc1,\n"
                    + "    g.descricao descmerc1,\n"
                    + "    coalesce(d.codtipo, 1) merc2,\n"
                    + "    coalesce(d.descricao, g.descricao) descmerc2,\n"
                    + "    1 merc3,\n"
                    + "    coalesce(d.descricao, g.descricao) descmerc3\n"
                    + "from\n"
                    + "    tproduto p\n"
                    + "inner join tgrupo g on (p.codgrupo = g.codgrupo) and\n"
                    + "    p.codempresa = g.codempresa\n"
                    + "left join ttipoprod d on (p.codtip = d.codtipo) and\n"
                    + "    p.codempresa = d.codempresa\n"
                    + "where\n"
                    + "    p.codempresa = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    p.codgrupo, p.codtip")) {
                while (rs.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setMerc1ID(rs.getString("merc1"));
                    imp.setMerc1Descricao(rs.getString("descmerc1"));
                    imp.setMerc2ID(rs.getString("merc2"));
                    imp.setMerc2Descricao(rs.getString("descmerc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rs.getString("descmerc3"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    codprd id,\n"
                    + "    codbarras codigobarras,\n"
                    + "    codund unidade\n"
                    + "from\n"
                    + "    tprodbarras\n"
                    + "where\n"
                    + "    codempresa = " + getLojaOrigem())) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    p.codprd id,\n"
                    + "    p.codbarras codigobarras,\n"
                    + "    p.nomefantasia descricaocompleta,\n"
                    + "    p.dtcadastramento datacadastro,\n"
                    + "    p.unidade,\n"
                    + "    p.codundcompra,\n"
                    + "    p.preco1 precovenda,\n"
                    + "    p.pesoliquido,\n"
                    + "    p.pesobruto,\n"
                    + "    p.estoqueminimo,\n"
                    + "    p.estoquemaximo,\n"
                    + "    p.custounitario custocomimposto,\n"
                    + "    p.codgrupo merc1,\n"
                    + "    coalesce(p.codtip, 1) merc2,\n"
                    + "    1 merc3,\n"
                    + "    p.margemlucrofisc,\n"
                    + "    p.margembrutalucro margem,\n"
                    + "    p.saldogeralfisico estoque,\n"
                    + "    p.inativo situacaocadastro,\n"
                    + "    p.codclas ncm,\n"
                    + "    p.cstpis,\n"
                    + "    p.cstcofins,\n"
                    + "    p.cstpisentrada,\n"
                    + "    p.cstcofinsentrada,\n"
                    + "    p.qtdembalagem,\n"
                    + "    p.cest,\n"
                    + "    p.codsit idtributacao\n"
                    + "from\n"
                    + "    tproduto p\n"
                    + "where\n"
                    + "    p.codempresa = " + getLojaOrigem() + "\n"
                    + "order by\n"
                    + "    p.codprd")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("codigobarras"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoGondola(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaocompleta"));
                    imp.setDataCadastro(rs.getDate("datacadastro"));
                    imp.setTipoEmbalagem(rs.getString("unidade"));
                    imp.setPrecovenda(rs.getDouble("precovenda"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setEstoqueMinimo(rs.getDouble("estoqueminimo"));
                    imp.setEstoqueMaximo(rs.getDouble("estoquemaximo"));
                    imp.setCustoComImposto(rs.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rs.getDouble("custocomimposto"));
                    imp.setCodMercadologico1(rs.getString("merc1"));
                    imp.setCodMercadologico2(rs.getString("merc2"));
                    imp.setCodMercadologico3(rs.getString("merc3"));
                    imp.setMargem(rs.getDouble("margem"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    if (rs.getString("situacaocadastro") != null) {
                        imp.setSituacaoCadastro("F".equals(rs.getString("situacaocadastro").trim()) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    }
                    imp.setNcm(rs.getString("ncm"));
                    imp.setPiscofinsCstCredito(rs.getString("cstpis"));
                    imp.setQtdEmbalagemCotacao(rs.getInt("qtdembalagem"));
                    imp.setCest(rs.getString("cest"));
                    imp.setIcmsDebitoId(rs.getString("idtributacao"));
                    imp.setIcmsCreditoId(rs.getString("idtributacao"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "    c.codcfo id,\n"
                    + "    c.nome,\n"
                    + "    c.nomefantasia,\n"
                    + "    c.cgccfo cnpj,\n"
                    + "    c.inscrestadual ie,\n"
                    + "    c.rua endereco,\n"
                    + "    c.numero,\n"
                    + "    c.complemento,\n"
                    + "    c.bairro,\n"
                    + "    c.cidade,\n"
                    + "    c.codetd uf,\n"
                    + "    c.cep, \n"
                    + "    c.telefone,\n"
                    + "    c.telefone2,\n"
                    + "    c.email,\n"
                    + "    c.limitecredito,\n"
                    + "    c.datacriacao,\n"
                    + "    c.datanasc,\n"
                    + "    c.nomemae,\n"
                    + "    c.nomepai,\n"
                    + "    c.ativo,\n"
                    + "    c.sexo,\n"
                    + "    c.estadocivil,\n"
                    + "    coalesce(obs.observacao, '') observacao,\n"
                    + "    tipo\n"
                    + "from\n"
                    + "    fcfo c\n"
                    + "left join fcfoobs obs on (c.codcfo = obs.codcfo)\n"
                    + "where\n"
                    + "    c.tipo in ('A', 'F') and\n"
                    + "    c.codempresa = " + getLojaOrigem())) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("nome"));
                    imp.setFantasia(rs.getString("nomefantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(rs.getString("telefone"));
                    if ((rs.getString("telefone2")) != null && (!"".equals(rs.getString("telefone2")))) {
                        imp.addContato("1", "TELEFONE", rs.getString("telefone2"), null, TipoContato.COMERCIAL, null);
                    }
                    imp.setDatacadastro(rs.getDate("datacriacao"));
                    if ((rs.getString("ativo") != null) && (!"".equals(rs.getString("ativo")))) {
                        imp.setAtivo("T".equals(rs.getString("ativo").trim()));
                    }
                    imp.addContato("1", "EMAIL", null, null, TipoContato.COMERCIAL, rs.getString("email"));
                    if((rs.getString("observacao") != null) && (!"".equals(rs.getString("observacao")))) {
                        imp.setObservacao(rs.getString("observacao"));
                    }
                    
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
