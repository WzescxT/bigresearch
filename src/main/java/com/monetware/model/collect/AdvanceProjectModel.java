package com.monetware.model.collect;

import java.util.ArrayList;

public class AdvanceProjectModel {

    private AdvanceProjectEntity advanceProjectEntity;

    private ArrayList<AdvanceTaskEntity> advanceTaskEntities;

    public AdvanceProjectModel(AdvanceProjectEntity advanceProjectEntity, ArrayList<AdvanceTaskEntity> advanceTaskEntities){

        this.advanceProjectEntity = advanceProjectEntity;
        this.advanceTaskEntities = advanceTaskEntities;

    }


    public ArrayList<AdvanceTaskEntity> getAdvanceTaskEntities() {
        return advanceTaskEntities;
    }

    public void setAdvanceTaskEntities(ArrayList<AdvanceTaskEntity> advanceTaskEntities) {
        this.advanceTaskEntities = advanceTaskEntities;
    }

    public AdvanceProjectEntity getAdvanceProjectEntity() {
        return advanceProjectEntity;
    }

    public void setAdvanceProjectEntity(AdvanceProjectEntity advanceProjectEntity) {
        this.advanceProjectEntity = advanceProjectEntity;
    }
}
