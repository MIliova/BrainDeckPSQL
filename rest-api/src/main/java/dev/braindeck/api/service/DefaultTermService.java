package dev.braindeck.api.service;


import dev.braindeck.api.entity.NewTerm;
import dev.braindeck.api.entity.Term;
import dev.braindeck.api.repository.TermRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;


@Service
@RequiredArgsConstructor
public class DefaultTermService implements TermService {

    private final TermRepository termRepository;

    @Override
    public List<Term> findTermsBySetId(int setId) {

        return  this.termRepository.findTermsBySetId(setId);
    }

    @Override
    public void createTerms(int setId, List<NewTerm> terms) {
        //ObjectMapper objectMapper = new ObjectMapper();
        //try {
            //List<Map<String, String>> terms = objectMapper.readValue(jsonTerms, new TypeReference<List<Map<String, String>>>(){});
            //for (Map<String, String> term : terms) {
                //this.termRepository.save(new Term(null, setId, term.get("term"), term.get("description")));
            for (NewTerm term : terms) {
                    this.termRepository.save(new Term(null, setId, term.getTerm(), term.getDescription()));
            }

        //} catch (IOException e) {
        //    e.printStackTrace();
       // }
    }



    @Override
    public void updateTerms(List<Term> terms) {
        //ObjectMapper objectMapper = new ObjectMapper();
        //try {
            //List<Map<String, String>> terms = objectMapper.readValue(jsonTerms, new TypeReference<List<Map<String, String>>>() {});
            //for (Map<String, String> term : terms) {
            //this.updateTerm(Integer.parseInt(term.get("id")), term.get("term"), term.get("description"));
        System.out.println(terms);
            for (Term term: terms) {
                this.updateTerm(term.getId(), term.getTerm(), term.getDescription());
            }
        //} catch (IOException e) {
        //    e.printStackTrace();
        //}
    }
    @Override
    public void updateTerm(Integer id, String term, String description) {
        this.termRepository.findById(id)
                .ifPresentOrElse(t -> {
                    t.setTerm(term);
                    t.setDescription(description);
                }, () -> {
                    throw new NoSuchElementException("errors.term.not_found");
                });
    }


    @Override
    public void deleteTermsBySetId(int setId) {
        this.termRepository.deleteTermsBySetId(setId);
    }

}

