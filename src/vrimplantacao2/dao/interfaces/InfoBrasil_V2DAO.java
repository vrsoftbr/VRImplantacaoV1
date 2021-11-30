/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class InfoBrasil_V2DAO extends InterfaceDAO {

    public String complSistema = "";
    public String i_tipoDocumento;

    @Override
    public String getSistema() {
        if ((complSistema != null) && (!complSistema.trim().isEmpty())) {
            return "InfoBrasil" + complSistema;
        } else {
            return "InfoBrasil";
        }
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_SOMENTE_PRODUTOS_ATIVOS,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.ESTOQUE_MAXIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.ICMS_SAIDA,
                OpcaoProduto.ICMS_SAIDA_FORA_ESTADO,
                OpcaoProduto.ICMS_SAIDA_NF,
                OpcaoProduto.ICMS_ENTRADA,
                OpcaoProduto.ICMS_ENTRADA_FORA_ESTADO,
                OpcaoProduto.ICMS_CONSUMIDOR,
                OpcaoProduto.NCM
        ));
    }
    
    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select loj_codigo, loj_nome, loj_cnpj from lojas order by loj_codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("loj_codigo"), rst.getString("loj_nome")));
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
                    "select\n"
                    + "    s.sec_codigo merc1,\n"
                    + "    s.sec_descricao merc1_desc,\n"
                    + "    coalesce(g.gru_codigo, 1) merc2,\n"
                    + "    coalesce(g.gru_descricao, s.sec_descricao) merc2_desc,\n"
                    + "    '1' merc3,\n"
                    + "    coalesce(g.gru_descricao, s.sec_descricao) merc3_desc\n"
                    + "from\n"
                    + "    seccao s\n"
                    + "left join grupospro g on s.sec_codigo = g.sec_codigo\n"
                    + "order by s.sec_codigo, g.gru_codigo"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("merc1"));
                    imp.setMerc1Descricao(rst.getString("merc1_desc"));
                    imp.setMerc2ID(rst.getString("merc2"));
                    imp.setMerc2Descricao(rst.getString("merc2_desc"));
                    imp.setMerc3ID(rst.getString("merc3"));
                    imp.setMerc3Descricao(rst.getString("merc3_desc"));
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
                    + "   p.pro_codigo id,\n"
                    + "   COALESCE(p.PRO_CODIGOBAR, p.pro_codigo) ean,\n"
                    + "   p.pro_descricao descricaocompleta,\n"
                    + "   p.pro_descfiscal descricaoreduzida,\n"
                    + "   p.pro_descricao descricaogondola,\n"
                    + "   case pro_situacao when 'I' then 0 else 1 end as id_situacaocadastro,\n"
                    + "   p.pro_datacadastro datacadastro,\n"
                    + "   g.sec_codigo mercadologico1,\n"
                    + "   p.gru_codigo mercadologico2,\n"
                    + "   '1' as mercadologico3,\n"
                    + "   p.pro_ncm ncm,\n"
                    + "   0 margem,\n"
                    + "   p.pro_datacadastro,\n"
                    + "   case p.pro_balanca when 'S' then 1 else 0 end as e_balanca,\n"
                    + "   p.pro_unidade,\n"
                    + "   p.pro_peso pesobruto,\n"
                    + "   e.pis_codigo piscofins_cst_sai,\n"
                    + "   e.pis_codigoent piscofins_cst_ent,\n"
                    + "   null piscofins_natrec,\n"
                    + "   p.pro_prccusto custo,\n"
                    + "   e.PRO_PRECO1 preco,\n"
                    + "   e.EST_MINIMO estoque_min,\n"
                    + "   e.EST_MAXIMO estoque_max,\n"
                    + "   e.est_apoio estoque,\n"
                    + "   pis_s.pis_codigo cst_pis_s,\n"
                    + "   pis_e.pis_codigo cst_pis_e,\n"
                    + "   p.pro_icms icms_aliq,\n"
                    + "   p.pro_reducaoicms icms_reducao\n"
                    + "from\n"
                    + "    produtos p\n"
                    + "left join grupospro g on p.gru_codigo = g.gru_codigo\n"
                    + "join estoque e on e.pro_codigo = p.pro_codigo and e.loj_codigo = " + getLojaOrigem() + "\n"
                    + "left join cst_pis pis_s on pis_s.pis_codigo = e.pis_codigo\n"
                    + "left join cst_pis pis_e on pis_e.pis_codigo = e.pis_codigoent\n"
                    + "order by\n"
                    + "    p.pro_codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaogondola"));
                    imp.setSituacaoCadastro(rst.getInt("id_situacaocadastro"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setCodMercadologico1(rst.getString("mercadologico1"));
                    imp.setCodMercadologico2(rst.getString("mercadologico2"));
                    imp.setCodMercadologico3(rst.getString("mercadologico3"));
                    imp.seteBalanca(rst.getInt("e_balanca") == 1);
                    imp.setTipoEmbalagem(rst.getString("pro_unidade"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setEstoqueMinimo(rst.getDouble("estoque_min"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_max"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_cst_sai"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofins_cst_ent"));

                    int cstIcms = 0;
                    
                    if ((rst.getDouble("icms_aliq") > 0)
                            && (rst.getDouble("icms_reducao") == 0)) {
                        cstIcms = 0;
                    } else if ((rst.getDouble("icms_aliq") > 0)
                            && (rst.getDouble("icms_reducao") == 0)) {
                        cstIcms = 20;
                    }
                    if (rst.getDouble("icms_reducao") == 100) {
                        cstIcms = 40;
                    }

                    imp.setIcmsCstSaida(cstIcms);
                    imp.setIcmsAliqSaida(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icms_reducao"));
                    imp.setIcmsCstSaidaForaEstado(cstIcms);
                    imp.setIcmsAliqSaidaForaEstado(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("icms_reducao"));                    
                    imp.setIcmsCstSaidaForaEstadoNF(cstIcms);
                    imp.setIcmsAliqSaidaForaEstadoNF(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("icms_reducao"));
                    imp.setIcmsCstEntrada(cstIcms);
                    imp.setIcmsAliqEntrada(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icms_reducao"));
                    imp.setIcmsCstEntradaForaEstado(cstIcms);
                    imp.setIcmsAliqEntradaForaEstado(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("icms_reducao"));
                    imp.setIcmsCstConsumidor(cstIcms);
                    imp.setIcmsAliqConsumidor(rst.getDouble("icms_aliq"));
                    imp.setIcmsReducaoConsumidor(rst.getDouble("icms_reducao"));
                    
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
            try (ResultSet rst = stm.executeQuery(
                    "select "
                    + "pro_codigo, "
                    + "pra_codigo "
                    + "from prod_agregados\n"
                    + "order by pro_codigo"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("pro_codigo"));
                    imp.setEan(rst.getString("pra_codigo"));
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
                    + "   f.cre_codigo id,\n"
                    + "   f.cre_datacadastro datacadastro,\n"
                    + "   f.cre_nome razao,\n"
                    + "   f.cre_fantasia fantasia,\n"
                    + "   f.cre_endereco endereco,\n"
                    + "   f.cre_numero numero,\n"
                    + "   f.cre_compl_endereco complemento,\n"
                    + "   f.cre_bairro bairro,\n"
                    + "   f.mun_codigo cidade,\n"
                    + "   f.cre_uf estado,\n"
                    + "   f.cre_cep cep,\n"
                    + "   f.cre_fone fone1,\n"
                    + "   f.cre_fonerep fone2,\n"
                    + "   f.cre_celular celular,\n"
                    + "   f.cre_celularrep,\n"
                    + "   f.cre_cgf inscricaoestadual,\n"
                    + "   f.cre_cnpj cnpj,\n"
                    + "   f.cre_email email,\n"
                    + "   case f.cre_situacao when 'I' then 1 else 0 end as bloqueado\n"
                    + "from\n"
                    + "   credores f\n"
                    + "order by\n"
                    + "   f.cre_codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("inscricaoestadual"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTel_principal(rst.getString("fone1"));

                    if ((rst.getString("fone2") != null)
                            && (!rst.getString("fone2").trim().isEmpty())) {
                        imp.addContato(
                                "TELEFONE 2",
                                rst.getString("fone2"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("celular") != null)
                            && (!rst.getString("celular").trim().isEmpty())) {
                        imp.addContato(
                                "CELULAR",
                                rst.getString("celular"),
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
                    + "   c.cli_codigo id,\n"
                    + "   c.cli_nome nome,\n"
                    + "   c.cli_fantasia fantasia,\n"
                    + "   c.cli_endereco res_endereco,\n"
                    + "   c.cli_numero res_numero,\n"
                    + "   c.cli_compl_endereco res_complemento,\n"
                    + "   c.cli_bairro res_bairro,\n"
                    + "   c.mun_codigo res_cidade,\n"
                    + "   c.cli_uf res_uf,\n"
                    + "   c.cli_cep res_cep,\n"
                    + "   c.cli_fone fone1,\n"
                    + "   c.cli_celular celular,\n"
                    + "   c.cli_identidade inscricaoestadual,\n"
                    + "   c.cli_cpf_cnpj cnpj,\n"
                    + "   case c.cli_sexo when 'F' then 0 else 1 end sexo,\n"
                    + "   c.cli_email email,\n"
                    + "   c.cli_datacadastro datacadastro,\n"
                    + "   c.cli_limite limitepreferencial,\n"
                    + "   case c.cli_bloqueio when 'S' then 1 else 0 end bloqueado,\n"
                    + "   case c.cli_situacao when 'I' then 0 else 1 end id_situacaocadastro,\n"
                    + "   c.cli_datanasc datanascimento,\n"
                    + "   c.cli_pai nomePai,\n"
                    + "   c.cli_mae nomeMae,\n"
                    + "   c.cli_cargo cargo,\n"
                    + "   c.cli_renda salario,\n"
                    + "   case c.cli_estadocivil\n"
                    + "        when 'C' then 2\n"
                    + "        when 'S' then 1\n"
                    + "        when 'V' then 3\n"
                    + "   else 0 end\n"
                    + "   estadocivil\n"
                    + "from\n"
                    + "   clientes c\n"
                    + "order by c.cli_codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("inscricaoestadual"));
                    imp.setEndereco(rst.getString("res_endereco"));
                    imp.setNumero(rst.getString("res_numero"));
                    imp.setComplemento(rst.getString("res_complemento"));
                    imp.setCep(rst.getString("res_cep"));
                    imp.setBairro(rst.getString("res_bairro"));
                    imp.setMunicipio(rst.getString("res_cidade"));
                    imp.setUf(rst.getString("res_uf"));
                    imp.setTelefone(rst.getString("fone1"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setEmail(rst.getString("email"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setSexo(rst.getInt("sexo") == 0 ? TipoSexo.FEMININO : TipoSexo.MASCULINO);
                    imp.setBloqueado(rst.getInt("bloqueado") == 1);
                    imp.setAtivo(rst.getInt("id_situacaocadastro") == 1);
                    imp.setNomePai(rst.getString("nomePai"));
                    imp.setNomeMae(rst.getString("nomeMae"));
                    imp.setCargo(rst.getString("cargo"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setEstadoCivil(rst.getInt("estadocivil"));
                    imp.setValorLimite(rst.getDouble("limitepreferencial"));
                    imp.setPermiteCheque(true);
                    imp.setPermiteCreditoRotativo(true);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    public List<String> getTipoDocumentos() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select (fpg_codigo||' - '||fpg_descricao) as documento from formaspag order by fpg_codigo"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("documento"));
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
                    + "    r.rec_numero as id,\n"
                    + "    r.cli_codigo,\n"
                    + "    c.cli_cpf_cnpj,\n"
                    + "    r.rec_datalanc,\n"
                    + "    r.rec_doc,\n"
                    + "    r.rec_valor,\n"
                    + "    r.rec_observacoes,\n"
                    + "    r.rec_datavenc\n"
                    + "from\n"
                    + "   contasreceber r\n"
                    + "join clientes c on r.cli_codigo = c.cli_codigo\n"
                    + "where\n"
                    + "    r.loj_codigo = " + getLojaOrigem() + "\n"
                    + "    and r.rec_datapag is null \n"
                    + "    and r.fpg_codigo in (" + i_tipoDocumento + ")\n"
                    + "order by\n"
                    + "   r.rec_datalanc"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("cli_codigo"));
                    imp.setDataEmissao(rst.getDate("rec_datalanc"));
                    imp.setDataVencimento(rst.getDate("rec_datavenc"));
                    imp.setNumeroCupom(rst.getString("rec_doc"));
                    imp.setValor(rst.getDouble("rec_valor"));
                    imp.setObservacao(rst.getString("rec_observacoes"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
