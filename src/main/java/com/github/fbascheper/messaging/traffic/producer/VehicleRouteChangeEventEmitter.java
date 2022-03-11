package com.github.fbascheper.messaging.traffic.producer;

import com.github.fbascheper.messaging.common.JacksonMapping;
import com.github.fbascheper.messaging.data.retriever.SensorDataRetriever;
import com.github.fbascheper.messaging.domain.GeographicCoordinates;
import com.github.fbascheper.messaging.domain.TrafficSensor;
import com.github.fbascheper.messaging.domain.VehicleClass;
import com.github.fbascheper.messaging.domain.VehicleRouteChangeEvent;
import io.smallrye.mutiny.Multi;
import io.smallrye.reactive.messaging.kafka.Record;
import org.eclipse.microprofile.reactive.messaging.Outgoing;
import org.slf4j.Logger;

import javax.annotation.PostConstruct;
import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.TreeSet;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;

import static org.slf4j.LoggerFactory.getLogger;

/**
 * A stub class that emits "vehicle route change" events.
 *
 * @author Frederieke Scheper
 * @since 03-11-2021
 */
@ApplicationScoped
public class VehicleRouteChangeEventEmitter {

    private static final Logger LOGGER = getLogger(VehicleRouteChangeEventEmitter.class);

    private static final VehicleRouteChangeEvent MOVING_VEHICLE_1_TEMPLATE = new VehicleRouteChangeEvent("LIC-ENSE-PLATE-1", VehicleClass.CAR, new ArrayList<>());
    private static final VehicleRouteChangeEvent MOVING_VEHICLE_2_TEMPLATE = new VehicleRouteChangeEvent("LIC-ENSE-PLATE-2", VehicleClass.MINIVAN, new ArrayList<>());
    private static final VehicleRouteChangeEvent MOVING_VEHICLE_3_TEMPLATE = new VehicleRouteChangeEvent("LIC-ENSE-PLATE-3", VehicleClass.CAR, new ArrayList<>());

    private final SensorDataRetriever sensorDataRetriever;
    private final JacksonMapping jacksonMapping;

    private final List<Integer> potentialHotspotIds;
    private final List<Integer> fastTrafficSensorIds;

