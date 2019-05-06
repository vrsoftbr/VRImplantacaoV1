package vrimplantacao2.dao.cadastro;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.List;
import java.util.Stack;
import vrframework.classe.Conexao;
import vrframework.classe.ProgressBar;
import vrimplantacao.utils.Utils;
import vrimplantacao2.vo.enums.SituacaoCadastro;
import vrimplantacao2.vo.cadastro.FamiliaProdutoVO;
import vrimplantacao2.utils.multimap.MultiMap;
import vrimplantacao2.vo.importacao.FamiliaProdutoIMP;

public class FamiliaProdutoDAO {
    
    private boolean gerarCodigo = false;

    public boolean isGerarCodigo() {
        return gerarCodigo;
    }

    /**
     * Determina se os ID dos produtos serão substituídos por novos.
     * @param gerarCodigo True gera novos códigos, False não gera.
     */
    public void setGerarCodigo(boolean gerarCodigo) {
        this.gerarCodigo = gerarCodigo;
    }
        
    private void atualizaAnteriores() throws Exception {
        anteriores = new MultiMap<>(3);
        try (Statement stm = Conexao.createStatement()) {
            //Se a tabela existir executa a consulta.
            if (Utils.existeTabela("implantacao", "codant_familiaproduto")) {
                try (ResultSet rst = stm.executeQuery(
                    "select \n" +
                    "	ca.impsistema, \n" +
                    "	ca.imploja, \n" +
                    "	ca.impid, \n" +
                    "	ca.codigoatual, \n" +
                    "	fa.descricao, \n" +
                    "	fa.id_situacaocadastro\n" +
                    "from \n" +
                    "	implantacao.codant_familiaproduto ca \n" +
                    "	left join familiaproduto fa on \n" +
                    "		ca.codigoatual = fa.id"
                )) {
                    while (rst.next()) {
                        FamiliaProdutoVO vo = new FamiliaProdutoVO();
                        vo.setId(rst.getInt("codigoatual"));
                        vo.setDescricao(rst.getString("descricao"));
                        vo.setSituacaoCadastro(SituacaoCadastro.getById(rst.getInt("id_situacaocadastro")));
                        anteriores.put(
                                vo, 
                                rst.getString("impsistema"), 
                                rst.getString("imploja"), 
                                rst.getString("impid"));
                    }
                }
            }
        }
    }
    
    /**
     * Cria a tabela no banco.
     * @throws Exception 
     */
    private void createTable() throws Exception {
        Conexao.createStatement().execute(
            "do $$\n" +
            "declare\n" +
            "begin\n" +
            "	if not exists(select table_name from information_schema.tables where table_schema = 'implantacao' and table_name = 'codant_familiaproduto') then\n" +
            "		create table implantacao.codant_familiaproduto (\n" +
            "			impsistema varchar, \n" +
            "			imploja varchar, \n" +
            "			impid varchar,\n" +
            "			codigoatual integer,\n" +
            "			primary key (impsistema, imploja, impid)\n" +
            "		);\n" +
            "		raise notice 'tabela criada';\n" +
            "	end if;\n" +
            "end;\n" +
            "$$;"
        );
    }
    
    /**
     * Gera uma listagem com os ids disponíveis para uso nas famílias.
     * @param limit Valor máximo de ids para serem gerados.
     * @return Pilha com todos os ids disponíveis.
     * @throws Exception 
     */
    private Stack<Integer> carregarIdsLivres(int limit) throws Exception {
        Stack<Integer> result = new Stack<>();
        try (Statement stm = Conexao.createStatement()) {
            try (ResultSet rst = stm.executeQuery(
                    "SELECT id FROM generate_series(1, " + limit + ") "
                            + "AS s(id) except select id from "
                            + "familiaproduto order by id desc"
            )) {
                while (rst.next()) {
                    result.add(rst.getInt("id"));
                }
            }
        }
        return result;
    }
    
