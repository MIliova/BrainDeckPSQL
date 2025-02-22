package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.SetDto;
import dev.braindeck.api.dto.SetWithCountDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.*;
import dev.braindeck.api.repository.SetRepository;
import dev.braindeck.api.repository.UserRepository;
import jakarta.persistence.Tuple;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultSetService implements SetService {

    private final SetRepository setRepository;
    private final TermService termService;
    private final UserRepository userRepository;

    @Override
    public List<SetWithCountDto> findAllByUserId(int userId) {
        UserEntity user = userRepository.findById(userId).orElseThrow(NoSuchElementException::new);
        List<Tuple> sets = this.setRepository.findAllByUserIdWithTermCount(userId);
        return Mapper.setsWithCountWithUserToDto(sets, user);
    }



    @Override
    @Transactional
    public SetDto createSet(String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<NewTermPayload> listTerms) {
        SetEntity setEntity =  this.setRepository.save(new SetEntity(null, title, description, termLanguageId, descriptionLanguageId, user, null));

        this.termService.createTerms(setEntity, listTerms);
        List<TermDto> terms = this.termService.findTermsBySetId(setEntity.getId());

        return Mapper.setToDto(setEntity, terms);
    }


    @Override
    public SetDto findSetById(int setId) {
        SetEntity setEntity =  this.setRepository.findByIdWithTerms(setId)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not_found"));
        List<TermDto> terms = this.termService.findTermsBySetId(setEntity.getId());
        return Mapper.setToDto(setEntity, terms);
    }

    @Override
    @Transactional
    public void updateSet(int setId, String title, String description, int termLanguageId, int descriptionLanguageId, UserEntity user, List<UpdateTermPayload> terms) {
        this.setRepository.findById(setId)
                .ifPresentOrElse(set -> {
                    set.setTitle(title);
                    set.setDescription(description);
                    set.setTermLanguageId(termLanguageId);
                    set.setDescriptionLanguageId(descriptionLanguageId);
//                    this.setRepository.save(set);
                }, () -> {
                    throw new NoSuchElementException("errors.set.not_found");
                });
        this.termService.updateTerms(terms);
    }
    @Override
    @Transactional
    public void deleteSet(int setId) {
        this.setRepository.deleteById(setId);
        this.termService.deleteTermsBySetId(setId);
    }
}
