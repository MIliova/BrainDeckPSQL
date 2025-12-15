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

    public NewDTermDto newDTermToDto(DTermEntity entity) {
        if (entity == null) {
            return null;
        }
        return new NewDTermDto(entity.getDraft().getId(), entity.getId(), entity.getTerm(), entity.getDescription());
    }

    public List <NewDTermDto> newDTermToDto(List<DTermEntity> listEntity) {
        if (listEntity == null) {
            return null;
        }
        List<NewDTermDto> lDto = new ArrayList<>();
        for (DTermEntity entity : listEntity) {
            lDto.add(new NewDTermDto(entity.getDraft().getId(), entity.getId(), entity.getTerm(), entity.getDescription()));
        }
        return lDto;
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

    public SetDto setToDto(SetEntity entity, List<TermDto> terms) {
        if (entity == null) {
            return null;
        }
        return new SetDto(
                entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(),
                entity.getDescriptionLanguageId(), userToDto(entity.getUser()), terms
        );
    }





    public UserDto userToDto(UserEntity user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName());
    }

    public DraftDto DraftSetToDto(DraftEntity entity) {
        if (entity == null) {
            return null;
        }
        return new DraftDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(), entity.getDescriptionLanguageId(),
                userToDto(entity.getUser()),draftTermsToDto(entity.getTerms()));
    }

    public DraftDto DraftSetToDto(DraftEntity entity, List<TermDto> terms) {
        if (entity == null) {
            return null;
        }
        return new DraftDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(), entity.getDescriptionLanguageId(),
                userToDto(entity.getUser()),terms);
    }
    public NewDraftDto NewDraftToDto(DraftEntity entity) {
        if (entity == null) {
            return null;
        }
        return new NewDraftDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(), entity.getDescriptionLanguageId());
    }




}
