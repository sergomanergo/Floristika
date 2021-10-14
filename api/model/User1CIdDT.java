package ru.kazachkov.florist.api.model;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.kazachkov.florist.interfaces.SpinnerText;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class User1CIdDT implements SpinnerText {
    String userName;
    String user1CId;
    String storage1CId;

    @Override
    public String getSpinnerText() {
        return userName;
    }
}
