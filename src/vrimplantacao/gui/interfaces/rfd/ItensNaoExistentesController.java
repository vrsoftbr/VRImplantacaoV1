package vrimplantacao.gui.interfaces.rfd;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.dao.cadastro.LojaDAO;
import vrimplantacao.gui.interfaces.rfd.ProdutoMapa.TipoMapa;
import vrimplantacao.utils.Utils;
import vrimplantacao.vo.loja.LojaVO;
import vrimplantacao2.dao.cadastro.produto2.ProdutoIDStack;
import vrimplantacao2.dao.cadastro.produto2.ProdutoIDStackProvider;
import vrimplantacao2.dao.cadastro.produto2.ProdutoRepositoryProvider;
import vrimplantacao2.parametro.Parametros;
import vrimplantacao2.utils.sql.SQLBuilder;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.cadastro.MercadologicoVO;
import vrimplantacao2.vo.cadastro.ProdutoAliquotaVO;
import vrimplantacao2.vo.cadastro.ProdutoAutomacaoVO;
import vrimplantacao2.vo.cadastro.ProdutoComplementoVO;
import vrimplantacao2.vo.cadastro.ProdutoVO;
import vrimplantacao2.vo.enums.Icms;
import vrimplantacao2.vo.enums.NaturezaReceitaVO;
import vrimplantacao2.vo.enums.SituacaoCadastro;

public class ItensNaoExistentesController {
    
    private int localizadoIndex = -1;
    private List<ProdutoVR> localizados = new ArrayList<>();
    private List<ProdutoMapa> mapeados;
    private boolean todos = false;

    public List<ProdutoVR> getLocalizados() {
        return localizados;
    }

    public List<ProdutoMapa> getMapeados() {
        return mapeados;
    }
    
    

    public int nextLocalizado() {
        if (localizadoIndex < getLocalizados().size() - 1) {
            localizadoIndex++;
        }
        return localizadoIndex;
    }

    public int priorLocalizado() {
        if (localizadoIndex > 0) {
            localizadoIndex--;
        }
        return localizadoIndex;
    }

