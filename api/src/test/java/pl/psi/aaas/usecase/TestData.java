package pl.psi.aaas.usecase;

import java.util.*;

public class TestData {
    public static final CalculationDefinitionIf ValidDefinition;

    static {
        Map<String, Long> timeSeriesIdsIn = new HashMap<>();
        timeSeriesIdsIn.put("A", 1L);
        timeSeriesIdsIn.put("B", 2L);
        timeSeriesIdsIn.put("C", 3L);

        Map<String, Long> timeSeriesIdsOut = new HashMap<>();
        timeSeriesIdsOut.put("Y", 101L);
        timeSeriesIdsOut.put("Z", 102L);

        ValidDefinition = new CalculationDefinition(
                "validScriptPath"
        );
    }

    public static final Double[] TS1 = new Double[]{1.0, 1.0, 1.0, 1.0};
    public static final Double[] TS2 = new Double[]{2.0, 2.0, 2.0, 2.0};
    public static final Double[] TS3 = new Double[]{3.0, 3.0, 3.0, 3.0};

    public static final Double[] TS1Res = new Double[]{-1.0, -1.0, -1.0, -1.0};
    public static final Map.Entry<String, Double[]> TS1ResM = new AbstractMap.SimpleEntry<>("Y", TS1Res);
    public static final Double[] TS2Res = new Double[]{-2.0, -2.0, -2.0, -2.0};
    public static final Map.Entry<String, Double[]> TS2ResM = new AbstractMap.SimpleEntry<>("Z", TS2Res);

    public static final CalculationDefinitionWithValues ValidDefinitionWithTS;

    static {
        List<Map.Entry<String, Double[]>> timeSeriesValuesIn = Arrays.asList(
                new AbstractMap.SimpleEntry<>("A", TS1),
                new AbstractMap.SimpleEntry<>("B", TS2),
                new AbstractMap.SimpleEntry<>("C", TS3)
        );
        ValidDefinitionWithTS = new CalculationDefinitionWithValues(
                ValidDefinition,
                timeSeriesValuesIn
        );
    }
}
