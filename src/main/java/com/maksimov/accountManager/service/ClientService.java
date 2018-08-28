package com.maksimov.accountManager.service;

import com.maksimov.accountManager.exception.ResourceNotFoundException;
import com.maksimov.accountManager.model.Client;
import com.maksimov.accountManager.repository.ClientRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Optional;

@Service
public class ClientService {
    private Logger logger = LoggerFactory.getLogger(ClientService.class);

    private ClientRepository clientRepository;

    @Autowired
    public ClientService(ClientRepository clientRepository) {
        this.clientRepository = clientRepository;
    }

    public Client save(@NotNull Client client){
        return clientRepository.save(client);
    }

    public List<Client> findAll(){
        return clientRepository.findAll();
    }

    public Client findById(Long id){
        Optional<Client> client = clientRepository.findById(id);
        if (!client.isPresent())
            throw new ResourceNotFoundException(id.toString());

        return client.get();
    }

    public void deleteById(Long id){
        Optional<Client> client = clientRepository.findById(id);
        if (!client.isPresent()) {
            throw new ResourceNotFoundException(id.toString());
        }

        clientRepository.deleteById(id);
    }
}
