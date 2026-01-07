package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.*;

import dev.braindeck.api.entity.*;
import dev.braindeck.api.repository.SetRepository;
import dev.braindeck.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class SetServiceImpl implements SetService {

    private final SetRepository setRepository;
    private final TermService termService;
    private final DraftService draftService;
    private final UserRepository userRepository;


    @Transactional
    @Override
    public SetDto createFromDraft(
            int draftId,
            String title,
            String description,
            int termLanguageId,
            int descriptionLanguageId,
            UserEntity user) {

        DraftEntity draftEntity = draftService.findEntityById(draftId, user.getId());
        if (draftEntity == null) {
            throw new ForbiddenException("errors.draft.not.belong.user");
        }
        SetEntity set = new SetEntity(title, description, termLanguageId, descriptionLanguageId, user);

        List<TermEntity> terms = draftEntity.getTerms().stream().map(term -> new TermEntity(term.getTerm(), term.getDescription(), set)).toList();
        set.setTerms(terms);
        SetEntity savedSet = setRepository.save(set);

        draftService.delete(draftEntity);
        return Mapper.setToDto(savedSet);
    }

    @Transactional
    @Override
    public SetCreatedDto create(
            String title,
            String description,
            int termLanguageId,
            int descriptionLanguageId,
            int userId,
            List<NewTermPayload> listTerms) {

        UserEntity userRef = userRepository.getReferenceById(userId);

        SetEntity set = new SetEntity(title, description, termLanguageId, descriptionLanguageId, userRef);

        List<TermEntity> terms = listTerms.stream()
                .map(payload -> new TermEntity(payload.getTerm(), payload.getDescription(), set))
                .toList();

        set.setTerms(terms);

        SetEntity saved = setRepository.save(set);
        return new SetCreatedDto(
                saved.getId(),
                saved.getUser().getId()
        );
    }

    @Transactional
    @Override
    public void update(int id, String title, String description, int termLanguageId, int descriptionLanguageId,
                       List<UpdateTermPayload> termsPayload, int currentUserId) {
        SetEntity set = setRepository.findById(id).orElseThrow(() -> new NoSuchElementException("errors.set.not.found"));

        if (!set.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.set.not.belong.user");
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
                if (!Objects.equals(term.getTerm(), payload.term()) || !Objects.equals(term.getDescription(), payload.description())) {
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
    public void delete(int id, int currentUserId) {
        SetEntity set = setRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("errors.set.not.found"));

        if (!set.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.set.not.belong.user");
        }

        setRepository.deleteById(id);
    }

    @Transactional(readOnly = true)
    @Override
    public SetDto findByIdForUser(int id, int currentUserId) {
        SetEntity set = setRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not.found"));

        if (!set.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.set.not.belong.user");
        }

        List<TermDto> terms = termService.findAllBySet(set);
        return Mapper.setToDto(set, terms);
    }

    @Transactional(readOnly = true)
    @Override
    public SetEntity findEntityById(int id, int userId) {
        SetEntity setEntity = setRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not.found"));
        if (!setEntity.getUser().getId().equals(userId)) {
            throw new ForbiddenException("errors.set.not.belong.user");
        }
        return setEntity;
    }

    @Transactional
    @Override
    public List<SetWithTCntUInfoDto> findAllByUserId(int userId) {
        return setRepository.findAllByUser(userId)
                .stream()
                .map(set -> {
                    String updatedAtLocal = set.updatedAt()
                            .atZone(ZoneId.systemDefault())
                            .format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

                    return new SetWithTCntUInfoDto(
                            set.id(),
                            set.title(),
                            set.description(),
                            updatedAtLocal,
                            set.termCount(),
                            set.userId(),
                            set.userName()
                    );
                })
                .toList();
    }

    @Transactional(readOnly = true)
    @Override
    public SetShortDto findById(int id) {
        SetShortDto setDto = setRepository.findDtoById(id)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not.found"));
        List<TermDto> terms = termService.findAllBySetId(id);
        return new SetShortDto(
                setDto.id(),
                setDto.title(),
                setDto.description(),
                setDto.userId(),
                setDto.userName(),
                terms
        );
    }

}
