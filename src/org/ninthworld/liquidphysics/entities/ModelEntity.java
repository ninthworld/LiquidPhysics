package org.ninthworld.liquidphysics.entities;

import org.ninthworld.liquidphysics.model.RawModel;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class ModelEntity extends Entity {

    private RawModel rawModel;

    public ModelEntity(){
        super();
        this.rawModel = null;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public void setRawModel(RawModel rawModel) {
        this.rawModel = rawModel;
    }
}
