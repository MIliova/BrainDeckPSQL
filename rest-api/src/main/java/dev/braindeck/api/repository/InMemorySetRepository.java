package dev.braindeck.api.repository;

import dev.braindeck.api.entity.Set;

import java.util.*;
import java.util.stream.IntStream;

public class InMemorySetRepository {

    private final List <Set> sets = Collections.synchronizedList(new LinkedList<>());

    public InMemorySetRepository() {
        IntStream.range(1, 10).forEach(i->{
                sets.add(new Set(i, "Deutsch Einfach gut! B1 L2%d".formatted(i), "Liebe Nachbarn%d".formatted(i), 1, 2, null,null,1));

        });
    }

    public List<Set> findAll() {

        return Collections.unmodifiableList(this.sets);
    }

    public Set save(Set set) {
        set.setId(this.sets.stream()
                .max(Comparator.comparingInt(Set::getId))
                .map(Set::getId)
                .orElse(0) + 1);
        this.sets.add(set);
        return set;
    }

    public Optional<Set> findById(Integer id) {
        return this.sets.stream()
                .filter(setOfTerm -> Objects.equals(id, setOfTerm.getId()))
                .findFirst();
    }

    public void deleteById(Integer id) {
        this.sets.removeIf(setOfTerm -> Objects.equals(id, setOfTerm.getId()));
    }


}
