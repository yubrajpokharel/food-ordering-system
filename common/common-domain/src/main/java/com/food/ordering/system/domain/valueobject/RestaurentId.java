package com.food.ordering.system.domain.valueobject;

import java.util.UUID;

public class RestaurentId extends BaseId<UUID> {
    public RestaurentId(UUID value) {
        super(value);
    }
}
