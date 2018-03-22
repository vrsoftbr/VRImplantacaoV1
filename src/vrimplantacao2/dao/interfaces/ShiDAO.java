package vrimplantacao2.dao.interfaces;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import vrframework.classe.ProgressBar;
import vrimplantacao.classe.ConexaoFirebird;
import vrimplantacao.dao.cadastro.NutricionalToledoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoItemVO;
import vrimplantacao.vo.vrimplantacao.NutricionalToledoVO;
import vrimplantacao2.dao.cadastro.Estabelecimento;
import vrimplantacao2.dao.cadastro.produto.OpcaoProduto;
import vrimplantacao2.gui.component.mapatributacao.MapaTributoProvider;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.mercadologico.MercadologicoNivelIMP;
import vrimplantacao2.vo.cadastro.oferta.SituacaoOferta;
import vrimplantacao2.vo.cadastro.oferta.TipoOfertaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.enums.TipoContato;
import vrimplantacao2.vo.enums.TipoEmpresa;
import vrimplantacao2.vo.enums.TipoSexo;
import vrimplantacao2.vo.importacao.ChequeIMP;
import vrimplantacao2.vo.importacao.ClienteIMP;
import vrimplantacao2.vo.importacao.CreditoRotativoIMP;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;
import vrimplantacao2.vo.importacao.MapaTributoIMP;
import vrimplantacao2.vo.importacao.OfertaIMP;
import vrimplantacao2.vo.importacao.ProdutoFornecedorIMP;
import vrimplantacao2.vo.importacao.ProdutoIMP;

/**
 *
 * @author Leandro
 */
public class ShiDAO extends InterfaceDAO implements MapaTributoProvider {

    public boolean eFicha = false;
    
    @Override
    public String getSistema() {
        return "SHI";
    }

