package dev.braindeck.web.service;

import dev.braindeck.web.controller.payload.SetViewModel;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class SetViewModelBuilder<T> {

    public SetViewModel<T> build(
            int id,
            String title,
            String description,
            Integer termLanguageId,
            Integer descriptionLanguageId,
            boolean isDraft,

            List<T> terms
    ) {
        return build(
                id,
                title,
                description,
                termLanguageId,
                descriptionLanguageId,
                isDraft,
                terms, false, null);
    }
    public SetViewModel<T> build(
            int id,
            String title,
            String description,
            Integer termLanguageId,
            Integer descriptionLanguageId,
            boolean isDraft,

            List<T> terms,
            boolean hasTermsError,
            Map<String, String> error
    ) {

        SetViewModel<T> vm = new SetViewModel<T>();
        vm.setId(id);
        vm.setTitle(title);
        vm.setDescription(description);
        vm.setTermLanguageId(termLanguageId);
        vm.setDescriptionLanguageId(descriptionLanguageId);
        vm.setDraft(isDraft);

        vm.setTerms(terms);
        vm.setHasTermsErrors(hasTermsError);
        vm.setError(error);

        return vm;
    }
}
