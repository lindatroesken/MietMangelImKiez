package de.lindatroesken.backend.service;

import de.lindatroesken.backend.controller.UnauthorizedUserException;
import de.lindatroesken.backend.model.AddressEntity;
import de.lindatroesken.backend.model.UserEntity;
import de.lindatroesken.backend.repo.AddressRepository;
import de.lindatroesken.backend.repo.UserRepository;
import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.persistence.EntityExistsException;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Set;


@Getter
@Setter
@Service
public class UserService {

    private final UserRepository userRepository;
    private final AddressRepository addressRepository;

    @Autowired
    public UserService(UserRepository userRepository, AddressRepository addressRepository) {
        this.userRepository = userRepository;
        this.addressRepository = addressRepository;
    }


    public List<UserEntity> findAll() {
        return userRepository.findAll();
    }

    public UserEntity findByUsername(String username) {
        return userRepository.findByUsername(username).orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public AddressEntity addNewAddress(String username, AddressEntity addressEntity) {
        UserEntity userEntity = findByUsername(username);
        Set<AddressEntity> existingUserAddresses = userEntity.getAddressList();
        if(addressExists(existingUserAddresses, addressEntity)){
            throw new EntityExistsException("Address already exists");
        }
        AddressEntity createdAddress = userEntity.addAddress(addressEntity);
        userRepository.save(userEntity);

        return createdAddress;

    }

    public boolean addressExists(Set<AddressEntity> existingAddressList, AddressEntity addressToCheck){
        for (AddressEntity address : existingAddressList){
            if (address.getStreet().equals(addressToCheck.getStreet())
                    && address.getNumber().equals(addressToCheck.getNumber())
                    && address.getZip().equals(addressToCheck.getZip())
                    && address.getCity().equals(addressToCheck.getCity())
                    && address.getCountry().equals(addressToCheck.getCountry())
            ) {return true;}
        }
        return false;
    }

    public List<AddressEntity> findAddressByUsername(String username) {
        UserEntity userEntity = findByUsername(username);
        return addressRepository.findAllByUserEntity(userEntity);
    }

    public AddressEntity editAddress(String username, Long addressId, AddressEntity addressEntity) {
        if (!addressEntity.getId().equals(addressId)){
            throw new IllegalArgumentException("Request body and ID doe not match");
        }
        UserEntity userEntity = findByUsername(username);
        AddressEntity existingAddressEntity = addressRepository.findById(addressId).orElseThrow(() -> new EntityNotFoundException("Address not found"));
        if (!existingAddressEntity.getUserEntity().equals(userEntity)){
            throw new UnauthorizedUserException("User can only edit own address");
        }
        if (addressEntity.getStreet() != null && !addressEntity.getStreet().equals(existingAddressEntity.getStreet())){
            existingAddressEntity.setStreet(addressEntity.getStreet());
        }
        if (addressEntity.getNumber() != null && !addressEntity.getNumber().equals(existingAddressEntity.getNumber())){
            existingAddressEntity.setNumber(addressEntity.getNumber());
        }
        if (addressEntity.getZip() != null && !addressEntity.getZip().equals(existingAddressEntity.getZip())){
            existingAddressEntity.setZip(addressEntity.getZip());
        }
        if (addressEntity.getCity() != null && !addressEntity.getCity().equals(existingAddressEntity.getCity())){
            existingAddressEntity.setCity(addressEntity.getCity());
        }
        if (addressEntity.getCountry() != null && !addressEntity.getCountry().equals(existingAddressEntity.getCountry())){
            existingAddressEntity.setCountry(addressEntity.getCountry());
        }
        return addressRepository.save(existingAddressEntity);
    }
}

