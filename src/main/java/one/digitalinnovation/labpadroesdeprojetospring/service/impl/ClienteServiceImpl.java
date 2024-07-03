package one.digitalinnovation.labpadroesdeprojetospring.service.impl;

import one.digitalinnovation.labpadroesdeprojetospring.model.Cliente;
import one.digitalinnovation.labpadroesdeprojetospring.model.ClienteRepositoryI;
import one.digitalinnovation.labpadroesdeprojetospring.model.Endereco;
import one.digitalinnovation.labpadroesdeprojetospring.model.EnderecoRepositoryI;
import one.digitalinnovation.labpadroesdeprojetospring.service.ClienteService;
import one.digitalinnovation.labpadroesdeprojetospring.service.ViaCepService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class ClienteServiceImpl implements ClienteService {

    @Autowired
    private ClienteRepositoryI clienteRepositoryI;

    @Autowired
    private EnderecoRepositoryI enderecoRepositoryI;

    @Autowired
    private ViaCepService viaCepService;


    @Override
    public Iterable<Cliente> buscarTodos() {
        return clienteRepositoryI.findAll();
    }

    @Override
    public Cliente buscarPorId(Long id) {
        return clienteRepositoryI.findById(id).orElse(null);
    }

    @Override
    public void inserir(Cliente cliente) {
        salvarClienteComCep(cliente);
    }


    @Override
    public void atualizar(Long id, Cliente cliente) {
        Optional<Cliente> oldCliente = clienteRepositoryI.findById(id);
        if (oldCliente.isPresent())
            salvarClienteComCep(cliente);
    }

    @Override
    public void deletar(Long id) {
        clienteRepositoryI.delete(buscarPorId(id));
    }
    private void salvarClienteComCep(Cliente cliente) {
        String cep = cliente.getEndereco().getCep();
        Endereco endereco = enderecoRepositoryI
                .findById(cep)
                .orElseGet(() -> {
                    Endereco enderecoNovo = viaCepService.consultarCep(cep);
                    enderecoRepositoryI.save(enderecoNovo);
                    return enderecoNovo;
                });
        cliente.setEndereco(endereco);
        clienteRepositoryI.save(cliente);
    }
}
