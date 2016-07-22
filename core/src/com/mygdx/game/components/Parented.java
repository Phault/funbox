package com.mygdx.game.components;

import com.artemis.Component;
import com.artemis.annotations.EntityId;

/**
 * Created by Casper on 19-07-2016.
 */
public class Parented extends Component {
    @EntityId public int Parent = -1;
}