    public List<ProdutoVR> atualizarLocalizados(String text) throws Exception {
        localizados = new ArrayList<>();
        localizadoIndex = -1;
        
        String[] aux = text.split(" ");
        String descricao = "";
        for (String a: aux) {
            if ("".equals(descricao)) {
                descricao = "%";
            }
            descricao += a.trim() + "%";
        }
        
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	p.id,\n" +
                    "	ean.codigobarras,\n" +
                    "	p.descricaocompleta,\n" +
                    "	p.descricaoreduzida\n" +
                    "from \n" +
                    "	produto p\n" +
                    "	left join produtoautomacao ean on\n" +
                    "		p.id = ean.id_produto\n" +
                    "where	\n" +
                    "	p.id::varchar = " + SQLUtils.stringSQL(text) + " or\n" +
                    "	ean.codigobarras::varchar = " + SQLUtils.stringSQL(text) + " or\n" +
                    "	p.descricaocompleta like upper(" + SQLUtils.stringSQL( descricao ) + ")\n" +
                    "limit 10"
            )) {
                while (rst.next()) {
                    ProdutoVR vo = new ProdutoVR(
                            rst.getInt("id"),
                            rst.getString("descricaocompleta"),
                            rst.getString("descricaoreduzida"),
                            rst.getLong("codigobarras")
                    );
                    
                    localizados.add(vo);
                }
            }
        }
        
        if (!localizados.isEmpty()) {            
            localizadoIndex = 0;
        }
        
        return localizados;
    }

    private List<ProdutoMapa> armazenados = new ArrayList<>();
    
    public void armazenar(TipoMapa tipo_mapa, String codRfd, String descricaoProduto) throws Exception {
        armazenados.add(new ProdutoMapa(tipo_mapa, codRfd, descricaoProduto, 0, false));
    }

    public void gravar() throws Exception {        
        Conexao.begin();
        try {
            ProgressBar.setStatus("Gravando códigos não localizados...");
            ProgressBar.setMaximum(armazenados.size());
            try (Statement stm = Conexao.createStatement()) {
                for (ProdutoMapa mapa: armazenados) {
                    try (ResultSet rst = stm.executeQuery(
                            "select * from implantacao.maparfd where tipo = " 
                                    + Utils.quoteSQL(mapa.getTipo().toString()) + " and codigorfd = "
                                    + Utils.quoteSQL(mapa.getCodrfd())
                    )) {
                        if (!rst.next()) {
                            
                            SQLBuilder sql = new SQLBuilder();
                            sql.setSchema("implantacao");
                            sql.setTableName("maparfd");
                            sql.put("tipo", mapa.getTipo().toString());
                            sql.put("codigorfd", mapa.getCodrfd());
                            sql.put("descricao", mapa.getDescricao());
                            sql.put("novo", false);
                            stm.execute(sql.getInsert());
                        }
                    }
                    ProgressBar.next();
                }
            }
            armazenados.clear();
            Conexao.commit();
        } catch (Exception e) {
            armazenados.clear();
            Conexao.rollback();
            throw e;
        }
    }

    
    
    public List<ProdutoMapa> carregarMapa(boolean todos, TipoMapa tipo) throws Exception {
        mapeados = new ArrayList<>();
        
        criarTabela();
        
        this.todos = todos;
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "select\n" +
                    "	tipo,\n" +
                    "	codigorfd,\n" +
                    "	descricao,\n" +
                    "	codigoatual,\n" +
                    "	novo\n" +
                    "from \n" +
                    "	implantacao.maparfd\n" +
                    "where\n" +
                    "	tipo = '" + tipo.toString() + "'\n" +
                    (!todos ? "	and codigoatual is null" : "")
            )) {
                while (rst.next()) {
                    TipoMapa tipoaux = TipoMapa.EAN;
                    switch(rst.getString("tipo")) {
                        case "EAN": { tipoaux = TipoMapa.EAN; }break;
                    }
                    ProdutoMapa mapa = new ProdutoMapa(
                        tipoaux,
                        rst.getString("codigorfd"),
                        rst.getString("descricao"),
                        rst.getInt("codigoatual"),
                        rst.getBoolean("novo")
                    );
                    
                    mapeados.add(mapa);
                }
            }
        }        
        
        produtoIDStack = new ProdutoIDStack(new ProdutoIDStackProvider());
        lojas = new LojaDAO().carregar();
        provider = new ProdutoRepositoryProvider();
        
        return mapeados;
    }

    private void criarTabela() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "create table if not exists implantacao.maparfd(\n" +
                    "	tipo varchar(5) not null,\n" +
                    "	codigorfd varchar not null,\n" +
                    "	descricao varchar not null,\n" +
                    "	codigoatual integer,\n" +
                    "	novo boolean,\n" +
                    "	primary key(tipo, codigorfd)\n" +
                    ");"
            );
        }
    }

    public void criarProduto(int index) throws Exception {
        if (index >= 0 && index < mapeados.size()) {
            try {
                Conexao.begin();
            
                ProdutoMapa mapa = mapeados.get(index);

                mapa.setCodigoAtual(produtoIDStack.obterID("-1", false));
                
                MercadologicoVO merc = provider.getMercadologico("-1", "-1", "-1", "0", "0");

                ProdutoVO vo = new ProdutoVO();   
                vo.setId(mapa.getCodigoAtual());
                vo.setMercadologico(merc);
                vo.setDescricaoCompleta(mapa.getDescricao());
                vo.setDescricaoReduzida(mapa.getDescricao());
                vo.setDescricaoGondola(mapa.getDescricao());
                vo.setPisCofinsNaturezaReceita(new NaturezaReceitaVO(196, 7, 999, "OUTRAS RECEITAS COM ISENCAO"));
                vo.setVendaPdv(false);
                vo.setIdFornecedorFabricante(1);
                provider.salvar(vo);

                ProdutoAliquotaVO aliq = vo.getAliquotas().make(Parametros.get().getUfPadrao().getId(), 1);
                aliq.setEstado(Parametros.get().getUfPadrao());
                aliq.setAliquotaCredito(Icms.getIsento());
                aliq.setAliquotaConsumidor(Icms.getIsento());
                aliq.setAliquotaCreditoForaEstado(Icms.getIsento());
                aliq.setAliquotaDebito(Icms.getIsento());
                aliq.setAliquotaDebitoForaEstado(Icms.getIsento());
                aliq.setAliquotaDebitoForaEstadoNf(Icms.getIsento());            
                provider.aliquota().salvar(aliq);            

                for (LojaVO loja: lojas) {
                    ProdutoComplementoVO compl = vo.getComplementos().make(loja.getId());
                    compl.setIdLoja(loja.getId());
                    compl.setDescontinuado(true);     
                    compl.setSituacaoCadastro(SituacaoCadastro.EXCLUIDO);
                    provider.complemento().salvar(compl, false);
                }
                
                ProdutoAutomacaoVO ean = vo.getEans().make((long) mapa.getCodigoAtual());
                ean.setCodigoBarras((long) mapa.getCodigoAtual());
                provider.automacao().salvar(ean);                

                try (Statement stm = Conexao.createStatement()) {
                    SQLBuilder sql = new SQLBuilder();

                    sql.setSchema("implantacao");
                    sql.setTableName("maparfd");
                    sql.setWhere(
                            "codigorfd = " + Utils.quoteSQL(mapa.getCodrfd()) +
                            " and tipo = " + Utils.quoteSQL(mapa.getTipo().toString())
                    );
                    sql.put("codigoatual", mapa.getCodigoAtual());
                    sql.put("novo", true);

                    stm.execute(sql.getUpdate());
                }
                
                if (!todos) {
                    mapeados.remove(index);
                }
                
                Conexao.commit();
            } catch (Exception e) {
                Conexao.rollback();
                throw e;
            }            
        }        
    }
    private List<LojaVO> lojas;    
    private ProdutoIDStack produtoIDStack;
    private ProdutoRepositoryProvider provider;

    public void selecionarProduto(int index) throws Exception {
        if (index >= 0 && index < mapeados.size()) {
            try {
                Conexao.begin();
            
                ProdutoMapa mapa = mapeados.get(index);
                ProdutoVR vo = localizados.get(localizadoIndex);

                mapa.setCodigoAtual(vo.getId());


                try (Statement stm = Conexao.createStatement()) {
                    SQLBuilder sql = new SQLBuilder();

                    sql.setSchema("implantacao");
                    sql.setTableName("maparfd");
                    sql.setWhere(
                            "codigorfd = " + Utils.quoteSQL(mapa.getCodrfd()) +
                            " and tipo = " + Utils.quoteSQL(mapa.getTipo().toString())
                    );
                    sql.put("codigoatual", mapa.getCodigoAtual());

                    stm.execute(sql.getUpdate());
                }
                
                if (!todos) {
                    mapeados.remove(index);
                }
                
                Conexao.commit();
            } catch (Exception e) {
                Conexao.rollback();
                throw e;
            }            
        }
    }
    
    
}
