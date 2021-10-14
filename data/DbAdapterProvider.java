package ru.kazachkov.florist.data;


import lombok.AllArgsConstructor;
import lombok.Getter;
import ru.kazachkov.florist.interfaces.DbAdapter;

@AllArgsConstructor(staticName = "of")
public class DbAdapterProvider<M> {
    @Getter
    private final DbAdapter adapter;
    @Getter
    private final M model;
}
