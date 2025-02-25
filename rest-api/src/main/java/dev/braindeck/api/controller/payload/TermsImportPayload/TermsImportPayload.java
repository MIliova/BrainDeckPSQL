package dev.braindeck.api.controller.payload.TermsImportPayload;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

@ImportValidation
public record TermsImportPayload(
        @NotBlank (message = "Скопируйте и вставьте ваши данные для импорта сюда")
        //@Size(min = 1, message = "")
        String text,

        @NotBlank (message = "Укажите разделитель между Термином и Определением")
        String colSeparator,

        @NotBlank (message = "Укажите разделитель между различными Терминами")
        String rowSeparator,

        String colCustom,

        String rowCustom
) {
//        private static final Set<String> VALID_COL_OPTIONS = Set.of("tab", "comma", "custom");
//        private static final Set<String> VALID_ROW_OPTIONS = Set.of("newline", "semicolon", "custom");
//
//        public TestImportPayload {
//
//                System.out.println("colSeparator"+colSeparator);
//                System.out.println("rowSeparator"+rowSeparator);
//                System.out.println("colCustom"+colCustom);
//                System.out.println("rowCustom"+rowCustom);
//
//
//
//
//                if (!VALID_COL_OPTIONS.contains(colSeparator)) {
//                        throw new IllegalArgumentException("Некорректное значение для Between term and definition: " + colSeparator);
//                }
//
//                if (!VALID_ROW_OPTIONS.contains(rowSeparator)) {
//                        throw new IllegalArgumentException("Некорректное значение для Between cards: " + rowSeparator);
//                }
//
////                if (collSeparator == null || collSeparator.isEmpty()) {
////                        throw new IllegalArgumentException("collSeparator не может быть пустым.");
////                }
////
////                if (rowSeparator == null || rowSeparator.isEmpty()) {
////                        throw new IllegalArgumentException("rowSeparator не может быть пустым.");
////                }
//                if (Objects.equals(colSeparator, "custom") && colCustom == null) {
//                        throw new IllegalArgumentException("Between term and definition не может быть пустым.");
//                }
//                if (Objects.equals(rowSeparator, "custom") && rowCustom == null) {
//                        throw new IllegalArgumentException("Between cards не может быть пустым.");
//                }
//        }
}
