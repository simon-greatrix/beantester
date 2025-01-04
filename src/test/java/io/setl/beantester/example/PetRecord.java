package io.setl.beantester.example;

import jakarta.annotation.Nonnull;

public record PetRecord(@Nonnull @lombok.NonNull String cat, String dog, String fish) {

}