    @Inject
    VehicleRouteChangeEventEmitter(
            SensorDataRetriever sensorDataRetriever
            , JacksonMapping jacksonMapping
    ) {

        this.sensorDataRetriever = sensorDataRetriever;
        this.jacksonMapping = jacksonMapping;

        this.potentialHotspotIds = new TreeSet<>( // remove duplicates and sort
                Arrays.asList(459, 460, 466, 467, 468, 474, 475, 476, 491, 496, 497, 498, 502, 548, 549, 550, 597, 645, 646, 647, 662, 663, 664, 665, 666, 667, 673, 675, 693, 716, 747, 748, 749, 753, 754, 755, 759, 894, 895, 896, 897, 926, 928, 929, 987, 994, 1023, 1056, 1057, 1108, 1148, 1174, 1231, 1232, 1233, 1234, 1235, 1236, 1237, 1238, 1251, 1252, 1253, 1254, 1255, 1256, 1260, 1261, 1262, 1273, 1291, 1298, 1369, 1376, 1377, 1389, 1395, 1396, 1399, 1400, 1410, 1417, 1419, 1423, 1449, 1463, 1471, 1472, 1474, 1481, 1525, 1529, 1530, 1606, 1645, 1750, 1751, 1756, 1757, 1758, 1774, 1779, 1780, 1782, 1783, 1784, 1790, 1791, 1802, 1803, 1804, 1809, 1811, 1818, 1826, 1864, 1866, 1873, 1896, 1897, 1898, 1908, 1909, 1910, 1923, 1929, 2000, 2012, 2055, 2061, 2063, 2064, 2069, 2086, 2087, 2101, 2121, 2123, 2186, 2223, 2225, 2231, 2251, 2252, 2253, 2254, 2255, 2256, 2259, 2261, 2262, 2263, 2264, 2265, 2266, 2268, 2269, 2270, 2271, 2272, 2273, 2274, 2275, 2276, 2277, 2281, 2282, 2283, 2285, 2286, 2288, 2289, 2340, 2341, 2414, 2417, 2444, 2446, 2562, 2580, 2582, 2607, 2612, 2614, 2615, 2616, 2619, 2629, 2634, 2635, 2656, 2687, 2690, 2807, 2808, 2867, 2868, 2884, 2929, 2956, 2989, 3013, 3018, 3019, 3025, 3038, 3048, 3085, 3088, 3089, 3091, 3092, 3096, 3097, 3098, 3114, 3115, 3116, 3120, 3121, 3131, 3132, 3146, 3147, 3176, 3177, 3178, 3179, 3181, 3183, 3186, 3187, 3248, 3249, 3250, 3257, 3258, 3259, 3263, 3269, 3270, 3271, 3281, 3282, 3283, 3337, 3338, 3339, 3398, 3399, 3400, 3402, 3403, 3404, 3410, 3411, 3412, 3418, 3419, 3434, 3435, 3497, 3697, 3698, 3700, 3701, 3702, 3715, 3716, 3718, 3721, 3729, 3735, 3747, 3748, 3749, 3750, 3751, 3752, 3753, 3754, 3755, 3756, 3757, 3758, 3759, 3760, 3761, 3788, 3819, 3820, 3821, 3822, 3828, 3830, 3831, 3832, 3843, 3844, 3847, 3848, 3850, 3851, 3852, 3853, 3857, 3858, 3859, 3860, 3882, 3883, 3884, 3885, 3887, 3889, 3891, 3892, 3893, 3894, 3895, 3896, 3897, 3898, 3899, 3900, 3901, 3902, 3903, 3910, 3911, 3912, 3913, 3916, 3917, 3918, 3920, 3921, 3925, 3968, 3969, 3970, 3976, 3984, 3985, 3987, 3992, 3993, 3995, 4000, 4001, 4004, 4045, 4087, 4088, 4090, 4095, 4096, 4131, 4132, 4138, 4148, 4149, 4195, 4196, 4199, 4200, 4201, 4202, 4203, 4244, 4245, 4253, 4312, 4313, 4314, 4357, 4364, 4454, 4500, 4501, 4536, 4542, 4543, 4547, 4548, 4552, 4559, 4564, 4566, 4567, 4568, 4569, 4571, 4582, 4583, 4591, 4598, 4600, 4603, 4605, 4612, 4613, 4618, 4620, 4621, 4625, 4626, 4629, 4634)
        ).stream().toList();

        this.fastTrafficSensorIds = new TreeSet<>( // remove duplicates and sort
                Arrays.asList(1681, 1682, 1683, 1685, 1690, 1694, 1697, 1698, 1701, 1702, 1711, 1722, 1724, 1725, 1732, 1735, 1743, 1744, 1747, 1787, 1807, 1817, 1823, 1841, 1842, 1846, 1847, 1851, 1852, 1862, 1863, 1884, 1885, 1890, 1891, 1894, 1914, 1915, 1918, 1934, 1935, 1939, 1942, 1944, 1945, 1946, 1954, 1955, 1959, 1960, 1961, 1962, 1963, 1964, 1969, 1970, 1972, 1977, 1978, 1979, 1986, 1990, 1993, 1994, 1997, 1998, 2001, 2002, 2006, 2007, 2008, 2013, 2014, 2015, 2016, 2021, 2022, 2024, 2029, 2030, 2031, 2032, 2039, 2040, 2042, 2066, 2067, 2072, 2073, 2075, 2081, 2082, 2085, 2103, 2104, 2105, 2106, 2111, 2112, 2113, 2114, 2127, 2128, 2129, 2130, 2151, 2153, 2159, 2160, 2162, 2163, 2170, 2177, 2181, 2182, 2185, 2192, 2195, 2214, 2215, 2216, 2222, 2234, 2235, 2238, 2240, 2241, 2243, 2244, 2247, 2250, 2308, 2309, 2310, 2311, 2312, 2313, 2314, 2315, 2316, 2324, 2325, 2329, 2330, 2357, 2361, 2365, 2369, 2372, 2373, 2376, 2377, 2380, 2381, 2383, 2384, 2385, 2388, 2389, 2394, 2395, 2399, 2400, 2401, 2403, 2404, 2405, 2407, 2408, 2409, 2410, 2412, 2422, 2428, 2429, 2431, 2432, 2438, 2439, 2440, 2442, 2443, 2449, 2450, 2454, 2455, 2459, 2460, 2462, 2463, 2474, 2475, 2476, 2477, 2480, 2482, 2483, 2485, 2486, 2487, 2489, 2490, 2491, 2492, 2493, 2500, 2525, 2526, 2527, 2535, 2536, 2537, 2541, 2542, 2550, 2552, 2553, 2554, 2555, 2557, 2558, 2559, 2573, 2574, 2576, 2577, 2583, 2584, 2586, 2587, 2588, 2593, 2594, 2595, 2596, 2597, 2637, 2638, 2639, 2641, 2642, 2646, 2647, 2651, 2654, 2655, 2657, 2658, 2662, 2666, 2670, 2675, 2676, 2677, 2685, 2686, 2697, 2698, 2700, 2706, 2707, 2708, 2709, 2715, 2716, 2717, 2723, 2724, 2725, 2730, 2731, 2732, 2733, 2745, 2746, 2748, 2750, 2751, 2753, 2754, 2755, 2756, 2757, 2758, 2759, 2760, 2761, 2762, 2763, 2764, 2766, 2767, 2769, 2770, 2772, 2773, 2782, 2783, 2785, 2786, 2788, 2789, 2793, 2794, 2796, 2797, 2798, 2799, 2800, 2811, 2812, 2814, 2820, 2821, 2824, 2828, 2829, 2830, 2831, 2833, 2837, 2838, 2839, 2840, 2841, 2842, 2843, 2844, 2846, 2856, 2857, 2858, 2861, 2862, 2863, 2864, 2872, 2873, 2876, 2879, 2880, 2885, 2886, 2887, 2888, 2894, 2895, 2896, 2903, 2904, 2905, 2906, 2912, 2913, 2914, 2915, 2933, 2934, 2935, 2937, 2938, 2939, 2940, 2951, 2952, 2953, 2954, 2955, 2957, 2958, 2961, 2983, 2984, 2991, 2993, 3016, 3028, 3046, 3047, 3051, 3052, 3053, 3059, 3060, 3061, 3078, 3079, 3082, 3083, 3092, 3101, 3102, 3104, 3105, 3113, 3119, 3123, 3129, 3130, 3150, 3153, 3163, 3164, 3171, 3190, 3195, 3200, 3202, 3203, 3205, 3206, 3208, 3209, 3211, 3212, 3214, 3215, 3221, 3233, 3238, 3239, 3244, 3253, 3256, 3261, 3262, 3267, 3288, 3289, 3291, 3294, 3295, 3297, 3298, 3299, 3302, 3303, 3305, 3306, 3307, 3308, 3309, 3310, 3311, 3312, 3313, 3314, 3315, 3317, 3318, 3319, 3322, 3323, 3325, 3326, 3327, 3336, 3341, 3342, 3343, 3348, 3349, 3350, 3351, 3352, 3359, 3360, 3362, 3367, 3368, 3369, 3370, 3371, 3372, 3382, 3384, 3385, 3389, 3390, 3392, 3393, 3394, 3456, 3457, 3460, 3461, 3464, 3465, 3471, 3472, 3473, 3474, 3480, 3481, 3482, 3489, 3492, 3496, 3500, 3501, 3510, 3515, 3516, 3517, 3521, 3527, 3528, 3529, 3530, 3531, 3535, 3536, 3537, 3538, 3546, 3547, 3549, 3550, 3557, 3560, 3561, 3570, 3571, 3573, 3574, 3578, 3579, 3581, 3582, 3583, 3584, 3585, 3586, 3587, 3588, 3590, 3591, 3592, 3593, 3594, 3596, 3597, 3599, 3600, 3601, 3602, 3603, 3605, 3608, 3611, 3612, 3614, 3615, 3617, 3618, 3619, 3620, 3621, 3624, 3626, 3631, 3635, 3636, 3638, 3641, 3642, 3663, 3664, 3665, 3669, 3670, 3671, 3710, 3714, 3730, 3731, 3803, 3804, 3805, 3809, 3812, 3813, 3817, 3929, 3930, 3936, 3937, 3944, 3945, 3946, 3949, 4006, 4007, 4010, 4011, 4013, 4014, 4016, 4017, 4018, 4019, 4020, 4022, 4023, 4025, 4028, 4029, 4031, 4032, 4034, 4035, 4036, 4038, 4039, 4042, 4043, 4049, 4050, 4051, 4052, 4053, 4057, 4058, 4059, 4060, 4061, 4065, 4066, 4075, 4076, 4084, 4085, 4092, 4093, 4099, 4100, 4101, 4103, 4104, 4110, 4111, 4112, 4117, 4118, 4119, 4120, 4121, 4122, 4142, 4143, 4144, 4153, 4154, 4156, 4157, 4158, 4162, 4173, 4174, 4175, 4177, 4178, 4179, 4216, 4256, 4257, 4259, 4260, 4262, 4263, 4265, 4266, 4268, 4269, 4271, 4272, 4282, 4283, 4284, 4285, 4286, 4287, 4288, 4289, 4290, 4291, 4292, 4293, 4294, 4295, 4304, 4305, 4306, 4307, 4317, 4345, 4347, 4348, 4349, 4409, 4410, 4411, 4421, 4422, 4423, 4426, 4428, 4438, 4439, 4442, 4462, 4470, 4475, 4476, 4478, 4492, 4524, 4525, 4531, 4532, 4546, 4563, 4648, 4649, 4652, 4654, 4655, 4667, 4673, 4688, 4696, 4698, 4700, 4701, 4702, 4710, 4712, 4714, 4716, 4717, 4718, 4719, 4720, 4722, 4723, 4724, 4725, 4726, 4728, 4729, 4730, 4732, 4733, 4734, 4736, 4739, 4740, 4742, 4743, 4744, 4746, 4747, 4748, 4750, 4751, 4752, 4753, 4754, 4755, 4757, 4758, 4759, 4760, 4762, 4770, 4772, 4793, 4794, 4796, 4800)
        ).stream().toList();

    }

