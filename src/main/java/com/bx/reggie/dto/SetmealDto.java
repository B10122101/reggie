package com.bx.reggie.dto;

import com.bx.reggie.entity.Setmeal;
import com.bx.reggie.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
@SuppressWarnings({"all"})
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
