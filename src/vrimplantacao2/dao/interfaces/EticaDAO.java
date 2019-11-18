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
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class EticaDAO extends InterfaceDAO {

    @Override
    public String getSistema() {
        return "Etica";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "id, razao_social\n"
                    + "from empresa\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("id"), rst.getString("razao_social")));
                }
            }
        }
        return result;
    }
    
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select distinct\n"
                    + "    g.id as merc1, g.descricao as desc_merc1,\n"
                    + "    sg.id as merc2, sg.descricao as desc_merc2\n"
                    + "from produtos p\n"
                    + "inner join grupo g on g.id = p.grupo\n"
                    + "inner join subgrupo sg on sg.id = p.subgrupo1\n"
                    + "order by g.id, sg.id"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("desc_merc1"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("desc_merc2"));
                    imp.setMerc3ID("1");
                    imp.setMerc3Descricao(imp.getMerc2Descricao());
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
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.id,\n"
                    + "    p.ean13,\n"
                    + "    p.descricao,\n"
                    + "    p.unidade,\n"
                    + "    p.fracionado,\n"
                    + "    p.fracionado_unidade,\n"
                    + "    p.dias_validade,\n"
                    + "    p.departamento,\n"
                    + "    p.grupo as merc1,\n"
                    + "    p.subgrupo1 as merc2,\n"
                    + "    '1' as merc3,\n"
                    + "    p.dun14,\n"
                    + "    p.estoque_minimo,\n"
                    + "    p.estoque,\n"
                    + "    p.margem_lucro,\n"
                    + "    p.preco_compra,\n"
                    + "    p.preco_venda,\n"
                    + "    p.codigo_ncm,\n"
                    + "    p.cest,\n"
                    + "    p.cst_pis,\n"
                    + "    p.cst_cofins,\n"
                    + "    p.cst_pis_entrada,\n"
                    + "    p.cst_cofins_entrada,\n"
                    + "    p.cod_nat_pc,\n"
                    + "    p.tributacao,\n"
                    + "    p.cst,\n"
                    + "    p.aliquota_icms,\n"
                    + "    p.reducao_base,\n"
                    + "    p.ativo\n"
                    + "from produtos p\n"
                    + "order by p.id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean13"));
                    imp.seteBalanca("S".equals(rst.getString("fracionado")));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("dias_validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setMargem(rst.getDouble("margem_lucro"));
                    imp.setSituacaoCadastro("T".equals(rst.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setCustoComImposto(rst.getDouble("preco_compra"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("codigo_ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_pis_entrada"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("cod_nat_pc"));
                    imp.setIcmsCst(Integer.parseInt(Utils.formataNumero(rst.getString("cst"))));
                    imp.setIcmsAliq(rst.getDouble("aliquota_icms"));
                    imp.setIcmsReducao(rst.getDouble("reducao_base"));
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
                    + "    f.id,\n"
                    + "    f.razao_social as razao,\n"
                    + "    f.nome as fantasia,\n"
                    + "    f.cnpj_cpf,\n"
                    + "    f.insc_rg,\n"
                    + "    f.endereco,\n"
                    + "    f.num_end,\n"
                    + "    f.complemento,\n"
                    + "    f.cep_end,\n"
                    + "    f.bairro_end,\n"
                    + "    f.cidade_end,\n"
                    + "    f.codcidade,\n"
                    + "    f.uf_end,\n"
                    + "    f.cod_pais,\n"
                    + "    f.end_entrega,\n"
                    + "    f.num_end_entrega,\n"
                    + "    f.cep_end_entrega,\n"
                    + "    f.compl_ent,\n"
                    + "    f.cid_end_entrega,\n"
                    + "    f.uf_end_ent,\n"
                    + "    f.telefone,\n"
                    + "    f.telefax,\n"
                    + "    f.email,\n"
                    + "    f.site,\n"
                    + "    f.data_cad,\n"
                    + "    f.observacoes,\n"
                    + "    f.ativo\n"
                    + "from contato f\n"
                    + "where f.tipo_contato = 'F'\n"
                    + "order by f.id"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("insc_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("num_end"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep_end"));
                    imp.setBairro(rst.getString("bairro_end"));
                    imp.setMunicipio(rst.getString("cidade_end"));
                    imp.setIbge_municipio(rst.getInt("codcidade"));
                    imp.setUf(rst.getString("uf_end"));
                    imp.setAtivo("T".equals(rst.getString("ativo")));
                    imp.setDatacadastro(rst.getDate("data_cad"));
                    imp.setObservacao(rst.getString("observacoes"));
                    imp.setTel_principal(rst.getString("telefone"));

                    if ((rst.getString("telefax") != null)
                            && (!rst.getString("telefax").trim().isEmpty())) {

                        imp.addContato(
                                "FAX",
                                rst.getString("telefax"),
                                null,
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
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.id,\n"
                    + "    c.razao_social as razao,\n"
                    + "    c.nome as fantasia,\n"
                    + "    c.cnpj_cpf,\n"
                    + "    c.insc_rg,\n"
                    + "    c.endereco,\n"
                    + "    c.num_end,\n"
                    + "    c.complemento,\n"
                    + "    c.cep_end,\n"
                    + "    c.bairro_end,\n"
                    + "    c.cidade_end,\n"
                    + "    c.codcidade,\n"
                    + "    c.uf_end,\n"
                    + "    c.cod_pais,\n"
                    + "    c.end_entrega,\n"
                    + "    c.num_end_entrega,\n"
                    + "    c.cep_end_entrega,\n"
                    + "    c.compl_ent,\n"
                    + "    c.cid_end_entrega,\n"
                    + "    c.uf_end_ent,\n"
                    + "    c.telefone,\n"
                    + "    c.telefax,\n"
                    + "    c.email,\n"
                    + "    c.site,\n"
                    + "    c.data_cad,\n"
                    + "    c.ativo,\n"
                    + "    c.limite_cred,\n"
                    + "    c.filiacao_pai,\n"
                    + "    c.filiacao_mae,\n"
                    + "    c.local_trab,\n"
                    + "    c.observacoes,\n"
                    + "    c.end_local_trab,\n"
                    + "    c.telefone_trab,\n"
                    + "    c.data_nasc,\n"
                    + "    c.credito,\n"
                    + "    c.dia_venc_finan\n"
                    + "from contato c\n"
                    + "where c.tipo_contato = 'C'\n"
                    + "order by c.id"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("fantasia"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj_cpf"));
                    imp.setInscricaoestadual(rst.getString("insc_rg"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("num_end"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep_end"));
                    imp.setBairro(rst.getString("bairro_end"));
                    imp.setMunicipio(rst.getString("cidade_end"));
                    imp.setMunicipioIBGE(rst.getInt("codcidade"));
                    imp.setUf(rst.getString("uf_end"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setFax(rst.getString("telefax"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("data_cad"));
                    imp.setAtivo("T".equals(rst.getString("ativo")));
                    imp.setValorLimite(rst.getDouble("limite_cred"));
                    imp.setNomeMae(rst.getString("filiacao_mae"));
                    imp.setNomePai(rst.getString("filiacao_pai"));
                    imp.setEmpresa(rst.getString("end_local_trab"));
                    imp.setEmpresaTelefone(rst.getString("telefone_trab"));
                    imp.setDataNascimento(rst.getDate("data_nasc"));
                    imp.setDiaVencimento(rst.getInt("dia_venc_finan"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    r.id,\n"
                    + "    r.idcontato as idcliente,\n"
                    + "    pe.coo as numerocupom,\n"
                    + "    r.data_emissao,\n"
                    + "    r.data_venc,\n"
                    + "    (r.valor_conta - r.valor_quitato) as valor,\n"
                    + "    r.status,\n"
                    + "    r.juros,\n"
                    + "    r.multa,\n"
                    + "    r.obs\n"
                    + "from contas r\n"
                    + "left join pedido pe on pe.id = r.idpedido and pe.idempresa = " + getLojaOrigem()
                    + " and r.idempresa = " + getLojaOrigem() + "\n"
                    + "where idcontato in (select id from contato where tipo_contato = 'C')\n"
                    + "and valor_quitato < valor_conta"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("data_emissao"));
                    imp.setDataVencimento(rst.getDate("data_venc"));
                    imp.setNumeroCupom(rst.getString("numerocupom"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setJuros(rst.getDouble("juros"));
                    imp.setMulta(rst.getDouble("multa"));
                    imp.setObservacao(rst.getString("obs"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