    @PostConstruct
    void onPostConstruct() {
        buildVehicleTemplateRoutesWithHotspots();
    }

    @Outgoing("vehicle-route-change-event-kafka-pdr")
    public Multi<Record<String, String>> sendRouteChangeEvents() {
        var ticks = Multi.createFrom().ticks().every(Duration.ofSeconds(30));
        var events = ticks.onItem().transformToMultiAndConcatenate(tick ->
                Multi.createFrom().items(movingVehicles(tick).stream()));
        return events.map(event -> Record.of(event.vehicleId(), jacksonMapping.toJson(event)));
    }

    private List<VehicleRouteChangeEvent> movingVehicles(long tick) {
        var movingVehicle1 = movingVehicle(MOVING_VEHICLE_1_TEMPLATE, tick);
        var movingVehicle2 = movingVehicle(MOVING_VEHICLE_2_TEMPLATE, tick);
        var movingVehicle3 = movingVehicle(MOVING_VEHICLE_3_TEMPLATE, tick);

        return Arrays.asList(movingVehicle1, movingVehicle2, movingVehicle3);
    }

    private VehicleRouteChangeEvent movingVehicle(VehicleRouteChangeEvent template, long tick) {
        return new VehicleRouteChangeEvent(template.vehicleId(), template.vehicleClass(), strippedRoute(template.route(), tick));
    }

