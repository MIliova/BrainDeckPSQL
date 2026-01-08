package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.dto.DraftDto;
import dev.braindeck.api.entity.DraftEntity;
import dev.braindeck.api.entity.NewDraftEntity;

import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.repository.DraftRepository;
import dev.braindeck.api.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.NoSuchElementException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class DraftServiceImpl implements DraftService {

    private final DraftRepository draftRepository;
    private final UserRepository userRepository;

    @Transactional
    public DraftDto getDraftDto(int userId) {
        DraftEntity draft = findOrCreateDraftEntity(userId);
        return Mapper.DraftEntityToDraftDto(draft);
    }

    @Override
    @Transactional
    public DraftEntity findOrCreateDraftEntity(int userId) {
        return draftRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                .orElseGet(() -> {
                    try {
                        UserEntity user = userRepository.getReferenceById(userId);
                        DraftEntity draft = new DraftEntity();
                        draft.setUser(user);
                        return draftRepository.save(draft);
                    } catch (DataIntegrityViolationException e) {
                        return draftRepository.findFirstByUserIdOrderByCreatedAtDesc(userId)
                                .orElseThrow(() -> new IllegalStateException(
                                        "Не удалось создать или найти драфт", e));
                    }
                });
    }

    @Transactional
    public DraftDto getDraftDtoById(int userId, int draftId) {
        DraftEntity draft = findDraftEntityById(userId, draftId);
        return Mapper.DraftEntityToDraftDto(draft);
    }

    @Transactional(readOnly = true)
    @Override
    public DraftEntity findDraftEntityById(int userId, int draftId) {
        DraftEntity draftEntity = draftRepository.findById(draftId)
                .orElseThrow(()-> new NoSuchElementException("errors.draft.not.found"));

        checkOwner(draftEntity, userId);
        return draftEntity;
    }





    @Transactional
    @Override
    public DraftEntity findOrCreateDraftEntityById(int userId, int draftId) {
        return Optional.ofNullable(draftId > 0 ? draftRepository.findById(draftId).orElse(null) : null)
                .filter(draft -> draft.getUser().getId().equals(userId))
                .orElseGet(() -> this.findOrCreateDraftEntity(userId));
    }



    @Override
    @Transactional
    public void update (int id, String title, String description, int termLanguageId, int descriptionLanguageId, int currentUserId) {
        DraftEntity draftEntity = this.findDraftEntityById(currentUserId, id);

        draftEntity.setTitle(title);
        draftEntity.setDescription(description);
        draftEntity.setTermLanguageId(termLanguageId);
        draftEntity.setDescriptionLanguageId(descriptionLanguageId);

        draftRepository.save(draftEntity);
    }

    @Override
    @Transactional
    public void delete(DraftEntity draftEntity) {
        draftRepository.delete(draftEntity);
    }

    @Override
    @Transactional
    public DraftDto deleteAndCreate(int draftId, UserEntity user) {
        DraftEntity draftEntity = this.findDraftEntityById(user.getId(), draftId);
        draftRepository.delete(draftEntity);
        DraftEntity draft =  draftRepository.save(new NewDraftEntity(user));
        return Mapper.DraftSetToDto(draft);
    }

    private void checkOwner(DraftEntity draftEntity, int currentUserId) {
        if (!draftEntity.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.draft.not.belong.user");
        }
    }
//    @Override
//    public DraftEntity create(UserEntity user) {
//        ensureNoExistingDraft(user);
//        return draftRepository.save(new NewDraftEntity(user));
//    }
//
//    private void ensureNoExistingDraft(UserEntity user) {
//        draftRepository.findFirstByUserId(user.getId())
//                .ifPresent(d -> { throw new DraftExistException("errors.draft.exist"); });
//    }
//    @Override
//    public NewDraftDto create(DraftPayload payload, UserEntity user) {
//        ensureNoExistingDraft(user);
//        return Mapper.NewDraftToDto(draftRepository.save(new DraftEntity(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user)));
//    }


}
