package vrimplantacao2.dao.cadastro.fornecedor;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.sistema.EstadoDAO;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.local.MunicipioDAO;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.collection.IDStack;
import vrimplantacao2.utils.multimap.KeyList;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoAnteriorVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorContatoVO;
import vrimplantacao2.vo.cadastro.fornecedor.FornecedorVO;
import vrimplantacao2.vo.enums.ContaContabilFinanceiro;
import vrimplantacao2.vo.enums.TipoIndicadorIE;
import vrimplantacao2.vo.enums.TipoInscricao;
import vrimplantacao2.vo.importacao.FornecedorContatoIMP;
import vrimplantacao2.vo.importacao.FornecedorIMP;

public class FornecedorDAO {

    private MultiMap<Long, FornecedorVO> cnpjExistentes;
    private int lojaVR;

    public FornecedorDAO() throws Exception {
        this.anteriorDAO = new FornecedorAnteriorDAO();
    }

    public MultiMap<Long, FornecedorVO> getCnpjExistente() throws Exception {
        if (cnpjExistentes == null) {
            atualizarCnpjExistentes();
        }
        return cnpjExistentes;
    }

    public void atualizarCnpjExistentes() throws Exception {
        cnpjExistentes = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	cnpj,\n"
                    + "	razaosocial,\n"
                    + "	nomefantasia\n"
                    + "from\n"
                    + "	fornecedor\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();
                    vo.setId(rst.getInt("id"));
                    vo.setCnpj(rst.getString("cnpj"));
                    vo.setRazaoSocial(rst.getString("razaosocial"));
                    vo.setNomeFantasia(rst.getString("nomefantasia"));
                    cnpjExistentes.put(vo, vo.getCnpj());
                }
            }
        }
    }

    /**
     * Converte e grava uma listagem de {@link FornecedorIMP} no banco de dados.
     *
     * @param fornecedores Listagem de fornecedores a serem gravadas.
     * @param opcoes Opções da importação.
     * @throws Exception
     */
    public void salvar(List<FornecedorIMP> fornecedores, OpcaoFornecedor... opcoes) throws Exception {
        Set<OpcaoFornecedor> opt = new LinkedHashSet<>(Arrays.asList(opcoes));
        MultiMap<String, FornecedorIMP> filtrados = filtrar(fornecedores);
        FornecedorContatoDAO contatoDAO = new FornecedorContatoDAO();
        FornecedorContatoAnteriorDAO anteriorContatoDAO = new FornecedorContatoAnteriorDAO();
        FornecedorPrazoPedidoDAO prazoDAO = new FornecedorPrazoPedidoDAO();
        FornecedorPagamentoDAO pagamentoDAO = new FornecedorPagamentoDAO();
        FornecedorPrazoDAO fornecedorPrazoDAO = new FornecedorPrazoDAO();

        MultiMap<String, FornecedorContatoAnteriorVO> anteriorContatos = anteriorContatoDAO.getAnteriores();

        organizar(filtrados);
        try {
            Conexao.begin();

            atualizaIdsVagos();
            atualizaExistentes();

            Parametros param = Parametros.get();
            ProgressBar.setStatus("Fornecedores - Gravando...");
            ProgressBar.setMaximum(filtrados.size());
            try (Statement stm = Conexao.createStatement()) {
                for (KeyList<String> keys : filtrados.keySet()) {
                    String[] chave = new String[]{
                        keys.get(0),
                        keys.get(1),
                        keys.get(2)
                    };
                    FornecedorIMP imp = filtrados.get(chave);
                    FornecedorVO vo = converter(imp);

                    boolean gravar = true;

                    if (!anteriorDAO.getAnteriores().containsKey(chave)) {
                        if (opt.contains(OpcaoFornecedor.DADOS)) {

                            if (gravar) {

                                //<editor-fold defaultstate="collapsed" desc="GERAR IDs">
                                boolean gerarId = false;
                                try {
                                    vo.setId(Integer.parseInt(imp.getImportId()));
                                    if (vo.getId() < 2 || vo.getId() > 999999) {
                                        gerarId = true;
                                    } else if (getExistentes().containsKey(vo.getId())) {
                                        gerarId = true;
                                    }
                                } catch (NumberFormatException e) {
                                    gerarId = true;
                                }
                                if (gerarId) {
                                    vo.setId((int) getIdsVagos().pop());
                                } else {
                                    getIdsVagos().remove(vo.getId());
                                }
                                //</editor-fold>



                                FornecedorVO cnpjExiste = getCnpjExistente().get(vo.getCnpj());
                                if (cnpjExiste == null) {

                                    SQLBuilder sql = new SQLBuilder();
                                    sql.setTableName("fornecedor");
                                    sql.put("id", vo.getId());// integer NOT NULL,
                                    sql.put("razaosocial", vo.getRazaoSocial());// character varying(40) NOT NULL,
                                    sql.put("nomefantasia", vo.getNomeFantasia());// character varying(30) NOT NULL,
                                    sql.put("endereco", vo.getEndereco());// character varying(40) NOT NULL,
                                    sql.put("bairro", vo.getBairro());// character varying(30) NOT NULL,
                                    sql.put("id_municipio", vo.getMunicipio() == null ? param.getMunicipioPadrao().getId() : vo.getMunicipio().getId());// integer NOT NULL,
                                    sql.put("cep", vo.getCep());// numeric(8,0) NOT NULL,
                                    sql.put("id_estado", vo.getEstado() == null ? param.getUfPadrao().getId() : vo.getEstado().getId());// integer NOT NULL,
                                    sql.put("telefone", vo.getTelefone());// character varying(14) NOT NULL,
                                    sql.put("id_tipoinscricao", vo.getTipoInscricao().getId());// integer NOT NULL,
                                    sql.put("inscricaoestadual", vo.getInscricaoEstadual());// character varying(20) NOT NULL,
                                    sql.put("cnpj", vo.getCnpj());// numeric(14,0) NOT NULL,
                                    sql.put("revenda", vo.getRevenda());// boolean NOT NULL,
                                    sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());// integer NOT NULL,
                                    sql.put("id_tipopagamento", vo.getTipoPagamento().getId());// integer NOT NULL,
                                    sql.put("numerodoc", 0);// integer NOT NULL,
                                    sql.put("pedidominimoqtd", vo.getPedidoMinimoQtd());// integer NOT NULL,
                                    sql.put("pedidominimovalor", vo.getPedidoMinimoValor());// numeric(11,2) NOT NULL,
                                    sql.put("serienf", "1");// varying(4) NOT NULL,
                                    sql.put("descontofunrural", false);// boolean NOT NULL,
                                    sql.put("senha", 0);// integer NOT NULL,
                                    sql.putNull("id_tiporecebimento");// integer,
                                    sql.put("agencia", "");// character varying(6) NOT NULL,
                                    sql.put("digitoagencia", "");// character varying(2) NOT NULL,
                                    sql.put("conta", "");// character varying(12) NOT NULL,
                                    sql.put("digitoconta", "");// character varying(2) NOT NULL,
                                    sql.put("id_banco", 804);// integer,
                                    sql.putNull("id_fornecedorfavorecido");// integer,
                                    sql.put("enderecocobranca", vo.getEnderecoCobranca());// character varying(40) NOT NULL,
                                    sql.put("bairrocobranca", vo.getBairroCobranca());// character varying(30) NOT NULL,
                                    sql.put("cepcobranca", vo.getCepCobranca());// numeric(18,0) NOT NULL,
                                    sql.put("id_municipiocobranca", vo.getMunicipioCobranca() == null ? param.getMunicipioPadrao().getId() : vo.getMunicipioCobranca().getId());// integer,
                                    sql.put("id_estadocobranca", vo.getEstadoCobranca() == null ? param.getUfPadrao().getId() : vo.getEstadoCobranca().getId());// integer,
                                    sql.put("bloqueado", vo.isBloqueado());// boolean NOT NULL,
                                    sql.putNull("id_tipomotivofornecedor");// integer,
                                    sql.putNull("datasintegra");// timestamp without time zone,
                                    sql.put("id_tipoempresa", 3);// integer NOT NULL,
                                    sql.put("inscricaosuframa", vo.getInscricaoSuframa());// character varying(9) NOT NULL,
                                    sql.put("utilizaiva", vo.isUtilizaiva());// boolean NOT NULL,
                                    sql.putNull("id_familiafornecedor");// integer,
                                    sql.putNull("id_tipoinspecao");// integer,
                                    sql.put("numeroinspecao", 0);// integer NOT NULL,
                                    sql.putNull("id_tipotroca");// integer,
                                    sql.put("id_tipofornecedor", vo.getTipoFornecedor().getId());// integer NOT NULL,
                                    sql.put("id_contacontabilfinanceiro", ContaContabilFinanceiro.PAGAMENTO_FORNECEDOR.getID());// integer,
                                    sql.put("utilizanfe", false);// boolean NOT NULL,
                                    sql.put("datacadastro", vo.getDataCadastro());// date NOT NULL,
                                    sql.put("utilizaconferencia", false);// boolean NOT NULL,
                                    sql.put("numero", vo.getNumero());// character varying(6) NOT NULL DEFAULT ''::character varying,
                                    sql.put("permitenfsempedido", false);// boolean NOT NULL DEFAULT false,
                                    sql.put("modelonf", "55");// character varying(2) NOT NULL DEFAULT ''::character varying,
                                    sql.put("emitenf", false);// boolean NOT NULL DEFAULT true,
                                    sql.put("tiponegociacao", 0);// integer NOT NULL DEFAULT 0,
                                    sql.put("utilizacrossdocking", false);// boolean NOT NULL DEFAULT false,
                                    sql.putNull("id_lojacrossdocking");// integer,
                                    sql.put("observacao", "IMPORTADO VR " + vo.getObservacao());// character varying(2500) NOT NULL DEFAULT '::character varying'::character varying,
                                    sql.put("id_pais", vo.getIdPais() == null ? 1058 : vo.getIdPais());// integer NOT NULL,
                                    sql.put("inscricaomunicipal", vo.getInscricaoMunicipal());// character varying(20) DEFAULT ''::character varying,
                                    sql.putNull("id_contacontabilfiscalpassivo");// bigint,
                                    sql.put("numerocobranca", vo.getNumeroCobranca());// character varying(6) NOT NULL DEFAULT '0'::character varying,
                                    sql.put("complemento", vo.getComplemento());// character varying(30) NOT NULL DEFAULT ''::character varying,
                                    sql.put("complementocobranca", vo.getComplementoCobranca());// character varying(30) NOT NULL DEFAULT ''::character varying,
                                    sql.putNull("id_contacontabilfiscalativo");// bigint,
                                    sql.put("utilizaedi", false);// boolean NOT NULL DEFAULT false,
                                    sql.put("tiporegravencimento", -1);// integer NOT NULL DEFAULT '-1'::integer,
                                    sql.put("nfemitidapostofiscal", false);// boolean DEFAULT false,
                                    incluirTipoIndicadorIE(vo, sql);

                                    stm.execute(sql.getInsert());
                                    getExistentes().put(vo, vo.getId());
                                    getCnpjExistente().put(vo, vo.getCnpj());
                                } else {
                                    vo.setId(cnpjExiste.getId());
                                }
                            }

                            FornecedorAnteriorVO anterior = vo.getAnteriores().make(chave);
                            anterior.setImportSistema(imp.getImportSistema());
                            anterior.setImportLoja(imp.getImportLoja());
                            anterior.setImportId(imp.getImportId());
                            if (!gravar) {
                                anterior.setCodigoAtual(null);
                            }
                            anterior.setRazao(Utils.acertarTexto(imp.getRazao()));
                            anterior.setFantasia(Utils.acertarTexto(imp.getFantasia()));
                            anterior.setCnpjCpf(Utils.acertarTexto(imp.getCnpj_cpf()));

                            anteriorDAO.salvar(anterior);
                        }
                    } else {
                        vo = anteriorDAO.getAnteriores().get(chave).getCodigoAtual();
                    }
                    if (opt.contains(OpcaoFornecedor.CONTATOS) && gravar) {
                        for (FornecedorContatoIMP impCont : imp.getContatos().values()) {

                            String[] chaveContato = new String[]{
                                impCont.getImportSistema(),
                                impCont.getImportLoja(),
                                impCont.getImportFornecedorId(),
                                impCont.getImportId()
                            };

                            if (!anteriorContatos.containsKey(chaveContato)) {
                                FornecedorContatoVO contato = vo.getContatos().make(
                                        chaveContato
                                );
                                contato.setNome(impCont.getNome());
                                contato.setTelefone(impCont.getTelefone());
                                contato.setCelular(impCont.getCelular());
                                contato.setEmail(impCont.getEmail());
                                contato.setTipoContato(impCont.getTipoContato());

                                contatoDAO.salvar(contato);

                                FornecedorContatoAnteriorVO antContato = new FornecedorContatoAnteriorVO();
                                antContato.setImportSistema(impCont.getImportSistema());
                                antContato.setImportLoja(impCont.getImportLoja());
                                antContato.setImportFornecedorId(impCont.getImportFornecedorId());
                                antContato.setImportId(impCont.getImportId());
                                antContato.setCodigoAtual(contato);
                                anteriorContatoDAO.salvar(antContato);

                                //Inclui na lista dos anteriores                                
                                anteriorContatos.put(
                                        antContato,
                                        chaveContato
                                );
                            }
                        }
                    }

                    for (Integer condicao : imp.getCondicoesPagamentos()) {
                        pagamentoDAO.salvar(vo.getId(), condicao);
                    }

                    if (imp.getPrazoEntrega() > 0 || imp.getPrazoSeguranca() > 0 || imp.getPrazoVisita() > 0) {
                        fornecedorPrazoDAO.salvar(lojaVR, vo.getId(), 0, imp.getPrazoEntrega(), imp.getPrazoVisita(), imp.getPrazoSeguranca());
                    }

                    /*if (imp.getPrazoEntrega() > 0) {
                     prazoDAO.salvarTodasLojas(new PrazoPedidoVO(vo.getId(), this.lojaVR, imp.getPrazoPedidoPadrao(), imp.getDiasPedidoParcialPadrao()));
                     }  */
                    ProgressBar.next();
                }
            }

            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }
    }

    private FornecedorVO converter(FornecedorIMP imp) throws Exception {
        FornecedorVO vo = new FornecedorVO();

        vo.setRazaoSocial(imp.getRazao());
        vo.setNomeFantasia(imp.getFantasia());
        vo.setCnpj(imp.getCnpj_cpf());
        vo.setInscricaoEstadual(imp.getIe_rg());
        vo.setInscricaoMunicipal(imp.getInsc_municipal());
        vo.setInscricaoSuframa(imp.getSuframa());
        vo.setBloqueado(!imp.isAtivo());
        vo.setTelefone(imp.getTel_principal());
        vo.setPedidoMinimoQtd(imp.getQtd_minima_pedido());
        vo.setPedidoMinimoValor(imp.getValor_minimo_pedido());
        vo.setDataCadastro(imp.getDatacadastro() != null ? imp.getDatacadastro() : new Date());
        vo.setObservacao(imp.getObservacao());
      //  vo.setTipoInscricao(TipoInscricao.analisarCnpjCpf(vo.getCnpj().+));

        //<editor-fold defaultstate="collapsed" desc="ENDEREÇO">
        vo.setEndereco(imp.getEndereco());
        vo.setNumero(imp.getNumero());
        vo.setComplemento(imp.getComplemento());
        vo.setBairro(imp.getBairro());
        vo.setMunicipio(municipioDAO.getMunicipio(imp.getIbge_municipio()));
        if (vo.getMunicipio() == null) {
            vo.setMunicipio(municipioDAO.getMunicipio(imp.getMunicipio(), imp.getUf()));
            if (vo.getMunicipio() == null) {
                vo.setMunicipio(Parametros.get().getMunicipioPadrao2());
            }
        }
        vo.setEstado(vo.getMunicipio().getEstado());
        vo.setCep(Utils.stringToInt(imp.getCep()));
        //</editor-fold>

        //<editor-fold defaultstate="collapsed" desc="ENDEREÇO COBRANÇA">
        vo.setEnderecoCobranca(imp.getCob_endereco());
        vo.setNumeroCobranca(imp.getCob_numero());
        vo.setComplementoCobranca(imp.getCob_complemento());
        vo.setBairroCobranca(imp.getCob_bairro());
        vo.setMunicipioCobranca(municipioDAO.getMunicipio(imp.getCob_ibge_municipio()));
        if (vo.getMunicipioCobranca() == null) {
            vo.setMunicipioCobranca(municipioDAO.getMunicipio(imp.getCob_municipio(), imp.getCob_uf()));
            if (vo.getMunicipioCobranca() == null) {
                vo.setMunicipioCobranca(Parametros.get().getMunicipioPadrao2());
            }
        }
        vo.setEstadoCobranca(vo.getMunicipioCobranca().getEstado());
        vo.setCepCobranca(Utils.stringToInt(imp.getCob_cep()));
        //</editor-fold>

        return vo;
    }

    private MultiMap<String, FornecedorIMP> filtrar(List<FornecedorIMP> fornecedores) throws Exception {
        MultiMap<String, FornecedorIMP> result = new MultiMap<>();

        for (FornecedorIMP imp : fornecedores) {
            result.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }

        return result;
    }

    public MultiMap<Integer, FornecedorVO> getExistentes() throws Exception {
        if (existentes == null) {
            atualizaExistentes();
        }
        return existentes;
    }

    public void atualizaExistentes() throws Exception {
        existentes = new MultiMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	razaosocial,\n"
                    + "	nomefantasia,\n"
                    + "	cnpj\n"
                    + "from \n"
                    + "	fornecedor \n"
                    + "order by \n"
                    + "	id"
            )) {
                while (rst.next()) {
                    FornecedorVO vo = new FornecedorVO();
                    vo.setId(rst.getInt("id"));
                    vo.setRazaoSocial(rst.getString("razaosocial"));
                    vo.setNomeFantasia(rst.getString("nomefantasia"));
                    vo.setCnpj(rst.getString("cnpj"));
                    existentes.put(vo, vo.getId());
                }
            }
        }
    }

    private final FornecedorAnteriorDAO anteriorDAO;
    private MultiMap<Integer, FornecedorVO> existentes;

    private void organizar(MultiMap<String, FornecedorIMP> filtrados) {
        MultiMap<String, FornecedorIMP> idsValidos = new MultiMap<>(3);
        MultiMap<String, FornecedorIMP> idsInvalidos = new MultiMap<>(3);

        for (FornecedorIMP imp : filtrados.values()) {
            String[] chave = new String[]{
                imp.getImportSistema(),
                imp.getImportLoja(),
                imp.getImportId()
            };
            try {
                if ("02782071000542".equals(imp.getCnpj_cpf())) {
                    System.out.println("Chegou");
                }
                int id = Integer.parseInt(imp.getImportId());
                if (id > 1 && id <= 999999) {
                    idsValidos.put(imp, chave);
                } else {
                    idsInvalidos.put(imp, chave);
                }
            } catch (NumberFormatException ex) {
                idsInvalidos.put(imp, chave);
            }
        }

        filtrados.clear();
        for (FornecedorIMP imp : idsValidos.getSortedMap().values()) {
            if ("02782071000542".equals(imp.getCnpj_cpf())) {
                System.out.println("Chegou2");
            }
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
        for (FornecedorIMP imp : idsInvalidos.getSortedMap().values()) {
            if ("02782071000542".equals(imp.getCnpj_cpf())) {
                System.out.println("Chegou3");
            }
            filtrados.put(
                    imp,
                    imp.getImportSistema(),
                    imp.getImportLoja(),
                    imp.getImportId()
            );
        }
    }

    private final MunicipioDAO municipioDAO = new MunicipioDAO();
    private final EstadoDAO estadoDAO = new EstadoDAO();

    private IDStack idsVagos;

    private IDStack getIdsVagos() throws Exception {
        if (idsVagos == null) {
            atualizaIdsVagos();
        }
        return idsVagos;
    }

    private void atualizaIdsVagos() throws Exception {
        idsVagos = new IDStack();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id from \n"
                    + "(SELECT id FROM generate_series(2, 999999)\n"
                    + "AS s(id) EXCEPT SELECT id FROM fornecedor WHERE id <= 999999) AS codigointerno ORDER BY id desc"
            )) {
                while (rst.next()) {
                    idsVagos.add(rst.getLong("id"));
                }
            }
        }
    }

    public void setLojaVR(int lojaVR) {
        this.lojaVR = lojaVR;
    }

    public void apagarContatos() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from fornecedorcontato;");
            stm.execute("drop table if exists implantacao.codant_fornecedorcontato;");
            stm.execute("alter sequence fornecedorcontato_id_seq restart with 1;");
        }
    }

    public void apagarTudo() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute("delete from fornecedorcontato;");
            stm.execute("delete from fornecedorprazo;");
            stm.execute("delete from fornecedorpagamento;");
            stm.execute("delete from fornecedorprazopedido;");
            stm.execute("delete from fornecedor where not id in (select id_fornecedor from loja) and id > 1;");
            stm.execute("drop table if exists implantacao.codant_fornecedor;");
            stm.execute("drop table if exists implantacao.codant_fornecedorcontato;");
            stm.execute("alter sequence fornecedorcontato_id_seq restart with 1;");
            stm.execute("alter sequence fornecedor_id_seq restart with 1;");
            stm.execute("alter sequence fornecedorprazo_id_seq restart with 1;");
            stm.execute("alter sequence fornecedorpagamento_id_seq restart with 1;");
            stm.execute("alter sequence fornecedorprazopedido_id_seq restart with 1;");
        }
    }

    public Map<String, FornecedorVO> getCnpjExistentes() throws Exception {
        Map<String, FornecedorVO> result = new LinkedHashMap<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n"
                    + "	id,\n"
                    + "	cnpj,\n"
                    + "	razaosocial,\n"
                    + "	nomefantasia\n"
                    + "from\n"
                    + "	fornecedor\n"
                    + "order by\n"
                    + "	id"
            )) {
                while (rst.next()) {
                    FornecedorVO forn = new FornecedorVO();

                    forn.setId(rst.getInt("id"));
                    forn.setCnpj(rst.getString("cnpj"));
                    forn.setRazaoSocial(rst.getString("razaosocial"));
                    forn.setNomeFantasia(rst.getString("nomefantasia"));

                    result.put(forn.getCnpj(), forn);
                }
            }
        }
        return result;
    }

    public void gravarFornecedor(FornecedorVO vo) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            SQLBuilder sql = new SQLBuilder();

            sql.setTableName("fornecedor");
            sql.put("id", vo.getId());// integer NOT NULL,
            sql.put("razaosocial", vo.getRazaoSocial());// character varying(40) NOT NULL,
            sql.put("nomefantasia", vo.getNomeFantasia());// character varying(30) NOT NULL,
            sql.put("endereco", vo.getEndereco());// character varying(40) NOT NULL,
            sql.put("bairro", vo.getBairro());// character varying(30) NOT NULL,
            sql.put("id_municipio", vo.getMunicipio() == null ? Parametros.get().getMunicipioPadrao().getId() : vo.getMunicipio().getId());// integer NOT NULL,
            sql.put("cep", vo.getCep());// numeric(8,0) NOT NULL,
            sql.put("id_estado", vo.getEstado() == null ? Parametros.get().getUfPadrao().getId() : vo.getEstado().getId());// integer NOT NULL,
            sql.put("telefone", vo.getTelefone());// character varying(14) NOT NULL,
            sql.put("id_tipoinscricao", vo.getTipoInscricao().getId());// integer NOT NULL,
            sql.put("inscricaoestadual", vo.getInscricaoEstadual() == null ? "" : vo.getInscricaoEstadual());// character varying(20) NOT NULL,
            sql.put("cnpj", vo.getCnpj());// numeric(14,0) NOT NULL,
            sql.put("revenda", vo.getRevenda());// boolean NOT NULL,
            sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());// integer NOT NULL,
            sql.put("id_tipopagamento", vo.getTipoPagamento() == null ? Parametros.get().getTipoPagamento().getId() : vo.getTipoPagamento().getId());// integer NOT NULL,
            sql.put("numerodoc", 0);// integer NOT NULL,
            sql.put("pedidominimoqtd", vo.getPedidoMinimoQtd());// integer NOT NULL,
            sql.put("pedidominimovalor", vo.getPedidoMinimoValor());// numeric(11,2) NOT NULL,
            sql.put("serienf", "1");// varying(4) NOT NULL,
            sql.put("descontofunrural", false);// boolean NOT NULL,
            sql.put("senha", 0);// integer NOT NULL,
            sql.putNull("id_tiporecebimento");// integer,
            sql.put("agencia", "");// character varying(6) NOT NULL,
            sql.put("digitoagencia", "");// character varying(2) NOT NULL,
            sql.put("conta", "");// character varying(12) NOT NULL,
            sql.put("digitoconta", "");// character varying(2) NOT NULL,
            sql.put("id_banco", vo.getIdBanco() != 0 ? vo.getIdBanco() : 804);// integer,
            sql.putNull("id_fornecedorfavorecido");// integer,
            sql.put("enderecocobranca", vo.getEnderecoCobranca());// character varying(40) NOT NULL,
            sql.put("bairrocobranca", vo.getBairroCobranca());// character varying(30) NOT NULL,
            sql.put("cepcobranca", vo.getCepCobranca());// numeric(18,0) NOT NULL,
            sql.put("id_municipiocobranca", vo.getMunicipioCobranca() == null ? Parametros.get().getMunicipioPadrao().getId() : vo.getMunicipioCobranca().getId());// integer,
            sql.put("id_estadocobranca", vo.getEstadoCobranca() == null ? Parametros.get().getUfPadrao().getId() : vo.getEstadoCobranca().getId());// integer,
            sql.put("bloqueado", vo.isBloqueado());// boolean NOT NULL,
            sql.putNull("id_tipomotivofornecedor");// integer,
            sql.putNull("datasintegra");// timestamp without time zone,
            sql.put("id_tipoempresa", 3);// integer NOT NULL,
            sql.put("inscricaosuframa", vo.getInscricaoSuframa());// character varying(9) NOT NULL,
            sql.put("utilizaiva", vo.isUtilizaiva());// boolean NOT NULL,
            sql.putNull("id_familiafornecedor");// integer,
            sql.putNull("id_tipoinspecao");// integer,
            sql.put("numeroinspecao", 0);// integer NOT NULL,
            sql.putNull("id_tipotroca");// integer,
            sql.put("id_tipofornecedor", vo.getTipoFornecedor().getId());// integer NOT NULL,
            sql.put("id_contacontabilfinanceiro", ContaContabilFinanceiro.PAGAMENTO_FORNECEDOR.getID());// integer,
            sql.put("utilizanfe", vo.isUtilizaNfe());// boolean NOT NULL,
            sql.put("datacadastro", vo.getDataCadastro());// date NOT NULL,
            sql.put("utilizaconferencia", false);// boolean NOT NULL,
            sql.put("numero", vo.getNumero());// character varying(6) NOT NULL DEFAULT ''::character varying,
            sql.put("permitenfsempedido", vo.isPermiteNfSemPedido());// boolean NOT NULL DEFAULT false,
            sql.put("modelonf", "55");// character varying(2) NOT NULL DEFAULT ''::character varying,
            sql.put("emitenf", false);// boolean NOT NULL DEFAULT true,
            sql.put("tiponegociacao", 0);// integer NOT NULL DEFAULT 0,
            sql.put("utilizacrossdocking", false);// boolean NOT NULL DEFAULT false,
            sql.putNull("id_lojacrossdocking");// integer,
            sql.put("observacao", "IMPORTADO VR " + vo.getObservacao() == null ? "" : vo.getObservacao());// character varying(2500) NOT NULL DEFAULT '::character varying'::character varying,
            sql.put("id_pais", vo.getIdPais() == null ? 1058 : vo.getIdPais());// integer NOT NULL,
            sql.put("inscricaomunicipal", vo.getInscricaoMunicipal());// character varying(20) DEFAULT ''::character varying,
            sql.putNull("id_contacontabilfiscalpassivo");// bigint,
            sql.put("numerocobranca", vo.getNumeroCobranca());// character varying(6) NOT NULL DEFAULT '0'::character varying,
            sql.put("complemento", vo.getComplemento());// character varying(30) NOT NULL DEFAULT ''::character varying,
            sql.put("complementocobranca", vo.getComplementoCobranca());// character varying(30) NOT NULL DEFAULT ''::character varying,
            sql.putNull("id_contacontabilfiscalativo");// bigint,
            sql.put("utilizaedi", false);// boolean NOT NULL DEFAULT false,
            sql.put("tiporegravencimento", -1);// integer NOT NULL DEFAULT '-1'::integer,
            sql.put("nfemitidapostofiscal", false);// boolean DEFAULT false,
            sql.put("id_tipoempresa", vo.getTipoEmpresa().getId());// integer not null,
            sql.put("id_tipocustodevolucaotroca", 1);
            incluirTipoIndicadorIE(vo, sql);
            stm.execute(sql.getInsert());
        }
    }

    public void atualizarFornecedor(FornecedorVO vo, Set<OpcaoFornecedor> opt) throws Exception {
        if (!opt.isEmpty()) {
            try (Statement stm = Conexao.createStatement()) {
                SQLBuilder sql = new SQLBuilder();
                sql.setTableName("fornecedor");
                if (opt.contains(OpcaoFornecedor.TELEFONE)) {
                    sql.put("telefone", vo.getTelefone());
                }
                if (opt.contains(OpcaoFornecedor.TIPO_INSCRICAO)) {
                    sql.put("id_tipoinscricao", vo.getTipoInscricao().getId());
                }
                if (opt.contains(OpcaoFornecedor.RAZAO_SOCIAL)) {
                    sql.put("razaosocial", vo.getRazaoSocial());
                }
                if (opt.contains(OpcaoFornecedor.NOME_FANTASIA)) {
                    sql.put("nomefantasia", vo.getNomeFantasia());
                }
                if (opt.contains(OpcaoFornecedor.ENDERECO)) {
                    sql.put("endereco", vo.getEndereco());
                }
                if (opt.contains(OpcaoFornecedor.ENDERECO_COMPLETO)) {
                    sql.put("endereco", vo.getEndereco());
                    sql.put("complemento", vo.getComplemento());
                    sql.put("numero", vo.getNumero());
                    sql.put("bairro", vo.getBairro());
                    sql.put("cep", vo.getCep());
                    sql.put("id_municipio", vo.getMunicipio().getId());
                    sql.put("id_estado", vo.getEstado() == null ? Parametros.get().getUfPadrao().getId() : vo.getEstado().getId());
                }
                if (opt.contains(OpcaoFornecedor.ENDERECO_COMPLETO_COBRANCA)) {
                    sql.put("enderecocobranca", vo.getEnderecoCobranca());
                    sql.put("complementocobranca", vo.getComplementoCobranca());
                    sql.put("numerocobranca", vo.getNumeroCobranca());
                    sql.put("bairrocobranca", vo.getBairroCobranca());
                    sql.put("cepcobranca", vo.getCepCobranca());
                    sql.put("id_municipiocobranca", vo.getMunicipioCobranca().getId());
                    sql.put("id_estadocobranca", vo.getEstadoCobranca() == null ? Parametros.get().getUfPadrao().getId() : vo.getEstadoCobranca().getId());
                }
                if (opt.contains(OpcaoFornecedor.COMPLEMENTO)) {
                    sql.put("complemento", vo.getComplemento());
                }
                if (opt.contains(OpcaoFornecedor.NUMERO)) {
                    sql.put("numero", vo.getNumero());
                }
                if (opt.contains(OpcaoFornecedor.BAIRRO)) {
                    sql.put("bairro", vo.getBairro());
                }
                if (opt.contains(OpcaoFornecedor.SITUACAO_CADASTRO)) {
                    sql.put("id_situacaocadastro", vo.getSituacaoCadastro().getId());
                    sql.put("revenda", vo.getRevenda());
                }
                if (opt.contains(OpcaoFornecedor.BLOQUEADO)) {
                    sql.put("bloqueado", vo.isBloqueado());
                }
                if (opt.contains(OpcaoFornecedor.TIPO_EMPRESA)) {
                    sql.put("id_tipoempresa", vo.getTipoEmpresa().getId());
                }
                if (opt.contains(OpcaoFornecedor.TIPO_FORNECEDOR)) {
                    sql.put("id_tipofornecedor", vo.getTipoFornecedor().getId());
                }
                if (opt.contains(OpcaoFornecedor.CNPJ_CPF)) {
                    sql.put("cnpj", vo.getCnpj());
                    sql.put("id_tipoinscricao", vo.getTipoInscricao().getId());
                }
                if (opt.contains(OpcaoFornecedor.INSCRICAO_ESTADUAL)) {
                    sql.put("inscricaoestadual", vo.getInscricaoEstadual());
                }
                if (opt.contains(OpcaoFornecedor.INSCRICAO_MUNICIPAL)) {
                    sql.put("inscricaomunicipal", vo.getInscricaoMunicipal());
                }
                if (opt.contains(OpcaoFornecedor.MUNICIPIO)) {
                    sql.put("id_municipio", vo.getMunicipio().getId());
                }
                if (opt.contains(OpcaoFornecedor.UF)) {
                    sql.put("id_estado", vo.getEstado() == null ? Parametros.get().getUfPadrao().getId() : vo.getEstado().getId());
                }
                if (opt.contains(OpcaoFornecedor.TIPO_PAGAMENTO)) {
                    sql.put("id_tipopagamento", vo.getTipoPagamento().getId());
                }
                if (opt.contains(OpcaoFornecedor.OBSERVACAO)) {
                    sql.put("observacao", vo.getObservacao());
                }
                if (opt.contains(OpcaoFornecedor.BANCO_PADRAO)) {
                    sql.put("id_banco", vo.getIdBanco());
                }
                if (opt.contains(OpcaoFornecedor.CEP)) {
                    sql.put("cep", vo.getCep());
                }
                if (opt.contains(OpcaoFornecedor.EMITE_NFE)) {
                    sql.put("utilizanfe", vo.isUtilizaNfe());
                }
                if (opt.contains(OpcaoFornecedor.PERMITE_NF_SEM_PEDIDO)) {
                    sql.put("permitenfsempedido", vo.isPermiteNfSemPedido());
                }
                if (opt.contains(OpcaoFornecedor.TIPO_INDICADOR_IE)) {
                    incluirTipoIndicadorIE(vo, sql);
                }
                if (opt.contains(OpcaoFornecedor.UTILIZAIVA)) {
                    sql.put("utilizaiva", vo.isUtilizaiva());
                }
                if (opt.contains(OpcaoFornecedor.DATA_CADASTRO)) {
                    sql.put("datacadastro", vo.getDataCadastro());
                }
                sql.setWhere("id = " + vo.getId());

                if (!sql.isEmpty()) {
                    stm.execute(sql.getUpdate());
                }
            }
        }
    }

    private void incluirTipoIndicadorIE(FornecedorVO vo, SQLBuilder sql) {
        if (vo.getTipoIndicadorIe() == null) {
            if (vo.getInscricaoEstadual() != null && !"".equals(vo.getInscricaoEstadual()) && !"ISENTO".equals(vo.getInscricaoEstadual())) {
                sql.put("id_tipoindicadorie", TipoIndicadorIE.CONTRIBUINTE_ICMS.getId());
            } else {
                sql.put("id_tipoindicadorie", TipoIndicadorIE.NAO_CONTRIBUINTE.getId());
            }
        } else {
            sql.put("id_tipoindicadorie", vo.getTipoIndicadorIe().getId());
        }
    }

    public void resetCnpjCpf(String sistema, String lojaOrigem) throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "update fornecedor set cnpj = id\n"
                    + "where\n"
                    + "	id in\n"
                    + "	(select\n"
                    + "		f.id\n"
                    + "	from \n"
                    + "		fornecedor f\n"
                    + "		join implantacao.codant_fornecedor ant on\n"
                    + "			ant.codigoatual = f.id\n"
                    + "	where\n"
                    + "		ant.importsistema = " + SQLUtils.stringSQL(sistema) + " and\n"
                    + "		ant.importloja = " + SQLUtils.stringSQL(lojaOrigem) + ");"
            );
        }
    }

}
