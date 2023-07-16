package ru.pin120.transystem.sendModels;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import ru.pin120.transystem.models.Cargo;
import ru.pin120.transystem.models.Warehouse;

import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CargoWithWarehouses {

    private Cargo cargo;
    private List<Warehouse> warehouses;
}
