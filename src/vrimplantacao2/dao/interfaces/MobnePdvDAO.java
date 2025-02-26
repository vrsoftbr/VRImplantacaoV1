package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.produto2.ProdutoBalancaDAO;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.vo.cadastro.ProdutoBalancaVO;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author leandro
 */
public class MobnePdvDAO extends InterfaceDAO implements MapaTributoProvider {

    private String complemento = "";
    private boolean somenteEansUnitarios = false;
    public void setComplemento(String complemento) {
        this.complemento = complemento == null ? "" : complemento.trim();
    }
    
    @Override
    public String getSistema() {
        return "".equals(complemento) ? "Mobne" : "Mobne - " + complemento;
    }

    public void setSomenteEansUnitarios(boolean somenteEansUnitarios) {
        this.somenteEansUnitarios = somenteEansUnitarios;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select distinct\n" +
                    "	trib.situacaotributacao cst,\n" +
                    "	trib.percaliquota aliquota,\n" +
                    "	trib.situacaosimples cst_simples,\n" +
                    "   trib.percoutro reducao\n" +        
                    "from\n" +
                    "	tb_produto p\n" +
                    "	join tb_divisao dv on\n" +
                    "		dv.nrodivisao = 1\n" +
                    "	left join tb_familia pf on\n" +
                    "		p.seqfamilia = pf.seqfamilia\n" +
                    "	left join tb_famdivisao ds on\n" +
                    "		pf.seqfamilia = ds.seqfamilia\n" +
                    "		and ds.nrodivisao = dv.nrodivisao\n" +
                    "	left join tb_tributacaouf trib on\n" +
                    "		trib.nrotributacao = ds.nrotributacao\n" +
                    "		and trib.uforigem = 'SP'\n" +
                    "		and trib.ufdestino = 'SP'\n" +
                    "		and trib.tipotributacao = 'SN'\n" +
                    "		and trib.nroregtributacao = 1\n" +
                    "order by\n" +
                    "	cst, aliquota, cst_simples"
            )) {
                while (rs.next()) {
                    result.add(
                            new MapaTributoIMP(
                                getCodigoTributacao(
                                        rs.getString("cst"),
                                        rs.getDouble("aliquota"),
                                        rs.getString("cst_simples"),
                                        rs.getDouble("reducao")
                                ),
                                String.format(
                                        "CST: %s - ALIQ: %.2f - CSOSN: %s - RED: %.2f",
                                        rs.getString("cst"),
                                        rs.getDouble("aliquota"),
                                        rs.getString("cst_simples"),
                                        rs.getDouble("reducao")
                                )
                            )
                    );
                }
            }
        }
        
        return result;
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select nroempresa, nomereduzido from tb_empresa order by nroempresa"
            )) {
                while (rs.next()) {
                    result.add(
                            new Estabelecimento(rs.getString("nroempresa"), rs.getString("nomereduzido"))
                    );
                }
            }
        }
        
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        Map<String, MercadologicoNivelIMP> result = new LinkedHashMap<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            //Nível 1
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 1\n" +
                    "order by\n" +
                    "	seqcategoria "
            )) {
                while (rs.next()) {
                    result.put(
                            rs.getString("id"),
                            new MercadologicoNivelIMP(rs.getString("id"), rs.getString("descricao"))
                    );
                }
            }
            
            //Nível 2
            Map<String, MercadologicoNivelIMP> nv2 = new LinkedHashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoriapai pai,\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 2\n" +
                    "order by\n" +
                    "	pai, seqcategoria "
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP pai = result.get(rs.getString("pai"));
                    if (pai != null) {
                        nv2.put(
                                rs.getString("id"),
                                pai.addFilho(rs.getString("id"), rs.getString("descricao"))
                        );
                    }
                }
            }
            
            //Nível 3
            Map<String, MercadologicoNivelIMP> nv3 = new LinkedHashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoriapai pai,\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 3\n" +
                    "order by\n" +
                    "	pai, seqcategoria "
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP pai = nv2.get(rs.getString("pai"));
                    if (pai != null) {
                        nv3.put(
                                rs.getString("id"),
                                pai.addFilho(rs.getString("id"), rs.getString("descricao"))
                        );
                    }
                }
            }
            
            //Nível 4
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	seqcategoriapai pai,\n" +
                    "	seqcategoria id,\n" +
                    "	categoria descricao\n" +
                    "FROM \n" +
                    "	tb_categoria tc\n" +
                    "where\n" +
                    "	nivelhierarquia = 4\n" +
                    "order by\n" +
                    "	pai, seqcategoria "
            )) {
                while (rs.next()) {
                    MercadologicoNivelIMP pai = nv3.get(rs.getString("pai"));
                    if (pai != null) {
                        pai.addFilho(rs.getString("id"), rs.getString("descricao"));
                    }
                }
            }
        }
        
        return new ArrayList<>(result.values());
    }

    private String getCodigoTributacao(String cst, double aliq, String csosn, double reducao) {
        return String.format(
                "%s-%.2f-%s-%.2f",
                cst,
                aliq,
                csosn,
                reducao
        );
    }

    private static class MercadologicoMobne {
        
        final String merc1;
        final String merc2;
        final String merc3;
        final String merc4;

        public MercadologicoMobne(String merc1, String merc2, String merc3, String merc4) {
            this.merc1 = merc1;
            this.merc2 = merc2;
            this.merc3 = merc3;
            this.merc4 = merc4;
        }
        
    }
    
    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            Map<String, MercadologicoMobne> mercadologico = new HashMap<>();
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	p.seqproduto id_produto,\n" +
                    "	merc1.seqcategoria merc1,\n" +
                    "	merc2.seqcategoria merc2,\n" +
                    "	merc3.seqcategoria merc3,\n" +
                    "	merc4.seqcategoria merc4\n" +
                    "from\n" +
                    "	tb_famdivisaocategoria tf\n" +
                    "	join tb_produto p on\n" +
                    "		tf.seqfamilia = p.seqfamilia \n" +
                    "	join tb_categoria merc4 on\n" +
                    "		merc4.seqcategoria = tf.seqcategoria \n" +
                    "	join tb_categoria merc3 on\n" +
                    "		merc3.seqcategoria = merc4.seqcategoriapai\n" +
                    "	join tb_categoria merc2 on\n" +
                    "		merc2.seqcategoria = merc3.seqcategoriapai\n" +
                    "	join tb_categoria merc1 on\n" +
                    "		merc1.seqcategoria = merc2.seqcategoriapai\n" +
                    "where\n" +
                    "	tf.nrodivisao = 1"
            )) {
                while (rs.next()) {
                    mercadologico.put(
                            rs.getString("id_produto"), 
                            new MercadologicoMobne(
                                    rs.getString("merc1"),
                                    rs.getString("merc2"),
                                    rs.getString("merc3"),
                                    rs.getString("merc4")
                            )
                    );
                }
            }
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	p.seqproduto id,\n" +
                    "	coalesce(ean.codacesso, p.seqproduto) ean,\n" +
                    "	coalesce(ean.qtdembalagem, emb.qtdembalagem) qtdembalagem,\n" +
                    "	emb.embalagem unidade,\n" +
                    "	pf.pesavel,\n" +
                    "	pf.permitemultiplicacao,\n" +
                    "	coalesce(p.qtddiavalidade, 0) validade,\n" +
                    "	pf.bebidaalcoolica,\n" +
                    "	p.desccompleta descricaocompleta,\n" +
                    "	p.descreduzida descricaoreduzida,\n" +
                    "	p.descgenerica descricaogondola,\n" +
                    "	emb.pesobruto,\n" +
                    "	emb.pesoliquido,\n" +
                    "	pe.estqloja estoque,\n" +
                    "	preco.qtdembalagem,\n" +
                    "	round(preco.preco / preco.qtdembalagem, 2) preco,\n" +
                    "	coalesce(pe.ativo, p.ativo) ativo,\n" +
                    "	pf.codnbmsh ncm,\n" +
                    "	pf.codcest cest,\n" +
                    "	coalesce(trib.situacaopis, pf.situacaopis) piscofins_debito,\n" +
                    "	trib.situacaotributacao cst,\n" +
                    "	trib.percaliquota aliquota,\n" +
                    "	trib.situacaosimples cst_simples,\n" +
                    "   trib.percoutro reducao\n" +        
                    "from\n" +
                    "	tb_produto p\n" +
                    "	join tb_divisao dv on\n" +
                    "		dv.nrodivisao = 1\n" +
                    "	join tb_segmento sg on\n" +
                    "		sg.nrosegmento = 1\n" +
                    "	left join tb_familia pf on\n" +
                    "		p.seqfamilia = pf.seqfamilia\n" +
                    "	left join tb_prodcodigo ean on\n" +
                    "		ean.seqproduto = p.seqproduto\n" +
                    "	left join tb_famdivisao ds on\n" +
                    "		pf.seqfamilia = ds.seqfamilia\n" +
                    "		and ds.nrodivisao = dv.nrodivisao\n" +
                    "	left join tb_tributacaouf trib on\n" +
                    "		trib.nrotributacao = ds.nrotributacao\n" +
                    "		and trib.uforigem = 'SP'\n" +
                    "		and trib.ufdestino = 'SP'\n" +
                    "		and trib.tipotributacao = 'SN'\n" +
                    "		and trib.nroregtributacao = 1\n" +
                    "	left join tb_prodempresa pe on\n" +
                    "		p.seqproduto = pe.seqproduto\n" +
                    "	left join tb_prodpreco preco on\n" +
                    "		p.seqproduto = preco.seqproduto\n" +
                    "		and preco.qtdembalagem = ean.qtdembalagem \n" +
                    "		and preco.nrosegmento = sg.nrosegmento \n" +
                    "	left join tb_famembalagem emb on\n" +
                    "		emb.seqfamilia = pf.seqfamilia\n" +
                    "		and emb.qtdembalagem = preco.qtdembalagem\n" +
                    (somenteEansUnitarios ? "where ean.qtdembalagem = 1\n" : "") +
                    "order by\n" +
                    "	p.seqproduto"
            )) {
                Map<Integer, ProdutoBalancaVO> balanca = new ProdutoBalancaDAO().getProdutosBalanca();
                while (rs.next()) {
                    ProdutoIMP imp = new ProdutoIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setEan(rs.getString("ean"));
                    ProdutoBalancaVO vo = balanca.get(Utils.stringToInt(imp.getEan()));
                    if (vo != null) {
                        imp.setQtdEmbalagem(1);
                        imp.setTipoEmbalagem("U".equals(vo.getPesavel()) ? "UN" : "KG");
                        imp.seteBalanca(true);
                        imp.setValidade(vo.getValidade());
                    } else {
                        imp.setQtdEmbalagem(rs.getInt("qtdembalagem"));
                        imp.setTipoEmbalagem(rs.getString("unidade"));
                        imp.seteBalanca("S".equals(rs.getString("pesavel")));
                        imp.setValidade(rs.getInt("validade"));
                    }
                    imp.setAceitaMultiplicacaoPDV("S".equals(rs.getString("permitemultiplicacao")));

                    MercadologicoMobne merc = mercadologico.get(imp.getImportId());
                    if (merc != null) {
                        imp.setCodMercadologico1(merc.merc1);
                        imp.setCodMercadologico2(merc.merc2);
                        imp.setCodMercadologico3(merc.merc3);
                        imp.setCodMercadologico4(merc.merc4);
                    }

                    imp.setVendaControlada("S".equals(rs.getString("bebidaalcoolica")));
                    imp.setDescricaoCompleta(rs.getString("descricaocompleta"));
                    imp.setDescricaoReduzida(rs.getString("descricaoreduzida"));
                    imp.setDescricaoGondola(rs.getString("descricaogondola"));
                    imp.setPesoBruto(rs.getDouble("pesobruto"));
                    imp.setPesoLiquido(rs.getDouble("pesoliquido"));
                    imp.setEstoque(rs.getDouble("estoque"));
                    imp.setPrecovenda(rs.getDouble("preco"));
                    imp.setSituacaoCadastro("S".equals(rs.getString("ativo")) ? SituacaoCadastro.ATIVO : SituacaoCadastro.EXCLUIDO);
                    imp.setNcm(rs.getString("ncm"));
                    imp.setCest(rs.getString("cest"));
                    imp.setPiscofinsCstDebito(rs.getString("piscofins_debito"));
                    String trib = getCodigoTributacao(
                             rs.getString("cst"),
                             rs.getDouble("aliquota"),
                             rs.getString("cst_simples"),
                             rs.getDouble("reducao")
                     );
                    imp.setIcmsDebitoId(trib);
                    imp.setIcmsDebitoForaEstadoId(trib);
                    imp.setIcmsDebitoForaEstadoNfId(trib);
                    imp.setIcmsConsumidorId(trib);
                    imp.setIcmsCreditoId(trib);
                    imp.setIcmsCreditoForaEstadoId(trib);

                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public Set<OpcaoProduto> getOpcoesDisponiveisProdutos() {
        return new HashSet<>(Arrays.asList(
                OpcaoProduto.MERCADOLOGICO_POR_NIVEL,
                OpcaoProduto.MERCADOLOGICO_PRODUTO,
                OpcaoProduto.MERCADOLOGICO_NAO_EXCLUIR,
                OpcaoProduto.PRODUTOS,
                OpcaoProduto.EAN,
                OpcaoProduto.IMPORTAR_EAN_MENORES_QUE_7_DIGITOS,
                OpcaoProduto.EAN_EM_BRANCO,
                OpcaoProduto.QTD_EMBALAGEM_COTACAO,
                OpcaoProduto.QTD_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_EAN,
                OpcaoProduto.TIPO_EMBALAGEM_PRODUTO,
                OpcaoProduto.PESAVEL,
                OpcaoProduto.VALIDADE,
                OpcaoProduto.VENDA_CONTROLADA,
                OpcaoProduto.DESC_COMPLETA,
                OpcaoProduto.DESC_REDUZIDA,
                OpcaoProduto.DESC_GONDOLA,
                OpcaoProduto.PESO_BRUTO,
                OpcaoProduto.PESO_LIQUIDO,
                OpcaoProduto.ESTOQUE,
                OpcaoProduto.PRECO,
                OpcaoProduto.ATIVO,
                OpcaoProduto.NCM,
                OpcaoProduto.CEST,
                OpcaoProduto.PIS_COFINS,
                OpcaoProduto.ICMS,
                OpcaoProduto.IMPORTAR_MANTER_BALANCA,
                OpcaoProduto.IMPORTAR_NAO_TRANSFORMAR_EAN_EM_UN
        ));
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	tp.seqpessoa id,\n" +
                    "	tp.cnpjcpf,\n" +
                    "	tp.inscrestadualrg ierg,\n" +
                    "	tp.orgexp orgaoemissor,\n" +
                    "	tp.nomerazao razao,\n" +
                    "	tp.nomefantasia fantasia,\n" +
                    "	tp.ativo,\n" +
                    "	tc.situacaocomercial bloqueado,\n" +
                    "	tc.dtahorultrestricao databloqueado,\n" +
                    "	tp.estadocivil,\n" +
                    "	tp.dtanascimento,\n" +
                    "	tp.sexo,\n" +
                    "	case when tc.situacaocredito in ('S','B') then 0 else 1 end permitecreditorotativo,\n" +
                    "	tc.observacao,\n" +
                    "	tc.prazomaximo,\n" +
                    "	tc.vlrlimiteglobal limite,\n" +
                    "	tp.inscmunicipal,\n" +
                    "	tp.email\n" +
                    "from\n" +
                    "	tb_pessoa tp\n" +
                    "	join tb_cliente tc on\n" +
                    "		tp.seqpessoa = tc.seqpessoa\n" +
                    "order by\n" +
                    "	tp.seqpessoa"
            )) {
                while (rs.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rs.getString("id"));
                    imp.setCnpj_cpf(rs.getString("cnpjcpf"));
                    imp.setIe_rg(rs.getString("ierg"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo("S".equals(rs.getString("ativo")));
                    imp.setBloqueado("B".equals(rs.getString("bloqueado")));
                    imp.setObservacao(rs.getString("observacao"));
                    imp.setPrazoEntrega(rs.getInt("prazomaximo"));
                    imp.setInsc_municipal(rs.getString("inscmunicipal"));
                    imp.addEmail("EMAIL", rs.getString("email"), TipoContato.COMERCIAL);
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();
        
        try (Statement st = ConexaoMySQL.getConexao().createStatement()) {
            try (ResultSet rs = st.executeQuery(
                    "select\n" +
                    "	tp.seqpessoa id,\n" +
                    "	tp.cnpjcpf,\n" +
                    "	tp.inscrestadualrg ierg,\n" +
                    "	tp.orgexp orgaoemissor,\n" +
                    "	tp.nomerazao razao,\n" +
                    "	tp.nomefantasia fantasia,\n" +
                    "	tp.ativo,\n" +
                    "	tc.situacaocomercial bloqueado,\n" +
                    "	tc.dtahorultrestricao databloqueado,\n" +
                    "	tp.estadocivil,\n" +
                    "	tp.dtanascimento,\n" +
                    "	tp.sexo,\n" +
                    "	case when tc.situacaocredito in ('S','B') then 0 else 1 end permitecreditorotativo,\n" +
                    "	tc.observacao,\n" +
                    "	tc.prazomaximo,\n" +
                    "	tc.vlrlimiteglobal limite,\n" +
                    "	tp.inscmunicipal\n" +
                    "from\n" +
                    "	tb_pessoa tp\n" +
                    "	join tb_cliente tc on\n" +
                    "		tp.seqpessoa = tc.seqpessoa\n" +
                    "order by\n" +
                    "	tp.seqpessoa"
            )) {
                while (rs.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    
                    imp.setId(rs.getString("id"));
                    imp.setCnpj(rs.getString("cnpjcpf"));
                    imp.setInscricaoestadual(rs.getString("ierg"));
                    imp.setOrgaoemissor(rs.getString("orgaoemissor"));
                    imp.setRazao(rs.getString("razao"));
                    imp.setFantasia(rs.getString("fantasia"));
                    imp.setAtivo("S".equals(rs.getString("ativo")));
                    imp.setBloqueado("B".equals(rs.getString("bloqueado")));
                    imp.setDataBloqueio(rs.getDate("databloqueado"));
                    imp.setEstadoCivil(rs.getString("estadocivil"));
                    imp.setDataNascimento(rs.getDate("dtanascimento"));
                    imp.setSexo(rs.getString("sexo"));
                    imp.setPermiteCreditoRotativo(rs.getBoolean("permitecreditorotativo"));
                    imp.setObservacao2(rs.getString("observacao"));
                    imp.setPrazoPagamento(rs.getInt("prazomaximo"));
                    imp.setValorLimite(rs.getDouble("limite"));
                    imp.setInscricaoMunicipal(rs.getString("inscmunicipal"));
                    
                    result.add(imp);
                }
            }
        }
        
        return result;
    }
    
}
