package dev.braindeck.api.service;

import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.Set;
import dev.braindeck.api.entity.Term;
import dev.braindeck.api.repository.SetRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultSetService implements SetService {

    private final SetRepository setRepository;
    private final TermService termService;


    @Override
    public List<Set> findAllByUserId(int userId) {
        return this.setRepository.findAllByUserId(userId);

    }

    @Override
    public List<Set> findAllByFolderId(int folderId) {
        return this.setRepository.findAllByFolderId(folderId);

    }

    @Override
    public Set createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, Integer userId, List<NewTerm> jsonTerms) {
        Set set =  this.setRepository.save(new Set(null, title, description, termLanguageId, descriptionLanguageId, null, null, 1 ));

        this.termService.createTerms(set.getId(), jsonTerms);
        List<Term> terms = this.termService.findTermsBySetId(set.getId());
        set.setTerms(terms);
        return set;
    }


    @Override
    public Set findSetById(int setId) {
        Set set =  this.setRepository.findById(setId)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not_found"));
        List<Term> terms = this.termService.findTermsBySetId(set.getId());
        set.setTerms(terms);
        return set;
    }

    @Override
    public void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<Term> terms) {
        this.setRepository.findById(setId)
                .ifPresentOrElse(set -> {
                    set.setTitle(title);
                    set.setDescription(description);
                    set.setTermLanguageId(termLanguageId);
                    set.setDescriptionLanguageId(descriptionLanguageId);
                }, () -> {
                    throw new NoSuchElementException("errors.set.not_found");
                });
        this.termService.updateTerms(terms);
    }
    @Override
    public void deleteSet(int setId) {
        this.setRepository.deleteById(setId);
        this.termService.deleteTermsBySetId(setId);
    }
}
