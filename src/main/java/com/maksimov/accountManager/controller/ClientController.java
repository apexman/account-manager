package com.maksimov.accountManager.controller;

import com.maksimov.accountManager.dto.ClientTO;
import com.maksimov.accountManager.model.Client;
import com.maksimov.accountManager.repository.ClientRepository;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(value = "/api/client")
public class ClientController {
    private final Logger logger = LoggerFactory.getLogger(ClientController.class);

    private ClientRepository clientRepository;
    private ModelMapper modelMapper;

    @Autowired
    public ClientController(ClientRepository clientRepository, ModelMapper modelMapper) {
        this.clientRepository = clientRepository;
        this.modelMapper = modelMapper;
    }

    @RequestMapping(value = "", method = RequestMethod.GET)
    public ResponseEntity<List<ClientTO>> getAll(){
        List<Client> clients = clientRepository.findAll();
        List<ClientTO> clientsTO = clients.stream()
                .map(client -> modelMapper.map(client, ClientTO.class))
                .collect(Collectors.toList());

        return ResponseEntity.ok().body(clientsTO);
    }

    public ResponseEntity<ClientTO> getById(Long id){
        return ResponseEntity.ok().body(modelMapper.map(clientRepository.findById(id), ClientTO.class));
    }
}
