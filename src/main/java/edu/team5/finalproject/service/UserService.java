
package edu.team5.finalproject.service;

import edu.team5.finalproject.dto.UserDto;
import edu.team5.finalproject.entity.User;
import edu.team5.finalproject.entity.enums.Role;
import edu.team5.finalproject.exception.ExceptionMessages;
import edu.team5.finalproject.exception.MyException;
import edu.team5.finalproject.mapper.GenericModelMapper;
import edu.team5.finalproject.repository.UserRepository;
import edu.team5.finalproject.utility.Utility;
import java.util.Collections;
import java.util.List;
import javax.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Service
@RequiredArgsConstructor
public class UserService implements UserDetailsService {

    private final GenericModelMapper mapper;
    private final UserRepository userRepository;
    private final BCryptPasswordEncoder encoder;

    public void create(UserDto dto) throws MyException {
        validateUser(dto);

        User user = mapper.map(dto, User.class);       

        user.setPassword(encoder.encode(user.getPassword()));
        user.setDeleted(false);
        userRepository.save(user);
    }

    public void createAdmin(UserDto dto) throws MyException {
        validateUser(dto);

        User user = mapper.map(dto, User.class);       

        user.setPassword(encoder.encode(user.getPassword()));
        user.setRole(Role.ADMIN);
        user.setDeleted(false);
        userRepository.save(user);
    }

    @Transactional
    public void update(UserDto dto) throws MyException {
        validateUpdate(dto);
        User user = mapper.map(dto, User.class);
        
        userRepository.save(user);
    }

    @Transactional(readOnly = true)
    public List<User> getAll() {
        return userRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<User> getAllAdmin(Boolean delete) {
        return userRepository.findAllAdmin(delete);
    }

    @Transactional(readOnly = true)
    public User getById(Long id) {
        return userRepository.findById(id).get();
    }

    @Transactional
    public void updateEnableById(Long id) {
        userRepository.enableById(id);
    }

    @Transactional
    public void deleteById(Long id) {
        userRepository.deleteById(id);
    }

    private void validateUser(UserDto dto) throws MyException {
        if (!Utility.validate(Utility.EMAIL_PATTERN, dto.getUserEmail())) 
            throw new MyException(ExceptionMessages.INVALID_EMAIL.get());

        if (!Utility.validate(Utility.PASSWORD_PATTERN, dto.getUserPassword())) 
            throw new MyException(ExceptionMessages.INVALID_PASSWORD.get());

        if (userRepository.existsByEmail(dto.getUserEmail())) 
            throw new MyException(ExceptionMessages.ALREADY_EXISTS_EMAIL.get());
    }

    private void validateUpdate(UserDto dto) throws MyException {
        if (!Utility.validate(Utility.EMAIL_PATTERN, dto.getUserEmail())) 
            throw new MyException(ExceptionMessages.INVALID_EMAIL.get());

        if (!Utility.validate(Utility.PASSWORD_PATTERN, dto.getUserPassword())) 
            throw new MyException(ExceptionMessages.INVALID_PASSWORD.get());
    }

    @Override
    public UserDetails loadUserByUsername(String email) {
        User user = userRepository.findByEmail(email).orElseThrow(() -> new UsernameNotFoundException("Usuario no encotrado."));
        GrantedAuthority authority = new SimpleGrantedAuthority("ROLE_" + user.getRole());

        ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.currentRequestAttributes();
        HttpSession session = attributes.getRequest().getSession(true);

        session.setAttribute("id", user.getId());
        session.setAttribute("email", user.getEmail());
        session.setAttribute("role", user.getRole());

        return new org.springframework.security.core.userdetails.User(user.getEmail(), user.getPassword(), Collections.singletonList(authority));
    }

}
