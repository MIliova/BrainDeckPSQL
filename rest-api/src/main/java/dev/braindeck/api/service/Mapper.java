package dev.braindeck.api.service;

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
    public SetDto setToDto(SetEntity entity, List<TermDto> terms) {
        if (entity == null) {
            return null;
        }
        return new SetDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(), entity.getDescriptionLanguageId(), entity.getUser().getId(),terms);
    }
    public List <SetDto> setsToDto(List<SetEntity> listEntity) {
        if (listEntity == null) {
            return null;
        }
        List<SetDto> lDto = new ArrayList<>();
        for (SetEntity entity : listEntity) {
            lDto.add(new SetDto(entity.getId(), entity.getTitle(), entity.getDescription(), entity.getTermLanguageId(), entity.getDescriptionLanguageId(), entity.getUser().getId(),null));
        }
        return lDto;
    }
    public UserDto userToDto(User user) {
        if (user == null) {
            return null;
        }
        return new UserDto(user.getId(), user.getName(), user.getEmail(), user.getPassword());
    }
}