    /**
     * Método que grava uma lista de famílias. O difierencial deste método é que
     * ele trabalha com a tabela implantacao.codantfamilia2 e permite importar
     * famílias com ids não inteiros.
     * @param familias Listagem das famílias.
     * @throws Exception 
     */
    public void salvar(List<FamiliaProdutoIMP> familias) throws Exception {
        
        MultiMap<String, FamiliaProdutoIMP> aux = organizarFamilias(familias);
        
        Stack<Integer> idsLivres = carregarIdsLivres(100000);
        
        ProgressBar.setStatus("Gravando as famílias....");
        ProgressBar.setMaximum(aux.size());
        try {
            Conexao.begin();
            
            createTable();
            
            try (Statement stm = Conexao.createStatement()) {
                for (FamiliaProdutoIMP oFamilia: aux.values()) {
                    if (!getAnteriores().containsKey(
                            oFamilia.getImportSistema(),
                            oFamilia.getImportLoja(),
                            oFamilia.getImportId()
                    )) {                        
                        
                        FamiliaProdutoVO vo = new FamiliaProdutoVO();
                        vo.setDescricao(oFamilia.getDescricao());
                        vo.setSituacaoCadastro(oFamilia.getSituacaoCadastro());
                        
                        //<editor-fold defaultstate="collapsed" desc="Valida o id da família">
                        if (gerarCodigo) {
                            vo.setId(idsLivres.pop());
                        } else {
                            boolean geraID = false;
                            //Verifica se o id dos sistema anterior é um id válido
                            //ou seja inteiro < que 100000
                            try {
                                vo.setId(Integer.parseInt(oFamilia.getImportId()));
                            } catch (NumberFormatException e) {
                                vo.setId(-1);
                            }
                            //Gera um novo se as condições forem atingidas.
                            if (vo.getId() < 1 || vo.getId() > 999999) {
                                //Se for um id inválido
                                geraID = true;
                            } else if (!idsLivres.contains(vo.getId())) {
                                //Se o ID do produto não estiver disponível
                                geraID = true;
                            }
                            
                            if (geraID) {
                                //Gera um novo id
                                vo.setId(idsLivres.pop());
                            } else {
                                //Se o id informado for válido, remove ele da 
                                //listagem de disponíveis
                                idsLivres.remove((Integer) vo.getId());
                            }
                        }
                        //</editor-fold>

                        stm.execute(
                            "INSERT INTO familiaproduto (id, descricao, id_situacaocadastro) values ("
                            + vo.getId() + ","
                            + Utils.quoteSQL(vo.getDescricao()) + ","
                            + vo.getSituacaoCadastro().getId()
                            + ");");
                        
                        stm.execute("insert into implantacao.codant_familiaproduto ("
                            + "impsistema, "
                            + "imploja, "
                            + "impid, "
                            + "codigoatual ) values (" 
                            + Utils.quoteSQL(oFamilia.getImportSistema()) + ", "
                            + Utils.quoteSQL(oFamilia.getImportLoja()) + ", "
                            + Utils.quoteSQL(oFamilia.getImportId()) + ", "
                            + vo.getId() + ""
                            + ");");
                        
                        getAnteriores().put(
                            vo,
                            oFamilia.getImportSistema(),
                            oFamilia.getImportLoja(),
                            oFamilia.getImportId()
                        );
                        
                    }
                    ProgressBar.next();
                }
            }
            
            Conexao.commit();
        } catch (Exception e) {
            Conexao.rollback();
            throw e;
        }        
    }

    private MultiMap<String, FamiliaProdutoIMP> organizarFamilias(List<FamiliaProdutoIMP> familias) {
        MultiMap<String, FamiliaProdutoIMP> organizados = new MultiMap<>();
        for (FamiliaProdutoIMP familia: familias) {
            organizados.put(familia, familia.getChave());
        }        
        return organizados;
    }

    public void apagarFamilia() throws Exception {
        try (Statement stm = Conexao.createStatement()) {
            stm.execute(
                    "delete from familiaproduto; "
                    + "alter sequence familiaproduto_id_seq restart with 1;"
                    + "drop table if exists implantacao.codant_familiaproduto;");
        }
    }
    
    private MultiMap<String, FamiliaProdutoVO> anteriores;
    public MultiMap<String, FamiliaProdutoVO> getAnteriores() throws Exception {
        if (anteriores == null) {
            atualizaAnteriores();
        }
        return anteriores;
    }
}
