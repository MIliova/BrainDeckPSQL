package dev.braindeck.api.service;

import dev.braindeck.api.controller.exception.ForbiddenException;
import dev.braindeck.api.controller.payload.DTermPayload.DTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.NewDTermDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftEntity;
import dev.braindeck.api.entity.DTermEntity;
import dev.braindeck.api.repository.DraftTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.regex.Pattern;

@Service
@RequiredArgsConstructor
public class DTermServiceImpl implements DTermService {

    private final DraftTermRepository draftTermRepository;
    private final DraftService draftService;
    private static final int TERM_MAX_LENGTH = 950;

    @Override
    public NewDTermDto autoCreate(int userId, int draftId, DTermPayload payload) {

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
    @Transactional
    public void autoUpdate(int userId, int draftId, int termId, DTermPayload payload) {
        DTermEntity term = draftTermRepository.findById(termId)
                .orElseThrow(() -> new NoSuchElementException("errors.term.not.found"));

        checkOwnership(term, draftId, userId);

        if (!Objects.equals(payload.term(), term.getTerm())) {
            term.setTerm(payload.term());
        }

        if (!Objects.equals(payload.description(), term.getDescription())) {
            term.setDescription(payload.description());
        }

        draftTermRepository.save(term);
    }

    @Override
    public List<TermDto> autoCreate(int userId, int draftId, List<DTermPayload> payloads) {

        DraftEntity draft = draftService.findOrCreateDraftEntityById(userId, draftId);
        List<DTermEntity> list = new ArrayList<>();
        for (DTermPayload p : payloads) {
            list.add(new DTermEntity(null, p.term(), p.description(), draft));
        }
        draftTermRepository.saveAll(list);
        return Mapper.draftTermsToDto(list);
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

//        while (text.length() > 0) {
//            int index = Math.min(text.indexOf(cS), TERM_MAX_LENGTH);
//            int lg = cS.length();
//            if (index == -1) {
//                index = Math.min(text.length(), TERM_MAX_LENGTH);
//                lg = 0;
//            }
//            String t1 = text.substring(0, index);
//            text = text.substring(index + lg);
//
//            if (text.isEmpty()) {
//                list.add(new ImportTermDto(t1, ""));
//                break;
//            }
//
//            index = Math.min(text.indexOf(rS), TERM_MAX_LENGTH);
//            lg = rS.length();
//            if (index == -1) {
//                index = Math.max(TERM_MAX_LENGTH, text.length());
//                lg = 0;
//            }
//            String t2 = text.substring(0, index);
//            text = text.substring(index + lg);
//            list.add(new ImportTermDto(t1, t2));
//        }



//        for (String row : text.split(rS)) {
//            String[] cols = row.split(cS, 2);
//            String first = cols[0];
//            String last = cols.length > 1 ? cols[1] : "";
//
//            while (!first.isEmpty() || !last.isEmpty()) {
//                String t1 = first.length() > TERM_MAX_LENGTH ? first.substring(0, TERM_MAX_LENGTH) : first;
//                String t2 = last.length() > TERM_MAX_LENGTH ? last.substring(0, TERM_MAX_LENGTH) : last;
//                list.add(new ImportTermDto(t1, t2));
//                first = first.length() > TERM_MAX_LENGTH ? first.substring(TERM_MAX_LENGTH) : "";
//                last = last.length() > TERM_MAX_LENGTH ? last.substring(TERM_MAX_LENGTH) : "";
//            }
//        }

        List<ImportTermDto> list = new ArrayList<>();
        rS = normalizeSeparator(rS);
        cS = normalizeSeparator(cS);
        text = text
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        rS = rS
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        cS = cS
                .replace("\r\n", "\n")
                .replace("\r", "\n");

        if (cS.contains(rS)) {

            String[] chunks = text.split(Pattern.quote(cS));

            for (int i = 0; i < chunks.length; i += 2) {

                String first = chunks[i].trim();
                String last = (i + 1 < chunks.length) ? chunks[i + 1].trim() : "";

                process(list, first, last);
            }

        } else {

            for (String row : text.split(Pattern.quote(rS))) {

                if (row.isBlank()) continue;

                String[] parts = row.split(Pattern.quote(cS), 2);

                String first = parts.length > 0 ? parts[0].trim() : "";
                String last  = parts.length > 1 ? parts[1].trim() : "";

                process(list, first, last);
            }
        }
        return list;
    }

    private void process(List<ImportTermDto> list, String first, String last) {

        int max = Math.max(first.length(), last.length());

        for (int i = 0; i < max; i += TERM_MAX_LENGTH) {

            String t1 = i < first.length()
                    ? first.substring(i, Math.min(i + TERM_MAX_LENGTH, first.length()))
                    : "";

            String t2 = i < last.length()
                    ? last.substring(i, Math.min(i + TERM_MAX_LENGTH, last.length()))
                    : "";

            if (t1.isBlank() && t2.isBlank()) continue;

            list.add(new ImportTermDto(t1, t2));
        }
    }

    private String normalizeSeparator(String s) {
        if (s == null) return null;

        return s
                .replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t");
    }
}

