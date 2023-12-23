package br.com.franca.apigastos.service;

import br.com.franca.apigastos.enums.StatusPessoaEnum;
import br.com.franca.apigastos.model.Pessoa;
import br.com.franca.apigastos.model.Usuario;
import br.com.franca.apigastos.repository.PessoaRepository;
import br.com.franca.apigastos.repository.UsuarioRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.Validator;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

@Service
public class PessoaService {

    @Autowired
    private PessoaRepository pessoaRepository;

    @Autowired
    private Validator validator;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    private Logger log = LoggerFactory.getLogger(PessoaService.class);


    public Pessoa salvarPessoa(Pessoa pessoa) {

        log.info("Inicio do processo salvar pessoa.");

        pessoa = pessoaRepository.save(pessoa);

        Date data = new Date(System.currentTimeMillis());
        SimpleDateFormat formatarDate = new SimpleDateFormat("yyyy-MM-dd");
        System.out.print(formatarDate.format(data));

        Usuario usuario = usuarioRepository.findByPessoa(pessoa.getId(), pessoa.getEmail());

        if (usuario == null) {
            String constraint = usuarioRepository.consultaConstraintAcesso();
            if (constraint != null) {
                jdbcTemplate.execute("begin; alter table usuarios_acesso drop constraint " + constraint + "; commit;");
            }
        }
        usuario = new Usuario();
        usuario.setDataAtualSenha(Calendar.getInstance().getTime().toInstant().atZone(Calendar.getInstance().getTimeZone().toZoneId()).toLocalDateTime());
        usuario.setPessoa(pessoa);
        usuario.setLogin(pessoa.getEmail());

        String senha = "" + Calendar.getInstance().getTimeInMillis();
        String senhaCript = new BCryptPasswordEncoder().encode(senha);

        usuario.setSenha(senhaCript);

        usuario = usuarioRepository.save(usuario);

        usuarioRepository.cadastrarAcessoUser(usuario.getId());

        return pessoa;
    }

    public List<Pessoa> listarPessoas() {

        log.info("Inicio do processo listagem de pessoa.");

        return pessoaRepository.findAll();
    }

    public Pessoa buscarPessoaPorId(Long id) {

        log.info("Inicio do processo buscar pessoa por id.");

        return pessoaRepository.findById(id).orElse(null);
    }

    public Pessoa buscarPessoaPorEmail(String email) {

        log.info("Inicio do processo buscar pessoa por email.");

        return pessoaRepository.findByEmail(email);
    }

    public Pessoa buscarPessoaPorIdAndEmail(Long id, String email) {

        log.info("Inicio do processo buscar pessoa por id e email.");

        return pessoaRepository.findByIdAndEmail(id, email).orElse(null);
    }

    public Pessoa buscarPessoaPorCpf(String cpf) {

        log.info("Inicio do processo buscar pessoa por cpf.");

        return pessoaRepository.findByCpf(cpf);
    }

    public List<Pessoa> buscarPessoaPorDataCadastro(LocalDate dataInicio, LocalDate dataFim) {

        log.info("Inicio do processo buscar pessoa por data de cadastro.");
        return pessoaRepository.findByDataCadastroBetween(dataInicio, dataFim);
    }

    public Pessoa inativarPessoa(Long id) {

        log.info("Inicio do processo inativar pessoa.");

        Pessoa pessoa = pessoaRepository.findById(id).orElse(null);

        if (pessoa == null) {
            log.error("Pessoa não encontrada no banco de dados.");
            return null;
        } else {
            pessoa.setStatus(StatusPessoaEnum.INATIVO);
            pessoa = pessoaRepository.save(pessoa);
            log.info("Pessoa inativada com sucesso.");
            return pessoa;
        }
    }
    public Pessoa ativarPessoa(Long id) {

        log.info("Inicio do processo ativar pessoa.");

        Pessoa pessoa = pessoaRepository.findById(id).orElse(null);

        if (pessoa == null) {
            log.error("Pessoa não encontrada no banco de dados.");
            return null;
        } else {
            pessoa.setStatus(StatusPessoaEnum.ATIVO);
            pessoa = pessoaRepository.save(pessoa);
            log.info("Pessoa ativada com sucesso.");
            return pessoa;
        }
    }
}
