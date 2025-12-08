package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.NewDTermPayload;
import dev.braindeck.api.controller.payload.UpdateDTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftSetEntity;
import dev.braindeck.api.entity.DTermEntity;
import dev.braindeck.api.entity.UserEntity;
import dev.braindeck.api.repository.DraftTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

@Service
@RequiredArgsConstructor
public class DTermServiceImpl implements DTermService {

    private final DraftTermRepository draftTermRepository;
    private final UserService userService;

    @Override
    public TermDto create(DraftSetEntity draft, NewDTermPayload payload) {
        DTermEntity entity = new DTermEntity(
                null,
                payload.getTerm(),
                payload.getDescription(),
                draft
        );
        draftTermRepository.save(entity);
        return Mapper.draftTermToDto(entity);
    }
    @Override
    public List<TermDto> create(DraftSetEntity draft, List<NewDTermPayload> payloads) {
        List<DTermEntity> list = new ArrayList<>();
        for (NewDTermPayload p : payloads) {
            list.add(new DTermEntity(null, p.getTerm(), p.getDescription(), draft));
        }
        draftTermRepository.saveAll(list);
        return Mapper.draftTermsToDto(list);
    }

    @Override
    @Transactional
    public void update(int draftId, int termId, UpdateDTermPayload payload) {
        DTermEntity term = draftTermRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("errors.term.not_found"));

        UserEntity user = userService.getCurrentUser();
        if (!term.getDraft().getId().equals(draftId)) {
            throw new ForbiddenException("errors.draft.forbidden");
        }

        if (!term.getDraft().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("errors.term.forbidden");
        }

        if (!Objects.equals(payload.term(), term.getTerm())) {
            term.setTerm(payload.term());
        }

        if (!Objects.equals(payload.description(), term.getDescription())) {
            term.setDescription(payload.description());
        }

        draftTermRepository.save(term);
    }

    @Override
    public void delete(int draftId, int termId) {
        DTermEntity term = draftTermRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("errors.term.not_found"));

        if (!term.getDraft().getId().equals(draftId)) {
            throw new ForbiddenException("errors.draft.forbidden");
        }

        UserEntity user = userService.getCurrentUser();
        if (!term.getDraft().getUser().getId().equals(user.getId())) {
            throw new ForbiddenException("errors.term.forbidden");
        }

        draftTermRepository.delete(term);
    }

    @Override
    public void deleteByDraftId(int draftId) {
        draftTermRepository.deleteByDraftId(draftId);
    }


    @Override
    public List<TermDto> findById(int draftId) {
        return Mapper.draftTermsToDto(this.draftTermRepository.findAllByDraftId(draftId));
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
        String [] rows = text.split(rS);
        for (String row : rows) {
            List<String> cols = new ArrayList<>(Arrays.asList(row.split(cS,2)));
            String first = cols.getFirst();
            while (first.length() > 950) {
                list.add(new ImportTermDto(first.substring(0,950), ""));
                first = first.substring(950);
            }
            String last = (cols.size() > 1 ? cols.getLast(): "");
            while (last.length() > 950) {
                list.add(new ImportTermDto(first, last.substring(0,950)));
                last = last.substring(950);
                first = "";
            }
            list.add(new ImportTermDto(first, last));
        }
        return list;
    }

}

