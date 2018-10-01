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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class UpFortiDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "UpForti";
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> merc = new LinkedHashMap<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "nivel1 as merc1,\n"
                    + "descricao as desc_merc1\n"
                    + "from grupoprod\n"
                    + "where nivel1 > 0\n"
                    + "and nivel2 = 0\n"
                    + "and nivel3 = 0\n"
                    + "order by nivel1"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("desc_merc1"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "nivel1 as merc1,\n"
                    + "nivel2 as merc2,\n"
                    + "descricao as merc2_desc\n"
                    + "from grupoprod\n"
                    + "where nivel1 > 0\n"
                    + "and nivel2 > 0\n"
                    + "and nivel3 = 0\n"
                    + "order by nivel1, nivel2"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        merc1.addFilho(
                                rst.getString("merc2"),
                                rst.getString("merc2_descricao")
                        );
                    }
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "nivel1 as merc1,\n"
                    + "nivel2 as merc2,\n"
                    + "nivel3 as merc3,\n"
                    + "descricao as merc3_desc\n"
                    + "from grupoprod\n"
                    + "where nivel1 > 0\n"
                    + "and nivel2 > 0\n"
                    + "and nivel3 > 0\n"
                    + "order by nivel1, nivel2, nivel3"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP merc1 = merc.get(rst.getString("merc1"));
                    if (merc1 != null) {
                        MercadologicoNivelIMP merc2 = merc1.getNiveis().get(rst.getString("merc2"));
                        if (merc2 != null) {
                            merc2.addFilho(
                                    rst.getString("merc3"),
                                    rst.getString("merc3_descricao")
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id_produto,\n"
                    + "codbarras,\n"
                    + "codigo,\n"
                    + "case exporta_balanca when 'S' then '1' else '0' end balanca,\n"
                    + "nivel1 as merc1,\n"
                    + "nivel2 as merc2,\n"
                    + "nivel3 as merc3,\n"
                    + "descricao,\n"
                    + "un as unidade,\n"
                    + "icms,\n"
                    + "quantidade as estoque,\n"
                    + "minimo as estminimo,\n"
                    + "maximo as estmaximo,\n"
                    + "precovenda,\n"
                    + "preco_custo,\n"
                    + "margem1,\n"
                    + "st,\n"
                    + "aliquota,\n"
                    + "tipo_aliquota,\n"
                    + "status_exclusao,\n"
                    + "ncm,\n"
                    + "cest,\n"
                    + "pis_cst,\n"
                    + "cofins_cst,\n"
                    + "nat_rec_cofins\n"
                    + "from produtos\n"
                    + "order by id_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigo"));
                    imp.seteBalanca((rst.getInt("balanca") == 1));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setSituacaoCadastro("N".equals(rst.getString("status_exclusao")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setMargem(rst.getDouble("margem1"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estminimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estmaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getInt("pis_cst"));
                    imp.setPiscofinsCstCredito(rst.getInt("cofins_cst"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("nat_rec_cofins"));
                    imp.setIcmsDebitoId(rst.getString("tipo_aliquota"));
                    imp.setIcmsCreditoId(rst.getString("tipo_aliquota"));
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.id_fornecedor,\n"
                    + "f.nome,\n"
                    + "f.fantasia,\n"
                    + "c.nome as municipio,\n"
                    + "c.uf as estado,\n"
                    + "c.codigo_cid as ibge_municipio,\n"
                    + "c.codigo_uf as ibge_uf,\n"
                    + "b.nome as bairro,\n"
                    + "r.nome as rua,\n"
                    + "f.numero,\n"
                    + "r.cep,\n"
                    + "f.compl,\n"
                    + "f.fone, \n"
                    + "f.fone2,\n"
                    + "f.fone3,\n"
                    + "f.email,\n"
                    + "f.site,\n"
                    + "f.data_cad,\n"
                    + "f.cnpj,\n"
                    + "f.ie,\n"
                    + "f.obs, \n"
                    + "f.status,\n"
                    + "f.codigo,\n"
                    + "f.tipo as tipoinscricao,\n"
                    + "f.codigo_pais\n"
                    + "from fornecedores f\n"
                    + "left join cidades c on c.id_cidade = f.id_cidade\n"
                    + "left join bairros b on b.id_bairro = f.id_bairro\n"
                    + "left join ruas r on r.id_rua = f.id_rua\n"
                    + "order by id_fornecedor"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_fornecedor"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo("A".equals(rst.getString("status")));
                    imp.setDatacadastro(rst.getDate("data_cad"));
                    imp.setEndereco(rst.getString("rua"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCep(rst.getString("cep"));
                    imp.setComplemento(rst.getString("compl"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setIbge_municipio(rst.getInt("ibge_municipio"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("estado"));
                    imp.setIbge_uf(rst.getInt("ibge_uf"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setTel_principal(rst.getString("fone"));

                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("fone3") != null)
                            && (!rst.getString("fone3").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 3",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("email") != null)
                            && (!rst.getString("email").trim().isEmpty())
                            && (rst.getString("email").contains("@"))) {
                        imp.addContato(
                                "EMAIL",
                                null,
                                null,
                                TipoContato.NFE,
                                rst.getString("email").toLowerCase()
                        );
                    }
                    if ((rst.getString("site") != null)
                            && (!rst.getString("site").trim().isEmpty())) {
                        imp.addContato(
                                "SITE",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("site").toLowerCase()
                        );
                    }
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "cl.id_cliente, \n"
                    + "cl.codigo,\n"
                    + "cl.nome,\n"
                    + "cl.fantasia,\n"
                    + "c.nome as municipio,\n"
                    + "c.uf as estado,\n"
                    + "c.codigo_cid as ibge_municipio,\n"
                    + "c.codigo_uf as ibge_uf,\n"
                    + "b.nome as bairro,\n"
                    + "r.nome as rua,\n"
                    + "cl.numero,\n"
                    + "cl.compl,\n"
                    + "r.cep,\n"
                    + "cl.fone,\n"
                    + "cl.fone2,\n"
                    + "cl.fone3,\n"
                    + "cl.email,\n"
                    + "cl.site,\n"
                    + "cl.data_cad,\n"
                    + "cl.cnpj,\n"
                    + "cl.ie,\n"
                    + "cl.obs,\n"
                    + "cl.obs2,\n"
                    + "cl.valor_limite,\n"
                    + "cl.status,\n"
                    + "cl.tipo as tipoinscricao,\n"
                    + "cl.estado_civil,\n"
                    + "cl.nome_conjuge,\n"
                    + "cl.data_nasc,\n"
                    + "cl.cod_cartao,\n"
                    + "cl.cli_func,\n"
                    + "cl.data_exclusao,\n"
                    + "cl.senha\n"
                    + "from clientes cl\n"
                    + "left join cidades c on c.id_cidade = cl.id_cidade\n"
                    + "left join bairros b on b.id_bairro = cl.id_bairro\n"
                    + "left join ruas r on r.id_rua = cl.id_rua\n"
                    + "order by cl.id_cliente"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                }
            }
        }
        return null;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id_carteira,\n"
                    + "id_caixa,\n"
                    + "cod_origem as cupom,\n"
                    + "data,\n"
                    + "data_cad,\n"
                    + "valor,\n"
                    + "juros,\n"
                    + "desconto,\n"
                    + "historico,\n"
                    + "id_vinculo as id_cliente,\n"
                    + "parcela,\n"
                    + "qtparcela,\n"
                    + "descparcela\n"
                    + "from carteira\n"
                    + "where pago = 'N'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                }
            }
        }
        return null;
    }
}
