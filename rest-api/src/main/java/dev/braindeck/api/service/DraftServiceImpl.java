package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.DraftExistException;
import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.DraftPayload;
import dev.braindeck.api.dto.DraftDto;
import dev.braindeck.api.dto.NewDraftDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftEntity;
import dev.braindeck.api.entity.NewDraftEntity;

import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.repository.DraftRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final DraftRepository draftRepository;
    private final DTermService draftTermService;

    @Override
    public NewDraftDto create(DraftPayload payload, UserEntity user) {
        ensureNoExistingDraft(user);
        return Mapper.NewDraftToDto(draftRepository.save(new DraftEntity(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user)));
    }

    @Override
    public DraftEntity create(UserEntity user) {
        ensureNoExistingDraft(user);
        return draftRepository.save(new NewDraftEntity(user));
    }

    private void ensureNoExistingDraft(UserEntity user) {
        draftRepository.findFirstByUserId(user.getId())
                .ifPresent(d -> { throw new DraftExistException("errors.draft.exist"); });
    }

    @Override
    @Transactional
    public void update (int id, String title, String description, int termLanguageId, int descriptionLanguageId, int currentUserId) {
        DraftEntity draft = findEntityById(id, currentUserId);

        draft.setTitle(title);
        draft.setDescription(description);
        draft.setTermLanguageId(termLanguageId);
        draft.setDescriptionLanguageId(descriptionLanguageId);

        draftRepository.save(draft);
    }

    @Override
    @Transactional
    public void delete(int id, int currentUserId) {
        DraftEntity draft = findEntityById(id, currentUserId);

        draftTermService.deleteByDraftId(id, currentUserId);
        draftRepository.delete(draft);
    }

    @Transactional(readOnly = true)
    @Override
    public DraftEntity findEntityById(int id, int currentUserId) {
        DraftEntity draft = draftRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("errors.draft.not.found"));

        checkOwner(draft, currentUserId);

        return draft;
    }

    @Transactional
    @Override
    public DraftEntity findEntityOrCreate(UserEntity user, int draftId) {
        if (draftId > 0) {
            DraftEntity draft = draftRepository.findById(draftId).orElse(null);
            if (draft != null && draft.getUser().getId().equals(user.getId())) {
                return draft;
            }
        }
        return draftRepository.findFirstByUserId(user.getId()).orElseGet(() -> create(user));
    }

    @Override
    public DraftDto findFirstByUserId(int userId) {
        DraftEntity draft = draftRepository.findFirstByUserId(userId)
                .orElse(null);
        List<TermDto> terms = (draft != null)
                ? draftTermService.findById(draft.getId(), userId)
                : Collections.emptyList();
        return Mapper.DraftSetToDto(draft, terms);
    }

    private void checkOwner(DraftEntity draft, int currentUserId) {
        if (!draft.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.draft.not.belong.user");
        }
    }

}
