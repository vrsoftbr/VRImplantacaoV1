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
import java.util.Map;
import vrimplantacao.classe.ConexaoSqlServer;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class FHOnlineDAO extends InterfaceDAO {

    public String id_loja;
    public boolean i_arquivoBalanca;

    @Override
    public String getSistema() {
        if ((id_loja != null) && (!id_loja.trim().isEmpty())) {
            return "FHOnline" + id_loja;
        } else {
            return "FHOnline";
        }
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "[Cod grupo] as cod_merc, \n"
                    + "[Nome grupo] as desc_merc \n"
                    + "from [Grupo de produtos] "
                    + "order by [Cod grupo]"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("cod_merc"));
                    imp.setMerc1Descricao(rst.getString("desc_merc"));
                    imp.setMerc2ID("1");
                    imp.setMerc2Descricao(rst.getString("desc_merc"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(rst.getString("desc_merc1"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "p.[Cod produto] as id,\n"
                    + "p.[Cod automacao] as codigobarras,\n"
                    + "'1' as qtdembalagem,\n"
                    + "p.[Sigla unidade] as unidade,\n"
                    + "case p.[Produto pesado] when 1 then 'S' else 'N' end as pesado,\n"
                    + "p.[Produto pesado] as balanca, \n"
                    + "p.[Descricao produto] as descricaocompleta,\n"
                    + "p.[Descricao produto] as descricaoreduzida,\n"
                    + "p.[Descricao produto] as descricaogondola,\n"
                    + "g.[Cod grupo] as cod_mercadologico,\n"
                    + "g.[Nome grupo] as mercadologico,\n"
                    + "convert(date, p.[Data do cadastro prod], 23) as datacadastro,\n"
                    + "p.[Margem lucro] as margem,\n"
                    + "p.[Estoque maximo] as estoquemaximo,\n"
                    + "p.[Estoque minimo] as estoqueminimo,\n"
                    + "p.[Estoque atual] as estoque,\n"
                    + "p.[Preço custo] as custocomimposto,\n"
                    + "p.[Preço custo] as custosemimposto,\n"
                    + "p.[Preço venda] as precovenda,\n"
                    + "p.[Produto ativo] as ativo,\n"
                    + "p.[NCM] as ncm,\n"
                    + "p.[CST PIS] as piscofins_cst_debito,\n"
                    + "p.[CST COFINS ENTR] as piscofins_cst_credito,\n"
                    + "p.[Cod natureza receita]as piscofins_natureza_receita,\n"
                    + "p.[Cst icms] as icms_cst,\n"
                    + "p.[Aliquota icms] as icms_aliquota,\n"
                    + "p.[Perc base icms] as icms_reduzido\n"
                    + "from Produtos p\n"
                    + "left join [Grupo de produtos] g on g.[Cod grupo] = p.[Cod grupo]\n"
                    + "where p.[Cod empresa] = " + getLojaOrigem() + "\n"
                    + "order by p.[Cod produto]"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("codigobarras"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.seteBalanca((rst.getInt("balanca") == 1));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("cod_mercadologico"));
                    imp.setCodMercadologico2("1");
                    imp.setCodMercadologico3("1");
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setEstoqueMaximo(rst.getDouble("estoquemaximo"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueminimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setSituacaoCadastro("S".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_debito"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_credito"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_natureza_receita"));
                    imp.setIcmsCst(rst.getInt("icms_cst"));
                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    imp.setIcmsReducao(rst.getDouble("icms_reduzido"));

                    if ((rst.getString("codigobarras") != null)
                            && (!rst.getString("codigobarras").trim().isEmpty())) {

                        if ((rst.getString("codigobarras").trim().length() == 6)
                                && ("20".equals(rst.getString("codigobarras").subSequence(0, 2)))) {

                            if (i_arquivoBalanca) {

                                ProdutoBalancaVO produtoBalanca;
                                long codigoProduto;
                                codigoProduto = Long.parseLong(rst.getString("codigobarras").substring(2, rst.getString("codigobarras").trim().length()));
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

                            } else {

                                if (rst.getInt("balanca") == 1) {
                                    imp.setEan(rst.getString("codigobarras").substring(2, rst.getString("codigobarras").trim().length()));
                                    imp.seteBalanca(true);
                                } else {
                                    imp.setEan(rst.getString("codigobarras"));
                                    imp.seteBalanca(false);
                                }
                            }
                        } else {
                            imp.setEan(rst.getString("codigobarras"));
                            imp.seteBalanca(false);
                        }
                    }
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
                    "select \n"
                    + "f.[Cod fornecedor] as id,\n"
                    + "f.[Nome fornecedor] as razao,\n"
                    + "f.[Nome fantasia] as fantasia,\n"
                    + "f.[Cpf] as cpf,\n"
                    + "f.[Cnpj] as cnpj,\n"
                    + "f.[Rg] as ie_rg,\n"
                    + "f.[Insc municipal] as insc_municipal,\n"
                    + "case f.[Status fornecedor] when 'Ativo' then 'S' else 'N' end ativo,\n"
                    + "f.[Endereço fornecedor] as endereco,\n"
                    + "f.[Num endereco] as numero,\n"
                    + "f.[Complemento] as complemento,\n"
                    + "f.[Bairro] as bairro,\n"
                    + "f.[Cidade] as municipio,\n"
                    + "f.[Uf] as uf,\n"
                    + "f.[Cep] as cep,\n"
                    + "f.[Endereço cobranca] as cob_endereco,\n"
                    + "f.[Num endereco cobranca] as cob_numero,\n"
                    + "f.[Complemento cobranca] as cob_complemento,\n"
                    + "f.[Bairro cobranca] as cob_bairro,\n"
                    + "f.[Cidade cobranca] as cob_municipio,\n"
                    + "f.[Uf cobranca] as cob_uf,\n"
                    + "f.[Cep cobranca] as cob_cep,\n"
                    + "f.[Tel forn] as tel_principal,\n"
                    + "CONVERT(date, f.[Data do cadastro forn], 23) as datacadastro,\n"
                    + "f.[Fax forn] as fax,\n"
                    + "f.[Celular] as celular,\n"
                    + "f.[Email] as email,\n"
                    + "from Fornecedores f\n"
                    + "where f.[Cod empresa] = " + getLojaOrigem() + "\n"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));

                    if ((rst.getString("cnpj") != null)
                            && (!rst.getString("cnpj").trim().isEmpty())) {
                        imp.setCnpj_cpf(rst.getString("cnpj"));
                    } else {
                        imp.setCnpj_cpf(rst.getString("cpf"));
                    }

                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setCob_endereco(rst.getString("cob_endereco"));
                    imp.setCob_numero(rst.getString("cob_numero"));
                    imp.setCob_complemento(rst.getString("cob_complemento"));
                    imp.setCob_bairro(rst.getString("cob_bairro"));
                    imp.setCob_municipio(rst.getString("cob_municipio"));
                    imp.setCob_uf(rst.getString("cob_uf"));
                    imp.setCob_cep(rst.getString("cob_cep"));
                    imp.setTel_principal(rst.getString("tel_principal"));

                    if ((rst.getString("fax") != null)
                            && (!rst.getString("fax").trim().isEmpty())) {
                        imp.addContato(
                                "FAX",
                                rst.getString("fax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                null,
                                rst.getString("celular"),
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
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
                    + "[Cod produto] as id_produto, \n"
                    + "[Cod fornecedor] as id_fornecedor,\n"
                    + "[Qtde embalagem] as qtdembalagem\n"
                    + "from Produtos"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> lojas = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select "
                    + "[Cod empresa] as id, "
                    + "[Razao social] as razao, "
                    + "[Cnpj] as cnpj "
                    + "from Empresas "
                    + "order by "
                    + "[Cod empresa]"
            )) {
                while (rs.next()) {
                    lojas.add(new Estabelecimento(rs.getString("id"), rs.getString("razao")));
                }
            }
        }
        return lojas;
    }
}