    public List<Estabelecimento> getLojasCliente(boolean sco) throws Exception {
        List<Estabelecimento> result = new ArrayList<>();
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {            
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "    codigo,\n"
                    + "    codigo || ' - ' || " + (sco ? "razsoc" : "nomexx") + " descricao\n"
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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
                    if (rst.getInt("ativo") == 1) {
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
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    pc.codpro,\n" +
                        "    pc.custo\n" +
                        "from\n" +
                        "    precocusto pc\n" +
                        "    join(select\n" +
                        "             codpro,\n" +
                        "             filial,\n" +
                        "             max(data) data\n" +
                        "         from\n" +
                        "             precocusto\n" +
                        "         group by\n" +
                        "             codpro, filial) a using (codpro, filial, data)\n" +
                        "where\n" +
                        "    pc.filial = " + getLojaOrigem()
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
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    e.codpro,\n" +
                        "    e.saldoatu,\n" +
                        "    e.saldoant,\n" +
                        "    e.movimento\n" +
                        "from\n" +
                        "    estoque e\n" +
                        "    join(select\n" +
                        "             codpro,\n" +
                        "             filial,\n" +
                        "             max(data) data\n" +
                        "         from\n" +
                        "             estoque\n" +
                        "         group by\n" +
                        "             codpro, filial) a using (codpro, filial, data)\n" +
                        "where\n" +
                        "    e.filial = " + getLojaOrigem()
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
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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
            try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
                try (ResultSet rst = stm.executeQuery(
                        "select\n" +
                        "    pc.codpro,\n" +
                        "    pc.custo\n" +
                        "from\n" +
                        "    precocusto pc\n" +
                        "    join(select\n" +
                        "             codpro,\n" +
                        "             filial,\n" +
                        "             max(data) data\n" +
                        "         from\n" +
                        "             precocusto\n" +
                        "         group by\n" +
                        "             codpro, filial) a using (codpro, filial, data)\n" +
                        "where\n" +
                        "    pc.filial = " + getLojaOrigem()
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    codigo,\n" +
                    "    descri\n" +
                    "from\n" +
                    "    icms\n" +
                    "order by\n" +
                    "    codigo"
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    f.codigo id,\n" +
                    "    f.nomexx razao,\n" +
                    "    f.fantas fantasia,\n" +
                    "    f.ciccgc cnpj,\n" +
                    "    f.inscrg ie_rg,\n" +
                    "    case f.inativ when 'S' then 0 else 1 end ativo,\n" +
                    "    f.endere endereco,\n" +
                    "    f.bairro,\n" +
                    "    f.cidade,\n" +
                    "    f.estado,\n" +
                    "    f.cepxxx cep,\n" +
                    "    f.pedmin valor_minimo_pedido,\n" +
                    "    f.dtcada datacadastro,\n" +
                    "    f.observ observacao,\n" +
                    "    f.entreg prazoEntrega,\n" +
                    "    f.frecom prazoVisita,\n" +
                    "    case when coalesce(trim(upper(f.simplesnac)),'N') = 'S' then 1 else 0 end simplesnac\n" +
                    "from\n" +
                    "    fornecedor f  \n" +
                    "order by\n" +
                    "    f.codigo"
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
                    
                    try (Statement stm2 = ConexaoFirebird.getConexao().createStatement()) {
                        try (ResultSet rst2 = stm2.executeQuery(
                                "select\n" +
                                "    codigo,\n" +
                                "    trim(coalesce(contato,'')) contato,\n" +
                                "    trim(coalesce(telefone,'')) telefone,\n" +
                                "    trim(coalesce(fax,'')) fax,\n" +
                                "    trim(coalesce(celular,'')) celular,\n" +
                                "    trim(coalesce(email,'')) email\n" +
                                "from\n" +
                                "    contato\n" +
                                "where\n" +
                                "    fornecedor = " + imp.getImportId() + "\n" +
                                "order by\n" +
                                "    codigo"
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
                    
                    try (Statement stm3 = ConexaoFirebird.getConexao().createStatement()) {
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    /*"select\n" +
                    "    fornec,\n" +
                    "    codpro,\n" +
                    "    codfor,\n" +
                    "    data,\n" +
                    "    embala\n" +
                    "from\n" +
                    "    codfornec\n" +
                    "order by\n" +
                    "    fornec,\n" +
                    "    codpro, \n" +
                    "    codfor"*/
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    c.codigo id,\n" +
                    "    c.fichax,\n" +
                    "    c.ciccgc cnpj,\n" +
                    "    case when char_length(trim(c.ciccgc)) > 11 then c.inscri_jur else c.rgnume_fis end inscricaoestadual,\n" +
                    "    c.rgorga_fis orgaoemissor,\n" +
                    "    c.razsoc razao,\n" +
                    "    c.nomexx fantasia,\n" +
                    "    case when c.situac <> 1 then 0 else 1 end ativo,\n" +
                    "    c.endere_res endereco,\n" +
                    "    c.numero_res numero,\n" +
                    "    c.comple_res complemento,\n" +
                    "    c.bairro_res bairro,\n" +
                    "    c.cidade_res municipio,\n" +
                    "    c.estado_res estado,\n" +
                    "    c.cepxxx_res cep,     \n" +
                    "    trim(coalesce(c.telddd_res,'')||coalesce(c.telfon_res, '')) telefone,\n" +
                    "    c.nascim_fis datanascimento,\n" +
                    "    c.datcad datacadastro,\n" +
                    "    case c.sexoxx_fis when 2 then 0 else 1 end sexo,\n" +
                    "    emp.descri tipo_emprego,\n" +
                    "    c.nomexx_com empresa,\n" +
                    "    c.endere_com empresa_endereco,\n" +
                    "    c.numero_com empresa_numero,\n" +
                    "    c.comple_com empresa_complemento,\n" +
                    "    c.bairro_com empresa_bairro,\n" +
                    "    c.cidade_com empresa_cidade,\n" +
                    "    c.estado_com empresa_estado,\n" +
                    "    c.cepxxx_com empresa_cep, \n" +
                    "    trim(coalesce(c.telddd_com,'')||coalesce(c.telfon_com, '')) telefone_empresa,\n" +
                    "    c.rendax_fis salario,\n" +
                    "    c.limcre,\n" +
                    "    c.nomcon_fis conjuge,\n" +
                    "    c.nompai_fis pai,\n" +
                    "    c.nommae_fis mae,\n" +
                    "    c.mensagemsemst observacao,\n" +
                    "    c.diaven diaVencimento,\n" +
                    "    c.emailx email,\n" +
                    "    l.valorx valorCredito,\n " +
                    "    l2.valorx valorCheque,\n"+
                    "    trim(coalesce(c.telddd_cel,'')||coalesce(c.telfon_cel, '')) celular\n" +
                    "from\n" +
                    "    clientes c\n" +
                    "    left join emprego emp on c.empreg_fis = emp.codigo\n" +
                    "    left join limite l on c.limcon = l.codigo\n"+
                    "    left join limite l2 on c.limche = l2.codigo\n"
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    d.FILIAL||'-'||d.TIPDOC||'-'||d.SEQUEN||'-'||d.DESDOB id,\n" +
                    "    cl.fichax,\n" +        
                    "    d.datcad,\n" +
                    "    c.cupomx,\n" +
                    "    c.caixax,\n" +
                    "    d.valorx,\n" +
                    "    d.observ,\n" +
                    "    d.client,\n" +
                    "    d.vencim,\n" +
                    "    cl.ciccgc\n" +
                    "from\n" +
                    "    documentos d\n" +
                    "    left join cupom c on d.idcupom = c.idcupom\n" +
                    "    join clientes cl on cl.codigo = d.client\n" +
                    "where\n" +
                    "    status = 1\n"
                    + " and d.filial = " + getLojaOrigem()
                    + " order by\n"
                    + "    d.datcad"
            )) {
                while (rst.next()) {
                    CreditoRotativoIMP imp = new CreditoRotativoIMP();
                    
                    imp.setId(rst.getString("id"));
                    imp.setDataEmissao(rst.getDate("datcad"));
                    imp.setNumeroCupom(rst.getString("cupomx"));
                    imp.setEcf(rst.getString("caixax"));
                    imp.setValor(rst.getDouble("valorx"));
                    imp.setObservacao(rst.getString("observ"));
                    if (eFicha) {
                        if ((rst.getString("fichax") != null) &&
                                (!rst.getString("fichax").trim().isEmpty())) {
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    c.filial||'-'||c.sequen id,\n" +
                    "    c.ciccgc cpf,\n" +
                    "    c.cheque,\n" +
                    "    c.bancox,\n" +
                    "    c.agenci,\n" +
                    "    c.contax,\n" +
                    "    c.dataxx,\n" +
                    "    c.vencim,\n"+
                    "    c.valorx,\n" +
                    "    case when char_length(trim(cl.ciccgc)) > 11 then cl.inscri_jur else cl.rgnume_fis end rg,\n" +
                    "    trim(coalesce(cl.telddd_res,'')||coalesce(cl.telfon_res, '')) telefone,\n" +
                    "    cl.razsoc,\n" +
                    "    c.observ,\n" +
                    "    case c.status when 1 then 0 else coalesce(c.motdv2, c.motdv1) end alinea,\n" +
                    "    c.datalt\n" +
                    "from\n" +
                    "    cheques c\n" +
                    "    join clientes cl on c.client = cl.codigo\n" +
                    "where\n" +
                    "    c.quitad is null\n"
                    + " and c.filial = " + getLojaOrigem()
                    + " order by\n"
                    +                    "    id"
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
    
    private List<NutricionalToledoVO> carregarNutricionalToledo() throws Exception {
        List<NutricionalToledoVO> vNutricionalToledo = new ArrayList<>();

        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
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
        
        try (Statement stm = ConexaoFirebird.getConexao().createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "    pv.filial,\n" +
                    "    pv.codpro idproduto,\n" +
                    "    pv.inipro datainicio,\n" +
                    "    pv.terpro datatermino,\n" +
                    "    pv.promoc precooferta\n" +
                    "from\n" +
                    "    precovenda pv\n" +
                    "where\n" +
                    "   pv.filial = " + getLojaOrigem() + " and\n" +
                    "   pv.promoc > 0 and\n" +
                    "    pv.terpro >= " + SQLUtils.stringSQL(
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
    
    
}
