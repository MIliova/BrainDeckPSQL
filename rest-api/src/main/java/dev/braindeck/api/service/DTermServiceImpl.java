package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.entity.DraftEntity;
import dev.braindeck.api.entity.DTermEntity;
import dev.braindeck.api.repository.DraftTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DTermServiceImpl implements DTermService {

    private final DraftTermRepository draftTermRepository;
    private final DraftService draftService;
    private static final int TERM_MAX_LENGTH = 950;

    @Override
    public NewDTermDto create(int userId, int draftId, DTermPayload payload) {
        DraftEntity draft = draftService.findOrCreateDraftEntityById(userId, draftId);
        DTermEntity entity = new DTermEntity(
                null,
                payload.term(),
                payload.description(),
                draft
        );
        draftTermRepository.save(entity);
        return Mapper.newDTermToDto(entity);
    }
    @Override
    public List<NewDTermDto> create(int userId, int draftId, List<DTermPayload> payloads) {
        DraftEntity draft = draftService.findOrCreateDraftEntityById(userId, draftId);
        List<DTermEntity> list = new ArrayList<>();
        for (DTermPayload p : payloads) {
            list.add(new DTermEntity(null, p.term(), p.description(), draft));
        }
        draftTermRepository.saveAll(list);
        return Mapper.newDTermToDto(list);
    }

    @Override
    @Transactional
    public void update(int termId, int draftId, int currentUserId, DTermPayload payload) {
        DTermEntity term = draftTermRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("errors.term.not.found"));

        checkOwnership(term, draftId, currentUserId);

        if (!Objects.equals(payload.term(), term.getTerm())) {
            term.setTerm(payload.term());
        }

        if (!Objects.equals(payload.description(), term.getDescription())) {
            term.setDescription(payload.description());
        }

        draftTermRepository.save(term);
    }

    @Override
    @Transactional
    public void delete(int termId, int draftId, int currentUserId) {
        DTermEntity term = draftTermRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("errors.term.not.found"));

        checkOwnership(term, draftId, currentUserId);

        draftTermRepository.delete(term);
    }

    private void checkOwnership(DTermEntity term, int draftId, int userId) {
        if (!term.getDraft().getId().equals(draftId)) {
            throw new ForbiddenException("errors.term.not.belong.draft");
        }
        if (!term.getDraft().getUser().getId().equals(userId)) {
            throw new ForbiddenException("errors.term.not.belong.user");
        }
    }

    @Override
    public List<ImportTermDto> prepareImport(String text, String colSeparator, String rowSeparator, String colCustom, String rowCustom) {
        String rS = switch (rowSeparator) {
            case "newline" -> "\n";
            case "semicolon" -> ";";
            default -> rowCustom;
        };
        String cS = switch (colSeparator) {
            case "tab" -> "\t";
            case "comma" -> ",";
            default -> colCustom;
        };

        List<ImportTermDto> list = new ArrayList<>();
        for (String row : text.split(rS)) {
            String[] cols = row.split(cS, 2);
            String first = cols[0];
            String last = cols.length > 1 ? cols[1] : "";

            while (!first.isEmpty() || !last.isEmpty()) {
                String t1 = first.length() > TERM_MAX_LENGTH ? first.substring(0, TERM_MAX_LENGTH) : first;
                String t2 = last.length() > TERM_MAX_LENGTH ? last.substring(0, TERM_MAX_LENGTH) : last;
                list.add(new ImportTermDto(t1, t2));
                first = first.length() > TERM_MAX_LENGTH ? first.substring(TERM_MAX_LENGTH) : "";
                last = last.length() > TERM_MAX_LENGTH ? last.substring(TERM_MAX_LENGTH) : "";
            }
        }
        return list;
    }

}

