/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2_5.dao.sistema;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import org.jdesktop.swingx.JXDatePicker;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoProduto;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Wagner
 */
public class Lince2_5DAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "LINCE";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.ESTOQUE_MINIMO,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.EXCECAO,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.PDV_VENDA
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.RAZAO_SOCIAL,
                OpcaoFornecedor.NOME_FANTASIA,
                OpcaoFornecedor.CNPJ_CPF,
                OpcaoFornecedor.INSCRICAO_ESTADUAL,
                OpcaoFornecedor.INSCRICAO_MUNICIPAL,
                OpcaoFornecedor.PRODUTO_FORNECEDOR,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.TELEFONE
        ));
    }

    @Override
    public Set<OpcaoCliente> getOpcoesDisponiveisCliente() {
        return new HashSet<>(Arrays.asList(
                OpcaoCliente.DADOS,
                OpcaoCliente.ENDERECO,
                OpcaoCliente.CONTATOS,
                OpcaoCliente.DATA_CADASTRO,
                OpcaoCliente.DATA_NASCIMENTO,
                OpcaoCliente.TELEFONE,
                OpcaoCliente.ESTADO_CIVIL,
                OpcaoCliente.EMPRESA,
                OpcaoCliente.SALARIO,
                OpcaoCliente.BLOQUEADO,
                OpcaoCliente.OBSERVACOES2,
                OpcaoCliente.OBSERVACOES,
                OpcaoCliente.NUMERO,
                OpcaoCliente.COMPLEMENTO,
                OpcaoCliente.SITUACAO_CADASTRO,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select distinct\n"
                    + "	case p.flg_situacao_trib \n"
                    + "	when 'T' then 0\n"
                    + "	when 'F' then 60\n"
                    + "	when 'N' then 41\n"
                    + "	else 40\n"
                    + "	end	icms_cst,\n"
                    + "	case p.flg_situacao_trib\n"
                    + "	when 'T' then coalesce(icms.percentual, 0)\n"
                    + "	else 0\n"
                    + "	end icms_aliquota\n"
                    + "from \n"
                    + "	produto p\n"
                    + "	left join icms on icms.cod_icms = p.cod_icms and icms.cod_loja = 1\n"
                    + "order by\n"
                    + "	1, 2"
            )) {
                while (rs.next()) {
                    String id = rs.getString("icms_cst") + "-" + String.format("%.2f", rs.getDouble("icms_aliquota"));
                    result.add(new MapaTributoIMP(
                            id,
                            String.format("%03d", rs.getInt("icms_cst")) + "-" + String.format("%.2f", rs.getDouble("icms_aliquota"))
                    ));
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
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "declare @id_loja integer = " + getLojaOrigem() + ";\n"
                    + "select\n"
                    + "	p.COD_PROD id,\n"
                    + "	p.DATA_MUDANCA_PRECO datacadastro,\n"
                    + "	coalesce(p.DATA_MUDANCA, p.DATA_MUDANCA_PRECO) dataultimaalteracao,\n"
                    + "	ean.ean ean,\n"
                    + "	p.FLG_UNIDADE_VENDA unidade,\n"
                    + "	coalesce(sec.CHK_BALANCA,subd.chk_balanca,dp.CHK_BALANCA) chk_balanca,\n"
                    + "	coalesce(sec.CHK_BALANCA_KG,subd.chk_balanca_kg,dp.CHK_BALANCA_KG) chk_balanca_kg,\n"
                    + "	p.VALIDADE validade,\n"
                    + "	p.DESCRICAO descricao,\n"
                    + "	p.COD_FORN,\n"
                    + "	nullif(p.COD_DEPART, 0) merc1,\n"
                    + "	nullif(p.COD_SUBDEPART, 0) merc2,\n"
                    + "	nullif(p.COD_SECAO, 0) merc3,\n"
                    + "	p.PESO_BRUTO peso_bruto,\n"
                    + "	p.PESO_LIQUIDO peso_liquido,\n"
                    + "	p.QTDE_ESTOQUE_MINIMO estoque_minimo,\n"
                    + "	p.QTDE_ESTOQUE_MAXIMO estoque_maximo,\n"
                    + "	p.QTDE_ESTOQUE_LOJA estoque,\n"
                    + "	p.perc_margem margem,\n"
                    + "	p.vlr_custo_receita custo,\n"
                    + "	p.VLR_PRECO preco,\n"
                    + "	case when p.CHK_ATIVO = 'T' then 1 else 0 end ativo,\n"
                    + "	p.COD_NCM ncm,\n"
                    + "	cest.CEST cest,\n"
                    + "	p.COD_PIS piscofins_saida,\n"
                    + "	p.nat_rec_pis piscofins_nat,\n"
                    + "	case p.flg_situacao_trib \n"
                    + "	when 'T' then 0\n"
                    + "	when 'F' then 60\n"
                    + "	when 'N' then 41\n"
                    + "	else 40\n"
                    + "	end	icms_cst,\n"
                    + "	case p.flg_situacao_trib\n"
                    + "	when 'T' then coalesce(icms.percentual, 0)\n"
                    + "	else 0\n"
                    + "	end icms_aliquota,\n"
                    + "	p.cod_tipo_prod tipo_produto\n"
                    + "from \n"
                    + "	produto p\n"
                    + "	left join (\n"
                    + "		select\n"
                    + "		cod_loja,\n"
                    + "		p.cod_prod,\n"
                    + "		p.codigo_barras ean\n"
                    + "	from\n"
                    + "		produto p\n"
                    + "	union\n"
                    + "	select\n"
                    + "		cod_loja,\n"
                    + "		cod_prod,\n"
                    + "		codigo_barras ean\n"
                    + "	from\n"
                    + "		barras_vinculada b\n"
                    + "	where not codigo_barras in (\n"
                    + "		select codigo_barras from produto where cod_loja = 1\n"
                    + "		union \n"
                    + "		select\n"
                    + "		nullif(coalesce(ltrim(rtrim(barras_embalagem)),''), '0')\n"
                    + "		from produto \n"
                    + "		where cod_loja = 1 and nullif(coalesce(ltrim(rtrim(barras_embalagem)),''), '0') != '')\n"
                    + "	) ean on p.cod_prod = ean.cod_prod and ean.COD_LOJA = @id_loja\n"
                    + "	left join PRODUTO_CEST cest on cest.id = p.id_cest\n"
                    + "	left join icms on icms.cod_icms = p.cod_icms and icms.cod_loja = @id_loja\n"
                    + "	left join DEPARTAMENTO dp on p.COD_DEPART = dp.COD_DEPART and dp.COD_LOJA = @id_loja\n"
                    + "	left join SUB_DEPARTAMENTO subd on p.COD_DEPART = subd.COD_DEPART and p.COD_SUBDEPART = subd.COD_SUBDEPART and subd.COD_LOJA = @id_loja\n"
                    + "	left join SECAO sec on p.COD_SECAO = sec.COD_SECAO and sec.COD_LOJA = @id_loja\n"
                    + "where\n"
                    + "	p.cod_loja = @id_loja\n"
                    + "order by id"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setDataAlteracao(rst.getDate("dataultimaalteracao"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));
                    imp.setValidade(rst.getInt("validade"));
                    if ("T".equals(rst.getString("chk_balanca")) || "KG".equals(imp.getTipoEmbalagem())) {
                        if ("T".equals(rst.getString("chk_balanca_kg"))) {
                            imp.seteBalanca("KG".equals(imp.getTipoEmbalagem()));
                        } else {
                            imp.seteBalanca(true);
                        }
                    } else {
                        imp.seteBalanca(false);
                    }
                    imp.setEan(rst.getString("ean"));
                    if (imp.getEan().startsWith("789000" + String.format("%06d", rst.getInt("id")))) {
                        imp.setEan(imp.getImportId());
                    }
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setFornecedorFabricante(rst.getString("COD_FORN"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(rst.getString("merc3"));
                    imp.setPesoBruto(rst.getDouble("peso_bruto"));
                    imp.setPesoLiquido(rst.getDouble("peso_liquido"));
                    imp.setEstoqueMinimo(rst.getDouble("estoque_minimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoque_maximo"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("preco"));
                    imp.setSituacaoCadastro(rst.getInt("ativo") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofins_saida"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("piscofins_nat"));

                    int codigoProduto = Utils.stringToInt(rst.getString("id"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);
                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        if (imp.getEan().startsWith("789000" + String.format("%06d", rst.getInt("id")))) {
                            imp.setEan(imp.getImportId());
                        }
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(0);
                    }

                    String idIcms = rst.getString("icms_cst") + "-" + String.format("%.2f", rst.getDouble("icms_aliquota"));
                    imp.setIcmsDebitoId(idIcms);
                    imp.setIcmsDebitoForaEstadoId(idIcms);
                    imp.setIcmsDebitoForaEstadoNfId(idIcms);
                    imp.setIcmsCreditoId(idIcms);
                    imp.setIcmsCreditoForaEstadoId(idIcms);
                    imp.setIcmsConsumidorId(idIcms);

//                    imp.setIcmsCst(Utils.stringToInt(rst.getString("icms_cst")));
//                    imp.setIcmsAliq(rst.getDouble("icms_aliquota"));
                    if (imp.getDescricaoCompleta().startsWith("MB ")) {
                        imp.setTipoProduto(TipoProduto.MERCADORIA_REVENDA);
                    } else {
                        imp.setTipoProduto(rst.getString("tipo_produto"));
                    }
                    imp.setFabricacaoPropria(imp.getTipoProduto() == TipoProduto.PRODUTO_ACABADO);

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
                    imp.addTelefone("FAX", rst.getString("FAX"));
                    imp.addEmail("NFE", rst.getString("EMAIL"), TipoContato.NFE);
                    imp.addContato(
                            rst.getString("CONTATO"),
                            rst.getString("TEL_CONTATO"),
                            null,
                            TipoContato.COMERCIAL,
                            null
                    );
                    imp.addContato(
                            rst.getString("REPRESENTANTE"),
                            rst.getString("TEL_REPRESENTANTE"),
                            null,
                            TipoContato.COMERCIAL,
                            null
                    );
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
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "declare @id_loja integer = " + getLojaOrigem() + ";\n"
                    + "select\n"
                    + "	p.COD_PROD produtoid,\n"
                    + "	ean.ean ean,\n"
                    + "	p.FLG_UNIDADE_VENDA unidade\n"
                    + "from \n"
                    + "	produto p\n"
                    + "	left join (\n"
                    + "		select\n"
                    + "		cod_loja,\n"
                    + "		p.cod_prod,\n"
                    + "		p.codigo_barras ean\n"
                    + "	from\n"
                    + "		produto p\n"
                    + "	union\n"
                    + "	select\n"
                    + "		cod_loja,\n"
                    + "		cod_prod,\n"
                    + "		codigo_barras ean\n"
                    + "	from\n"
                    + "		barras_vinculada b\n"
                    + "	where not codigo_barras in (\n"
                    + "		select codigo_barras from produto where cod_loja = 1\n"
                    + "		union \n"
                    + "		select\n"
                    + "		nullif(coalesce(ltrim(rtrim(barras_embalagem)),''), '0')\n"
                    + "		from produto \n"
                    + "		where cod_loja = 1 and nullif(coalesce(ltrim(rtrim(barras_embalagem)),''), '0') != '')\n"
                    + "	) ean on p.cod_prod = ean.cod_prod and ean.COD_LOJA = @id_loja\n"
                    + "	left join PRODUTO_CEST cest on cest.id = p.id_cest\n"
                    + "	left join icms on icms.cod_icms = p.cod_icms and icms.cod_loja = @id_loja\n"
                    + "	left join DEPARTAMENTO dp on p.COD_DEPART = dp.COD_DEPART and dp.COD_LOJA = @id_loja\n"
                    + "	left join SUB_DEPARTAMENTO subd on p.COD_DEPART = subd.COD_DEPART and p.COD_SUBDEPART = subd.COD_SUBDEPART and subd.COD_LOJA = @id_loja\n"
                    + "	left join SECAO sec on p.COD_SECAO = sec.COD_SECAO and sec.COD_LOJA = @id_loja\n"
                    + "where\n"
                    + "	p.cod_loja = @id_loja\n"
                    + "order by id"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("produtoid"));
                    imp.setEan(rst.getString("ean"));
                    if (imp.getEan().startsWith("789000" + String.format("%06d", rst.getInt("produtoid")))) {
                        imp.setEan(imp.getImportId());
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

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        JXDatePicker dataInserida = new org.jdesktop.swingx.JXDatePicker();
        //JOptionPane.showOptionDialog(null, dataInserida, "Escolha a data inicial", 0, 0, null, null, dataInserida);
        int resposta = JOptionPane.showConfirmDialog(null, dataInserida, "Selecione o tipo de conta", JOptionPane.OK_OPTION, JOptionPane.QUESTION_MESSAGE);
        if (resposta != JOptionPane.YES_OPTION){
            dataInserida = null;
        }
        System.out.println(dataInserida.getDate() == null ? "" : new SimpleDateFormat("yyyy-MM-dd").format(dataInserida.getDate()));

        
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	l.lancamento,\n"
                    + "	l.cod_forn,\n"
                    + "	coalesce(l.documento, l.origem) documento,\n"
                    + "	l.data,\n"
                    + "	l.vencimento,\n"
                    + "	l.vlr_valor valor,\n"
                    + "	l.descricao,\n"
                    + "	l.observacao,\n"
                    + "	doc.descricao tipo_doc\n"
                    + "from\n"
                    + "	FIN_LANCAMENTO l\n"
                    + "	left join fin_tipo_doc doc on\n"
                    + "		l.COD_TIPO_DOC = doc.COD_TIPO_DOC and\n"
                    + "		l.cod_loja = doc.cod_loja\n"
                    + "where\n"
                    + "	l.cod_loja = " + getLojaOrigem() + " and\n"
                    + (dataInserida.getDate() == null ? "" : " l.vencimento >= '" + new SimpleDateFormat("yyyy-MM-dd").format(dataInserida.getDate()) + "' and\n")
                    + "	l.pagamento is null\n"
                    + "order by\n"
                    + "	l.data"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("lancamento"));
                    imp.setIdFornecedor(rst.getString("cod_forn"));
                    imp.setDataEntrada(rst.getDate("data"));
                    imp.setDataEmissao(rst.getDate("data"));
                    imp.setDataHoraAlteracao(rst.getTimestamp("data"));
                    imp.setNumeroDocumento(rst.getString("documento"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(
                            "DESCRICAO: " + rst.getString("descricao")
                            + "   OBSERVACAO: " + rst.getString("observacao")
                    //                            + "   TIPO DOC.: " + rst.getString("tipo_doc")
                    );
                    imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ReceitaIMP> getReceitas() throws Exception {
        List<ReceitaIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "r.COD_PROD,\n"
                    + "p.DESCRICAO as DESC_PROD,\n"
                    + "p.FLG_UNIDADE_VENDA as unidade,\n"
                    + "coalesce(r.NOME_ETIQUETA, p.DESCRICAO) as NOMERECEITA,\n"
                    + "r.VLR_TOTAL,\n"
                    + "r.VLR_PRECO_UND,\n"
                    + "r.VLR_PRECO_KG,\n"
                    + "r.VLR_TOTAL_EMBALAGEM,\n"
                    + "r.QTDE_PRODUCAO_KG,\n"
                    + "r.QTDE_INGREDIENTE,\n"
                    + "r.QTDE_PRODUCAO_UND,\n"
                    + "r.QTDE_MINIMA,\n"
                    + "r.QTDE_MAXIMA,\n"
                    + "r.QTDE_PERDA,\n"
                    + "r.PERC_PERDA,\n"
                    + "r.OBS1 as RECEITA,\n"
                    + "r.OBS_ETIQUETA,\n"
                    + "pc.COD_PROD as ITEM,\n"
                    + "pc.DESCRICAO as DESC_PROD_ITEM,\n"
                    + "c.PESO,\n"
                    + "c.VALOR,\n"
                    + "c.PERCENTUAL\n"
                    + "from RECEITA r\n"
                    + "inner join PRODUTO p on p.COD_PROD = r.COD_PROD\n"
                    + "inner join COMPOSICAO c on c.COD_PROD = r.COD_PROD\n"
                    + "inner join PRODUTO pc on pc.COD_PROD = c.COD_PROD_COMP\n"
                    + "where r.COD_LOJA = " + getLojaOrigem() + "\n"
                    + "order by r.COD_PROD\n"
            )) {
                while (rst.next()) {

                    double qtdEmbUtilizado = 0;
                    qtdEmbUtilizado = rst.getDouble("PESO") * 1000;

                    ReceitaIMP imp = new ReceitaIMP();
                    imp.setImportloja(getLojaOrigem());
                    imp.setImportsistema(getSistema());
                    imp.setImportid(rst.getString("COD_PROD"));
                    imp.setIdproduto(rst.getString("COD_PROD"));
                    imp.setDescricao(rst.getString("NOMERECEITA"));
                    imp.setFichatecnica(rst.getString("RECEITA"));
                    imp.setQtdembalagemreceita((int) qtdEmbUtilizado);
                    imp.setQtdembalagemproduto((rst.getInt("QTDE_PRODUCAO_UND") == 0 ? 1 : rst.getInt("QTDE_PRODUCAO_UND")) * 1000);

                    if ((rst.getDouble("QTDE_PRODUCAO_KG") > 0)
                            && (rst.getDouble("QTDE_PRODUCAO_UND") == 0)) {

                        imp.setRendimento(rst.getDouble("QTDE_PRODUCAO_KG"));

                    } else if ((rst.getDouble("QTDE_PRODUCAO_KG") == 0)
                            && (rst.getDouble("QTDE_PRODUCAO_UND") > 0)) {

                        imp.setRendimento(rst.getDouble("QTDE_PRODUCAO_UND"));

                    } else if ((rst.getDouble("QTDE_PRODUCAO_KG") > 0)
                            && (rst.getDouble("QTDE_PRODUCAO_UND") > 0)) {

                        if (rst.getString("unidade").contains("KG")) {

                            imp.setRendimento(rst.getDouble("QTDE_PRODUCAO_KG"));

                        } else {

                            imp.setRendimento(rst.getDouble("QTDE_PRODUCAO_UND"));
                        }

                    } else {
                        imp.setRendimento(0);
                    }

                    imp.getProdutos().add(rst.getString("ITEM"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new Lince2_5DAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new Lince2_5DAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaIMP next;
        private Set<String> uk = new HashSet<>();

        private void obterNext() {
            try {
                SimpleDateFormat timestampDate = new SimpleDateFormat("yyyy-MM-dd");
                SimpleDateFormat timestamp = new SimpleDateFormat("yyyy-MM-dd hh:mm");

                if (next == null) {
                    if (rst.next()) {
                        next = new VendaIMP();
                        String id = rst.getString("id_venda");
                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }
                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("hora");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("valor"));
                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpf"));
                        next.setNomeCliente(rst.getString("nomecliente"));
                        next.setCancelado(rst.getBoolean("cancelado"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {

            String strDataInicio = new SimpleDateFormat("yyyy-MM-dd").format(dataInicio);
            String strDataTermino = new SimpleDateFormat("yyyy-MM-dd").format(dataTermino);
            this.sql
                    = "SELECT\n"
                    + "	c.seq id_venda,\n"
                    + "	CASE WHEN c.SEQ IS NULL THEN c.SEQ||'-'||c.NUM_OPER ELSE c.SEQ END numerocupom,\n"
                    + "	c.COD_CAIXA ecf,\n"
                    + "	c.DATA data,\n"
                    + "	c.HORA hora,\n"
                    + "	c.VALORTOTAL valor,\n"
                    + "	c.DESCONTO desconto,\n"
                    + "	c.COD_CLI id_cliente,\n"
                    + "	cc.CGC cpf,\n"
                    + "	cc.NOME nomecliente,\n"
                    + "	CASE WHEN c.SITUACAO = 'A' THEN 0 ELSE 1 END cancelado\n"
                    + "FROM\n"
                    + "	CAIXA c\n"
                    + "	LEFT JOIN CADCLI cc ON c.COD_CLI = cc.CODIGO \n"
                    + "WHERE\n"
                    + "	c.DATA BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "	AND c.NATUREZA = 500";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaIMP next() {
            obterNext();
            VendaIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }

    private static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoSqlServer.getConexao().createStatement();
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();

                        next.setVenda(rst.getString("id_venda"));
                        next.setId(rst.getString("id_item"));
                        next.setSequencia(rst.getInt("nritem"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("valor"));
                        next.setValorDesconto(rst.getDouble("desconto"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "WITH teste AS (SELECT NUM_OPER, max(NUMEROITEM) n FROM venda GROUP BY 1)\n"
                    + "SELECT  \n"
                    + " v.NUMEROITEM nritem,\n"
                    + "	c.SEQ id_venda,\n"
                    + "	v.SEQ id_item,\n"
                    + "	v.COD_PROD id_produto,\n"
                    + "	v.NOME_PROD descricao,\n"
                    + "	v.QUANTIDADE quantidade,\n"
                    + "	v.PRECO_VEND valor,\n"
                    + "	v.UNIDADE unidade,\n"
                    + "	(c.DESCONTO/t.n) desconto\n"
                    + "FROM\n"
                    + " VENDA v \n"
                    + " JOIN CAIXA c ON v.NUM_OPER = c.NUM_OPER \n"
                    + " JOIN teste t ON t.num_oper = v.NUM_OPER \n"
                    + "WHERE v.DATA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'\n"
                    + "	AND NATUREZA = 500\n"
                    + "GROUP BY 1, 2, 3, 4, 5, 6, 7 ,8 , 9";
            LOG.log(Level.FINE, "SQL da venda: " + sql);
            rst = stm.executeQuery(sql);
        }

        @Override
        public boolean hasNext() {
            obterNext();
            return next != null;
        }

        @Override
        public VendaItemIMP next() {
            obterNext();
            VendaItemIMP result = next;
            next = null;
            return result;
        }

        @Override
        public void remove() {
            throw new UnsupportedOperationException("Not supported.");
        }
    }
}