    private List<GeographicCoordinates> strippedRoute(List<GeographicCoordinates> entireRoute, long tick) {
        List<GeographicCoordinates> result;

        if (tick <= entireRoute.size()) {
            // vehicle reached next step in the entire route
            result = entireRoute.subList((int) tick, entireRoute.size());
        } else {
            // edge case: destination reached.
            result = entireRoute.subList(entireRoute.size(), entireRoute.size());
        }

        return result;
    }

    private void buildVehicleTemplateRoutesWithHotspots() {
        MOVING_VEHICLE_1_TEMPLATE.route().addAll(hotspotsRoute(24));
        MOVING_VEHICLE_2_TEMPLATE.route().addAll(hotspotsRoute(24));

        // choose random route for vehicle 3, without hotspots
        MOVING_VEHICLE_3_TEMPLATE.route().addAll(noHotspotsRoute(30, true));

    }

    private List<GeographicCoordinates> hotspotsRoute(int length) {

        // Create a route beginning and ending with no-hotspots

        var result = noHotspotsRoute(3, false);
        result.addAll(route(potentialHotspotIds, length));
        result.addAll(noHotspotsRoute(3, false));

        LOGGER.debug("Created route with hotspots: {}", result);

        return result;
    }

    private List<GeographicCoordinates> noHotspotsRoute(int length, boolean log) {
        var result = route(fastTrafficSensorIds, length);

        if (log) {
            LOGGER.debug("Created route without hotspots: {}", result);
        }

        return result;
    }

    private List<GeographicCoordinates> route(List<Integer> sensorIds, int routeLength) {
        int origin = ThreadLocalRandom.current().nextInt(0, sensorIds.size());
        int bound = origin + routeLength;
        if (bound > sensorIds.size()) {
            bound = sensorIds.size();
        }

        var routeIds = sensorIds.subList(origin, bound);

        return routeIds.stream()
                .map(id -> this.sensorDataRetriever.getTrafficSensors()
                        .stream()
                        .filter(s -> s.id().equals(id))
                        .findFirst()
                        .orElseThrow(() -> new IllegalArgumentException("Could not find sensor with id = " + id)))
                .map(TrafficSensor::geographicCoordinates)
                .collect(Collectors.toList());
    }

}
