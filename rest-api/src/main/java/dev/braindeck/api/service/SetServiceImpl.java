package dev.braindeck.api.service;

import com.fasterxml.jackson.databind.JsonNode;
import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.*;

import dev.braindeck.api.entity.*;
import dev.braindeck.api.repository.SetRepository;
import dev.braindeck.api.repository.TermRepository;
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

    private final UserRepository userRepository;
    private final SetRepository setRepository;
    private final TermRepository termRepository;

    private final TermService termService;
    private final DraftService draftService;

    @Transactional
    @Override
    public SetCreatedDto create(
            int userId,
            String title,
            String description,
            int termLanguageId,
            int descriptionLanguageId,
            List<NewTermPayload> listTerms) {

        UserEntity userRef = userRepository.getReferenceById(userId);

        SetEntity set = new SetEntity(title, description, termLanguageId, descriptionLanguageId, userRef);
        SetEntity saved = setRepository.save(set);

        List<TermEntity> terms = listTerms.stream()
                .map(payload -> new TermEntity(payload.getTerm(), payload.getDescription(), saved))
                .toList();
        termRepository.saveAll(terms);

        return new SetCreatedDto(
                saved.getId(),
                saved.getUser().getId()
        );
    }

    @Transactional
    @Override
    public SetDto createFromDraft(
            int userId,
            int draftId,
            String title,
            String description,
            int termLanguageId,
            int descriptionLanguageId) {

        System.out.println("createFromDraft " + draftId);

        DraftEntity draftEntity = draftService.findDraftEntityById(userId, draftId);
        if (draftEntity == null) {
            throw new ForbiddenException("errors.draft.not.belong.user");
        }

        UserEntity user = userRepository.getReferenceById(userId);
        SetEntity set = new SetEntity(title, description, termLanguageId, descriptionLanguageId, user);
//        SetEntity savedSet = setRepository.save(set); //
//        entityManager.flush(); //

        List<TermEntity> terms = draftEntity.getTerms().stream().map(term -> new TermEntity(term.getTerm(), term.getDescription(), set)).toList();

//        termRepository.saveAll(terms); //
        set.setTerms(terms);
        SetEntity savedSet = setRepository.save(set);

        draftService.delete(draftEntity);

        return Mapper.setToDto(savedSet);
    }

    @Transactional(readOnly = true)
    @Override
    public SetShortDto findShortById(int id) {
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
    public SetEditDto findSetEditDtoById(int userId, int id) {
        SetEntity set = findById(userId, id);
        return new SetEditDto(
                set.getId(),
                set.getTitle(),
                set.getDescription(),
                set.getTermLanguageId(),
                set.getDescriptionLanguageId(),
                Mapper.termsToDto(set.getTerms())
        );
    }

    @Transactional(readOnly = true)
    @Override
    public SetEntity findById(int userId, int id) {

        SetEntity set = setRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not.found"));

        if (!set.getUser().getId().equals(userId)) {
            throw new ForbiddenException("errors.set.not.belong.user");
        }
        return set;
    }

    @Override
    @Transactional
    public void autoUpdate (int userId, int id, JsonNode body) {

        SetEntity set = this.findById(userId, id);

        if (body.has("title")) {
            JsonNode node = body.get("title");
            set.setTitle(node.isNull() ? null : node.asText());
        }

        if (body.has("description")) {
            JsonNode node = body.get("description");
            set.setDescription(node.isNull() ? null : node.asText());
        }

        if (body.has("termLanguageId")) {
            JsonNode node = body.get("termLanguageId");
            set.setTermLanguageId(node.isNull() ? null : node.asInt());
        }

        if (body.has("descriptionLanguageId")) {
            JsonNode node = body.get("descriptionLanguageId");
            set.setDescriptionLanguageId(node.isNull() ? null : node.asInt());
        }

        setRepository.save(set);
    }


    @Transactional
    @Override
    public void update(int id, String title, String description, int termLanguageId, int descriptionLanguageId,
                       List<UpdateTermPayload> termsPayload, int userId) {
        SetEntity set = setRepository.findById(id).orElseThrow(() -> new NoSuchElementException("errors.set.not.found"));

        if (!set.getUser().getId().equals(userId)) {
            throw new ForbiddenException("errors.set.not.belong.user");
        }

        set.setTitle(title);
        set.setDescription(description);
        set.setTermLanguageId(termLanguageId);
        set.setDescriptionLanguageId(descriptionLanguageId);

        Map<Integer, TermEntity> existingTerms = set.getTerms().stream().collect(Collectors.toMap(TermEntity::getId, termEntity -> termEntity));

        List<TermEntity> toInsert = new ArrayList<>();
        List<TermEntity> toUpdate = new ArrayList<>();
        List<TermEntity> toDelete = new ArrayList<>();

        for (UpdateTermPayload payload : termsPayload) {
            if (payload.id() != null && existingTerms.containsKey(payload.id())) {
                TermEntity term = existingTerms.get(payload.id());
                if (!Objects.equals(term.getTerm(), payload.term()) ||
                        !Objects.equals(term.getDescription(), payload.description())) {
                    term.setTerm(payload.term());
                    term.setDescription(payload.description());
                    toUpdate.add(term);
                }
            } else {
                TermEntity term = new TermEntity(payload.term(), payload.description(), set);
                toInsert.add(term);
            }
        }

        for (TermEntity term : set.getTerms()) {
            boolean existsInPayload = termsPayload.stream()
                    .anyMatch(p -> p.id() != null && p.id().equals(term.getId()));
            if (!existsInPayload) {
                toDelete.add(term);
            }
        }
        if (!toDelete.isEmpty()) {
            termRepository.deleteAll(toDelete);
        }

        if (!toInsert.isEmpty()) {
            termRepository.saveAll(toInsert);
        }

        if (!toUpdate.isEmpty()) {
            termRepository.saveAll(toUpdate);
        }

        setRepository.save(set);
    }

    @Transactional
    @Override
    public void delete(int id, int userId) {
        SetEntity set = setRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("errors.set.not.found"));

        if (!set.getUser().getId().equals(userId)) {
            throw new ForbiddenException("errors.set.not.belong.user");
        }

        setRepository.deleteById(id);
    }







}
