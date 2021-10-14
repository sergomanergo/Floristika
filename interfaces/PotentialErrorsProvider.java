package ru.kazachkov.florist.interfaces;

import ru.kazachkov.florist.api.model.AuthResult;

/**
 * Created by ishmukhametov on 21.04.16.
 */
public interface PotentialErrorsProvider {
    AuthResult getAuthResult();
}
