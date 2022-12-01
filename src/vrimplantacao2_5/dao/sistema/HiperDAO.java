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
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoSqlServer;

/**
 *
 * @author Wagner
 */
public class HiperDAO extends InterfaceDAO implements MapaTributoProvider {

    private String lojaCliente;

    public String getLojaCliente() {
        return this.lojaCliente;
    }

    @Override
    public String getSistema() {
        return "HIPER";
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
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL_REPLICAR,
                OpcaoProduto.MERCADOLOGICO_PRODUTO
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
                OpcaoFornecedor.TELEFONE,
                OpcaoFornecedor.SITUACAO_CADASTRO
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
                    "select DISTINCT\n"
                    + "    case \n"
                    + "    	when cst_icms.codigo_situacao_tributaria is null \n"
                    + "    	then '00'\n"
                    + "    	else cst_icms.codigo_situacao_tributaria\n"
                    + "    end cst_icms,\n"
                    + "    coalesce(icm.aliquota_icms, 0) as aliq_icms,\n"
                    + "    coalesce(icm.reducao_base_calculo, 0) as red_icms\n"
                    + "from \n"
                    + "	view_hiperpdv_produto_tributacao_icms icm\n"
                    + "    left join situacao_tributaria_icms cst_icms on cst_icms.id_situacao_tributaria_icms = icm.id_situacao_tributaria_icms\n"
                    + "    	and icm.uf_de = (select distinct c.uf from filial f, cidade c where f.id_cidade = c.id_cidade and codigo_filial = 1) and \n"
                    + "		icm.uf_para = (select distinct c.uf from filial f, cidade c where f.id_cidade = c.id_cidade and codigo_filial = 1)"
            )) {
                while (rs.next()) {
                    String id = rs.getString("cst_icms") + "-" + rs.getString("aliq_icms") + "-" + rs.getString("red_icms");
                    result.add(new MapaTributoIMP(
                            id,
                            id,
                            rs.getInt("cst_icms"),
                            rs.getDouble("aliq_icms"),
                            rs.getDouble("red_icms")));
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
                    "select \n"
                    + "id_hierarquia_produto as merc1,\n"
                    + "nome as merc1_descricao\n"
                    + "from hierarquia_produto\n"
                    + "where id_hierarquia_produto_pai is null\n"
                    + "order by cast(id_hierarquia_produto as integer)"
            )) {
                while (rst.next()) {
                    MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                    imp.setId(rst.getString("merc1"));
                    imp.setDescricao(rst.getString("merc1_descricao"));
                    merc.put(imp.getId(), imp);
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "id_hierarquia_produto_pai as merc1,\n"
                    + "sequencia as merc2,\n"
                    + "nome as merc2_descricao\n"
                    + "from hierarquia_produto\n"
                    + "where id_hierarquia_produto_pai is not null\n"
                    + "order by cast(id_hierarquia_produto_pai as integer), "
                    + " cast(sequencia as integer)"
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
                    "select \n"
                    + "id_hierarquia_produto_pai as merc1,\n"
                    + "sequencia as merc2,\n"
                    + "'1' as merc3,\n"
                    + "nome as merc3_descricao\n"
                    + "from hierarquia_produto\n"
                    + "where id_hierarquia_produto_pai is not null\n"
                    + "order by cast(id_hierarquia_produto_pai as integer), cast(sequencia as integer)"
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

    /*
    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	DISTINCT \n"
                    + "	COD_MARCA mercid1,\n"
                    + "	m.DESCRICAO descri1,\n"
                    + "	COALESCE(COD_SUBGRUPO,	COD_MARCA) mercid2,\n"
                    + "	COALESCE(s.DESCRICAO,	m.DESCRICAO) descri2,\n"
                    + "	COALESCE(COD_SUBGRUPO,	COD_MARCA) mercid3,\n"
                    + "	COALESCE(s.DESCRICAO,	m.DESCRICAO) descri3\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "LEFT JOIN MARCAS m ON m.CODIGO = p.COD_MARCA\n"
                    + "LEFT JOIN SUBGRUPOS s ON s.CODIGO = p.COD_SUBGRUPO"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setMerc1ID(rst.getString("mercid1"));
                    imp.setMerc1Descricao(rst.getString("descri1"));
                    imp.setMerc2ID(rst.getString("mercid2"));
                    imp.setMerc2Descricao(rst.getString("descri2"));
                    imp.setMerc3ID(rst.getString("mercid3"));
                    imp.setMerc3Descricao(rst.getString("descri3"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
     */
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoSqlServer.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "    p.id_produto, \n"
                    + "    e.codigo_barras,\n"
                    + "    e.sigla_unidade_logistica,\n"
                    + "    cad.multiplicador as qtdembalagem,\n"
                    + "    '1' as qtdembalagemean, \n"
                    + "    p.nome as descricao, \n"
                    + "    p.situacao,\n"
                    + "    u.sigla as tipoembalagem,\n"
                    + "    p.id_hierarquia_produto as mercadologico,\n"
                    + "    cast(p.data_hora_cadastro as date) as datacadastro,\n"
                    + "    p.preco_custo,\n"
                    + "    p.preco_aquisicao,\n"
                    + "    p.preco_venda,\n"
                    + "    pis.codigo_situacao_tributaria_pis as cst_pis,\n"
                    + "    pis.nome as desc_pis,\n"
                    + "	 pis_e.codigo_situacao_tributaria_pis cst_pis_e,\n"
                    + "	 pis_e.nome as desc_pis_e,\n"
                    + "    cofins.codigo_situacao_tributaria_cofins as cst_cofins,\n"
                    + "    cofins.nome as desc_cofins,\n"
                    + "	 cofins_e.codigo_situacao_tributaria_cofins cst_cofins_e,\n"
                    + "	 cofins_e.nome as desc_cofins_e,\n"
                    + "    nat.codigo_natureza_receita as naturezareceita,\n"
                    + "    nat2.codigo_natureza_receita as naturezareceita2,\n"
                    + "    p.id_ncm as ncm,\n"
                    + "    p.codigo_cest as cest,\n"
                    + "    p.dias_validade,\n"
                    + "    p.markup_varejo,\n"
                    + "    p.produto_integrado_balanca as balanca,\n"
                    + "    est.quantidade as estoque,\n"
                    + "    cst_icms.codigo_situacao_tributaria as cst_icms,\n"
                    + "    coalesce(icm.aliquota_icms, 0) as aliq_icms,\n"
                    + "    coalesce(icm.reducao_base_calculo, 0) as red_icms,\n"
                    + "    substring(rt.nome, 0, 3) cst_regra\n"
                    + "from produto p\n"
                    + "    inner join unidade_medida u on u.id_unidade_medida = p.id_unidade_medida\n"
                    + "LEFT JOIN cadastro_logistico_produto cad on p.id_produto = cad.id_produto\n"
                    + "    and cad.multiplicador = (SELECT MAX(multiplicador) from cadastro_logistico_produto c where c.id_produto = cad.id_produto) \n"
                    + "    left join produto_sinonimo e on e.id_produto = p.id_produto\n"
                    + "    left join hierarquia_produto m on m.id_hierarquia_produto = p.id_hierarquia_produto\n"
                    + "    left join situacao_tributaria_pis pis on pis.id_situacao_tributaria_pis = p.id_situacao_tributaria_pis\n"
                    + "    left join situacao_tributaria_cofins cofins on cofins.id_situacao_tributaria_cofins = p.id_situacao_tributaria_cofins\n"
                    + "	 left join situacao_tributaria_pis pis_e on pis_e.id_situacao_tributaria_pis = p.id_situacao_tributaria_pis_entrada\n"
                    + "    left join situacao_tributaria_cofins cofins_e on cofins_e.id_situacao_tributaria_cofins = p.id_situacao_tributaria_cofins_entrada\n"
                    + "    left join saldo_estoque est on est.id_produto = p.id_produto\n"
                    + "    left join natureza_receita_pis_cofins nat on nat.id_natureza_receita_pis_cofins = p.id_natureza_receita_pis\n"
                    + "    left join natureza_receita_pis_cofins nat2 on nat2.id_natureza_receita_pis_cofins = p.id_natureza_receita_cofins\n"
                    + "    left join view_hiperpdv_produto_tributacao_icms icm on icm.id_produto = p.id_produto\n"
                    + "    left join situacao_tributaria_icms cst_icms on cst_icms.id_situacao_tributaria_icms = icm.id_situacao_tributaria_icms\n"
                    + "    	and icm.uf_de = (select distinct c.uf from filial f, cidade c where f.id_cidade = c.id_cidade and codigo_filial = " + getLojaOrigem() + ") and \n"
                    + "		icm.uf_para = (select distinct c.uf from filial f, cidade c where f.id_cidade = c.id_cidade and codigo_filial = " + getLojaOrigem() + ")\n"
                    + "left join regra_tributacao_produto rp on p.id_produto = rp.id_produto\n"
                    + "left join regra_tributacao rt on rp.id_regra_tributacao = rt.id_regra_tributacao\n"
                    + "order by id_produto"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id_produto"));
                    imp.setEan(rst.getString("codigo_barras"));
                    imp.seteBalanca(rst.getInt("balanca") == 1);
                    imp.setSituacaoCadastro(rst.getInt("situacao") == 1 ? 1 : 0);

                    if (imp.isBalanca()) {
                        imp.setEan(imp.getImportId());
                    }

                    if (imp.getEan() != null && !imp.getEan().equals("") && imp.getEan().length() < 7) {
                        imp.setManterEAN(true);
                    }

                    imp.setValidade(rst.getInt("dias_validade"));
                    imp.setDescricaoCompleta(rst.getString("descricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagemean"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("tipoembalagem"));
                    imp.setTipoEmbalagemVolume(rst.getString("tipoembalagem"));

                    /*String merc = rst.getString("mercadologico") != null ? rst.getString("mercadologico") : "";
                    String[] cods = merc.split("\\.");

                    for (int i = 0; i < cods.length; i++) {
                        switch (i) {
                            case 0:
                                imp.setCodMercadologico1(cods[i]);
                                break;
                            case 1:
                                imp.setCodMercadologico2(cods[i]);
                                break;
                        }
                    }

                    imp.setCodMercadologico3("1");*/
                    imp.setCodMercadologico1(rst.getString("mercadologico"));
                    imp.setMargem(rst.getDouble("markup_varejo"));
                    imp.setCustoComImposto(rst.getDouble("preco_custo"));
                    imp.setCustoSemImposto(imp.getCustoComImposto());
                    imp.setPrecovenda(rst.getDouble("preco_venda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("cst_pis"));
                    imp.setPiscofinsCstCredito(rst.getString("cst_cofins_e"));
                    imp.setPiscofinsNaturezaReceita(rst.getString("naturezareceita"));

                    //Debito
                    imp.setIcmsCstSaida(rst.getInt("cst_icms"));
                    imp.setIcmsAliqSaida(rst.getDouble("aliq_icms"));
                    imp.setIcmsReducaoSaida(rst.getDouble("red_icms"));

                    String cst = rst.getString("cst_icms");

                    if (cst == null || cst.isEmpty()) {
                        imp.setIcmsCstSaida(rst.getInt("cst_regra"));
                    }

                    if (imp.getIcmsAliqSaida() == 0 && imp.getIcmsReducaoSaida() == 0 && imp.getIcmsCstSaida() == 0) {
                        imp.setIcmsAliqSaida(17);
                    }

                    //Credito
                    imp.setIcmsCstEntrada(imp.getIcmsCstSaida());
                    imp.setIcmsAliqEntrada(imp.getIcmsAliqSaida());
                    imp.setIcmsReducaoEntrada(rst.getDouble("red_icms"));

                    imp.setIcmsCstEntradaForaEstado(imp.getIcmsCstSaida());
                    imp.setIcmsAliqEntradaForaEstado(imp.getIcmsAliqSaida());
                    imp.setIcmsReducaoEntradaForaEstado(rst.getDouble("red_icms"));

                    //Consumidor
                    imp.setIcmsCstConsumidor(imp.getIcmsCstSaida());
                    imp.setIcmsAliqConsumidor(imp.getIcmsAliqSaida());
                    imp.setIcmsReducaoConsumidor(rst.getDouble("red_icms"));

                    imp.setIcmsCstSaidaForaEstado(imp.getIcmsCstSaida());
                    imp.setIcmsAliqSaidaForaEstado(imp.getIcmsAliqSaida());
                    imp.setIcmsReducaoSaidaForaEstado(rst.getDouble("red_icms"));

                    imp.setIcmsCstSaidaForaEstadoNF(imp.getIcmsCstSaida());
                    imp.setIcmsAliqSaidaForaEstadoNF(imp.getIcmsAliqSaida());
                    imp.setIcmsReducaoSaidaForaEstadoNF(rst.getDouble("red_icms"));

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
                    "select\n"
                    + "f.id_entidade as id,\n"
                    + "f.nome as razao,\n"
                    + "j.nome_fantasia as fantasia,\n"
                    + "j.cnpj,\n"
                    + "j.ie,\n"
                    + "fi.cpf,\n"
                    + "fi.rg,\n"
                    + "cast(f.data_hora_cadastro as date) as datacadastro,\n"
                    + "f.logradouro as endereco,\n"
                    + "f.numero_endereco,\n"
                    + "f.bairro,\n"
                    + "f.complemento,\n"
                    + "f.cep,\n"
                    + "c.id_ibge as ibge_cidade,\n"
                    + "c.nome as cidade,\n"
                    + "c.uf,\n"
                    + "f.site,\n"
                    + "f.observacao,\n"
                    + "f.fone_primario_ddd as ddd1,\n"
                    + "f.fone_primario_numero as telefone1,\n"
                    + "f.fone_primario_nome_contato as contato1,\n"
                    + "f.fone_secundario_ddd as ddd2,\n"
                    + "f.fone_secundario_numero as telefone2,\n"
                    + "f.fone_secundario_nome_contato as contato2,\n"
                    + "f.email,\n"
                    + "f.logradouro_cobranca as endereco_cobranca,\n"
                    + "f.numero_endereco_cobranca as numero_cobranca,\n"
                    + "f.bairro_cobranca,\n"
                    + "f.complemento_cobranca,\n"
                    + "f.cep_cobranca,\n"
                    + "cc.id_ibge as cidade_ibge_cobranca,\n"
                    + "cc.nome as cidade_cobranca, \n"
                    + "cc.uf as uf_cobranca,\n"
                    + "f.inativo,\n"
                    + "f.flag_funcionario,\n"
                    + "f.flag_cliente\n"
                    + "from entidade f \n"
                    + "left join pessoa_juridica j on j.id_entidade = f.id_entidade\n"
                    + "left join pessoa_fisica fi on fi.id_entidade = f.id_entidade\n"
                    + "left join cidade c on c.id_cidade = f.id_cidade\n"
                    + "left join cidade cc on cc.id_cidade = f.id_cidade_cobranca\n"
                    + "where flag_fornecedor = 1"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie"));
                    imp.setAtivo(rst.getInt("inativo") == 0);
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero_endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setIbge_municipio(rst.getInt("ibge_cidade"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTel_principal(rst.getString("ddd1") + rst.getString("telefone1"));
                    imp.setCob_endereco(rst.getString("endereco_cobranca"));
                    imp.setCob_numero(rst.getString("numero_cobranca"));
                    imp.setCob_bairro(rst.getString("bairro_cobranca"));
                    imp.setCob_complemento(rst.getString("complemento_cobranca"));
                    imp.setCob_cep(rst.getString("cep_cobranca"));
                    imp.setCob_ibge_municipio(rst.getInt("cidade_ibge_cobranca"));
                    imp.setCob_municipio(rst.getString("cidade_cobranca"));
                    imp.setCob_uf(rst.getString("uf_cobranca"));
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
                    + "id_produto, \n"
                    + "id_entidade as id_fornecedor,\n"
                    + "(cast(id_entidade as varchar)+cast(id_produto as varchar)) as codigoexterno\n"
                    + "from produto_fornecedor"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("id_produto"));
                    imp.setIdFornecedor(rst.getString("id_fornecedor"));
                    imp.setCodigoExterno(rst.getString("codigoexterno"));
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
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	id_produto,\n"
                    + "	codigo_barras,\n"
                    + "	sigla_unidade_logistica embalagem\n"
                    + "from \n"
                    + "	produto_sinonimo")) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("codigo_barras"));
                    imp.setTipoEmbalagem(rs.getString("embalagem"));

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
                    "select\n"
                    + "f.id_entidade as id,\n"
                    + "f.nome as razao,\n"
                    + "j.nome_fantasia as fantasia,\n"
                    + "j.cnpj,\n"
                    + "j.ie,\n"
                    + "fi.cpf,\n"
                    + "fi.rg,\n"
                    + "cast(f.data_hora_cadastro as date) as datacadastro,\n"
                    + "f.logradouro as endereco,\n"
                    + "f.numero_endereco,\n"
                    + "f.bairro,\n"
                    + "f.complemento,\n"
                    + "f.cep,\n"
                    + "c.id_ibge as ibge_cidade,\n"
                    + "c.nome as cidade,\n"
                    + "c.uf,\n"
                    + "f.site,\n"
                    + "f.observacao,\n"
                    + "f.fone_primario_ddd as ddd1,\n"
                    + "f.fone_primario_numero as telefone1,\n"
                    + "f.fone_primario_nome_contato as contato1,\n"
                    + "f.fone_secundario_ddd as ddd2,\n"
                    + "f.fone_secundario_numero as telefone2,\n"
                    + "f.fone_secundario_nome_contato as contato2,\n"
                    + "f.email,\n"
                    + "f.logradouro_cobranca as endereco_cobranca,\n"
                    + "f.numero_endereco_cobranca as numero_cobranca,\n"
                    + "f.bairro_cobranca,\n"
                    + "f.complemento_cobranca,\n"
                    + "f.cep_cobranca,\n"
                    + "cc.id_ibge as cidade_ibge_cobranca,\n"
                    + "cc.nome as cidade_cobranca, \n"
                    + "cc.uf as uf_cobranca,\n"
                    + "f.inativo,\n"
                    + "f.flag_funcionario,\n"
                    + "f.flag_cliente,\n"
                    + "f.limite_credito\n"
                    + "from entidade f \n"
                    + "left join pessoa_juridica j on j.id_entidade = f.id_entidade\n"
                    + "left join pessoa_fisica fi on fi.id_entidade = f.id_entidade\n"
                    + "left join cidade c on c.id_cidade = f.id_cidade\n"
                    + "left join cidade cc on cc.id_cidade = f.id_cidade_cobranca\n"
                    + "where flag_cliente = 1"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj(rst.getString("cnpj") == null ? rst.getString("cpf") : rst.getString("cnpj"));
                    imp.setInscricaoestadual(rst.getString("ie"));
                    imp.setAtivo(rst.getInt("inativo") == 0);
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero_endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setCep(rst.getString("cep"));
                    imp.setMunicipioIBGE(rst.getInt("ibge_cidade"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("uf"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setTelefone(rst.getString("ddd1") + rst.getString("telefone1"));
                    imp.setCobrancaEndereco(rst.getString("endereco_cobranca"));
                    imp.setCobrancaNumero(rst.getString("numero_cobranca"));
                    imp.setCobrancaBairro(rst.getString("bairro_cobranca"));
                    imp.setCobrancaComplemento(rst.getString("complemento_cobranca"));
                    imp.setCobrancaCep(rst.getString("cep_cobranca"));
                    imp.setCobrancaMunicipioIBGE(rst.getInt("cidade_ibge_cobranca"));
                    imp.setCobrancaMunicipio(rst.getString("cidade_cobranca"));
                    imp.setCobrancaUf(rst.getString("uf_cobranca"));
                    imp.setValorLimite(rst.getDouble("limite_credito"));
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
            try (ResultSet rs = stm.executeQuery(
                    "SELECT \n"
                    + "	id_documento_receber,\n"
                    + "	id_tipo_documento_financeiro,\n"
                    + "	id_entidade,\n"
                    + "	data_emissao,\n"
                    + "	data_vencimento,\n"
                    + "	data_quitacao,\n"
                    + "	valor,\n"
                    + "	saldo,\n"
                    + "	situacao,\n"
                    + "	numero_documento_receber,\n"
                    + "	descricao,\n"
                    + "	id_entidade_portador,\n"
                    + "	id_centro_lucro\n"
                    + "FROM \n"
                    + "	documento_receber\n"
                    + "where\n"
                    + "	situacao = 1 and \n"
                    + "	id_filial_geracao = " + getLojaOrigem() + " and\n"
                    + "   id_tipo_documento_financeiro in (3, 103)\n"
                    + "   /*\n"
                    + "    * 3 crediatrio\n"
                    + "    * 104 deposito bancario\n"
                    + "    * 2 cheque\n"
                    + "    * 103 boleto\n"
                    + "    * 100 cartoes deb ou cred\n"
                    + "    *\n"
                    + "    */"
            /*and \n" +
                    "	id_entidade not in (6, 90, 91, 92, 96, 93)"*/)) {
                while (rs.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rs.getString("id_documento_receber"));
                    imp.setIdCliente(rs.getString("id_entidade"));
                    imp.setDataEmissao(rs.getDate("data_emissao"));
                    imp.setDataVencimento(rs.getDate("data_vencimento"));
                    imp.setValor(rs.getDouble("saldo"));
                    imp.setNumeroCupom(rs.getString("numero_documento_receber"));
                    imp.setObservacao(rs.getString("descricao"));

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
        return new HiperDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new HiperDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
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
                    = "";
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
                    = "";
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
