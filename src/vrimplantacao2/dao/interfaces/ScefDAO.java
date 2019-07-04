/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.logging.Logger;
import org.apache.commons.lang3.StringUtils;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.classe.ConexaoOracle;
import vrimplantacao.dao.cadastro.ProdutoBalancaDAO;
import vrimplantacao.vo.vrimplantacao.ProdutoBalancaVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ConveniadoIMP;
import vrimplantacao2.vo.importacao.ConvenioEmpresaIMP;
import vrimplantacao2.vo.importacao.ConvenioTransacaoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author lucasrafael
 */
public class ScefDAO extends InterfaceDAO implements MapaTributoProvider {

    private static final Logger LOG = Logger.getLogger(ScefDAO.class.getName());
    public String v_empresaConvenio = "";
    public String v_lojaVR = "1";

    @Override
    public String getSistema() {
        return "Scef";
    }

    public List<Estabelecimento> getLojas() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "e.empcodigo codigo,\n"
                    + "(trim(e.empnome)||' - LOCAL '||l.loccodigo||' '||l.locnome) descricao,\n"
                    + "l.loccodigo,\n"
                    + "l.locnome\n"
                    + "from local l\n"
                    + "inner join empresa e on e.empcodigo = l.empcodigo\n"
                    + "order by loccodigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("loccodigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "sitcodigo, \n"
                    + "sitdescricao,\n"
                    + "pro_sit_trib_nf cst,\n"
                    + "siticms aliq,\n"
                    + "0 reducao\n"
                    + "from situacaotributaria"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(
                            rst.getString("sitcodigo"),
                            rst.getString("sitdescricao"),
                            rst.getInt("cst"),
                            rst.getDouble("aliq"),
                            rst.getDouble("reducao")
                    ));
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
                    + "p.procodigo,\n"
                    + "b.codbarra,\n"
                    + "p.cifcodigo,\n"
                    + "e.prequantidade,\n"
                    + "p.prodescricao,\n"
                    + "p.prodesc_imp_fiscal,\n"
                    + "p.propreco_custo_sem_icms custocomimposto,\n"
                    + "p.propreco_custo_com_icms custosemimposto,\n"
                    + "p.propreco_custo_medio_cicms,\n"
                    + "p.propreco_custo_medio_sicms,\n"
                    + "p.propreco_venda precovenda,\n"
                    + "p.promargem,\n"
                    + "p.proinclusao datacadastro,\n"
                    + "p.probalanca,\n"
                    + "case p.status when 'A' then '1' else '0' end status,\n"
                    + "p.embcodigo,\n"
                    + "p.embcodigo_unidade,\n"
                    + "p.proicms,\n"
                    + "p.proreducao_base_icms,\n"
                    + "p.prodiasvencimento,\n"
                    + "p.pro_sit_trib_nf,\n"
                    + "p.proexclusao dataexclusao,\n"
                    + "p.proncm,\n"
                    + "p.stpcodigo,\n"
                    + "p.stpcodigo_entrada,\n"
                    + "p.stccodigo,\n"
                    + "p.stccodigo_entrada,\n"
                    + "c.cestchave,\n"
                    + "s.prosit_tributaria sit_trib,\n"
                    + "p.prosit_tributaria sit_prod,\n"
                    + "s.pstdescricao sit_descricao,\n"
                    + "(((p.propreco_venda * e.prequantidade))) preco_venda,\n"
                    + "(((p.propreco_venda * e.prequantidade) * (e.prepercembalagem * -1)) / 100) as valor_desconto,\n"
                    + "(e.prepercembalagem * -1) porcentagem_desconto\n"
                    + "from produto p\n"
                    + "left join cest c on c.cestcodigo = p.cestcodigo\n"
                    + "left join prosituacao_tributaria s on s.prosit_tributaria = p.prosit_tributaria\n"
                    + "left join proembala e on e.procodigo = p.procodigo\n"
                    + "left join procodbarra b on b.precodigo = e.precodigo\n"
                    + "where p.status = 'A'"
            )) {
                Map<Integer, ProdutoBalancaVO> produtosBalanca = new ProdutoBalancaDAO().carregarProdutosBalanca();
                while (rst.next()) {                   
                    
                    ProdutoIMP imp = new ProdutoIMP();
                    ProdutoBalancaVO produtoBalanca;
                    String ean;
                    long codigoProduto;
                    
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("procodigo"));
                    
                    ean = rst.getString("codbarra");
                    
                    if ((ean != null)
                            && (!ean.trim().isEmpty())) {
                        if ((ean.trim().length() > 7) && (ean.contains("0000000"))) {
                            ean = ean.substring(1, 5);
                            
                            codigoProduto = Long.parseLong(ean);
                            if (codigoProduto <= Integer.MAX_VALUE) {
                                produtoBalanca = produtosBalanca.get((int) codigoProduto);
                            } else {
                                produtoBalanca = null;
                            }

                            if (produtoBalanca != null) {
                                imp.seteBalanca(true);
                                imp.setValidade(produtoBalanca.getValidade() > 1 ? produtoBalanca.getValidade() : 0);
                                imp.setEan(ean);
                            } else {
                                imp.setValidade(0);
                                imp.seteBalanca(false);
                                imp.setEan(rst.getString("codbarra"));
                            }                            
                        } else if ((ean.trim().length() > 7) && (!ean.contains("0000000"))) {
                          
                            imp.setEan(ean);
                            imp.seteBalanca(false);
                            
                        } else if (ean.trim().length() < 7) {
                            
                            imp.seteBalanca(false);
                            
                            if ((rst.getInt("prequantidade") > 1)) {

                                if (ean.trim().length() == 1) {
                                    ean = "9999999" + ean.trim();
                                } else if (ean.trim().length() == 2) {
                                    ean = "999999" + ean.trim();
                                } else if (ean.trim().length() == 3) {
                                    ean = "99999" + ean.trim();
                                } else if (ean.trim().length() == 4) {
                                    ean = "9999" + ean.trim();
                                } else if (ean.trim().length() == 5) {
                                    ean = "999" + ean.trim();
                                } else {
                                    ean = "99" + ean.trim();
                                }
                                
                                imp.setEan(ean);
                            }
                        }
                    } else {                        
                        imp.seteBalanca(false);                        
                        if (rst.getInt("prequantidade") > 1) {
                            imp.setEan(StringUtils.leftPad(imp.getImportId(), 8, "9"));
                        }
                    }
                    
                    imp.setSituacaoCadastro(rst.getInt("status") == 1 ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setTipoEmbalagem(rst.getString("embcodigo"));
                    imp.setQtdEmbalagem(rst.getInt("prequantidade"));
                    imp.setDataCadastro(rst.getDate("proinclusao"));
                    imp.setDescricaoCompleta(rst.getString("prodescricao"));
                    imp.setDescricaoReduzida(imp.getDescricaoCompleta());
                    imp.setDescricaoGondola(imp.getDescricaoCompleta());
                    imp.setMargem(rst.getDouble("promargem"));
                    imp.setCustoComImposto(rst.getDouble("custocomimposto"));
                    imp.setCustoSemImposto(rst.getDouble("custosemimposto"));
                    imp.setAtacadoPorcentagem(rst.getDouble("porcentagem_desconto"));
                    imp.setPrecovenda(rst.getDouble("precovenda"));
                    imp.setPiscofinsCstDebito(rst.getString("stpcodigo"));
                    imp.setPiscofinsCstCredito(rst.getString("stpcodigo_entrada"));
                    imp.setNcm(rst.getString("proncm"));
                    imp.setCest(rst.getString("cestchave"));
                    imp.setIcmsDebitoId(rst.getString("cifcodigo"));
                    imp.setIcmsCreditoId(rst.getString("cifcodigo"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "procodigo, \n"
                        + "estatual\n"
                        + "from estoque\n"
                        + "where loccodigo = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportSistema(getSistema());
                        imp.setImportId(rst.getString("procodigo"));
                        imp.setEstoque(rst.getDouble("estatual"));
                        result.add(imp);
                    }
                }
                return result;
            }
        }
        return null;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "f.pescodigo,\n"
                    + "f.pesnome,\n"
                    + "f.pesapelido,\n"
                    + "f.pesendereco,\n"
                    + "f.pesendereco_numero,\n"
                    + "f.pesendereco_complemento,\n"
                    + "f.pesbairro,\n"
                    + "f.pescidade,\n"
                    + "f.pesuf, \n"
                    + "f.pesfone, \n"
                    + "f.pesfax, \n"
                    + "f.pesemail,\n"
                    + "f.pescep,\n"
                    + "f.pesobservacao,\n"
                    + "f.pesdatainclusao,\n"
                    + "p.pfoprazopagto,\n"
                    + "p.pfoprazoentrega,\n"
                    + "p.pfocontato,\n"
                    + "p.pfocontatorep,\n"
                    + "cast(j.pjucnpj as varchar(20)) cnpj_cpf,\n"
                    + "cast(j.pjuinsestadual as varchar(20)) ie_rg\n"
                    + "FROM PESSOA f\n"
                    + "inner join pesfornecedor p on p.pescodigo = f.pescodigo\n"
                    + "inner join pesjuridica j on j.pescodigo = f.pescodigo\n"
                    + "WHERE f.PESFORNECEDOR = 'S'\n"
                    + "union all\n"
                    + "select\n"
                    + "f.pescodigo,\n"
                    + "f.pesnome,\n"
                    + "f.pesapelido,\n"
                    + "f.pesendereco,\n"
                    + "f.pesendereco_numero,\n"
                    + "f.pesendereco_complemento,\n"
                    + "f.pesbairro,\n"
                    + "f.pescidade,\n"
                    + "f.pesuf, \n"
                    + "f.pesfone, \n"
                    + "f.pesfax, \n"
                    + "f.pesemail,\n"
                    + "f.pescep,\n"
                    + "f.pesobservacao,\n"
                    + "f.pesdatainclusao,\n"
                    + "p.pfoprazopagto,\n"
                    + "p.pfoprazoentrega,\n"
                    + "p.pfocontato,\n"
                    + "p.pfocontatorep,\n"
                    + "cast(fis.pficpf as varchar(20)) cnpj_cpf,\n"
                    + "cast(fis.pfirg as varchar(20)) ie_rg\n"
                    + "FROM PESSOA f\n"
                    + "inner join pesfornecedor p on p.pescodigo = f.pescodigo\n"
                    + "inner join pesfisica fis on fis.pescodigo = f.pescodigo\n"
                    + "WHERE f.PESFORNECEDOR = 'S'"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setImportId(rst.getString("pescodigo"));
                    imp.setRazao(rst.getString("pesnome"));
                    imp.setFantasia(rst.getString("pesapelido"));
                    imp.setCnpj_cpf(rst.getString("cnpj_cpf"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setEndereco(rst.getString("pesendereco"));
                    imp.setNumero(rst.getString("pesendereco_numero"));
                    imp.setComplemento(rst.getString("pesendereco_complemento"));
                    imp.setBairro(rst.getString("pesbairro"));
                    imp.setMunicipio(rst.getString("pescidade"));
                    imp.setUf(rst.getString("pesuf"));
                    imp.setCep(rst.getString("pescep"));
                    imp.setDatacadastro(rst.getDate("pesdatainclusao"));
                    imp.setPrazoEntrega(rst.getInt("pfoprazoentrega"));
                    imp.setTel_principal(rst.getString("pesfone"));
                    imp.setObservacao(rst.getString("pesobservacao"));

                    if ((rst.getString("pfocontato") != null)
                            && (!rst.getString("pfocontato").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATO " + rst.getString("pfocontato"));
                    }
                    if ((rst.getString("pfocontatorep") != null)
                            && (!rst.getString("pfocontatorep").trim().isEmpty())) {
                        imp.setObservacao(imp.getObservacao() + " CONTATOREP " + rst.getString("pfocontatorep"));
                    }

                    if ((rst.getString("pesfax") != null)
                            && (!rst.getString("pesfax").trim().isEmpty())) {
                        imp.addContato(
                                "1",
                                "FAX",
                                rst.getString("pesfax"),
                                null,
                                TipoContato.COMERCIAL,
                                null
                        );
                    }
                    if ((rst.getString("pesemail") != null)
                            && (!rst.getString("pesemail").trim().isEmpty())) {
                        imp.addContato(
                                "2",
                                "EMAIL",
                                null,
                                null,
                                TipoContato.COMERCIAL,
                                rst.getString("pesemail").toLowerCase()
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
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "pescodigo,\n"
                    + "procodigo,\n"
                    + "ultatualizacao,\n"
                    + "forxproreferencia\n"
                    + "from fornecedorxproduto"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportSistema(getSistema());
                    imp.setIdProduto(rst.getString("procodigo"));
                    imp.setIdFornecedor(rst.getString("pescodigo"));
                    imp.setCodigoExterno(rst.getString("forxproreferencia"));
                    imp.setDataAlteracao(rst.getDate("ultatualizacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConvenioEmpresaIMP> getConvenioEmpresa() throws Exception {
        List<ConvenioEmpresaIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "plccodigo codigo,\n"
                    + "plcdescricao descricao,\n"
                    + "current_timestamp as dataatual\n"
                    + "from planodeconta\n"
                    + "where plccodigo in (" + v_empresaConvenio + ")"
            )) {
                while (rst.next()) {
                    ConvenioEmpresaIMP imp = new ConvenioEmpresaIMP();
                    imp.setId(rst.getString("codigo"));
                    imp.setRazao(rst.getString("descricao"));
                    imp.setDataInicio(rst.getDate("dataatual"));
                    imp.setDataTermino(rst.getDate("dataatual"));
                    imp.setDesconto(0);
                    imp.setDiaInicioRenovacao(1);
                    imp.setBloqueado(false);
                    result.add(imp);
                }
            }
        }
        return result;
    }

    @Override
    public List<ConveniadoIMP> getConveniado() throws Exception {
        List<ConveniadoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.pescodigo,\n"
                    + "p.pesnome,\n"
                    + "f.pficpf,\n"
                    + "f.pfirg,\n"
                    + "c.pcllimitecredito\n"
                    + "from pessoa p\n"
                    + "inner join pescliente c on c.pescodigo = p.pescodigo\n"
                    + "inner join pesfisica f on f.pescodigo = p.pescodigo\n"
                    + "where p.pescliente = 'S'\n"
                    + "and p.plccodigo_receber in (" + v_empresaConvenio + ")"
            )) {
                while (rst.next()) {
                    ConveniadoIMP imp = new ConveniadoIMP();
                    imp.setId(rst.getString("pescodigo"));
                    imp.setCnpj(rst.getString("pficpf"));
                    imp.setNome(rst.getString("pesnome"));
                    imp.setIdEmpresa(v_empresaConvenio);
                    imp.setBloqueado(false);
                    imp.setConvenioLimite(rst.getDouble("pcllimitecredito"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
    
    @Override
    public List<ConvenioTransacaoIMP> getConvenioTransacao() throws Exception {
        List<ConvenioTransacaoIMP> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "titcodigo,\n"
                    + "pescodigo, \n"
                    + "titdocumento,\n"
                    + "(titvalor - tittotalpago) valor,\n"
                    + "titemissao,\n"
                    + "titobservacao,\n"
                    + "titnumparcelas,\n"
                    + "titstatus\n"
                    + "from titulo\n"
                    + "where titstatus = 'A'\n"
                    + "and empcodigo = " + getLojaOrigem()
            )) {
                SimpleDateFormat format = new SimpleDateFormat("1yyMMdd");
                while (rst.next()) {
                    ConvenioTransacaoIMP imp = new ConvenioTransacaoIMP();
                    imp.setId(rst.getString("titcodigo"));
                    imp.setIdConveniado(rst.getString("pescodigo"));
                    imp.setNumeroCupom(rst.getString("titdocumento"));
                    imp.setDataHora(rst.getTimestamp("titemissao"));
                    imp.setValor(rst.getDouble("valor"));
                    imp.setObservacao(rst.getString("titobservacao"));
                    result.add(imp);
                }
            }
        }
        return result;
    }
}
