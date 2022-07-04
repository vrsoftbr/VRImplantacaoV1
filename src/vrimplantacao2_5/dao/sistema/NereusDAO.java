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
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.cliente.OpcaoCliente;
import vrimplantacao2.dao.cadastro.fornecedor.OpcaoFornecedor;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoPostgres;

/**
 *
 * @author Alan
 */
public class NereusDAO extends InterfaceDAO implements MapaTributoProvider {

    @Override
    public String getSistema() {
        return "Nereus";
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.DATA_CADASTRO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.MERCADOLOGICO,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.FAMILIA,
                OpcaoProduto.FAMILIA_PRODUTO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.TROCA,
                OpcaoProduto.MARGEM,
                OpcaoProduto.VENDA_PDV,
                OpcaoProduto.PDV_VENDA,
                OpcaoProduto.PRECO,
                OpcaoProduto.CUSTO,
                OpcaoProduto.CUSTO_COM_IMPOSTO,
                OpcaoProduto.CUSTO_SEM_IMPOSTO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.NATUREZA_RECEITA,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.ATUALIZAR_SOMAR_ESTOQUE,
                OpcaoProduto.OFERTA,
                OpcaoProduto.DESCONTINUADO,
                OpcaoProduto.VOLUME_QTD,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.FABRICANTE
        ));
    }

    @Override
    public Set<OpcaoFornecedor> getOpcoesDisponiveisFornecedor() {
        return new HashSet<>(Arrays.asList(
                OpcaoFornecedor.ENDERECO,
                OpcaoFornecedor.DADOS,
                OpcaoFornecedor.CONTATOS,
                OpcaoFornecedor.SITUACAO_CADASTRO,
                OpcaoFornecedor.TIPO_EMPRESA,
                OpcaoFornecedor.PAGAR_FORNECEDOR,
                OpcaoFornecedor.PRODUTO_FORNECEDOR
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
                OpcaoCliente.VENCIMENTO_ROTATIVO,
                OpcaoCliente.CLIENTE_EVENTUAL,
                OpcaoCliente.RECEBER_CREDITOROTATIVO));
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	aliq.id_grade_trib id,\n"
                    + "	cst.codigo||'-'||aliq.per_icms||'-'||aliq.per_red_icms descricao,\n"
                    + "	cst.codigo cst_saida,\n"
                    + "	aliq.per_icms aliquota_saida,\n"
                    + "	aliq.per_red_icms reducao_saida\n"
                    + "from\n"
                    + "	fs_grade_trib_aliq aliq\n"
                    + "	join tb_cst cst on aliq.id_cst = cst.id_cst and cst.tipo_imposto = 'ICMS'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    result.add(new MapaTributoIMP(
                            rs.getString("id"),
                            rs.getString("descricao"),
                            rs.getInt("cst_saida"),
                            rs.getDouble("aliquota_saida"),
                            rs.getDouble("reducao_saida"))
                    );
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	e.id_prod id_produto,\n"
                    + "	ean13 ean,\n"
                    + "	coalesce(f.fator,1) qtde_emb,\n"
                    + "	u.sigla tipo_emb\n"
                    + "from\n"
                    + "	eq_prod_ean e\n"
                    + "	join eq_prod p on p.id_prod = e.id_prod \n"
                    + "	join tb_unid u on u.id_unid = p.id_unid_v \n"
                    + "	left join tb_fatorcx f on f.id_fatorcx = e.id_fatorcx\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id_produto"));
                    imp.setEan(rs.getString("ean"));
                    imp.setQtdEmbalagem(rs.getInt("qtde_emb"));
                    imp.setTipoEmbalagem(rs.getString("tipo_emb"));

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
                    "WITH estoque AS (\n"
                    + "SELECT\n"
                    + "	CODIPRO,\n"
                    + "	max(CAST(ANO || '-' || MES || '-' || '01' AS date)) AS DATA\n"
                    + "	FROM ESTOSI\n"
                    + "	WHERE EMP_CODIGO = " + getLojaOrigem() + "\n"
                    + "	GROUP BY CODIPRO)\n"
                    + "SELECT\n"
                    + "	p.CODIPRO idproduto,\n"
                    + "	CASE\n"
                    + "		WHEN CAST(ean.COD_BARR AS bigint) > 999999\n"
                    + "	  	THEN ean.COD_BARR ||(SELECT * FROM SP_PAF_DIGITO_EAN13(ean.COD_BARR))\n"
                    + "		ELSE ean.COD_BARR\n"
                    + "	END ean,\n"
                    + "	DESCRICAO descricaocompleta,\n"
                    + "	DESCRI_AB descricaoreduzida,\n"
                    + "	un_v.ABRE_EMB tipoembalagem,\n"
                    + "	un_c.ABRE_EMB emb_compra,\n"
                    + "	ean.QTD_UNI qtdembalagem,\n"
                    + "	PESO_BRU pesobruto,\n"
                    + "	PESO_LIQ pesoliquido,\n"
                    + "	CASE\n"
                    + "		WHEN balanca = 'S' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END e_balanca,\n"
                    + "	CODIFAM familia,\n"
                    + "	p.codifab fabricante,\n"
                    + "	CODIGRU merc1,\n"
                    + "	CODISGR merc2,\n"
                    + "	CODISGR merc3,\n"
                    + "	CODINCM ncm,\n"
                    + "	p.CEST cest,\n"
                    + "	p.PERC_LUC margem,\n"
                    + "	pl.UC_CUSTO_C custocomimposto,\n"
                    + "	pl.UC_CUSTO_S custosemimposto,\n"
                    + "	pr.PREVE precovenda,\n"
                    + "	COALESCE (p.ESTOMIN,\n"
                    + "	0) estmin,\n"
                    + "	p.ESTOMAX estmax,\n"
                    + "	CAST(a.ANO || '-' || a.MES || '-' || '01' AS date) DATA,\n"
                    + "	a.CODIPRO,\n"
                    + "	a.ESTOANT estoque,\n"
                    + "	tc.CODITR id_credito,\n"
                    + "	td.CODITR id_debito,\n"
                    + "	tdfe.CODITR id_debito_fe,\n"
                    + "	DATA_ALTERA data_alteracao,\n"
                    + "	pcc.CST_PIS piscofinscredito,\n"
                    + "	pcd.CST_PIS piscofinsdebito,\n"
                    + "	CASE\n"
                    + "		WHEN p.atides = 'A' THEN 1\n"
                    + "		ELSE 0\n"
                    + "	END situacaocadastro\n"
                    + "FROM\n"
                    + "	PRODUTOS p\n"
                    + "	JOIN PRODUTOS_LOJAS pl ON pl.CODIPRO = p.CODIPRO\n"
                    + "	JOIN estosi a ON a.CODIPRO = p.CODIPRO AND a.EMP_CODIGO = pl.EMP_CODIGO\n"
                    + "	JOIN COD_BARR ean ON p.CODIPRO = ean.CODIPRO\n"
                    + "	JOIN EMBALAG un_v ON un_v.CODIEMB = p.CODIEMB_V\n"
                    + "	JOIN EMBALAG un_c ON un_c.CODIEMB = p.CODIEMB_C\n"
                    + "	JOIN PRECOS_LOJAS pr ON p.CODIPRO = pr.CODIPRO AND pr.AGP_CODIGO = 1\n"
                    + "	JOIN TRIBUTA_LOJAS ti ON ti.CODIPRO = p.CODIPRO\n"
                    + "	JOIN TRIBUTA tc ON tc.CODITR = ti.CODITRE \n"
                    + "	JOIN TRIBUTA td ON td.CODITR = ti.CODITRC\n"
                    + "	JOIN TRIBUTA tdfe ON tdfe.CODITR = ti.CODITRI\n"
                    + "	JOIN TRIBUTA_PIS pcc ON pcc.CODITRPIS = p.TRPIS_C AND pcc.ENTR_SAI = 'E'\n"
                    + "	JOIN TRIBUTA_PIS pcd ON pcd.CODITRPIS = p.TRPIS_V AND pcd.ENTR_SAI = 'S'\n"
                    + "	JOIN estoque e ON e.CODIPRO = a.CODIPRO AND CAST(a.ANO || '-' || a.MES || '-' || '01' AS date) = e.data\n"
                    + "WHERE\n"
                    + "	pl.EMP_CODIGO = " + getLojaOrigem() + "\n"
                    + "ORDER BY 1 DESC"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rst.getString("idproduto"));
                    imp.setEan(rst.getString("ean"));
                    imp.setFornecedorFabricante(rst.getString("fabricante"));

                    imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                    imp.setTipoEmbalagem(rst.getString("tipoembalagem"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setTipoEmbalagemCotacao(rst.getString("emb_compra"));
                    imp.setQtdEmbalagemCotacao(rst.getInt("qtdembalagem"));
                    imp.setPesoBruto(rst.getDouble("pesobruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoliquido"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));
                    imp.setIdFamiliaProduto(rst.getString("familia"));
                    imp.setCodMercadologico1(rst.getString("merc1"));
                    imp.setCodMercadologico2(rst.getString("merc2"));
                    imp.setCodMercadologico3(imp.getCodMercadologico2());

                    imp.setNcm(rst.getString("ncm"));
                    imp.setCest(rst.getString("cest"));

                    imp.setMargem(rst.getDouble("margem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoqueMinimo(rst.getDouble("estmin"));
                    imp.setEstoqueMaximo(rst.getDouble("estmax"));
                    imp.setEstoque(rst.getDouble("estoque"));

                    imp.setDataAlteracao(rst.getDate("data_alteracao"));
                    imp.setSituacaoCadastro(rst.getInt("situacaocadastro"));

                    String idIcmsDebito, IdIcmsCredito, IdIcmsForaEstado;

                    idIcmsDebito = rst.getString("id_debito");
                    IdIcmsCredito = rst.getString("id_credito");
                    IdIcmsForaEstado = rst.getString("id_debito_fe");

                    imp.setIcmsDebitoId(idIcmsDebito);
                    imp.setIcmsDebitoForaEstadoId(IdIcmsForaEstado);
                    imp.setIcmsDebitoForaEstadoNfId(IdIcmsForaEstado);
                    imp.setIcmsConsumidorId(idIcmsDebito);
                    imp.setIcmsCreditoId(IdIcmsCredito);
                    imp.setIcmsCreditoForaEstadoId(IdIcmsCredito);

                    imp.setPiscofinsCstCredito(rst.getString("piscofinscredito"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsdebito"));

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
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 c.id_pes id,\n"
                    + "	 razao,\n"
                    + "	 fantasia,\n"
                    + "	 case when tipo_fj = 'F' then cpf else cnpj end cnpj,\n"
                    + "	 case when tipo_fj = 'F' then rg else insc end ie,\n"
                    + "	 e.n_endereco endereco,\n"
                    + "	 e.n_nro_endereco numero,\n"
                    + "	 e.n_complemento complemento,\n"
                    + "	 e.n_bairro bairro,\n"
                    + "	 m.municipio cidade,\n"
                    + "	 u.uf uf,\n"
                    + "	 e.n_cep cep,\n"
                    + "	 e.n_fone telefone,\n"
                    + "	 email,\n"
                    + "	 dt_cad data_cadastro,\n"
                    + "	 case id_tipo_situacao when 4  then 1 else 0 end ativo,\n"
                    + "  obs_v observacao\n"
                    + "from\n"
                    + "	 fn_pes c\n"
                    + "	 join fn_pes_tipo tp on tp.id_pes = c.id_pes\n"
                    + "	 join fn_pes_end e on e.id_pes = c.id_pes\n"
                    + "	 join tb_municipio m on m.id_municipio = e.id_n_municipio\n"
                    + "	 join tb_uf u on u.id_uf = m.id_uf\n"
                    + "where\n"
                    + "	 tp.tipo = 'FOR'\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setImportId(rs.getString("id"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setCnpj_cpf(rs.getString("cnpj"));
                    imp.setIe_rg(rs.getString("ie"));
                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));
                    imp.setTel_principal(Utils.acertarTexto(rs.getString("telefone")));

                    String email = Utils.acertarTexto(rs.getString("email")).toLowerCase();
                    if (!"".equals(email)) {
                        imp.addContato("1", "Email", "", "", TipoContato.COMERCIAL,
                                (email.length() > 50 ? email.substring(0, 50) : email));
                    }

                    imp.setDatacadastro(rs.getDate("data_cadastro"));
                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoFornecedorIMP> getProdutosFornecedores() throws Exception {
        List<ProdutoFornecedorIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	id_prod id_produto,\n"
                    + "	id_pes id_fornecedor,\n"
                    + "	codigo codexterno,\n"
                    + "	f.fator qtde_embalagem\n"
                    + "from\n"
                    + "	eq_prod_ref pf\n"
                    + "	join tb_fatorcx f on pf.id_fatorcx = f.id_fatorcx\n"
                    + "order by 2,1"
            )) {
                while (rs.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());

                    imp.setIdProduto(rs.getString("id_produto"));
                    imp.setIdFornecedor(rs.getString("id_fornecedor"));
                    imp.setQtdEmbalagem(rs.getDouble("qtde_embalagem"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoPostgres.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select\n"
                    + "	 c.id_pes id,\n"
                    + "	 case when tipo_fj = 'F' then cpf else cnpj end cpf_cnpj,\n"
                    + "	 case when tipo_fj = 'F' then rg else insc end rg_ie,\n"
                    + "	 razao,\n"
                    + "	 case when fantasia = '(ATUALIZAR)' then razao else fantasia end fantasia,\n"
                    + "	 e.n_endereco endereco,\n"
                    + "	 e.n_nro_endereco numero,\n"
                    + "	 e.n_complemento complemento,\n"
                    + "	 e.n_bairro bairro,\n"
                    + "	 m.municipio cidade,\n"
                    + "	 u.uf uf,\n"
                    + "	 e.n_cep cep,\n"
                    + "	 e.n_fone telefone,\n"
                    + "	 e.n_celular celular,\n"
                    + "	 email,\n"
                    + "	 cargo,\n"
                    + "	 vr_renda salario,\n"
                    + "	 trabalho_local empresa,\n"
                    + "	 trabalho_fone tel_empresa,\n"
                    + "	 dt_nasc data_nasc,\n"
                    + "	 dt_cad data_cadastro,\n"
                    + "	 conjuge,\n"
                    + "	 dt_nasc_conjuge nasc_conjuge,\n"
                    + "	 mae nomemae,\n"
                    + "	 pai nomepai,\n"
                    + "	 case id_tipo_situacao when 4  then 1 else 0 end ativo,\n"
                    + "	 case id_tipo_situacao when 5 then 1 else 0 end bloqueado,\n"
                    + "  obs_v observacao\n"
                    + "from\n"
                    + "	 fn_pes c\n"
                    + "	 join fn_pes_tipo tp on tp.id_pes = c.id_pes\n"
                    + "	 join fn_pes_end e on e.id_pes = c.id_pes\n"
                    + "	 join tb_municipio m on m.id_municipio = e.id_n_municipio\n"
                    + "	 join tb_uf u on u.id_uf = m.id_uf\n"
                    + "where\n"
                    + "	 tp.tipo in ('CLI','CRC','FUN')\n"
                    + "order by 1"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cpf_cnpj"));
                    imp.setInscricaoestadual(rs.getString("rg_ie"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));

                    imp.setEndereco(rs.getString("endereco"));
                    imp.setNumero(rs.getString("numero"));
                    imp.setComplemento(rs.getString("complemento"));
                    imp.setBairro(rs.getString("bairro"));
                    imp.setMunicipio(rs.getString("cidade"));
                    imp.setUf(rs.getString("uf"));
                    imp.setCep(rs.getString("cep"));

                    imp.setTelefone(rs.getString("telefone"));
                    imp.setCelular(rs.getString("celular"));
                    imp.setEmail(rs.getString("email"));

                    imp.setCargo(rs.getString("cargo"));
                    imp.setSalario(rs.getDouble("salario"));
                    imp.setEmpresa(rs.getString("empresa"));
                    imp.setEmpresaTelefone(rs.getString("tel_empresa"));

                    imp.setDataNascimento(rs.getDate("data_nasc"));
                    imp.setDataCadastro(rs.getDate("data_cadastro"));
                    imp.setNomeConjuge(rs.getString("conjuge"));
                    imp.setDataNascimentoConjuge(rs.getDate("nasc_conjuge"));
                    imp.setNomeMae(rs.getString("nomemae"));
                    imp.setNomePai(rs.getString("nomepai"));

                    imp.setAtivo(rs.getBoolean("ativo"));
                    imp.setBloqueado(rs.getBoolean("bloqueado"));
                    imp.setObservacao(rs.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new NereusDAO.VendaIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new NereusDAO.VendaItemIterator(getLojaOrigem(), this.dataInicioVenda, this.dataTerminoVenda);
    }

    public void setDataInicioVenda(Date date) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date date) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setSubTotalImpressora(rst.getDouble("total"));
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
                    + "	REPLACE((MOV_LOJA||MOV_COO||MOV_PDV||MOV_DT_MOVIMENTO), '-', '') AS id_venda,\n"
                    + "	MOV_LOJA loja,\n"
                    + "	MOV_PDV pdv,\n"
                    + "	MOV_ECF ecf,\n"
                    + "	MOV_COO numerocupom,\n"
                    + "	MOV_DT_MOVIMENTO data,\n"
                    //+ " SUBSTRING(MOV_DTHR_REGISTRO FROM 12 FOR 8) hora,\n"
                    + " '00:00:00' hora,\n"
                    + "	CAST(sum(MOV_VLR_TOTAL) AS numeric(11,2)) total,\n"
                    + "	CAST(sum(MOV_DESCONTO_CUPOM) AS numeric(11,2)) desconto,\n"
                    + "	CAST(sum(MOV_ACRESCIMO_CUPOM) AS numeric(11,2)) acrescimo\n"
                    + "FROM\n"
                    + "	TB_PDV_MOVOUTRA\n"
                    + "WHERE\n"
                    + "	PRO_ID IS NOT NULL\n"
                    + "	AND MOV_LOJA = " + idLojaCliente + "\n"
                    + "	AND MOV_DT_MOVIMENTO BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'\n"
                    + "GROUP BY 1, 2, 3, 4, 5, 6";
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

        private Statement stm = ConexaoPostgres.getConexao().createStatement();
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
                        next.setSequencia(rst.getInt("nroitem"));
                        next.setProduto(rst.getString("produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
                        next.setCodigoBarras(rst.getString("codigobarras"));
                        next.setDescricaoReduzida(rst.getString("descricao"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setPrecoVenda(rst.getDouble("precovenda"));
                        next.setTotalBruto(rst.getDouble("total"));

                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino) throws Exception {
            this.sql
                    = "SELECT\n"
                    + "	REPLACE((pdv.MOV_LOJA || pdv.MOV_COO || pdv.MOV_PDV || pdv.MOV_DT_MOVIMENTO), '-', '') AS id_venda,\n"
                    + "	REPLACE((pdv.MOV_LOJA || pdv.MOV_COO || pdv.MOV_PDV || pdv.MOV_DT_MOVIMENTO || pdv.PRO_ID || pdv.MOV_SEQ_COO), '-', '') AS id_item,\n"
                    + "	SUBSTRING(pdv.PRO_COD_BARRA FROM 1 FOR CHAR_LENGTH(pdv.PRO_COD_BARRA)-1) ean,\n"
                    + "	p.PRO_DESCRICAO produto,\n"
                    + "	pdv.MOV_LOJA AS loja,\n"
                    + "	pdv.MOV_PDV AS pdv,\n"
                    + "	pdv.MOV_ECF AS ecf,\n"
                    + "	pdv.MOV_COO AS numerocupom,\n"
                    + "	pdv.MOV_SEQ_COO AS sequencia,\n"
                    + "	pdv.MOV_DT_MOVIMENTO AS DATA,\n"
                    + " '00:00:00' hora,\n"
                    + "	CASE\n"
                    + "	 WHEN pdv.MOV_TPO_REGISTRO = 10 THEN 1\n"
                    + "	 ELSE 0\n"
                    + "	END cancelado,\n"
                    + "	p.PRO_UN_REFERENCIA AS unidade,\n"
                    + "	pdv.MOV_QTD_ITEM AS qtd,\n"
                    + "	pdv.MOV_VLR_UNIT AS valorunitario,\n"
                    + "	pdv.MOV_VLR_TOTAL AS valortotal,\n"
                    + "	pdv.MOV_DESCONTO_ITEM AS desconto\n"
                    + "FROM\n"
                    + "	TB_PDV_MOVOUTRA pdv\n"
                    + "JOIN TB_PRODUTOS p ON p.pro_id = pdv.PRO_ID\n"
                    + "WHERE\n"
                    + "	pdv.PRO_ID IS NOT NULL\n"
                    + "	AND pdv.MOV_LOJA = " + idLojaCliente + "\n"
                    + "	AND pdv.MOV_DT_MOVIMENTO BETWEEN '" + dataInicio + "' AND '" + dataTermino + "'";
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
