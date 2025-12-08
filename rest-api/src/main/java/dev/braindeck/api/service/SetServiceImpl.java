package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithTermCountDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.*;
import dev.braindeck.api.repository.SetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetServiceImpl implements SetService {

    private final SetRepository setRepository;
    private final TermService termService;
    private final UserService userService;

    @Transactional
    @Override
    public SetDto create(String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<NewTermPayload> listTerms) {
        SetEntity set = new SetEntity(title, description, termLanguageId, descriptionLanguageId, user);
        List<TermEntity> terms = listTerms.stream()
                .map(payload -> new TermEntity(payload.getTerm(), payload.getDescription(), set))
                .toList();
        set.setTerms(terms);
        return Mapper.setToDto(setRepository.save(set));
    }

    @Transactional
    @Override
    public void update(int setId, String title, String description, int termLanguageId, int descriptionLanguageId,
                       UserEntity user, List<UpdateTermPayload> termsPayload) {
        SetEntity set = setRepository.findById(setId).orElseThrow(() -> new NoSuchElementException("Set not found"));

        UserEntity currentUser = userService.getCurrentUser();
        if (!set.getUser().getId().equals(currentUser.getId())) {
            throw new ForbiddenException("Set does not belong to this user");
        }

        set.setTitle(title);
        set.setDescription(description);
        set.setTermLanguageId(termLanguageId);
        set.setDescriptionLanguageId(descriptionLanguageId);

        Map<Integer, TermEntity> existingTerms = set.getTerms().stream().collect(Collectors.toMap(TermEntity::getId, termEntity -> termEntity));

        List<TermEntity> newTermList = new ArrayList<>();
        for (UpdateTermPayload payload : termsPayload) {
            if (payload.id() != null && existingTerms.containsKey(payload.id()) ) {
                TermEntity term = existingTerms.get(payload.id());
                if (!term.getTerm().equals(payload.term()) || !term.getDescription().equals(payload.description())) {
                    term.setTerm(payload.term());
                    term.setDescription(payload.description());
                }
            } else {
                TermEntity term = new TermEntity(payload.term(), payload.description(), set);
                newTermList.add(term);
            }
        }

        set.getTerms().removeIf(t -> termsPayload.stream()
                .noneMatch(p -> p.id() != null && p.id().equals(t.getId())));

        set.getTerms().addAll(newTermList);

        setRepository.save(set);
    }

    @Transactional
    @Override
    public void delete(int setId) {
        setRepository.deleteById(setId);
    }

    @Transactional
    @Override
    public List<SetWithTermCountDto> findAllByUserId(int userId) {
        return setRepository.findAllByUser(userId);
    }

    @Transactional(readOnly = true)
    @Override
    public SetDto findById(int setId) {
        SetEntity setEntity = setRepository.findById(setId)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not_found"));
        List<TermDto> terms = termService.findAllBySet(setEntity);
        return Mapper.setToDto(setEntity, terms);
    }

    @Transactional(readOnly = true)
    @Override
    public SetEntity findEntityById(int setId) {
        return setRepository.findById(setId)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not_found"));
    }
}
