package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.dto.ImportTermDto;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.DraftSetEntity;
import dev.braindeck.api.entity.DraftTermEntity;
import dev.braindeck.api.repository.DraftTermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DefaultDraftTermService implements DraftTermService {

    private final DraftTermRepository draftTermRepository;

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

    @Override
    public void createTerms(DraftSetEntity draft, List<NewTermPayload> terms) {
        for (NewTermPayload term : terms) {
            this.draftTermRepository.save(new DraftTermEntity(null, term.getTerm(), term.getDescription(), draft));
        }
    }

    @Override
    public List<TermDto> findTermsByDraftId(int draftId) {
        return Mapper.draftTermsToDto(this.draftTermRepository.findAllByDraftId(draftId));
    }

    @Override
    public void deleteByDraftId(int id) {

        this.draftTermRepository.deleteByDraftId(id);
    }


}

