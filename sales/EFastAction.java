package ru.kazachkov.florist.sales;


import lombok.Getter;
import ru.kazachkov.florist.R;

public enum EFastAction {

    SELL(1, R.string.sell, R.drawable.ic_donep),

    RETURN_PREPAYMENT(3, R.string.return_prepayment, R.drawable.ic_account),

    SURCHARGE(5, R.string.surcharge, R.drawable.ic_account),

    RETURN_PAYMENT(7, R.string.return_payment, R.drawable.ic_account),

    EDIT_COMPOSITION(10, R.string.edit_composition, R.drawable.ic_create),

    CHANGE_PROVIDER(20, R.string.change_provider, R.drawable.ic_face),

    CHANGE_RECIPIENT(25, R.string.change_recipient, R.drawable.ic_face),

    EXTRA_INFORMATION(30, R.string.extra_info, R.drawable.ic_i),

    COMMENT(33, R.string.will_comment, R.drawable.ic_com),

    COPY(35, R.string.copy_or_dublicate, R.drawable.ic_content),

    MOVE(40, R.string.will_move, R.drawable.ic_compare),

    REMOVE(50, R.string.remove, R.drawable.ic_dp),
    NONE(-1, 0, 0);


    /**
     * 1. Продать
     * 3. Вернуть предоплату
     * 5. Доплатить
     * 7. Вернуть оплату
     * 10. Отредактировать состав
     * 20. Изменить поставщика
     * 25. Изменить получателя
     * 30. Внести доп. информацию
     * 33. Прокомментировать
     * 35. Дублировать (или скопировать)
     * 40. Переместить
     * 50. Удалить
     * 1. Sell
     *        3. Return the prepayment
     *        5. Surcharge
     *        7. Return payment
     *        10. Edit the composition
     *        20. Change provider
     *        25. Change the recipient
     *        30. Make extra. information
     *        33. Comment
     *        35. Duplicate (or copy)
     *        40. Move
     *        50. Remove.
     **/


    @Getter
    private final int id;
    @Getter
    private final int titleResourceId;
    @Getter
    private final int iconResourceId;

    EFastAction(int id, int titleResourceId, int iconResourceId) {
        this.id = id;
        this.titleResourceId = titleResourceId;
        this.iconResourceId = iconResourceId;
    }

    public static EFastAction getById(int i) {
        for (EFastAction action : values()) {
            if (action.id == i) return action;
        }
        throw new NullPointerException("Enum not have OrderAction with id: " + i);
    }

}
