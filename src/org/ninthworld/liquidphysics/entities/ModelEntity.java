package org.ninthworld.liquidphysics.entities;

import org.jbox2d.dynamics.Body;
import org.ninthworld.liquidphysics.model.RawModel;

/**
 * Created by NinthWorld on 9/29/2016.
 */
public class ModelEntity extends Entity {

    private RawModel rawModel;
    private Body body;

    public ModelEntity(){
        super();
        this.rawModel = null;
        this.body = null;
    }

    public RawModel getRawModel() {
        return rawModel;
    }

    public void setRawModel(RawModel rawModel) {
        this.rawModel = rawModel;
    }

    public Body getBody() {
        return body;
    }

    public void setBody(Body body) {
        this.body = body;
    }
}
