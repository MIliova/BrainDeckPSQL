package dev.braindeck.api.service;

import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.Set;
import dev.braindeck.api.entity.Term;
import dev.braindeck.api.entity.User;
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
    public Set createSet(String title, String description, int termLanguageId, int descriptionLanguageId, User user, List<NewTerm> listTerms) {
        Set set =  this.setRepository.save(new Set(null, title, description, termLanguageId, descriptionLanguageId, user, null));

        this.termService.createTerms(set, listTerms);
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
    public void updateSet(int setId, String title, String description, int termLanguageId, int descriptionLanguageId, User user, List<Term> terms) {
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
