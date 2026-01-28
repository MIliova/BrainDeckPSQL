package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.entity.*;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.repository.SetRepository;
import dev.braindeck.api.repository.TermRepository;
import dev.braindeck.api.repository.UserRepository;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermRepository termRepository;
    private final SetRepository setRepository;
    private final SetService setService;

//    @Override
//    public NewDTermDto autoCreate(int userId, int draftId, DTermPayload payload) {
//        SetEntity set = setService.findById(userId, draftId);
//        TermEntity entity = new TermEntity(
//                null,
//                payload.term(),
//                payload.description(),
//                set
//        );
//        termRepository.save(entity);
//        return Mapper.newTermToDto(entity);
//    }

//    @Override
//    @Transactional
//    public void autoUpdate(int userId, int draftId, int termId, DTermPayload payload) {
//        TermEntity term = termRepository.findById(termId)
//                .orElseThrow(() -> new NoSuchElementException("errors.term.not.found"));
//
//        checkOwnership(term, draftId, userId);
//
//        if (!Objects.equals(payload.term(), term.getTerm())) {
//            term.setTerm(payload.term());
//        }
//
//        if (!Objects.equals(payload.description(), term.getDescription())) {
//            term.setDescription(payload.description());
//        }
//
//        termRepository.save(term);
//    }

    private void checkOwnership(TermEntity term, int setId, int userId) {
        if (!term.getSet().getId().equals(setId)) {
            throw new ForbiddenException("errors.term.not.belong.set");
        }
        if (!term.getSet().getUser().getId().equals(userId)) {
            throw new ForbiddenException("errors.term.not.belong.user");
        }
    }



    @Override
    public TermDto create(
            int userId,
            int setId,
            DTermPayload payload
    ) {

        SetEntity set = setRepository.findBySetIdAndUserId(setId, userId)
                .orElseThrow(() -> new ForbiddenException("errors.set.not.belong.user"));

        TermEntity entity = new TermEntity(
                payload.term(),
                payload.description(),
                set);
        entity = termRepository.save(entity);
        return new TermDto(entity.getId(), entity.getTerm(), entity.getDescription());
    }

    @Override
    public List<TermDto> create(
            int userId,
            int setId,
            List<DTermPayload> payloads) {

        SetEntity set = setRepository.findBySetIdAndUserId(setId, userId)
                .orElseThrow(() -> new ForbiddenException("errors.set.not.belong.user"));

        return Mapper.termsToDto(termRepository.saveAll(
                payloads.stream()
                        .map(payload -> new TermEntity(payload.term(), payload.description(), set))
                        .toList()
        ));
    }

    @Override
    @Transactional
    public void update(int termId, int setId, int currentUserId, UpdateTermPayload payload) {
        TermEntity term = termRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("errors.term.not.found"));
        if (!term.getSet().getId().equals(setId)) {
            throw new ForbiddenException("errors.term.not.belong.set");
        }
        if (!term.getSet().getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.term.not.belong.user");
        }
        if (!payload.term().equals(term.getTerm())) {
            term.setTerm(payload.term());
        }
        if (!payload.description().equals(term.getDescription())) {
            term.setDescription(payload.description());
        }
        termRepository.save(term);
    }

    @Override
    public void delete(int termId, int setId, int currentUserId) {
        TermEntity term = termRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("errors.term.not.found"));

        if (!term.getSet().getId().equals(setId)) {
            throw new ForbiddenException("errors.term.not.belong.set");
        }

        if (!term.getSet().getUser().getId().equals(currentUserId)) {
            throw new ForbiddenException("errors.term.not.belong.user");
        }

        termRepository.delete(term);
    }

    @Override
    public List<TermDto> findAllBySet(SetEntity setEntity) {
        return Mapper.termsToDto(termRepository.findAllBySet(setEntity));
    }

    @Override
    public List<TermDto> findAllBySetId(int setId) {
        return Mapper.termsToDto(termRepository.findAllBySetId(setId));
    }


//    @Override
//    public void deleteAllBySetId(int setId) {
//        termRepository.deleteBySetId(setId);
//    }
}

