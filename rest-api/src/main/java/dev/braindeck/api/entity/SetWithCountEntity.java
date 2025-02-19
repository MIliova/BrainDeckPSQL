package dev.braindeck.api.entity;

import lombok.Getter;

@Getter
public class SetWithCountEntity {

    private final SetEntity set;
    private final Long termCount;

    public SetWithCountEntity(SetEntity set, Long termCount) {
        this.set = set;
        this.termCount = termCount;
    }

}
