package vrimplantacao2.dao.interfaces;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.NutricionalToledoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.nutricional.OpcaoNutricional;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.dao.cadastro.venda.VendaHistoricoIMP;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.MathUtils;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.cadastro.receita.OpcaoReceitaBalanca;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.ContaPagarIMP;
import vrimplantacao2.vo.importacao.ContaPagarVencimentoIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.NutricionalIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;
import vrimplantacao2.vo.importacao.ReceitaBalancaIMP;
import vrimplantacao2.vo.importacao.VendaIMP;
import vrimplantacao2.vo.importacao.VendaItemIMP;

/**
 *
 * @author Leandro
 */
public class ShiDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean eFicha = false;
    private static final Logger LOG = Logger.getLogger(ShiDAO.class.getName());

    private Connection sco;
    private Connection sfi;
    private Connection cli;
    private Connection cupom;
    private Date dataInicioVenda;
    private Date dataTerminoVenda;

    public void setDataInicioVenda(Date dataInicioVenda) {
        this.dataInicioVenda = dataInicioVenda;
    }

    public void setDataTerminoVenda(Date dataTerminoVenda) {
        this.dataTerminoVenda = dataTerminoVenda;
    }

    @Override
    public String getSistema() {
        return "SHI";
    }

    public List<Estabelecimento> getLojasCliente() throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    codigo || ' - ' || razsoc descricao\n"
                    + "from filial\n"
                    + "order by codigo"
            )) {
                while (rst.next()) {
                    result.add(new Estabelecimento(rst.getString("codigo"), rst.getString("descricao")));
                }
            }
        }
        return result;
    }

    @Override
    public List<MercadologicoNivelIMP> getMercadologicoPorNivel() throws Exception {
        List<MercadologicoNivelIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT CODIGO, DESCRI FROM GRUPO ORDER BY char_length(codigo), codigo"
            )) {
                MultiMap<String, MercadologicoNivelIMP> mercs = new MultiMap<>();
                while (rst.next()) {

                    String merc = rst.getString("CODIGO") != null ? rst.getString("CODIGO") : "";
                    String[] cods = merc.split("\\.");

                    if (cods.length == 1) {
                        MercadologicoNivelIMP imp = new MercadologicoNivelIMP();
                        imp.setId(cods[0]);
                        imp.setDescricao(rst.getString("descri"));
                        mercs.put(imp, imp.getId());
                        result.add(imp);
                    } else if (cods.length == 2) {
                        mercs.put(mercs.get(cods[0])
                                .addFilho(cods[1], rst.getString("descri")), cods[0], cods[1]);
                    } else if (cods.length == 3) {
                        mercs.put(mercs.get(cods[0], cods[1])
                                .addFilho(cods[2], rst.getString("descri")), cods[0], cods[1], cods[2]);
                    } else if (cods.length == 4) {
                        mercs.put(mercs.get(cods[0], cods[1], cods[2])
                                .addFilho(cods[3], rst.getString("descri")), cods[0], cods[1], cods[2], cods[3]);
                    } else if (cods.length == 5) {
                        mercs.get(cods[0], cods[1], cods[2], cods[3])
                                .addFilho(cods[4], rst.getString("descri"));
                    }
                }
            }
        }

        return result;
    }

    @Override
    public List<FamiliaProdutoIMP> getFamiliaProduto() throws Exception {
        List<FamiliaProdutoIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT ID, DESCRI descricao FROM ALTERN order by id"
            )) {
                while (rst.next()) {
                    FamiliaProdutoIMP imp = new FamiliaProdutoIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos() throws Exception {
        List<ProdutoIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.codigo id,\n"
                    + "    p.inclusao datacadastro,\n"
                    + "    coalesce(ean.barras, p.codigo) ean,\n"
                    + "    p.embala qtdEmbalagemCx,\n"
                    + "    p.uniemb,\n"
                    + "    p.unipro unidade,\n"
                    + "    case p.balanc when 'S' then 1 else 0 end e_balanca,\n"
                    + "    p.altern,\n"
                    + "    p.valpre validade,\n"
                    + "    p.descri descricaocompleta,\n"
                    + "    p.fantas descricaoreduzida,\n"
                    + "    p.grupox mercadologico,\n"
                    + "    p.peso,\n"
                    + "    pv.estmin,\n"
                    + "    pv.estmax,\n"
                    + "    0 as estoque,\n"
                    + "    pv.lucro,\n"
                    + "    0 custo,\n"
                    + "    pv.preco,\n"
                    + "    case p.inativ when 'S' then 0 else 1 end ativo,\n"
                    + "    ncm.clafis ncm,\n"
                    + "    p.cest,\n"
                    + "    p.cstpis,\n"
                    + "    p.cstcofins,\n"
                    + "    p.cstpiscr,\n"
                    + "    p.cstcofinscr,\n"
                    + "    p.natrec,\n"
                    + "    icm.icms\n"
                    + "from\n"
                    + "    produtos p\n"
                    + "    join filial f on f.codigo = " + getLojaOrigem() + "\n"
                    + "    left join barras ean on ean.codpro = p.codigo\n"
                    + "    left join precovenda pv on pv.codpro = p.codigo and pv.filial = f.codigo\n"
                    + "    left join clafis ncm on ncm.id = p.idclafis\n"
                    + "    left join icmsprod icm on icm.codpro = p.codigo and icm.estado = f.estado\n"
                    + "order by\n"
                    + "    p.codigo"
            )) {
                while (rst.next()) {
                    {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("id"));
                        imp.setEan(rst.getString("ean"));
                        imp.setTipoEmbalagem(rst.getString("unidade"));
                        imp.seteBalanca(rst.getBoolean("e_balanca"));
                        imp.setValidade(rst.getInt("validade"));
                        imp.setDescricaoCompleta(rst.getString("descricaocompleta"));
                        imp.setDescricaoReduzida(rst.getString("descricaoreduzida"));
                        imp.setDescricaoGondola(rst.getString("descricaocompleta"));
                        imp.setIdFamiliaProduto(rst.getString("altern"));

                        String merc = rst.getString("mercadologico") != null ? rst.getString("mercadologico") : "";
                        String[] cods = merc.split("\\.");

                        for (int i = 0; i < cods.length; i++) {
                            switch (i) {
                                case 0:
                                    imp.setCodMercadologico1(cods[i]);
                                    break;
                                case 1:
                                    imp.setCodMercadologico2(cods[i]);
                                    break;
                                case 2:
                                    imp.setCodMercadologico3(cods[i]);
                                    break;
                                case 3:
                                    imp.setCodMercadologico4(cods[i]);
                                    break;
                                case 4:
                                    imp.setCodMercadologico5(cods[i]);
                                    break;
                            }
                        }

                        imp.setPesoBruto(rst.getDouble("peso"));
                        imp.setPesoLiquido(rst.getDouble("peso"));
                        imp.setEstoqueMinimo(rst.getDouble("estmin"));
                        imp.setEstoqueMaximo(rst.getDouble("estmax"));
                        imp.setMargem(rst.getDouble("lucro"));
                        imp.setPrecovenda(rst.getDouble("preco"));
                        imp.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("ativo")));
                        imp.setNcm(rst.getString("ncm"));
                        imp.setCest(rst.getString("cest"));
                        imp.setIcmsDebitoId(rst.getString("icms"));
                        imp.setIcmsCreditoId(rst.getString("icms"));
                        imp.setPiscofinsCstDebito(Utils.stringToInt(rst.getString("cstpis")));
                        imp.setPiscofinsCstCredito(Utils.stringToInt(rst.getString("cstpiscr")));
                        imp.setPiscofinsNaturezaReceita(Utils.stringToInt(rst.getString("natrec")));
                        result.add(imp);
                    }
                }
            }
        }
        return result;
    }

    @Override
    public List<ProdutoIMP> getProdutos(OpcaoProduto opcao) throws Exception {
        if (opcao == OpcaoProduto.CUSTO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = sco.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    pc.codpro,\n"
                        + "    pc.custo\n"
                        + "from\n"
                        + "    precocusto pc\n"
                        + "    join(select\n"
                        + "             codpro,\n"
                        + "             filial,\n"
                        + "             max(data) data\n"
                        + "         from\n"
                        + "             precocusto\n"
                        + "         group by\n"
                        + "             codpro, filial) a using (codpro, filial, data)\n"
                        + "where\n"
                        + "    pc.filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("codpro"));
                        imp.setCustoComImposto(rst.getDouble("custo"));
                        imp.setCustoSemImposto(rst.getDouble("custo"));
                        result.add(imp);
                    }
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.ESTOQUE) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = sco.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    e.codpro,\n"
                        + "    e.saldoatu,\n"
                        + "    e.saldoant,\n"
                        + "    e.movimento\n"
                        + "from\n"
                        + "    estoque e\n"
                        + "    join(select\n"
                        + "             codpro,\n"
                        + "             filial,\n"
                        + "             max(data) data\n"
                        + "         from\n"
                        + "             estoque\n"
                        + "         group by\n"
                        + "             codpro, filial) a using (codpro, filial, data)\n"
                        + "where\n"
                        + "    e.filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("codpro"));
                        imp.setEstoque(rst.getDouble("saldoatu"));
                        result.add(imp);
                    }
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.SUGESTAO_COTACAO || opcao == OpcaoProduto.SUGESTAO_PEDIDO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = sco.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select p.codigo codProduto, p.descri, p.catego,\n"
                        + "       c.codigo codCategoria, c.descri descCategoria\n"
                        + "from produtos p\n"
                        + "inner join categoria c on c.codigo = p.catego "
                        + "join filial f on f.codigo = " + getLojaOrigem() + "\n"
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("codigo"));
                        if (rst.getInt("codCategoria") == 1) {
                            imp.setSugestaoPedido(true);
                            imp.setSugestaoCotacao(false);
                        } else if (rst.getInt("codCategoria") == 2) {
                            imp.setSugestaoPedido(false);
                            imp.setSugestaoCotacao(true);
                        } else {
                            imp.setSugestaoPedido(true);
                            imp.setSugestaoCotacao(true);
                        }
                        result.add(imp);
                    }
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.CUSTO_COM_IMPOSTO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = sco.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    ri.codpro,\n"
                        + "    ri.cusmed\n"
                        + "from recitem ri\n"
                        + "join(\n"
                        + "select\n"
                        + "    codpro,\n"
                        + "    filial,\n"
                        + "    max(data) data\n"
                        + "from recitem\n"
                        + "group by\n"
                        + "    codpro, filial) a using (codpro, filial, data)\n"
                        + "where ri.tipmov = 1\n"
                        + "and ri.filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("codpro"));
                        imp.setCustoComImposto(rst.getDouble("cusmed"));
                        result.add(imp);
                    }
                }
            }
            return result;
        } else if (opcao == OpcaoProduto.CUSTO_SEM_IMPOSTO) {
            List<ProdutoIMP> result = new ArrayList<>();
            try (Statement stm = sco.createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n"
                        + "    pc.codpro,\n"
                        + "    pc.custo\n"
                        + "from\n"
                        + "    precocusto pc\n"
                        + "    join(select\n"
                        + "             codpro,\n"
                        + "             filial,\n"
                        + "             max(data) data\n"
                        + "         from\n"
                        + "             precocusto\n"
                        + "         group by\n"
                        + "             codpro, filial) a using (codpro, filial, data)\n"
                        + "where\n"
                        + "    pc.filial = " + getLojaOrigem()
                )) {
                    while (rst.next()) {
                        ProdutoIMP imp = new ProdutoIMP();
                        imp.setImportSistema(getSistema());
                        imp.setImportLoja(getLojaOrigem());
                        imp.setImportId(rst.getString("codpro"));
                        imp.setCustoSemImposto(rst.getDouble("custo"));
                        result.add(imp);
                    }
                }
            }
            return result;
        }

        return null;
    }

    @Override
    public List<MapaTributoIMP> getTributacao() throws Exception {
        List<MapaTributoIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    descri\n"
                    + "from\n"
                    + "    icms\n"
                    + "order by\n"
                    + "    codigo"
            )) {
                while (rst.next()) {
                    result.add(new MapaTributoIMP(rst.getString("codigo"), rst.getString("descri")));
                }
            }
        }

        return result;
    }

    @Override
    public List<FornecedorIMP> getFornecedores() throws Exception {
        List<FornecedorIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    f.codigo id,\n"
                    + "    f.nomexx razao,\n"
                    + "    f.fantas fantasia,\n"
                    + "    f.ciccgc cnpj,\n"
                    + "    f.inscrg ie_rg,\n"
                    + "    case f.inativ when 'S' then 0 else 1 end ativo,\n"
                    + "    f.endere endereco,\n"
                    + "    f.bairro,\n"
                    + "    f.cidade,\n"
                    + "    f.estado,\n"
                    + "    f.cepxxx cep,\n"
                    + "    f.pedmin valor_minimo_pedido,\n"
                    + "    f.dtcada datacadastro,\n"
                    + "    f.observ observacao,\n"
                    + "    f.entreg prazoEntrega,\n"
                    + "    f.frecom prazoVisita,\n"
                    + "    case when coalesce(trim(upper(f.simplesnac)),'N') = 'S' then 1 else 0 end simplesnac\n"
                    + "from\n"
                    + "    fornecedor f  \n"
                    + "order by\n"
                    + "    f.codigo"
            )) {
                while (rst.next()) {
                    FornecedorIMP imp = new FornecedorIMP();
                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setImportId(rst.getString("id"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setCnpj_cpf(rst.getString("cnpj"));
                    imp.setIe_rg(rst.getString("ie_rg"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("cidade"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setValor_minimo_pedido(rst.getDouble("valor_minimo_pedido"));
                    imp.setDatacadastro(rst.getDate("datacadastro"));
                    imp.setObservacao(rst.getString("observacao"));
                    imp.setPrazoEntrega(rst.getInt("prazoEntrega"));
                    imp.setPrazoVisita(rst.getInt("prazoVisita"));
                    if (rst.getBoolean("simplesnac")) {
                        imp.setTipoEmpresa(TipoEmpresa.EPP_SIMPLES);
                    } else {
                        imp.setTipoEmpresa(TipoEmpresa.LUCRO_REAL);
                    }

                    try (Statement stm2 = sco.createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n"
                                + "    codigo,\n"
                                + "    trim(coalesce(contato,'')) contato,\n"
                                + "    trim(coalesce(telefone,'')) telefone,\n"
                                + "    trim(coalesce(fax,'')) fax,\n"
                                + "    trim(coalesce(celular,'')) celular,\n"
                                + "    trim(coalesce(email,'')) email\n"
                                + "from\n"
                                + "    contato\n"
                                + "where\n"
                                + "    fornecedor = " + imp.getImportId() + "\n"
                                + "order by\n"
                                + "    codigo"
                        )) {
                            boolean primeiro = true;
                            while (rst2.next()) {
                                String fone = !"".equals(rst2.getString("telefone")) ? rst2.getString("telefone") : rst2.getString("celular");
                                if (primeiro && !"".equals(fone)) {
                                    primeiro = false;
                                    imp.setTel_principal(fone);
                                }
                                imp.addContato(
                                        rst2.getString("codigo"),
                                        rst2.getString("contato"),
                                        rst2.getString("telefone"),
                                        rst2.getString("celular"),
                                        TipoContato.COMERCIAL,
                                        rst2.getString("email")
                                );
                                if (!"".equals(rst2.getString("fax"))) {
                                    imp.addContato(
                                            rst2.getString("codigo"),
                                            "FAX",
                                            rst2.getString("telefone"),
                                            rst2.getString("celular"),
                                            TipoContato.COMERCIAL,
                                            rst2.getString("email")
                                    );
                                }
                            }
                        }
                    }

                    try (Statement stm3 = sco.createStatement()) {
                        try (ResultSet rst3 = stm3.executeQuery(
                                "select f.codigo, f.ciccgc, f.nomexx,\n"
                                + "       f.pagame, p.descri, p.numpar,\n"
                                + "       coalesce(p.dias01, 0) dias01, coalesce(p.dias02, 0) dias02,\n"
                                + "       coalesce(p.dias03, 0) dias03, coalesce(p.dias04, 0) dias04,\n"
                                + "       coalesce(p.dias05, 0) dias05, coalesce(p.dias06, 0) dias06,\n"
                                + "       coalesce(p.dias07, 0) dias07, coalesce(p.dias08, 0) dias08,\n"
                                + "       coalesce(p.dias09, 0) dias09, coalesce(p.dias10, 0) dias10,\n"
                                + "       coalesce(p.dias11, 0) dias11, coalesce(p.dias12, 0) dias12,\n"
                                + "       coalesce(p.dias13, 0) dias13, coalesce(p.dias14, 0) dias14,\n"
                                + "       coalesce(p.dias15, 0) dias15, coalesce(p.dias16, 0) dias16,\n"
                                + "       coalesce(p.dias17, 0) dias17, coalesce(p.dias18, 0) dias18,\n"
                                + "       coalesce(p.dias19, 0) dias19, coalesce(p.dias20, 0) dias20,\n"
                                + "       coalesce(p.dias21, 0) dias21, coalesce(p.dias22, 0) dias22,\n"
                                + "       coalesce(p.dias23, 0) dias23, coalesce(p.dias24, 0) dias24\n"
                                + "  from fornecedor f\n"
                                + " inner join condfatur p on p.codigo = f.pagame "
                                + " where f.codigo = '" + imp.getImportId() + "'"
                        )) {
                            int numParcelas, i = 1;
                            if (rst3.next()) {
                                numParcelas = rst3.getInt("numpar");
                                while (i <= numParcelas) {
                                    if (i == 1) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias01"));
                                    }
                                    if (i == 2) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias02"));

                                    }
                                    if (i == 3) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias03"));
                                    }
                                    if (i == 4) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias04"));
                                    }
                                    if (i == 5) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias05"));
                                    }
                                    if (i == 6) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias06"));
                                    }
                                    if (i == 7) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias07"));
                                    }
                                    if (i == 8) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias08"));
                                    }
                                    if (i == 9) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias09"));
                                    }
                                    if (i == 10) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias10"));
                                    }
                                    if (i == 11) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias11"));
                                    }
                                    if (i == 12) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias12"));
                                    }
                                    if (i == 13) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias13"));
                                    }
                                    if (i == 14) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias14"));
                                    }
                                    if (i == 15) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias15"));
                                    }
                                    if (i == 16) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias16"));
                                    }
                                    if (i == 17) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias17"));
                                    }
                                    if (i == 18) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias18"));
                                    }
                                    if (i == 19) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias19"));
                                    }
                                    if (i == 20) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias20"));
                                    }
                                    if (i == 21) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias21"));
                                    }
                                    if (i == 22) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias22"));
                                    }
                                    if (i == 23) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias23"));
                                    }
                                    if (i == 24) {
                                        imp.addPagamento(
                                                String.valueOf(i),
                                                rst3.getInt("dias24"));
                                    }
                                    i++;
                                }
                            }
                        }
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

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select p.codigo codPro,  p.reffab codExt, p.fornec codFor, "
                    + "p.embala emb, current_date data\n"
                    + "from produtos p\n"
                    + "union\n"
                    + "select pf.codpro codPro, pf.codfor codExt, pf.fornec codFor, "
                    + "pf.embala emb,  pf.data\n"
                    + "from codfornec pf"
            )) {
                while (rst.next()) {
                    ProdutoFornecedorIMP imp = new ProdutoFornecedorIMP();

                    imp.setImportSistema(getSistema());
                    imp.setImportLoja(getLojaOrigem());
                    imp.setIdFornecedor(rst.getString("codFor"));
                    imp.setIdProduto(rst.getString("codPro"));
                    imp.setCodigoExterno(rst.getString("codExt"));
                    imp.setDataAlteracao(rst.getDate("data"));
                    imp.setQtdEmbalagem(rst.getDouble("emb"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ClienteIMP> getClientes() throws Exception {
        List<ClienteIMP> result = new ArrayList<>();

        try (Statement stm = cli.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.codigo id,\n"
                    + "    c.fichax,\n"
                    + "    c.ciccgc cnpj,\n"
                    + "    case when char_length(trim(c.ciccgc)) > 11 then c.inscri_jur else c.rgnume_fis end inscricaoestadual,\n"
                    + "    c.rgorga_fis orgaoemissor,\n"
                    + "    c.razsoc razao,\n"
                    + "    c.nomexx fantasia,\n"
                    + "    case when c.situac <> 1 then 0 else 1 end ativo,\n"
                    + "    c.endere_res endereco,\n"
                    + "    c.numero_res numero,\n"
                    + "    c.comple_res complemento,\n"
                    + "    c.bairro_res bairro,\n"
                    + "    c.cidade_res municipio,\n"
                    + "    c.estado_res estado,\n"
                    + "    c.cepxxx_res cep,     \n"
                    + "    trim(coalesce(c.telddd_res,'')||coalesce(c.telfon_res, '')) telefone,\n"
                    + "    c.nascim_fis datanascimento,\n"
                    + "    c.datcad datacadastro,\n"
                    + "    case c.sexoxx_fis when 2 then 0 else 1 end sexo,\n"
                    + "    emp.descri tipo_emprego,\n"
                    + "    c.nomexx_com empresa,\n"
                    + "    c.endere_com empresa_endereco,\n"
                    + "    c.numero_com empresa_numero,\n"
                    + "    c.comple_com empresa_complemento,\n"
                    + "    c.bairro_com empresa_bairro,\n"
                    + "    c.cidade_com empresa_cidade,\n"
                    + "    c.estado_com empresa_estado,\n"
                    + "    c.cepxxx_com empresa_cep, \n"
                    + "    trim(coalesce(c.telddd_com,'')||coalesce(c.telfon_com, '')) telefone_empresa,\n"
                    + "    c.rendax_fis salario,\n"
                    + "    c.limcre,\n"
                    + "    c.nomcon_fis conjuge,\n"
                    + "    c.nompai_fis pai,\n"
                    + "    c.nommae_fis mae,\n"
                    + "    c.mensagemsemst observacao,\n"
                    + "    c.diaven diaVencimento,\n"
                    + "    c.emailx email,\n"
                    + "    l.valorx valorCredito,\n "
                    + "    l2.valorx valorCheque,\n"
                    + "    trim(coalesce(c.telddd_cel,'')||coalesce(c.telfon_cel, '')) celular\n"
                    + "from\n"
                    + "    clientes c\n"
                    + "    left join emprego emp on c.empreg_fis = emp.codigo\n"
                    + "    left join limite l on c.limcon = l.codigo\n"
                    + "    left join limite l2 on c.limche = l2.codigo\n"
                    + "where c.filial = " + getLojaOrigem()
                    + " order by\n"
                    + "    c.codigo"
            )) {
                while (rst.next()) {
                    ClienteIMP imp = new ClienteIMP();
                    if (eFicha) {
                        String id;
                        if (rst.getString("fichax") != null && !rst.getString("fichax").isEmpty()) {
                            id = rst.getString("fichax");
                        } else {
                            id = "A" + rst.getString("codigo");
                        }
                        imp.setId(id);
                    } else {
                        imp.setId(rst.getString("codigo"));
                    }
                    imp.setCnpj(Utils.formataNumero(rst.getString("cnpj")));
                    imp.setInscricaoestadual(Utils.formataNumero(rst.getString("inscricaoestadual")));
                    imp.setOrgaoemissor(rst.getString("orgaoemissor"));
                    imp.setRazao(rst.getString("razao"));
                    imp.setFantasia(rst.getString("fantasia"));
                    imp.setAtivo(rst.getBoolean("ativo"));
                    imp.setEndereco(rst.getString("endereco"));
                    imp.setNumero(rst.getString("numero"));
                    imp.setComplemento(rst.getString("complemento"));
                    imp.setBairro(rst.getString("bairro"));
                    imp.setMunicipio(rst.getString("municipio"));
                    imp.setUf(rst.getString("estado"));
                    imp.setCep(rst.getString("cep"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setDataNascimento(rst.getDate("datanascimento"));
                    imp.setDataCadastro(rst.getDate("datacadastro"));
                    imp.setSexo(rst.getInt("sexo") != 0 ? TipoSexo.MASCULINO : TipoSexo.FEMININO);
                    imp.setEmpresa(rst.getString("empresa"));
                    imp.setEmpresaEndereco(rst.getString("empresa_endereco"));
                    imp.setEmpresaNumero(rst.getString("empresa_numero"));
                    imp.setEmpresaComplemento(rst.getString("empresa_complemento"));
                    imp.setEmpresaBairro(rst.getString("empresa_bairro"));
                    imp.setEmpresaMunicipio(rst.getString("empresa_cidade"));
                    imp.setEmpresaUf(rst.getString("empresa_estado"));
                    imp.setEmpresaCep(rst.getString("empresa_cep"));
                    imp.setEmpresaTelefone(rst.getString("telefone_empresa"));
                    imp.setSalario(rst.getDouble("salario"));
                    imp.setValorLimite(rst.getDouble("valorCredito"));
                    imp.setNomeConjuge(rst.getString("conjuge"));
                    imp.setNomePai(rst.getString("pai"));
                    imp.setNomeMae(rst.getString("mae"));
                    imp.setObservacao(
                            (rst.getString("tipo_emprego") != null ? "TIPO EMPREGO: " + rst.getString("tipo_emprego") : "")
                            + (rst.getString("observacao"))
                    );
                    imp.setDiaVencimento(rst.getInt("diaVencimento"));
                    imp.setEmail(rst.getString("email"));
                    imp.setCelular(rst.getString("celular"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<CreditoRotativoIMP> getCreditoRotativo() throws Exception {
        List<CreditoRotativoIMP> result = new ArrayList<>();

        try (Statement stm = cli.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    d.FILIAL||'-'||d.TIPDOC||'-'||d.SEQUEN||'-'||d.DESDOB id,\n "
                    + "    d.SEQUEN,\n"
                    + "    cl.fichax,\n"
                    + "    d.dataxx,\n"
                    + "    c.cupomx as cupom,\n"
                    + "    d.cupomx as cupom_doc,\n"
                    + "    c.caixax,\n"
                    + "    d.valorx,\n"
                    + "    d.observ,\n"
                    + "    d.client,\n"
                    + "    d.vencim,\n"
                    + "    cl.ciccgc\n"
                    + "from\n"
                    + "    documentos d\n"
                    + "    left join cupom c on d.idcupom = c.idcupom\n"
                    + "    join clientes cl on cl.codigo = d.client\n"
                    + "where\n"
                    + "    status = 1\n"
                    + " and d.filial = " + getLojaOrigem()
                    + " order by\n"
                    + "    d.datcad"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();

                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("dataxx"));

                    if ((rst.getString("cupom") != null)
                            && (!rst.getString("cupom").trim().isEmpty())) {
                        imp.setNumeroCupom(rst.getString("cupom"));
                    } else {
                        imp.setNumeroCupom(rst.getString("SEQUEN"));
                    }

                    imp.setEcf(rst.getString("caixax"));
                    imp.setValor(rst.getDouble("valorx"));
                    imp.setObservacao(rst.getString("observ"));
                    if (eFicha) {
                        if ((rst.getString("fichax") != null)
                                && (!rst.getString("fichax").trim().isEmpty())) {
                            imp.setIdCliente(rst.getString("fichax"));
                        } else {
                            imp.setIdCliente("A" + rst.getString("client"));
                        }
                    } else {
                        imp.setIdCliente(rst.getString("client"));
                    }
                    imp.setDataVencimento(rst.getDate("vencim"));
                    imp.setCnpjCliente(rst.getString("ciccgc"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ChequeIMP> getCheques() throws Exception {
        List<ChequeIMP> result = new ArrayList<>();

        try (Statement stm = cli.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    c.filial||'-'||c.sequen id,\n"
                    + "    c.ciccgc cpf,\n"
                    + "    c.cheque,\n"
                    + "    c.bancox,\n"
                    + "    c.agenci,\n"
                    + "    c.contax,\n"
                    + "    c.dataxx,\n"
                    + "    c.vencim,\n"
                    + "    c.valorx,\n"
                    + "    case when char_length(trim(cl.ciccgc)) > 11 then cl.inscri_jur else cl.rgnume_fis end rg,\n"
                    + "    trim(coalesce(cl.telddd_res,'')||coalesce(cl.telfon_res, '')) telefone,\n"
                    + "    cl.razsoc,\n"
                    + "    c.observ,\n"
                    + "    case c.status when 1 then 0 else coalesce(c.motdv2, c.motdv1) end alinea,\n"
                    + "    c.datalt\n"
                    + "from\n"
                    + "    cheques c\n"
                    + "    join clientes cl on c.client = cl.codigo\n"
                    + "where\n"
                    + "    c.quitad is null\n"
                    + " and c.filial = " + getLojaOrigem()
                    + " order by\n"
                    + "    id"
            )) {
                while (rst.next()) {
                    ChequeIMP imp = new ChequeIMP();
                    imp.setId(rst.getString("id"));
                    imp.setCpf(rst.getString("cpf"));
                    imp.setNumeroCheque(rst.getString("cheque"));
                    imp.setBanco(Utils.stringToInt(rst.getString("bancox")));
                    imp.setAgencia(rst.getString("agenci"));
                    imp.setConta(rst.getString("contax"));
                    imp.setDate(rst.getDate("dataxx"));
                    imp.setDataDeposito(rst.getDate("vencim"));
                    imp.setValor(rst.getDouble("valorx"));
                    imp.setRg(rst.getString("rg"));
                    imp.setTelefone(rst.getString("telefone"));
                    imp.setNome(rst.getString("razsoc"));
                    imp.setObservacao("IMPORTADO VR " + rst.getString("observ"));
                    imp.setAlinea(Utils.stringToInt(rst.getString("alinea")));
                    imp.setDataHoraAlteracao(rst.getTimestamp("datalt"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<NutricionalIMP> getNutricional(Set<OpcaoNutricional> opcoes) throws Exception {
        List<NutricionalIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "n.codpro as id,\n"
                    + "substring(p.descri from 1 for 20) as descricao,\n"
                    + "n.porcao as porcao,\n"
                    + "n.valcal as caloria,\n"
                    + "n.carboi as carboidratos,\n"
                    + "n.protei as proteina,\n"
                    + "n.gortot as gorduras,\n"
                    + "n.gorsat as gorduras_saturada,\n"
                    + "n.colest,\n"
                    + "n.fibra as fibra_alimentar,\n"
                    + "n.calcio as calcio,\n"
                    + "n.ferro as ferro,\n"
                    + "n.sodio as sodio,\n"
                    + "n.vdvalcal,\n"
                    + "n.vdcarboi,\n"
                    + "n.vdprotei,\n"
                    + "n.vdgortot,\n"
                    + "n.vdgorsat,\n"
                    + "n.vdcolest,\n"
                    + "n.vdfibra,\n"
                    + "n.vdcalcio,\n"
                    + "n.vdferro,\n"
                    + "n.vdsodio\n"
                    + "from tabnutric n\n"
                    + "inner join produtos p on p.codigo = n.codpro"
            )) {
                while (rst.next()) {
                    NutricionalIMP imp = new NutricionalIMP();
                    imp.setId(rst.getString("id"));
                    imp.setDescricao(rst.getString("descricao"));
                    imp.setSituacaoCadastro(SituacaoCadastro.ATIVO);
                    imp.setCaloria(rst.getInt("caloria"));
                    imp.setCarboidrato(rst.getDouble("carboidratos"));
                    imp.setProteina(rst.getDouble("proteina"));
                    imp.setGordura(rst.getDouble("gorduras"));
                    imp.setFibra(rst.getDouble("fibra_alimentar"));
                    imp.setCalcio(rst.getDouble("calcio"));
                    imp.setFerro(rst.getDouble("ferro"));
                    imp.setSodio(rst.getDouble("sodio"));
                    imp.setPorcao(rst.getString("porcao"));
                    //imp.getMensagemAlergico().add(rst.getString("mensagemalergico"));                    
                    imp.addProduto(rst.getString("id"));
                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ReceitaBalancaIMP> getReceitaBalanca(Set<OpcaoReceitaBalanca> opt) throws Exception {
        List<ReceitaBalancaIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "p.codigo id_produto,\n"
                    + "p.descri desc_produto,\n"
                    + "p.receita,\n"
                    + "r.descri desc_receita\n"
                    + "from produtos p\n"
                    + "join receita r on r.codigo = p.receita and r.codigo > 0"
            )) {
                while (rst.next()) {
                    ReceitaBalancaIMP imp = new ReceitaBalancaIMP();

                    imp.setId(rst.getString("receita") + "-" + rst.getString("id_produto"));
                    imp.setDescricao(rst.getString("desc_produto"));
                    imp.setReceita(rst.getString("desc_receita"));
                    imp.getProdutos().add(rst.getString("id_produto"));
                    result.add(imp);
                }
            }
        }
        return result;
    }

    private List<NutricionalToledoVO> carregarNutricionalToledo() throws Exception {
        List<NutricionalToledoVO> vNutricionalToledo = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select n.codpro,\n"
                    + "'TOLEDO' descricao,\n"
                    + "n.porcao,\n"
                    + "n.valcal,\n"
                    + "n.carboi,\n"
                    + "n.protei,\n"
                    + "n.gortot,\n"
                    + "n.gorsat,\n"
                    + "n.colest,\n"
                    + "n.fibra,\n"
                    + "n.calcio,\n"
                    + "n.ferro,\n"
                    + "n.sodio,\n"
                    + "n.vdvalcal,\n"
                    + "n.vdcarboi,\n"
                    + "n.vdprotei,\n"
                    + "n.vdgortot,\n"
                    + "n.vdgorsat,\n"
                    + "n.vdcolest,\n"
                    + "n.vdfibra,\n"
                    + "n.vdcalcio,\n"
                    + "n.vdferro,\n"
                    + "n.vdsodio\n"
                    + "from tabnutric n\n"
                    + "inner join produtos p on p.codigo = n.codpro\n"
                    + "where p.balanc = 'S'"
            )) {
                while (rst.next()) {
                    NutricionalToledoVO oNutricionalToledo = new NutricionalToledoVO();
                    oNutricionalToledo.setCaloria(rst.getInt("valcal"));
                    oNutricionalToledo.setCarboidrato(rst.getDouble("carboi"));
                    oNutricionalToledo.setProteina(rst.getDouble("protei"));
                    oNutricionalToledo.setGordura(rst.getDouble("gortot"));
                    oNutricionalToledo.setGordurasaturada(rst.getDouble("gorsat"));
                    oNutricionalToledo.setGorduratrans(0);
                    oNutricionalToledo.setFibra(rst.getDouble("fibra"));
                    oNutricionalToledo.setSodio(rst.getDouble("sodio"));
                    oNutricionalToledo.setDescricao(rst.getString("descricao"));
                    oNutricionalToledo.setId_tipomedida(2);
                    NutricionalToledoItemVO oNutricionalToledoItem = new NutricionalToledoItemVO();
                    oNutricionalToledoItem.setStrID(rst.getString("codpro"));
                    oNutricionalToledo.vNutricionalToledoItem.add(oNutricionalToledoItem);
                    vNutricionalToledo.add(oNutricionalToledo);
                }
            }
        }
        return vNutricionalToledo;
    }

    public void importarNutricionalToledo() throws Exception {
        try {
            ProgressBar.setStatus("Carregando dados...Nutricional Toledo...");
            List<NutricionalToledoVO> vNutricionalToledo = carregarNutricionalToledo();
            if (!vNutricionalToledo.isEmpty()) {
                new NutricionalToledoDAO().salvarV2(vNutricionalToledo, getSistema(), getLojaOrigem());
            }
        } catch (Exception ex) {
            throw ex;
        }
    }

    @Override
    public List<OfertaIMP> getOfertas(Date dataTermino) throws Exception {
        if (dataTermino == null) {
            dataTermino = new Date();
        }
        List<OfertaIMP> result = new ArrayList<>();

        try (Statement stm = sco.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    pv.filial,\n"
                    + "    pv.codpro idproduto,\n"
                    + "    pv.inipro datainicio,\n"
                    + "    pv.terpro datatermino,\n"
                    + "    pv.promoc precooferta\n"
                    + "from\n"
                    + "    precovenda pv\n"
                    + "where\n"
                    + "   pv.filial = " + getLojaOrigem() + " and\n"
                    + "   pv.promoc > 0 and\n"
                    + "    pv.terpro >= " + SQLUtils.stringSQL(
                            new SimpleDateFormat("dd.MM.yyyy").format(dataTermino)
                    )
            )) {
                while (rst.next()) {
                    OfertaIMP imp = new OfertaIMP();

                    imp.setIdProduto(rst.getString("idproduto"));
                    imp.setDataInicio(rst.getDate("datainicio"));
                    imp.setDataFim(rst.getDate("datatermino"));
                    imp.setPrecoOferta(rst.getDouble("precooferta"));
                    imp.setSituacaoOferta(SituacaoOferta.ATIVO);
                    imp.setTipoOferta(TipoOfertaVO.CAPA);

                    result.add(imp);
                }
            }
        }

        return result;
    }

    @Override
    public List<ContaPagarIMP> getContasPagar() throws Exception {
        List<ContaPagarIMP> result = new ArrayList<>();

        try (Statement stm = sfi.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    p.sequen id,\n"
                    + "    nullif(trim(e.idfornec),'') id_fornecedor,\n"
                    + "    p.docume numerodocumento,\n"
                    + "    p.dataxx dataemissao,\n"
                    + "    p.dataentra dataentrada,\n"
                    + "    p.valorpago,\n"
                    + "                         \n"
                    + "    p.vencim vencimento,\n"
                    + "    p.valorx valor,\n"
                    + "    p.parcela,\n"
                    + "    p.histbaixa observacao\n"
                    + "from\n"
                    + "    docpagar p\n"
                    + "    join entidade e on\n"
                    + "        p.entidade = e.codigo\n"
                    + "where\n"
                    + "    p.filial = " + getLojaOrigem().substring(0, getLojaOrigem().length() - 1) + "\n"
                    + "    and p.status = 'P'\n"
                    + "    and not nullif(trim(e.idfornec),'') is null\n"
                    + "order by\n"
                    + "    p.sequen"
            )) {
                while (rst.next()) {
                    ContaPagarIMP imp = new ContaPagarIMP();

                    imp.setId(rst.getString("id"));
                    imp.setIdFornecedor(Utils.stringLong(rst.getString("id_fornecedor")));
                    imp.setNumeroDocumento(rst.getString("numerodocumento"));
                    imp.setDataEmissao(rst.getDate("dataemissao"));
                    imp.setDataEntrada(rst.getDate("dataentrada"));
                    imp.setObservacao(rst.getString("observacao"));
                    ContaPagarVencimentoIMP parc = imp.addVencimento(rst.getDate("vencimento"), rst.getDouble("valor"));
                    parc.setNumeroParcela(Utils.stringToInt(rst.getString("parcela"), 1));
                    parc.setObservacao(rst.getString("observacao"));

                    result.add(imp);
                }
            }
        }

        return result;
    }

    private class ProdutoComplementoParcial {

        int id;
        String impId;
        double pisDeb = 0;
        double pisCred = 0;
        double pisRed = 0;
        double icmsDebCst = 0;
        double icmsDebAliq = 0;
        double icmsDebRed = 0;
        double icmsCredCst = 0;
        double icmsCredAliq = 0;
        double icmsCredRed = 0;
    }

    @Override
    public List<VendaHistoricoIMP> getHistoricoVenda() throws Exception {
        List<VendaHistoricoIMP> result = new ArrayList<>();
        Map<String, String> eans = new HashMap<>();
        Map<String, ProdutoComplementoParcial> aliquotas = new HashMap<>();

        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select \n"
                    + "	ant.impid,\n"
                    + "	min(ean.codigobarras) codigobarras\n"
                    + "from\n"
                    + "	produtoautomacao ean\n"
                    + "	join implantacao.codant_produto ant on\n"
                    + "		ant.codigoatual = ean.id_produto and\n"
                    + "		ant.impsistema = '" + getSistema() + "' and\n"
                    + "         ant.imploja = '" + getLojaOrigem() + "' \n"
                    + "group by\n"
                    + "	ant.impid"
            )) {
                while (rst.next()) {
                    eans.put(rst.getString("impid"), rst.getString("codigobarras"));
                }
            }

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	ant.impid,\n"
                    + "	ant.codigoatual,\n"
                    + "	pisdeb.valorpis pisdeb,\n"
                    + "	piscred.valorpis piscred,\n"
                    + "	piscred.reduzidocredito pisred,\n"
                    + "	aliqdeb.situacaotributaria icmsdeb_cst,\n"
                    + "	aliqdeb.porcentagem icmsdeb_aliq,\n"
                    + "	aliqdeb.reduzido icmsdeb_red,\n"
                    + "	aliqcred.situacaotributaria icmscred_cst,\n"
                    + "	aliqcred.porcentagem icmscred_aliq,\n"
                    + "	aliqcred.reduzido icmscred_red\n"
                    + "from\n"
                    + "	produtoaliquota al\n"
                    + "	join (\n"
                    + "			select \n"
                    + "				loja.id, \n"
                    + "				f.id_estado\n"
                    + "			from\n"
                    + "				loja\n"
                    + "				join fornecedor f on\n"
                    + "					loja.id_fornecedor = f.id\n"
                    + "	) loja on\n"
                    + "		loja.id = 1 and\n"
                    + "		loja.id_estado = al.id_estado\n"
                    + "	join produto p on\n"
                    + "		al.id_produto = p.id\n"
                    + "	join implantacao.codant_produto ant on\n"
                    + "		ant.impsistema = '" + getSistema() + "' and\n"
                    + "		ant.imploja = '" + getLojaOrigem() + "' and\n"
                    + "		ant.codigoatual = al.id_produto\n"
                    + "	join tipopiscofins pisdeb on\n"
                    + "		p.id_tipopiscofins = pisdeb.id\n"
                    + "	join tipopiscofins piscred on\n"
                    + "		p.id_tipopiscofinscredito = piscred.id\n"
                    + "	join aliquota aliqdeb on\n"
                    + "		al.id_aliquotadebito = aliqdeb.id\n"
                    + "	join aliquota aliqcred on\n"
                    + "		al.id_aliquotacredito = aliqcred.id\n"
                    + "order by\n"
                    + "	1"
            )) {
                while (rst.next()) {
                    ProdutoComplementoParcial pcp = new ProdutoComplementoParcial();
                    pcp.id = rst.getInt("codigoatual");
                    pcp.impId = rst.getString("impid");
                    pcp.pisDeb = rst.getDouble("pisdeb");
                    pcp.pisCred = rst.getDouble("piscred");
                    pcp.pisRed = rst.getDouble("pisred");
                    pcp.icmsDebCst = rst.getDouble("icmsdeb_cst");
                    pcp.icmsDebAliq = rst.getDouble("icmsdeb_aliq");
                    pcp.icmsDebRed = rst.getDouble("icmsdeb_red");
                    pcp.icmsCredCst = rst.getDouble("icmscred_cst");
                    pcp.icmsCredAliq = rst.getDouble("icmscred_aliq");
                    pcp.icmsCredRed = rst.getDouble("icmscred_red");
                    aliquotas.put(pcp.impId, pcp);
                }
            }
        }

        try (Statement stm = cupom.createStatement()) {
            SimpleDateFormat fmt = new SimpleDateFormat("yyyy-MM-dd");

            System.out.println(
                    "select\n"
                    + "m.data as data,\n"
                    + "m.ecf,\n"
                    + "m.venda as totaldia,\n"
                    + "m.serie as numeroserie,\n"
                    + "m.pdv,\n"
                    + "m.vlcanc,\n"
                    + "m.gtinicial,\n"
                    + "m.gtfinal,\n"
                    + "m.valorz,\n"
                    + "i.cupom, \n"
                    + "i.item as sequencia,\n"
                    + "p.codpro as id_produto,\n"
                    + "i.produto as ean,\n"
                    + "i.preuni as precovenda,\n"
                    + "i.quanti as quantidade,\n"
                    + "i.valor as valortotalitem,\n"
                    + "i.sittri as icms,\n"
                    + "i.vlicms as valor_icms,\n"
                    + "i.vpis, \n"
                    + "i.vcofins\n"
                    + "from movdia m\n"
                    + "join item i on i.idmovdia = m.id\n"
                    + "join prod p on p.barras = i.produto\n"
                    + "where m.filial = '" + getLojaOrigem() + "'\n"
                    + "and m.data >= '" + fmt.format(dataInicioVenda) + "'\n"
                    + "and m.data <= '" + fmt.format(dataTerminoVenda) + "'"
            );

            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "m.data as data,\n"
                    + "m.ecf,\n"
                    + "m.venda as totaldia,\n"
                    + "m.serie as numeroserie,\n"
                    + "m.pdv,\n"
                    + "m.vlcanc,\n"
                    + "m.gtinicial,\n"
                    + "m.gtfinal,\n"
                    + "m.valorz,\n"
                    + "i.cupom, \n"
                    + "i.item as sequencia,\n"
                    + "p.codpro as id_produto,\n"
                    + "i.produto as ean,\n"
                    + "i.preuni as precovenda,\n"
                    + "i.quanti as quantidade,\n"
                    + "i.valor as valortotalitem,\n"
                    + "i.sittri as icms,\n"
                    + "i.vlicms as valor_icms,\n"
                    + "i.vpis, \n"
                    + "i.vcofins\n"
                    + "from movdia m\n"
                    + "join item i on i.idmovdia = m.id\n"
                    + "join prod p on p.barras = i.produto\n"
                    + "where m.filial = '" + getLojaOrigem() + "'\n"
                    + "and m.data >= '" + fmt.format(dataInicioVenda) + "'\n"
                    + "and m.data <= '" + fmt.format(dataTerminoVenda) + "'"
            )) {
                while (rst.next()) {
                    VendaHistoricoIMP imp = new VendaHistoricoIMP();
                    imp.setIdProduto(rst.getString("id_produto"));

                    String ean = eans.get(imp.getIdProduto());
                    if (ean == null) {
                        ean = imp.getIdProduto();
                    }
                    imp.setEan(ean);

                    ProdutoComplementoParcial pcp = aliquotas.get(imp.getIdProduto());

                    imp.setData(fmt.parse(rst.getString("data")));
                    imp.setCustoComImposto(0);
                    imp.setCustoSemImposto(0);
                    imp.setPrecoVenda(rst.getDouble("precovenda"));
                    imp.setQuantidade(rst.getDouble("quantidade"));
                    imp.setValorTotal(rst.getDouble("valortotalitem"));
                    imp.setIcmsCredito(MathUtils.round((imp.getValorTotal() * ((100 - pcp.icmsCredRed) / 100)) * pcp.icmsCredAliq / 100, 2));
                    imp.setIcmsDebito(MathUtils.round((imp.getValorTotal() * ((100 - pcp.icmsDebRed) / 100)) * pcp.icmsDebAliq / 100, 2));
                    imp.setPisCofinsCredito(MathUtils.round((imp.getValorTotal() * ((100 - pcp.pisRed) / 100)) * pcp.pisCred / 100, 2));
                    imp.setPisCofinsDebito(MathUtils.round(imp.getValorTotal() * pcp.pisDeb / 100, 2));

                    result.add(imp);
                }
            }
        }
        return result;
    }

    public Connection getSco() {
        return sco;
    }

    public void setSco(Connection sco) {
        this.sco = sco;
    }

    public Connection getSfi() {
        return sfi;
    }

    public void setSfi(Connection sfi) {
        this.sfi = sfi;
    }

    public Connection getCli() {
        return cli;
    }

    public void setCli(Connection cli) {
        this.cli = cli;
    }

    public Connection getCupom() {
        return this.cupom;
    }

    public void setCupom(Connection cupom) {
        this.cupom = cupom;
    }

    @Override
    public Iterator<VendaIMP> getVendaIterator() throws Exception {
        return new VendaIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, cupom);
    }

    @Override
    public Iterator<VendaItemIMP> getVendaItemIterator() throws Exception {
        return new VendaItemIterator(getLojaOrigem(), dataInicioVenda, dataTerminoVenda, cupom);
    }

    private static class VendaIterator implements Iterator<VendaIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm;
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

                        String id = rst.getString("data")
                                + "-"
                                + rst.getString("ecf")
                                + "-"
                                + rst.getString("numerocupom")
                                + "-"
                                + rst.getString("vc_clientepreferencial");

                        if (!uk.add(id)) {
                            LOG.warning("Venda " + id + " já existe na listagem");
                        }

                        next.setId(id);
                        next.setNumeroCupom(Utils.stringToInt(rst.getString("numerocupom")));
                        next.setEcf(Utils.stringToInt(rst.getString("ecf")));
                        next.setData(rst.getDate("data"));
                        next.setIdClientePreferencial(rst.getString("vc_clientepreferencial"));

                        String horaInicio = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horainicio");
                        String horaTermino = timestampDate.format(rst.getDate("data")) + " " + rst.getString("horatermino");

                        next.setCancelado("C".equals(rst.getString("status")));
                        next.setHoraInicio(timestamp.parse(horaInicio));
                        next.setHoraTermino(timestamp.parse(horaTermino));
                        next.setSubTotalImpressora(rst.getDouble("subtotalimpressora"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setNumeroSerie(rst.getString("numeroserie"));
                        next.setModeloImpressora(rst.getString("modelo"));
                        next.setChaveCfe(rst.getString("ChaveCfe"));
                    }
                }
            } catch (SQLException | ParseException ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        public VendaIterator(String idLojaCliente, Date dataInicio, Date dataTermino, Connection con) throws Exception {
            this.sql
                    = "select\n"
                    + "m.data,\n"
                    + "cb.hora as horainicio,\n"
                    + "cb.hora as horatermino,\n"
                    + "m.ecf,\n"
                    + "m.serie as numeroserie,\n"
                    + "cb.cupom as numerocupom,\n"
                    + "cb.chave as ChaveCfe,\n"
                    + "c.codcli as vc_clientepreferencial,\n"
                    + "cb.valor as subtotalimpressora,\n"
                    + "0 as desconto,\n"
                    + "0 as acrescimo,\n"
                    + "'' as modelo,\n"
                    + "cb.status\n"
                    + "from movdia m\n"
                    + "left join cabec cb on cb.idmovdia = m.id\n"
                    + "left join convenio c on c.idmovdia = m.id and cb.cupom = c.cupom\n"
                    + "where m.filial = " + idLojaCliente + "\n"
                    + "and m.data >= '" + FORMAT.format(dataInicio) + "'\n"
                    + "and m.data <= '" + FORMAT.format(dataTermino) + "'";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            stm = con.createStatement();
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

    private static class VendaItemIterator extends ShiDAO implements Iterator<VendaItemIMP> {

        private final static SimpleDateFormat FORMAT = new SimpleDateFormat("yyyy-MM-dd");

        private Statement stm;
        private ResultSet rst;
        private String sql;
        private VendaItemIMP next;

        private void obterNext() {
            try {
                if (next == null) {
                    if (rst.next()) {
                        next = new VendaItemIMP();
                        String idVenda = rst.getString("data")
                                + "-"
                                + rst.getString("ecf")
                                + "-"
                                + rst.getString("numerocupom")
                                + "-"
                                + rst.getString("vc_clientepreferencial");

                        String id = rst.getString("data")
                                + "-"
                                + rst.getString("ecf")
                                + "-"
                                + rst.getString("numerocupom")
                                + "-"
                                + rst.getString("vc_clientepreferencial")
                                + "-"
                                + rst.getString("produto");

                        next.setId(id);
                        next.setVenda(idVenda);
                        next.setProduto(rst.getString("produto"));
                        next.setQuantidade(rst.getDouble("quantidade"));
                        next.setTotalBruto(rst.getDouble("total"));
                        next.setValorDesconto(rst.getDouble("desconto"));
                        next.setValorAcrescimo(rst.getDouble("acrescimo"));
                        next.setCancelado("C".equals(rst.getString("status")));
                        next.setCodigoBarras(rst.getString("codigobarras"));

                        String trib = Utils.acertarTexto(rst.getString("codaliq_venda"));
                        if (trib == null || "".equals(trib)) {
                            trib = Utils.acertarTexto(rst.getString("codaliq_produto"));
                        }

                        obterAliquota(next, trib);
                    }
                }
            } catch (Exception ex) {
                LOG.log(Level.SEVERE, "Erro no método obterNext()", ex);
                throw new RuntimeException(ex);
            }
        }

        /**
         * Método temporario, desenvolver um mapeamento eficiente da tributação.
         *
         * @param item
         * @throws SQLException
         */
        public void obterAliquota(VendaItemIMP item, String icms) throws SQLException {
            /*
             0700   7.00    ALIQUOTA 07%
             1200   12.00   ALIQUOTA 12%
             1800   18.00   ALIQUOTA 18%
             2500   25.00   ALIQUOTA 25%
             1100   11.00   ALIQUOTA 11%
             I      0.00    ISENTO
             F      0.00    SUBST TRIBUTARIA
             N      0.00    NAO INCIDENTE
             */
            int cst;
            double aliq;
            switch (icms) {
                case "0700":
                    cst = 0;
                    aliq = 7;
                    break;
                case "1200":
                    cst = 0;
                    aliq = 12;
                    break;
                case "1800":
                    cst = 0;
                    aliq = 18;
                    break;
                case "2500":
                    cst = 0;
                    aliq = 25;
                    break;
                case "1100":
                    cst = 0;
                    aliq = 11;
                    break;
                case "F":
                    cst = 60;
                    aliq = 0;
                    break;
                case "N":
                    cst = 41;
                    aliq = 0;
                    break;
                case "I":
                    cst = 40;
                    aliq = 0;
                    break;
                default:
                    cst = 40;
                    aliq = 0;
                    break;
            }
            item.setIcmsCst(cst);
            item.setIcmsAliq(aliq);
        }

        public VendaItemIterator(String idLojaCliente, Date dataInicio, Date dataTermino, Connection con) throws Exception {
            this.sql
                    = "select\n"
                    + "m.data,\n"
                    + "cb.hora as horainicio,\n"
                    + "cb.hora as horatermino,\n"
                    + "m.ecf,\n"
                    + "m.serie as numeroserie,\n"
                    + "cb.cupom as numerocupom,\n"
                    + "cb.chave as ChaveCfe,\n"
                    + "c.codcli as vc_clientepreferencial,\n"
                    + "cb.valor as subtotalimpressora,\n"
                    + "i.produto as codigobarras,\n"
                    + "i.codpro as produto,\n"
                    + "i.quanti as quantidade,\n"
                    + "i.valor as total,\n"
                    + "i.sittri as codaliq_venda,\n"
                    + "i.status,\n"
                    + "0 as desconto,\n"
                    + "0 as acrescimo\n"
                    + "from movdia m\n"
                    + "left join cabec cb on cb.idmovdia = m.id\n"
                    + "left join convenio c on c.idmovdia = m.id and cb.cupom = c.cupom\n"
                    + "left join item i on i.idmovdia = m.id and cb.cupom = i.cupom\n"
                    + "where m.filial = " + idLojaCliente + "\n"
                    + "and m.data >= '" + FORMAT.format(dataInicio) + "'\n"
                    + "and m.data <= '" + FORMAT.format(dataTermino) + "'";

            LOG.log(Level.FINE, "SQL da venda: " + sql);
            stm = con.createStatement();
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
