package dev.braindeck.api.service;

import dev.braindeck.api.dto.*;
import dev.braindeck.api.entity.*;
import lombok.experimental.UtilityClass;

import java.util.ArrayList;
import java.util.List;

@UtilityClass
public class Mapper {

    public List<TermDto> termsToDto(List<TermEntity> listEntity) {
        if (listEntity == null) {
            return null;
        }
        List<TermDto> lDto = new ArrayList<>();
        for (TermEntity entity : listEntity) {
            lDto.add(new TermDto(entity.getId(), entity.getTerm(), entity.getDescription()));
        }
        return lDto;
    }

    public List<TermDto> draftTermsToDto(List<DTermEntity> listEntity) {
        if (listEntity == null) {
            return null;
        }
        List<TermDto> lDto = new ArrayList<>();
        for (DTermEntity entity : listEntity) {
            lDto.add(new TermDto(entity.getId(), entity.getTerm(), entity.getDescription()));
        }
        return lDto;
    }

    public TermDto draftTermToDto(DTermEntity entity) {
        if (entity == null) {
            return null;
        }
        return new TermDto(entity.getId(), entity.getTerm(), entity.getDescription());
    }

    public SetDto setToDto(SetEntity entity) {
        if (entity == null) {
            return null;
        }
        return new SetDto(
                entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(),
                entity.getDescriptionLanguageId(), userToDto(entity.getUser()), termsToDto(entity.getTerms())
        );
    }

    public SetDto setToDto(SetEntity entity, List<TermDto> terms) {
        if (entity == null) {
            return null;
        }
        return new SetDto(
                entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(),
                entity.getDescriptionLanguageId(), userToDto(entity.getUser()), terms
        );
    }


    public List <SetDto> setsToDto(List<SetEntity> listEntity) {
        if (listEntity == null) {
            return null;
        }
        List<SetDto> lDto = new ArrayList<>();
        for (SetEntity entity : listEntity) {
            lDto.add(new SetDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(),
                    entity.getDescriptionLanguageId(), userToDto(entity.getUser()),null));
        }
        return lDto;
    }


    public UserDto userToDto(UserEntity user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName());
    }

    public DraftSetDto DraftSetToDto(DraftSetEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DraftSetDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(), entity.getDescriptionLanguageId(),
                userToDto(entity.getUser()),draftTermsToDto(entity.getTerms()));
    }

    public DraftSetDto DraftSetToDto(DraftSetEntity entity, List<TermDto> terms) {
        if (entity == null) {
            return null;
        }
        return new DraftSetDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(), entity.getDescriptionLanguageId(),
                userToDto(entity.getUser()),terms);
    }
//    public List<SetWithCountDto> setsWithCountWithUserToDto(List<Tuple> listTuple, UserEntity user) {
//        if (listTuple == null) {
//            return null;
//        }
//        List<SetWithCountEntity> listEntity = listTuple
//                .stream()
//                .map(tuple -> new SetWithCountEntity(tuple.get(0, SetEntity.class), tuple.get(1, Long.class)))
//                .toList();
//
//        List<SetWithCountDto> lDto = new ArrayList<>();
//        for (SetWithCountEntity entity : listEntity) {
//            lDto.add(new SetWithCountDto(
//                    entity.getSet().getId(), entity.getSet().getTitle(),
//                    entity.getSet().getDescription(),
//                    entity.getSet().getTermLanguageId(),
//                    entity.getSet().getDescriptionLanguageId(), userToDto(user),
//                    entity.getTermCount()
//            ));
//        }
//        return lDto;
//
//    }

    public DraftDto DraftToDto(DraftEntity entity, List<TermDto> terms) {
        if (entity == null) {
            return null;
        }
        return new DraftDto(entity.getId(), entity.getUser().getId(),terms);
    }


}
