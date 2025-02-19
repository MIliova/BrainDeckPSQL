package dev.braindeck.api.service;

import dev.braindeck.api.entity.User;
import dev.braindeck.api.entity.UserDto;
import dev.braindeck.api.repository.UserRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class DefaultUserService implements UserService {
    private final UserRepository userRepository;

    @Override
    public User findById(int id) {
        return this.userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("errors.user.not_found"));
    }

    public UserDto findCurrentUser () {
        return Mapper.userToDto(this.findById(1));
    }

}
