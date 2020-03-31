package vrimplantacao2.dao.interfaces.hipcom;

import java.sql.ResultSet;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.logging.Logger;
import vrimplantacao.classe.ConexaoMySQL;
import vrimplantacao.utils.Utils;
import vrimplantacao2.dao.cadastro.venda.MultiStatementIterator;
import vrimplantacao2.utils.sql.SQLUtils;
import vrimplantacao2.vo.importacao.VendaIMP;

/**
 *
 * @author Leandro
 */
public class HipcomVendaIterator extends MultiStatementIterator<VendaIMP> {
    
    private static final Logger LOG = Logger.getLogger(HipcomVendaIterator.class.getName());

     public HipcomVendaIterator(String idLojas, Date dataInicial, Date dataTermino) throws Exception {
        super(
            new CustomNextBuilder(),
            new StatementBuilder() {
                @Override
                public Statement makeStatement() throws Exception {
                    return ConexaoMySQL.getConexao().createStatement(ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
                }
            }
        );
        
        for (String statement : SQLUtils.quebrarSqlEmMeses(getFullSQL(idLojas), dataInicial, dataTermino, new SimpleDateFormat("yyyy-MM-dd"))) {
            this.addStatement(statement);
        }
    }

    private String getFullSQL(String idLojaCliente) throws Exception {

        return 
            "select\n" +
            "	v.id,\n" +
            "	v.numero,\n" +
            "	v.caixa,\n" +
            "	v.data_cupom,\n" +
            "	concat(c.cliloja,'-',c.clicod) id_cliente,\n" +
            "	cast(v.data_cupom as time) horainicio,\n" +
            "	cast(v.data_cupom as time) horafim,\n" +
            "	case when v.cancelado = 'S' then 1 else 0 end cancelado,\n" +
            "	v.cpf_cnpj,\n" +
            "	v.serie numeroserie,\n" +
            "	v.modelo_documento_fiscal,\n" +
            "	c.clinome nomecliente\n" +
            "from\n" +
            "	hip_cupom v\n" +
            "	left join clicli c on\n" +
            "		c.clicpfcnpj = nullif(v.cpf_cnpj,1) \n" +
            "where	\n" +
            "	v.loja = " + idLojaCliente + " and\n" +
            "	cast(v.data_cupom as date) >= '{DATA_INICIO}' and\n" +
            "	cast(v.data_cupom as date) <= '{DATA_TERMINO}'\n" +
            "order by\n" +
            "	v.id";

    }
    
    private static class CustomNextBuilder implements NextBuilder<VendaIMP> {
        @Override
        public VendaIMP makeNext(ResultSet rs) throws Exception {
            VendaIMP next = new VendaIMP();
            
            next.setId(rs.getString("id"));
            next.setNumeroCupom(Utils.stringToInt(rs.getString("numero")));
            next.setEcf(Utils.stringToInt(rs.getString("caixa")));
            next.setData(rs.getDate("data_cupom"));
            next.setIdClientePreferencial(rs.getString("id_cliente"));
            next.setHoraInicio(rs.getTime("horainicio"));
            next.setHoraTermino(rs.getTime("horafim"));
            next.setCancelado(rs.getBoolean("cancelado"));
            next.setCpf(rs.getString("cpf_cnpj"));
            next.setNumeroSerie(rs.getString("numeroserie"));
            next.setNomeCliente(rs.getString("nomecliente"));
            
            return next;
        }
    }
    
}

/*
CREATE OR REPLACE
ALGORITHM = UNDEFINED VIEW `view_vendas_pdv_antiga` AS
select
    concat((concat(lpad(locate(substr(lpad(`hipcca`.`cccl`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccl`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0), cast(concat(lpad((ord(substr(`hipcca`.`cccd`, 1, 1)) - 32), 2, '0'), lpad((ord(substr(`hipcca`.`cccd`, 2, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipcca`.`cccd`, 3, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipcca`.`cccd`, 4, 1)) - 32), 2, '0')) as date),(concat(lpad(locate(substr(lpad(`hipcca`.`ccct`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`ccct`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0),(concat(lpad(locate(substr(lpad(`hipcca`.`cccc`, 3, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccc`, 3, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccc`, 3, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0)) AS `id_cupom`,
    (concat(lpad(locate(substr(lpad(`hipcca`.`cccl`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccl`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `loja`,
    (concat(lpad(locate(substr(lpad(`hipcca`.`ccct`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`ccct`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `terminal`,
    (concat(lpad(locate(substr(lpad(`hipcca`.`cccx`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccx`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `caixa`,
    (concat(lpad(locate(substr(lpad(`hipcca`.`cccc`, 3, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccc`, 3, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccc`, 3, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `numero_cupom`,
    (concat(lpad(locate(substr(lpad(`hipcca`.`cccp`, 7, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccp`, 7, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccp`, 7, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccp`, 7, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccp`, 7, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccp`, 7, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccp`, 7, '0'), 7, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `codigo_plu_bar`,
    (concat(lpad(locate(substr(lpad(`hipcca`.`cccs`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccs`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `sequencia`,
    concat(lpad(locate(substr(lpad(`hipcca`.`cccn`, 4, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccn`, 4, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccn`, 4, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccn`, 4, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) AS `serie_aparelho`,
    `hipcca`.`cccg` AS `legenda`,
    `hipcca`.`ccccodpiscofs` AS `pis_cofins`,
    round((concat(lpad(locate(substr(lpad(`hipcca`.`cccaliq`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccaliq`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 100), 2) AS `aliquota_icms`,
    (concat(lpad(locate(substr(lpad(`hipcca`.`cccvdd`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccvdd`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `codigo_vendedor`,
    round((concat(lpad(locate(substr(lpad(`hipcca`.`cccq`, 4, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccq`, 4, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccq`, 4, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccq`, 4, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 3) AS `quantidade`,
    round((concat(lpad(locate(substr(lpad(`hipcca`.`cccu`, 6, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccu`, 6, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccu`, 6, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccu`, 6, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccu`, 6, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccu`, 6, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 3) AS `valor_unitario`,
    round((concat(lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 2) AS `valor_total`,
    round((concat(lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccv`, 6, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 2) AS `valor_pago`,
    round((concat(lpad(locate(substr(lpad(`hipcca`.`cccitdsc`, 5, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccitdsc`, 5, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccitdsc`, 5, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccitdsc`, 5, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipcca`.`cccitdsc`, 5, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 2) AS `valor_desconto_item`,
    0 AS `valor_acrescimo_cupom`,
    0 AS `valor_desconto_cupom`,
    `hipcca`.`cccitcan` AS `item_cancelado`,
    `hipcca`.`ccccpcan` AS `cupom_cancelado`,
    cast(concat(lpad((ord(substr(`hipcca`.`cccd`, 1, 1)) - 32), 2, '0'), lpad((ord(substr(`hipcca`.`cccd`, 2, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipcca`.`cccd`, 3, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipcca`.`cccd`, 4, 1)) - 32), 2, '0')) as date) AS `data`,
    concat(lpad(locate(substr(lpad(`hipcca`.`ccch`, 3, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), ':', lpad(locate(substr(lpad(`hipcca`.`ccch`, 3, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), ':', lpad(locate(substr(lpad(`hipcca`.`ccch`, 3, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) AS `hora`,
    `hipcca`.`cccprom` AS `promocao`,
    (case
        when (`hipcca`.`cccmoddoc` = '59') then 'SAT'
        when (`hipcca`.`cccmoddoc` = '65') then 'NFCE'
        else 'ECF' end) AS `tipo_fiscal`
from
    `hipcca`
where
    (`hipcca`.`cccp` not in ('{DESCT}',
    '{ACRES}'))
    
    
    
    
    
    
CREATE OR REPLACE
ALGORITHM = UNDEFINED VIEW `view_vendas_pdv` AS
select
    `hipccc`.`cccid` AS `id`,
    concat((concat(lpad(locate(substr(lpad(`hipccc`.`cccl`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccl`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0), cast(concat(lpad((ord(substr(`hipccc`.`cccd`, 1, 1)) - 32), 2, '0'), lpad((ord(substr(`hipccc`.`cccd`, 2, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipccc`.`cccd`, 3, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipccc`.`cccd`, 4, 1)) - 32), 2, '0')) as date),(concat(lpad(locate(substr(lpad(`hipccc`.`ccct`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`ccct`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0),(concat(lpad(locate(substr(lpad(`hipccc`.`cccc`, 3, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccc`, 3, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccc`, 3, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0)) AS `id_cupom`,
    (concat(lpad(locate(substr(lpad(`hipccc`.`cccl`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccl`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `loja`,
    (concat(lpad(locate(substr(lpad(`hipccc`.`ccct`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`ccct`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `terminal`,
    (concat(lpad(locate(substr(lpad(`hipccc`.`cccx`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccx`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `caixa`,
    (concat(lpad(locate(substr(lpad(`hipccc`.`cccc`, 3, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccc`, 3, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccc`, 3, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `numero_cupom`,
    (concat(lpad(locate(substr(lpad(`hipccc`.`cccp`, 7, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccp`, 7, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccp`, 7, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccp`, 7, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccp`, 7, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccp`, 7, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccp`, 7, '0'), 7, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `codigo_plu_bar`,
    (concat(lpad(locate(substr(lpad(`hipccc`.`cccs`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccs`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `sequencia`,
    concat(lpad(locate(substr(lpad(`hipccc`.`cccn`, 4, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccn`, 4, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccn`, 4, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccn`, 4, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) AS `serie_aparelho`,
    `hipccc`.`cccg` AS `legenda`,
    `hipccc`.`ccccodpiscofs` AS `pis_cofins`,
    round((concat(lpad(locate(substr(lpad(`hipccc`.`cccaliq`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccaliq`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 100), 2) AS `aliquota_icms`,
    (concat(lpad(locate(substr(lpad(`hipccc`.`cccvdd`, 2, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccvdd`, 2, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) + 0) AS `codigo_vendedor`,
    round((concat(lpad(locate(substr(lpad(`hipccc`.`cccq`, 4, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccq`, 4, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccq`, 4, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccq`, 4, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 3) AS `quantidade`,
    round((concat(lpad(locate(substr(lpad(`hipccc`.`cccu`, 6, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccu`, 6, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccu`, 6, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccu`, 6, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccu`, 6, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccu`, 6, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 3) AS `valor_unitario`,
    round((concat(lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 2) AS `valor_total`,
    round((concat(lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccv`, 6, '0'), 6, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 2) AS `valor_pago`,
    round((concat(lpad(locate(substr(lpad(`hipccc`.`cccitdsc`, 5, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccitdsc`, 5, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccitdsc`, 5, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccitdsc`, 5, '0'), 4, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), lpad(locate(substr(lpad(`hipccc`.`cccitdsc`, 5, '0'), 5, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) / 1000), 2) AS `valor_desconto_item`,
    0 AS `valor_acrescimo_cupom`,
    0 AS `valor_desconto_cupom`,
    `hipccc`.`cccitcan` AS `item_cancelado`,
    `hipccc`.`ccccpcan` AS `cupom_cancelado`,
    cast(concat(lpad((ord(substr(`hipccc`.`cccd`, 1, 1)) - 32), 2, '0'), lpad((ord(substr(`hipccc`.`cccd`, 2, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipccc`.`cccd`, 3, 1)) - 32), 2, '0'), '-', lpad((ord(substr(`hipccc`.`cccd`, 4, 1)) - 32), 2, '0')) as date) AS `data`,
    concat(lpad(locate(substr(lpad(`hipccc`.`ccch`, 3, '0'), 1, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), ':', lpad(locate(substr(lpad(`hipccc`.`ccch`, 3, '0'), 2, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0'), ':', lpad(locate(substr(lpad(`hipccc`.`ccch`, 3, '0'), 3, 1), convert('123456789ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz!@#$%^&*()-_=+\\|[]{}:;,./?¾¿ÀÁÂÃÄÅÆÇÈÉ' using latin1)), 2, '0')) AS `hora`,
    `hipccc`.`cccprom` AS `promocao`,
    (case
        when (`hipccc`.`cccmoddoc` = '59') then 'SAT'
        when (`hipccc`.`cccmoddoc` = '65') then 'NFCE'
        else 'ECF' end) AS `tipo_fiscal`
from
    `hipccc`
where
    (`hipccc`.`cccp` not in ('{DESCT}',
    '{ACRES}'))
*/
