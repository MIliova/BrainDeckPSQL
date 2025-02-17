package dev.braindeck.api.repository;

import dev.braindeck.api.entity.Term;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.IntStream;


public class InMemoryTermRepository {

    private final List<Term> terms = Collections.synchronizedList( new LinkedList<>());

//    public InMemoryTermRepository() {
//        AtomicInteger maxTerm = new AtomicInteger();
//        IntStream.range(1, 10).forEach(
//                j -> {
//                    IntStream.range(1, 10).forEach(i-> terms.add(
//                        new Term(
//                                i + maxTerm.get(),
//                                j,
//                                "beschimpfen\n%d".formatted(i + maxTerm.get()),
//                                "ругать, оскорблять\n%d".formatted(j),
//                                1)
//                    ));
//                    maxTerm.addAndGet(10);
//                });
//        //System.out.println(this.terms);
//
//    }

//    public List<Term> findBySetId(Integer setId) {
//        return this.terms.stream()
//                .filter(set-> Objects.equals(setId, set.getSetId()))
//                .collect(Collectors.toList());
//    }

//    public void save(Term term) {
//        term.setId(this.terms.stream()
//                .max(Comparator.comparingInt(Term::getId))
//                .map(Term::getId)
//                .orElse(0) + 1);
//        this.terms.add(term);
//    }
//
//    public Optional<Term> findById(Integer id) {
//        return this.terms.stream()
//                .filter(set->Objects.equals(id,set.getId()))
//                .findFirst();
//    }
//
//    public void deleteBySetId(Integer setId) {
//        this.terms.removeIf(term -> Objects.equals(setId, term.getSetId()));
//    }
}
