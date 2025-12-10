package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.DraftExistException;
import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.NewDraftPayload;
import dev.braindeck.api.dto.DraftSetDto;
import dev.braindeck.api.dto.NewDraftDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftSetEntity;
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
    public NewDraftDto create(NewDraftPayload payload, UserEntity user) {
        DraftSetEntity draft = draftRepository.findFirstByUserId(user.getId()).orElse(null);
        if (draft != null) {
            throw new DraftExistException("errors.draft.exist");
        }
        draft = draftRepository.save(
                new DraftSetEntity(payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user));
        return Mapper.NewDraftToDto(draft);
    }

    @Override
    public void update (int id, String title, String description, int termLanguageId, int descriptionLanguageId, int currentUserId) {
        DraftSetEntity draft = draftRepository.findById(id).orElseThrow(()-> new NoSuchElementException("errors.draft.not.found"));
        if (!draft.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.draft.not.belong.user");
        }

        draft.setTitle(title);
        draft.setDescription(description);
        draft.setTermLanguageId(termLanguageId);
        draft.setDescriptionLanguageId(descriptionLanguageId);

        draftRepository.save(draft);
    }

    @Override
    @Transactional
    public void delete(int id, int currentUserId) {
        DraftSetEntity draft = draftRepository.findById(id)
                .orElseThrow(() -> new NoSuchElementException("errors.draft.not.found"));

        if (!draft.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.draft.not.belong.user");
        }

        draftTermService.deleteByDraftId(id, currentUserId);
        draftRepository.delete(draft);
    }

    @Transactional(readOnly = true)
    @Override
    public DraftSetEntity findEntityById(int id, int currentUserId) {
        DraftSetEntity draft = draftRepository.findById(id)
                .orElseThrow(()-> new NoSuchElementException("errors.draft.not.found"));

        if (!draft.getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.draft.not.belong.user");
        }
        return draft;
    }

    @Override
    public DraftSetDto findFirstByUserId(int userId) {
        DraftSetEntity draft = draftRepository.findFirstByUserId(userId)
                .orElse(null);
                //.orElseThrow(()-> new NoSuchElementException("errors.draft.not_found"));
        List<TermDto> terms = (draft != null)
                ? draftTermService.findById(draft.getId(), userId)
                : Collections.emptyList();
        return Mapper.DraftSetToDto(draft, terms);
    }

//    @Override
//    public DraftSetDto findByIdForUser(int id, int currentUserId) {
//        DraftSetEntity draft = draftRepository.findById(id)
//                .orElseThrow(() -> new NoSuchElementException("errors.draft.not_found"));
//
//        if (!draft.getUser().getId().equals(currentUserId)) {
//            throw new ForbiddenException("errors.draft.forbidden");
//        }
//        List<TermDto> terms = draftTermService.findById(draft.getId());
//        return Mapper.DraftSetToDto(draft, terms);
//    }

}
