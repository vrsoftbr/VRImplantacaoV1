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
import java.util.Date;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import static vr.core.utils.StringUtils.LOG;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.dao.interfaces.InterfaceDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.MercadologicoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.PromocaoIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;
import vrimplantacao2_5.dao.conexao.ConexaoMySQL;
import vrimplantacao2_5.vo.sistema.TslVO;

/**
 *
 * @author Michael
 */
public class TslDAO extends InterfaceDAO implements MapaTributoProvider {

    public TslVO tslVO = null;
    private final String SISTEMA = "TSL";

    @Override
    public String getSistema() {
        return SISTEMA;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	EMPCNPJ cnpj,\n"
                    + "	EMPNOM  razao\n"
                    + "FROM\n"
                    + "	tsc008a"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("cnpj"), rst.getString("razao")));
                }
            }
        }

        return result;
    }

    public List<String> getNomeLojaCliente() throws Exception {
        List<String> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT\n"
                    + "	EMPNOM descricao\n"
                    + "FROM\n"
                    + "	tsc008a"
            )) {
                while (rst.next()) {
                    result.add(rst.getString("descricao"));
                }
            }
        }

        return result;
    }

    @Override
    public List<PromocaoIMP> getPromocoes() throws Exception {
        List<PromocaoIMP> Result = new ArrayList<>();
        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = stm.executeQuery(
                    "select \n"
                    + "	p.SEQPROMO01 id_promocao,\n"
                    + "	p.DESCPROMO descricao,\n"
                    + "	p.dtinipromo inicio,\n"
                    + "	p.dtfimpromo termino,\n"
                    + "	t.CODBAR ean,\n"
                    + "	t.SEQ003 id_produto,\n"
                    + "	pr.DESCPRO descricaocompleta,\n"
                    + "	p.LEVAQTD quantidade,\n"
                    + "	p.VALORFIXO paga\n"
                    + "from\n"
                    + "	tspromo01 p\n"
                    + "join tspromo02 t on t.SEQPROMO01 = p.SEQPROMO01 \n"
                    + "join tslc003 pr on pr.CODIGO = t.SEQ003 "
            )) {
                while (rs.next()) {
                    PromocaoIMP imp = new PromocaoIMP();

                    imp.setId_promocao(rs.getString("id_promocao"));
                    imp.setDescricao(rs.getString("descricao"));
                    imp.setDataInicio(rs.getDate("inicio"));
                    imp.setDataTermino(rs.getDate("termino"));
                    imp.setEan(rs.getString("ean"));
                    imp.setId_produto(rs.getString("id_produto"));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setQuantidade(rs.getDouble("quantidade"));
                    imp.setPaga(rs.getDouble("paga"));

                    Result.add(imp);
                }
            }
        }
        return Result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CODIGO id,\n"
                    + "	ICMS aliquota,\n"
                    + "	DESCRICAO\n"
                    + "from\n"
                    + "	tslc036"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("id"),
                            rst.getString("descricao")
                    ));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoIMP> getMercadologicos() throws Exception {
        List<MercadologicoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	CODIGO m1,\n"
                    + "	DESCRICAO desc1,\n"
                    + "	codigo m2,\n"
                    + "	descricao desc2,\n"
                    + "	codigo m3,\n"
                    + "	DESCRICAO desc3\n"
                    + "from\n"
                    + "	tslc033\n"
                    + "order by 1, 2"
            )) {
                while (rst.next()) {
                    MercadologicoIMP imp = new MercadologicoIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setMerc1ID(rst.getString("m1"));
                    imp.setMerc1Descricao(rst.getString("desc1"));
                    imp.setMerc2ID(rst.getString("m2"));
                    imp.setMerc2Descricao(rst.getString("desc2"));
                    imp.setMerc3ID(rst.getString("m2"));
                    imp.setMerc3Descricao(rst.getString("desc2"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + " p.CODIGO Id,\n"
                    + "case \n"
                    + "	when p.DTATU = '0000-00-00'\n"
                    + "	then now()\n"
                    + "	else p.DTATU\n"
                    + "	end dataCadastro,\n"
                    + "	p.DTATU dataAlteracao,\n"
                    + "	substring(CODBAR from 4) ean,\n"
                    + "	CASE \n"
                    + "	WHEN p.CODBAR LIKE '00000%'\n"
                    + "	THEN SUBSTRING(p.CODBAR FROM 8) \n"
                    + "	else substring(CODBAR from 4) \n"
                    + "	END cod_balanca,\n"
                    + "	p.PESADO e_balanca,\n"
                    + "	p.QTDITEMD qtdEmbalagem,\n"
                    + "	p.UNI tipoEmbalagem,\n"
                    + "	p.DESCPRO descricaoCompleta,\n"
                    + "	p.DESCPDV descricaoReduzida,\n"
                    + "	p.DESCPDV descricaoGondola,  \n"
                    + "    p.grupo m1cod,\n"
                    + "    p.grupo m2cod,\n"
                    + "    p.grupo m3cod,\n"
                    + "    p.PBRUTO pesoBruto,\n"
                    + "    p.PLIQUIDO pesoLiquido,\n"
                    + "    p.ESTOQUE estoqueMaximo,\n"
                    + "    p.MIN estoqueMinimo,\n"
                    + "    p.ESTOQUE estoque,\n"
                    + "    p.CUSTOCOM custoSemImposto, \n"
                    + "    p.CUSTO custoComImposto,\n"
                    + "    p.PRECO1 precovenda,  \n"
                    + "    CASE \n"
                    + "		WHEN p.INATIVO = '0'\n"
                    + "		THEN 1\n"
                    + "		ELSE 0\n"
                    + "    END situacaoCadastro,\n"
                    + "    p.NCM ncm,\n"
                    + "    p.PIS_MIX piscofinsCstDebito,\n"
                    + "    p.ALIICMS_MIX icmsAliqEntrada,\n"
                    + "    p.REDUCAO_MIX icmsReducaoEntrada, \n"
                    + "    case \n"
                    + "    when p.CST_MIX = ''\n"
                    + "    then '040'\n"
                    + "    else p.CST_MIX \n"
                    + "    end icmsCstSaida,\n"
                    + "	p.ICMS icms,\n"
                    + "	case \n"
                    + "	when p.CEST = ''\n"
                    + "	then substring(p.ncm from 1 for 2)\n"
                    + "	else p.cest\n"
                    + "	end cest,\n"
                    + "	p.PIS piscofinsCstDebito,\n"
                    + "	p.ALICOFINS piscofinsCstCredito,\n"
                    + "	substring(p.CST_MIX from 2) cst,\n"
                    + "	p.ALICOFINS,\n"
                    + "    p.REDUCAO_MIX icmsReducaoSaida,\n"
                    + "    COALESCE(p.CST_MIX = NULL, '040') icmsCstSaidaForaEstado\n"
                    + "    from tslc003 p\n"
                    + "    left join tslc033 m on p.GRUPO = m.CODIGO"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.seteBalanca(rst.getBoolean("e_balanca"));

                    int codigoProduto = Utils.stringToInt(rst.getString("cod_balanca"), -2);
                    ProdutoBalancaVO produtoBalanca = produtosBalanca.get(codigoProduto);

                    if (produtoBalanca != null) {
                        imp.setEan(String.valueOf(produtoBalanca.getCodigo()));
                        imp.seteBalanca(true);
                        imp.setTipoEmbalagem("U".equals(produtoBalanca.getPesavel()) ? "UN" : "KG");
                        imp.setValidade(produtoBalanca.getValidade());
                        imp.setQtdEmbalagem(1);
                    } else {
                        imp.setEan(rst.getString("ean"));
                        imp.seteBalanca(false);
                        imp.setTipoEmbalagem(rst.getString("tipoEmbalagem"));
                        imp.setValidade(0);
                        imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    }

                    imp.setDescricaoCompleta(rst.getString("descricaoCompleta"));
                    imp.setDescricaoReduzida(rst.getString("descricaoReduzida"));
                    imp.setDescricaoGondola(rst.getString("descricaoGondola"));
                    imp.setPesoBruto(rst.getDouble("pesoBruto"));
                    imp.setPesoLiquido(rst.getDouble("pesoLiquido"));
                    imp.setDataCadastro(rst.getDate("dataCadastro"));
                    imp.setCodMercadologico1(rst.getString("m1cod"));
                    imp.setCodMercadologico2(rst.getString("m2cod"));
                    imp.setCodMercadologico3(rst.getString("m3cod"));
                    imp.setCustoComImposto(rst.getDouble("custoComImposto"));
                    imp.setCustoSemImposto(rst.getDouble("custoSemImposto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setEstoque(rst.getDouble("estoque"));
                    imp.setEstoqueMinimo(rst.getDouble("estoqueMinimo"));
                    imp.setEstoqueMaximo(rst.getDouble("estoqueMaximo"));
                    imp.setNcm(rst.getString("ncm"));
                    imp.setSituacaoCadastro(rst.getInt("situacaoCadastro"));
                    imp.setCest(rst.getString("cest"));
                    imp.setPiscofinsCstDebito(rst.getString("piscofinsCstDebito"));
                    imp.setPiscofinsCstCredito(rst.getString("piscofinsCstCredito"));
                    imp.setQtdEmbalagem(rst.getInt("qtdembalagem"));
                    imp.setIcmsAliqEntrada(rst.getDouble("icmsAliqEntrada"));
                    imp.setIcmsReducaoEntrada(rst.getDouble("icmsReducaoEntrada"));
                    imp.setIcmsReducaoSaida(rst.getDouble("icmsReducaoSaida"));
                    imp.setIcmsCstSaida(rst.getInt("icmsCstSaida"));
                    imp.setIcmsCstSaidaForaEstado(rst.getInt("icmsCstSaidaForaEstado"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getEANs() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	CODIGO id,\n"
                    + "	substring(CODBAR from 4) ean,\n"
                    + "	UNI unidade\n"
                    + "from\n"
                    + "	tslc003"
            )) {
                while (rst.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("id"));
                    imp.setEan(rst.getString("ean"));
                    imp.setTipoEmbalagem(rst.getString("unidade"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    " SELECT \n"
                    + " CLICOD id,\n"
                    + " CLINOM nome,\n"
                    + " CLIEND endereco,\n"
                    + " NUMERO numero,\n"
                    + " CLIBAI bairro,\n"
                    + " CLICEP cep,\n"
                    + " CLICID municipio,\n"
                    + " CLIEST uf,\n"
                    + " CLICGC cpfcnpj,\n"
                    + " CLIEST inscest,\n"
                    + " CLITEL telefone,\n"
                    + " CELULAR celular,\n"
                    + " OBS obs,\n"
                    + " CADASTRO dtcadastro,\n"
                    + " nullif(DTNASC, '0000-00-00') dtnasc,\n"
                    + " ESTCIVIL CIVIL,\n"
                    + " CLICOMPLE complemento,\n"
                    + " CONJUJE nomeconjuge,\n"
                    + " nullif(NASCONJ, '0000-00-00'),\n"
                    + " CPFCONJ,\n"
                    + " PAI nomepai,\n"
                    + " MAE nomemae,\n"
                    + " EMRPESA empresa,\n"
                    + " RENDA renda,\n"
                    + " LIMITE limite,\n"
                    + " case \n"
                    + " when BLOQUEIO like 'N'\n"
                    + " then 0\n"
                    + " else 1\n"
                    + " end ativo\n"
                    + " from tslc001"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();

                    imp.setId(rst.getString("id"));
                    imp.setRazao(rst.getString("nome"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setDataNascimento(rst.getDate("dtnasc"));
                    imp.setUf(rst.getString("uf"));
                    imp.setCep(rst.getString("cep"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setCnpj(rst.getString("cpfcnpj"));
                    imp.setInscricaoestadual(rst.getString("inscest"));
                    imp.setComplemento(rst.getString("complemento"));

                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setSalario(rst.getDouble("renda"));
                    imp.setCelular(rst.getString("celular"));
                    imp.setDataCadastro(rst.getDate("dtcadastro"));
                    imp.setValorLimite(rst.getDouble("limite"));
                    imp.setNomePai(rst.getString("nomepai"));
                    imp.setNomeMae(rst.getString("nomemae"));
                    imp.setEstadoCivil(rst.getString("civil"));
                    imp.setNomeConjuge(rst.getString("nomeconjuge"));
                    imp.setObservacao(rst.getString("obs"));
                    imp.setAtivo(rst.getBoolean("ativo"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT \n"
                    + " RECSEQ id,\n"
                    + " RECCLI idcliente,\n"
                    + " RECEMPR filial,\n"
                    + " RECCOD numerodocumento,\n"
                    + " RECEMISS dataemissao,\n"
                    + " RECTOTAL valor,\n"
                    + " RECVENCI datavencimento\n"
                    + "FROM tsm003\n"
                    + "WHERE \n"
                    + " RECBAIXA <> 'S'"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    imp.setId(rst.getString("id"));
                    imp.setIdCliente(rst.getString("idcliente"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataVencimento(rst.getDate("datavencimento"));
                    imp.setNumeroCupom(rst.getString("numerodocumento"));
                    imp.setValor(rst.getDouble("valor"));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new TslDAO.VendaIterator(getLojaOrigem(), this.tslVO.getDataInicioVenda(), this.tslVO.getDataTerminoVenda());
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new TslDAO.VendaItemIterator(getLojaOrigem(), this.tslVO.getDataInicioVenda(), this.tslVO.getDataTerminoVenda());
    }

    public static class VendaIterator implements Iterator<VendaIMP> {

        public final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
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

                        next.setIdClientePreferencial(rst.getString("id_cliente"));
                        next.setCpf(rst.getString("cpf"));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("emissao"));
                        String horaInicio = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("emissao")) + " " + rst.getString("horatermino");
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setValorAcrescimo(rst.getDouble("desconto"));
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
                    = "select\n"
                    + "	v.seq id_venda,\n"
                    + "	case\n"
                    + "		when v.CUPOM is null then CONCAT(v.CUPOM, '-', substring(v.MICRO from 4))\n"
                    + "		when v.CUPOM like '000001' then CONCAT(v.CUPOM, '-', substring(v.seq from 5))\n"
                    + "		else v.CUPOM\n"
                    + "	end numerocupom,\n"
                    + "	substring(v.MICRO from 4) ecf,\n"
                    + "	v.DATA emissao,\n"
                    + "	v.HORAINI horainicio,\n"
                    + "	v.HORAFIM horatermino,\n"
                    + "	v.BRUTO subtotalimpressora,\n"
                    + "	v.DESCONTO desconto,\n"
                    + "	v.CODCLI id_cliente,\n"
                    + "	v.CPF cpf,\n"
                    + "	case\n"
                    + "		when v.AUTORIZADO = 1 then 0\n"
                    + "		else 1\n"
                    + "	end cancelado\n"
                    + "FROM\n"
                    + "	tslv010 v\n"
                    + "WHERE\n"
                    + "	v.DATA BETWEEN '" + strDataInicio + "' AND '" + strDataTermino + "'";
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

    public static class VendaItemIterator implements Iterator<VendaItemIMP> {

        private Statement stm = ConexaoMySQL.getConexao().createStatement();
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
                        next.setProduto(rst.getString("descricao"));
                        next.setProduto(rst.getString("id_produto"));
                        next.setUnidadeMedida(rst.getString("unidade"));
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
                    = "SELECT  \n"
                    + " v.SEQ nritem,\n"
                    + "	c.seq id_venda,\n"
                    + "	v.SEQ id_item,\n"
                    + "	v.CODPROD id_produto,\n"
                    + "	v.DESCRICAO descricao,\n"
                    + "	v.QUANT quantidade,\n"
                    + "	v.UNIT valor,\n"
                    + "	v.UNIDADE unidade,\n"
                    + "	v.DESCONTO desconto\n"
                    + "FROM\n"
                    + " tslv011 v \n"
                    + " JOIN tslv010 c ON v.CUPOM = c.SEQ \n"
                    + "WHERE c.DATA BETWEEN '" + VendaIterator.FORMAT.format(dataInicio) + "' AND '" + VendaIterator.FORMAT.format(dataTermino) + "'";
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
