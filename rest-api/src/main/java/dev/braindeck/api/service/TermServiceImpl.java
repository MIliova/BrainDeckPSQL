package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.TermEntity;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.repository.TermRepository;
import dev.braindeck.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermRepository termRepository;
    private final UserService userService;

    @Override
    public TermDto create(SetEntity set, NewTermPayload payload) {
        TermEntity entity = new TermEntity(payload.getTerm(), payload.getDescription(), set);
        entity = termRepository.save(entity);
        return new TermDto(entity.getId(), entity.getTerm(), entity.getDescription());
    }

    @Override
    public List<TermDto> create(SetEntity set, List<NewTermPayload> terms) {
        return Mapper.termsToDto(termRepository.saveAll(
                terms.stream()
                        .map(payload -> new TermEntity(payload.getTerm(), payload.getDescription(), set))
                        .toList()
        ));
    }

    @Override
    @Transactional
    public void update(UpdateTermPayload payload) {
        TermEntity term = termRepository.findById(payload.id()).orElseThrow(() -> new NoSuchElementException("Term not found"));

        UserEntity currentUser = userService.getCurrentUser();
        if (!term.getSet().getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Term does not belong to this user");
        }

        if (!payload.description().equals(term.getDescription())) {
            term.setDescription(payload.description());
        }
        if (!payload.term().equals(term.getTerm())) {
            term.setTerm(payload.term());
        }
        termRepository.save(term);
    }

    @Override
    public List<TermDto> findAllBySet(SetEntity setEntity) {
        return Mapper.termsToDto(termRepository.findAllBySet(setEntity));
    }





    @Override
    public void deleteById(int id) {
        termRepository.deleteById(id);
    }

    @Override
    public void deleteAllBySetId(int setId) {
        termRepository.deleteBySetId(setId);
    }





}

