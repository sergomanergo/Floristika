package ru.kazachkov.florist.model.md;

import ru.kazachkov.florist.interfaces.IOrderGlance;
import ru.kazachkov.florist.viewmodel.vm.SaleVM;

public interface SaleModel extends SaleVM {
    IOrderGlance getOrderGlance();
}
