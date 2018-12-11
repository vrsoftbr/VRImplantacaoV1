/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class LinceDAO extends InterfaceDAO implements MapaTributoProvider {

    public String complementoSistema = "";
    
    @Override
    public String getSistema() {
        return "Lince" + ("".equals(complementoSistema) ? "" : " - " + complementoSistema);
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        Set<OpcaoProduto> s = new HashSet<>();
        
        s.addAll(OpcaoProduto.getMercadologico());
        s.addAll(OpcaoProduto.getFamilia());
        
        return s;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList();

        try (Statement stmt = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stmt.executeQuery(
                    "select id, descritivo from tributacao"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(rs.getString("id"), rs.getString("descritivo")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "m1.COD_DEPART as merc1, m1.DESCRICAO as desc_merc1, \n"
                    + "coalesce(m2.COD_SUBDEPART, '1') as merc2, coalesce(m2.DSC_SUBDEPART, m1.DESCRICAO) as desc_merc2,\n"
                    + "coalesce(m3.COD_SECAO, '1') as merc3, coalesce(m3.DSC_SECAO, m2.DSC_SUBDEPART) as desc_merc3\n"
                    + "from \n"
                    + "DEPARTAMENTO m1\n"
                    + "left join SUB_DEPARTAMENTO m2 on m2.COD_DEPART = m1.COD_DEPART\n"
                    + "left join SECAO m3 on m3.COD_SUBDEPART = m2.COD_SUBDEPART and m3.COD_DEPART = m1.COD_DEPART\n"
                    + "where \n"
                    + "m1.COD_LOJA = 1\n"
                    + "order by \n"
                    + "m1.COD_DEPART, m2.COD_SUBDEPART, m3.COD_SECAO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("desc_merc3"));
                    result.add(imp);
                }
            }
            return result;
        }
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.COD_PROD,\n"
                    + "p.CODIGO_BARRAS,\n"
                    + "p.FLG_UNIDADE_VENDA,\n"
                    + "p.DESCRICAO,\n"
                    + "p.VALIDADE, \n"
                    + "p.COD_DEPART, \n"
                    + "p.COD_SUBDEPART, \n"
                    + "p.COD_SECAO,\n"
                    + "p.PESO_BRUTO,\n"
                    + "p.PESO_LIQUIDO,\n"
                    + "p.CHK_ATIVO,\n"
                    + "p.VLR_PRECO,\n"
                    + "p.VLR_ULTIMO_CUSTO,\n"
                    + "p.QTDE_ESTOQUE_MINIMO,\n"
                    + "p.QTDE_ESTOQUE_MAXIMO,\n"
                    + "p.QTDE_ESTOQUE_LOJA,\n"
                    + "p.FLG_SITUACAO_TRIB,\n"
                    + "p.CST,\n"
                    + "p.COD_ICMS, \n"
                    + "icm.PERCENTUAL,\n"
                    + "p.COD_PIS,\n"
                    + "pis.DESCRICAO as desc_pis,\n"
                    + "p.nat_rec_pis,\n"
                    + "p.COD_COFINS,\n"
                    + "cof.DESCRICAO as desc_cofins,\n"
                    + "p.COD_NCM,\n"
                    + "cest.CEST\n"
                    + "from produto p\n"
                    + "left join PRODUTO_COFINS cof on cof.COD_COFINS = p.COD_COFINS\n"
                    + "left join PRODUTO_PIS pis on pis.COD_PIS = p.COD_PIS\n"
                    + "left join PRODUTO_CEST cest on cest.id = p.id_cest\n"
                    + "left join ICMS icm on icm.COD_ICMS = p.COD_ICMS\n"
                    + "where p.COD_LOJA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("COD_PROD"));
                    imp.setEan(rst.getString("CODIGO_BARRAS"));
                    imp.setDescricaoCompleta(rst.getString("DESCRICAO"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("FLG_UNIDADE_VENDA"));
                    imp.setValidade(rst.getInt("VALIDADE"));
                    imp.setCodMercadologico1(rst.getString("COD_DEPART"));
                    imp.setCodMercadologico2(rst.getString("COD_SUBDEPART"));
                    imp.setCodMercadologico3(rst.getString("COD_SECAO"));
                    imp.setPesoBruto(rst.getDouble("PESO_BRUTO"));
                    imp.setPesoLiquido(rst.getDouble("PESO_LIQUIDO"));
                    imp.setSituacaoCadastro("T".equals(rst.getString("CHK_ATIVO")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setPrecovenda(rst.getDouble("VLR_PRECO"));
                    imp.setCustoComImposto(rst.getDouble("VLR_ULTIMO_CUSTO"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("QTDE_ESTOQUE_MINIMO"));
                    imp.setEstoqueMaximo(rst.getDouble("QTDE_ESTOQUE_MAXIMO"));
                    imp.setEstoque(rst.getDouble("QTDE_ESTOQUE_LOJA"));
                    imp.setNcm(rst.getString("COD_NCM"));
                    imp.setCest(rst.getString("CEST"));
                    imp.setPiscofinsCstDebito(rst.getString("COD_PIS"));
                    imp.setPiscofinsCstCredito(rst.getString("COD_COFINS"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("nat_rec_pis"));
                    imp.setIcmsDebitoId(rst.getString("COD_ICMS"));
                    imp.setIcmsCreditoId(rst.getString("COD_ICMS"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "ean.CODIGO_BARRAS, ean.COD_PROD, p.FLG_UNIDADE_VENDA\n"
                    + "from barras_vinculada ean\n"
                    + "inner join PRODUTO p on p.COD_PROD = ean.COD_PROD\n"
                    + "where ean.COD_LOJA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("COD_PROD"));
                    imp.setEan(rst.getString("CODIGO_BARRAS"));
                    imp.setTipoEmbalagem(rst.getString("FLG_UNIDADE_VENDA"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select  \n"
                    + "f.COD_FORN,\n"
                    + "f.RAZAO_SOCIAL,\n"
                    + "f.NOME,\n"
                    + "f.ENDERECO,\n"
                    + "f.NUMERO,\n"
                    + "f.BAIRRO,\n"
                    + "f.CIDADE,\n"
                    + "f.UF,\n"
                    + "f.CEP,\n"
                    + "f.TELEFONE,\n"
                    + "f.FAX,\n"
                    + "f.CPF,\n"
                    + "f.INSCRICAO_ESTADUAL,\n"
                    + "f.CONTATO,\n"
                    + "f.TEL_CONTATO,\n"
                    + "f.REPRESENTANTE,\n"
                    + "f.TEL_REPRESENTANTE,\n"
                    + "f.EMAIL,\n"
                    + "f.OBS\n"
                    + "from fornecedor f\n"
                    + "where f.COD_LOJA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("COD_FORN"));
                    imp.setRazao(rst.getString("RAZAO_SOCIAL"));
                    imp.setCnpj_cpf(rst.getString("CPF"));
                    imp.setIe_rg(rst.getString("INSCRICAO_ESTADUAL"));
                    imp.setFantasia(rst.getString("NOME"));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("UF"));
                    imp.setTel_principal(rst.getString("TELEFONE"));
                    imp.setObservacao(rst.getString("OBS"));

                    if ((rst.getString("FAX") != null)
                            && (!rst.getString("FAX").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("FAX"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("EMAIL") != null)
                            && (!rst.getString("EMAIL").trim().isEmpty())) {
                        imp.addContato(
                                null,
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("EMAIL").toLowerCase()
                        );
                    }
                    if ((rst.getString("TEL_CONTATO") != null)
                            && (!rst.getString("TEL_CONTATO").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("CONTATO"),
                                rst.getString("TEL_CONTATO"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("TEL_REPRESENTANTE") != null)
                            && (!rst.getString("TEL_REPRESENTANTE").trim().isEmpty())) {
                        imp.addContato(
                                rst.getString("REPRESENTANTE"),
                                rst.getString("TEL_REPRESENTANTE"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "COD_PROD, \n"
                    + "COD_FORN, \n"
                    + "FLG_UNIDADE_COMPRA, \n"
                    + "REFERENCIA_FORN, \n"
                    + "CUSTO_EMB \n"
                    + "from produto\n"
                    + "where COD_FORN IS NOT NULL\n"
                    + "and COD_LOJA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("COD_PROD"));
                    imp.setIdFornecedor(rst.getString("COD_FORN"));
                    imp.setCustoTabela(rst.getDouble("CUSTO_EMB"));
                    imp.setCodigoExterno(rst.getString("REFERENCIA_FORN"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "c.COD_CLI,\n"
                    + "c.DIA_VENCIMENTO,\n"
                    + "c.CODIGO_BARRAS,\n"
                    + "c.RAZAO_SOCIAL,\n"
                    + "c.NOME,\n"
                    + "c.ENDERECO,\n"
                    + "c.BAIRRO,\n"
                    + "c.CIDADE,\n"
                    + "c.CEP,\n"
                    + "c.COMPLEMENTO,\n"
                    + "c.NUMERO,\n"
                    + "c.TELEFONE,\n"
                    + "c.UF,\n"
                    + "c.CNPJ_CPF,\n"
                    + "c.RG_IE,\n"
                    + "c.CELULAR,\n"
                    + "c.EMAIL,\n"
                    + "c.PONTO_REFERENCIA,\n"
                    + "c.PROFISSAO,\n"
                    + "c.NOME_MAE,\n"
                    + "c.NOME_PAI,\n"
                    + "c.CHK_ATIVO,\n"
                    + "c.DATA_NASCIMENTO,\n"
                    + "c.VLR_LIMITE_COMPRAS,\n"
                    + "c.OBS\n"
                    + "from cliente c\n"
                    + "where c.COD_LOJA = " + getLojaOrigem()
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("COD_CLI"));
                    imp.setRazao(rst.getString("RAZAO_SOCIAL"));
                    imp.setFantasia(rst.getString("NOME"));
                    imp.setCnpj(rst.getString("CNPJ_CPF"));
                    imp.setInscricaoestadual(rst.getString("RG_IE"));
                    imp.setAtivo("T".equals(rst.getString("CHK_ATIVO")));
                    imp.setEndereco(rst.getString("ENDERECO"));
                    imp.setNumero(rst.getString("NUMERO"));
                    imp.setComplemento(rst.getString("COMPLEMENTO"));
                    imp.setCep(rst.getString("CEP"));
                    imp.setBairro(rst.getString("BAIRRO"));
                    imp.setMunicipio(rst.getString("CIDADE"));
                    imp.setUf(rst.getString("UF"));
                    imp.setTelefone(rst.getString("TELEFONE"));
                    imp.setCelular(rst.getString("CELULAR"));
                    imp.setEmail(rst.getString("EMAIL"));
                    imp.setObservacao(rst.getString("OBS"));
                    imp.setDataNascimento(rst.getDate("DATA_NASCIMENTO"));
                    imp.setCargo(rst.getString("PROFISSAO"));
                    imp.setNomeMae(rst.getString("NOME_MAE"));
                    imp.setNomePai(rst.getString("NOME_PAI"));
                    imp.setValorLimite(rst.getDouble("VLR_LIMITE_COMPRAS"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "COD_FATURA,\n"
                    + "COD_CLI,\n"
                    + "DATA,\n"
                    + "VENCIMENTO,\n"
                    + "(VLR_VALOR - coalesce(VLR_PAGTO, 0)) as VALOR,\n"
                    + "VLR_JUROS_ATRASO\n"
                    + "from FATURA "
                    + "where CHK_FAT_BAIXADA = 'F'\n"
                    + "order by DATA"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("COD_FATURA"));
                    imp.setIdCliente(rst.getString("COD_CLI"));
                    imp.setDataEmissao(rst.getDate("DATA"));
                    imp.setDataVencimento(rst.getDate("VENCIMENTO"));
                    imp.setValor(rst.getDouble("VALOR"));
                    imp.setJuros(rst.getDouble("VLR_JUROS_ATRASO"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select cod_loja, cnpj, nome_reduzido from loja order by 1"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(
                            rst.getString("cod_loja"),
                            rst.getString("cnpj") + " - " + rst.getString("nome_reduzido")
                    ));
                }
            }
        }
        
        return result;
    }
}
