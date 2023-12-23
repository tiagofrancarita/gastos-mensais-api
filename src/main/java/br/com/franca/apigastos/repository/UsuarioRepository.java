package br.com.franca.apigastos.repository;

import br.com.franca.apigastos.model.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface UsuarioRepository extends JpaRepository<Usuario, Long>{


    @Query(value = "SELECT u FROM Usuario u WHERE u.pessoa.id = ?1 or u.login = ?2")
    Usuario findByPessoa(Long id, String email);

    @Query(nativeQuery = true,value = "SELECT constraint_name FROM information_schema.constraint_column_usage WHERE table_name = 'usuarios_acesso' AND column_name = 'acesso_id' AND constraint_name <> 'unique_acesso_user' ")
    String consultaConstraintAcesso();

    @Transactional
    @Modifying
    @Query(nativeQuery = true, value = "INSERT INTO usuarios_acesso (usuario_id, acesso_id) VALUES (?1,(SELECT id FROM acesso WHERE descricao_acesso='ROLE_USER'))")
    void cadastrarAcessoUser(Long id);
}




