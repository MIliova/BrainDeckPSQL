package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.entity.*;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.repository.SetRepository;
import dev.braindeck.api.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class TermServiceImpl implements TermService {

    private final TermRepository termRepository;
    private final SetRepository setRepository;

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

//    @Override
//    public void deleteAllBySetId(int setId) {
//        termRepository.deleteBySetId(setId);
//    }
}

