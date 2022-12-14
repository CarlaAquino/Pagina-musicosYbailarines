package edu.team5.finalproject.service;

import edu.team5.finalproject.dto.ClientUserDto;
import edu.team5.finalproject.entity.Client;
import edu.team5.finalproject.entity.User;
import edu.team5.finalproject.entity.enums.Role;
import edu.team5.finalproject.exception.ExceptionMessages;
import edu.team5.finalproject.exception.MyException;
import edu.team5.finalproject.mapper.GenericModelMapper;
import edu.team5.finalproject.repository.ClientRepository;
import edu.team5.finalproject.repository.UserRepository;
import edu.team5.finalproject.utility.Utility;
import java.util.List;

import lombok.RequiredArgsConstructor;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ClientService {

    private final GenericModelMapper mapper;
    private final ClientRepository clientRepository;
    private final UserRepository userRepository;
    private final ImageService imageService;

    public void create(ClientUserDto dto, MultipartFile image) throws MyException {
        Client client = mapper.map(dto, Client.class);

        client.setProfileImage((!image.isEmpty()) ? imageService.imageToString(image) : imageService.defaultImage());        

        User user = userRepository.findByEmail(dto.getUserEmail()).get();

        client.setUser(user);
        client.setDeleted(false);
        client.getUser().setRole(Role.CLIENT);
        clientRepository.save(client);
    }

    @Transactional
    public void update(ClientUserDto dto, MultipartFile image) throws MyException {
        validateUpdate(dto);
        
        Client client = getById(dto.getId());
                
        if (!image.isEmpty()) client.setProfileImage(imageService.imageToString(image));

        client.setNickname(dto.getClientNickname());        
        clientRepository.save(client);
    }

    @Transactional
    public void updateEnableById(Long id) {
        clientRepository.enableById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        clientRepository.deleteById(id);        
    }
    
    @Transactional(readOnly = true)
    public Client getByIdUser(Long id) {
        return clientRepository.getByIdUser(id).get();
    }
    
    @Transactional(readOnly = true)
    public List<Client> getByBoolean(Boolean delete) {
        return clientRepository.getByBoolean(delete);
    }
    
    @Transactional(readOnly = true)
    public List<Client> getAll() {
        return clientRepository.findAll();
    }

    @Transactional(readOnly = true)
    public Client getById(Long id) {
        return clientRepository.findById(id).get();
    }

    public void validateClientDto(ClientUserDto dto) throws MyException {
        if (!Utility.validate(Utility.USERNAME_PATTERN, dto.getClientNickname()))
            throw new MyException(ExceptionMessages.INVALID_USERNAME.get());
            
        if (clientRepository.existsByNickname(dto.getClientNickname()))
            throw new MyException(ExceptionMessages.ALREADY_EXISTS_USERNAME.get());    
    }
 
    private void validateUpdate(ClientUserDto dto) throws MyException {
        if (!Utility.validate(Utility.USERNAME_PATTERN, dto.getClientNickname()))
            throw new MyException(ExceptionMessages.INVALID_USERNAME.get());
    }
    
}


