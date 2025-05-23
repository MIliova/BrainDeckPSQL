package dev.braindeck.api.service;

import dev.braindeck.api.controller.payload.NewTermPayload;
import dev.braindeck.api.controller.payload.UpdateTermPayload;
import dev.braindeck.api.entity.SetEntity;
import dev.braindeck.api.dto.TermDto;
import dev.braindeck.api.entity.TermEntity;
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
    public List<TermDto> findTermsBySetId(int setId) {
        return Mapper.termsToDto(this.termRepository.findAllBySetId(setId));
    }

    @Override
    public void createTerms(SetEntity set, List<NewTermPayload> terms) {
            for (NewTermPayload term : terms) {
                    this.termRepository.save(new TermEntity(null, term.getTerm(), term.getDescription(), set));
            }
    }

    @Override
    public void updateTerms(List<UpdateTermPayload> terms) {
        //ObjectMapper objectMapper = new ObjectMapper();
        //try {
            //List<Map<String, String>> terms = objectMapper.readValue(jsonTerms, new TypeReference<List<Map<String, String>>>() {});
            //for (Map<String, String> term : terms) {
            //this.updateTerm(Integer.parseInt(term.get("id")), term.get("term"), term.get("description"));
        System.out.println(terms);
            for (UpdateTermPayload term: terms) {
                this.updateTerm(term.id(), term.term(), term.description());
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
                    this.termRepository.save(t);
                }, () -> {
                    throw new NoSuchElementException("errors.term.not_found");
                });
    }


    @Override
    public void deleteTermsBySetId(int setId) {
        this.termRepository.deleteBySetId(setId);
    }

    @Override
    public void deleteTermById(int id) {
        this.termRepository.deleteById(id);
    }



}

