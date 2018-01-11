package vrimplantacao.dao.cadastro;

import vrframework.classe.Util;
import vrframework.classe.VRException;
import vrimplantacao.vo.loja.UsuarioVO;

public class UsuarioDAO {

    public UsuarioVO autenticar(String i_usuario, String i_senha, int i_idLoja) throws Exception {
        UsuarioVO oUsuario = new UsuarioVO();

        if (Util.isSenhaEspecial(i_usuario, i_senha)) {
            oUsuario.nome = "VR SOFTWARE";

        } else {
            throw new VRException("Usuário e/ou senha inválido(s)");
        }

        return oUsuario;
    }
}
