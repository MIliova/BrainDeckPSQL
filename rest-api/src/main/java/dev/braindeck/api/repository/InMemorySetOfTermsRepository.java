package dev.braindeck.api.repository;

import dev.braindeck.api.entity.SetOfTerms;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.stream.IntStream;

public class InMemorySetOfTermsRepository {

    private final List <SetOfTerms> setOfTerms = Collections.synchronizedList(new LinkedList<>());

    public InMemorySetOfTermsRepository() {
        IntStream.range(1, 10).forEach(i->{
                setOfTerms.add(new SetOfTerms(i, "Deutsch Einfach gut! B1 L2%d".formatted(i), "Liebe Nachbarn%d".formatted(i), 1, 2, null,1,1));

        });
    }

//    @Override
//    public List<SetOfTerms> findAll() {
//
//        return Collections.unmodifiableList(this.setOfTerms);
//    }
//
//    @Override
//    public SetOfTerms save(SetOfTerms setOfTerms) {
//        setOfTerms.setId(this.setOfTerms.stream()
//                .max(Comparator.comparingInt(SetOfTerms::getId))
//                .map(SetOfTerms::getId)
//                .orElse(0) + 1);
//        this.setOfTerms.add(setOfTerms);
//        return setOfTerms;
//    }
//
//    @Override
//    public Optional<SetOfTerms> findById(Integer id) {
//        return this.setOfTerms.stream()
//                .filter(setOfTerm -> Objects.equals(id, setOfTerm.getId()))
//                .findFirst();
//    }
//
//    @Override
//    public void deleteById(Integer id) {
//        this.setOfTerms.removeIf(setOfTerm -> Objects.equals(id, setOfTerm.getId()));
//    }


}
