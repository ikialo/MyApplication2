package com.metrolinq.isaac.myapplication.MapCalcClasses;

import java.util.List;

public interface DirectionFinderListener {
    void onDirectionFinderStart();
    void onDirectionFinderSuccess(List<Route> route);
}
