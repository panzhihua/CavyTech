package com.cavytech.wear2.cavylifeband;

/**
 * Created by blacksmith on 2016/5/3.
 */
public class PedometerData {
    public static final int BeforeYesterday = 0;
    public static final int Yesterday = 1;
    public static final int Today = 2;
    public int SearchDay;
    public int Time, Tilts, Steps;

    public PedometerData()
    {
        SearchDay = 0;
        Time = 0;
        Tilts = 0;
        Steps = 0;
    }
}
