package com.pippsford.beantester.sample.beans;

import jakarta.annotation.Nonnull;

public record PetRecord(@Nonnull @lombok.NonNull String cat, String dog, String fish) {

}
