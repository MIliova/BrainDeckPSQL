package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.DraftExistException;
import dev.braindeck.api.controller.payload.NewSetFromDraftPayload;
import dev.braindeck.api.dto.DraftSetDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftSetEntity;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.repository.DraftSetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DefaultDraftSetService implements DraftSetService {

    private final DraftSetRepository draftSetRepository;
    private final DraftTermService draftTermService;

    @Override
    @Transactional
    public DraftSetDto create(UserEntity user, NewSetFromDraftPayload payload) {

        DraftSetEntity draftSetEntity = this.draftSetRepository.findFirstByUserId(user.getId()).orElseThrow(()-> new NoSuchElementException("errors.draft.not_found"));

        if (draftSetEntity == null || draftSetEntity.getId() == null || Objects.equals(payload.id(), draftSetEntity.getId())) {

            if (draftSetEntity == null){
                draftSetEntity = this.draftSetRepository.save(new DraftSetEntity(null, payload.title(), payload.description(), payload.termLanguageId(), payload.descriptionLanguageId(), user, null));
            }

            this.draftTermService.createTerms(draftSetEntity, payload.terms());
            List<TermDto> terms = this.draftTermService.findTermsByDraftId(draftSetEntity.getId());
            return Mapper.DraftSetToDto(draftSetEntity, terms);

        } else {
            throw new DraftExistException("errors.draft.exist");
        }
    }

    @Override
    public DraftSetDto findFirstByUserId(int userId) {
        DraftSetEntity draftSetEntity = this.draftSetRepository.findFirstByUserId(userId)
                .orElse(null);
                //.orElseThrow(()-> new NoSuchElementException("errors.draft.not_found"));
        List<TermDto> terms = (draftSetEntity != null)
                ? this.draftTermService.findTermsByDraftId(draftSetEntity.getId())
                : Collections.emptyList();
        return Mapper.DraftSetToDto(draftSetEntity, terms);
    }

    @Override
    public DraftSetDto findById(int draftId) {
        DraftSetEntity draftSetEntity = this.draftSetRepository.findById(draftId)
                .orElseThrow(()-> new NoSuchElementException("errors.draft.not_found"));
        List<TermDto> terms = this.draftTermService.findTermsByDraftId(draftSetEntity.getId());
        return Mapper.DraftSetToDto(draftSetEntity, terms);
    }

    @Override
    @Transactional
    public void delete(int id) {
        //this.draftTermService.deleteByDraftId(id);
        //System.out.println("deleted " + id);
        this.draftSetRepository.deleteById(id);
    }
}
