package de.lindatroesken.backend.service;

import de.lindatroesken.backend.model.AddressEntity;
import de.lindatroesken.backend.model.UserEntity;
import de.lindatroesken.backend.repo.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Optional;


@Getter
@Setter
@Service
public class UserService {

    private UserRepository userRepository;

    @Autowired
    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }


    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public Optional<UserEntity> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public AddressEntity addNewAddress(String username, AddressEntity addressEntity) {
        UserEntity userEntity = findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
        AddressEntity createdAddress = userEntity.addAddress(addressEntity);
        userRepository.save(userEntity);


        return createdAddress;


    }
}

