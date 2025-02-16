package dev.braindeck.api.service;

import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.SetOfTerms;
import dev.braindeck.api.entity.Term;
import dev.braindeck.api.repository.SetOfTermsRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;

@Service
@RequiredArgsConstructor
public class DefaultSetOfTermsService implements SetOfTermsService {

    private final SetOfTermsRepository setOfTermsRepository;
    private final TermService termService;


    @Override
    public List<SetOfTerms> findAllSets() {
        return this.setOfTermsRepository.findAll();

    }
    @Override
    public SetOfTerms createSet(String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<NewTerm> jsonTerms) {
        SetOfTerms setOfTerms =  this.setOfTermsRepository.save(new SetOfTerms(null, title, description, termLanguageId, descriptionLanguageId, null));

        this.termService.createTerms(setOfTerms.getId(), jsonTerms);
        List<Term> terms = this.termService.findTermsBySetId(setOfTerms.getId());
        setOfTerms.setTerms(terms);
        return setOfTerms;
    }


    @Override
    public SetOfTerms findSetById(int setId) {
        SetOfTerms setOfTerms =  this.setOfTermsRepository.findById(setId)
                .orElseThrow(()-> new NoSuchElementException("errors.set.not_found"));
        List<Term> terms = this.termService.findTermsBySetId(setOfTerms.getId());
        setOfTerms.setTerms(terms);
        return setOfTerms;
    }

    @Override
    public void updateSet(int setId, String title, String description, Integer termLanguageId, Integer descriptionLanguageId, List<Term> terms) {
        this.setOfTermsRepository.findById(setId)
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
        this.setOfTermsRepository.deleteById(setId);
        this.termService.deleteTermsBySetId(setId);
    }
}
