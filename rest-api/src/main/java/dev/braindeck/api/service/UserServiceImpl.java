package dev.braindeck.api.service;

import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.NoSuchElementException;

@RequiredArgsConstructor
@Service
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    @Override
    public UserEntity findById(int id) {
        return this.userRepository.findById(id).orElseThrow(() -> new NoSuchElementException("errors.user.not_found"));
    }

    @Override
    public UserEntity getCurrentUser () {

        return this.findById(1);
    }

//    public UserWithDraftDto findByIdWithDraft(int id) {
//        return this.userRepository.findByIdWithDraft(id).orElseThrow(() -> new NoSuchElementException("errors.user.not_found"));
//    }

}
