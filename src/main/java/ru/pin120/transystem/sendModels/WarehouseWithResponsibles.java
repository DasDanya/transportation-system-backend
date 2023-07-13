package ru.pin120.transystem.sendModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pin120.transystem.models.Responsible;
import ru.pin120.transystem.models.Warehouse;

import java.util.List;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class WarehouseWithResponsibles {

    private Warehouse warehouse;
    private List<Responsible> responsibles;
}
