val defaultProperties = mapOf(
    legacy(
        "1.12.2",
        "1.12.2-14.23.5.2860",
        39,
        4
    ),
    legacy("1.7.10",
        "10.13.4.1614-1.7.10",
        12,
        4),
    recent("1.21.4",
        "21.4.124",
        "1.21.4-20241203.161809",
        "0.119.3+1.21.4",
        61,
        46,
        21
        )
    ,
    recent("1.21.5",
        "21.5.87",
        "1.21.5-20250325.162830",
        "0.128.1+1.21.5",
        71,
        55,
        21
    ),
    recent("1.21.6",
        "21.6.20-beta",
        "1.21.6-20250617.151856",
        "0.128.2+1.21.6",
        80,
        63,
        21
    ),
    recent("1.21.7",
        "21.7.25-beta",
        "1.21.7-20250711.194848",
        "0.129.0+1.21.7",
        81,
        64,
        21
    ),
    recent("1.21.8",
        "21.8.15",
        "1.21.8-20250717.133445",
        "0.129.0+1.21.8",
        81,
        64,
        21
    ),

)

fun legacy(mcVersion: String,
           forgeVersion: String,
           mappingsVersion: Int,
           resourcePackFormat: Int,
           datapackFormat: Int = 4,
           javaVersion: Int = 8,
           mappingsChannel: String = "stable"
): Pair<String,Map<String, String>> = mcVersion to mapOf(
    Keys.minecraftVersion to mcVersion,
    Keys.forgeVersion to forgeVersion,
    Keys.mappingsVersion to mappingsVersion.toString(),
    Keys.resourcePackFormat to resourcePackFormat.toString(),
    Keys.dataPackFormat to datapackFormat.toString(),
    Keys.javaVersion to javaVersion.toString(),
    Keys.mappingsChannel to mappingsChannel,
    Keys.dfuVersion to "4.1.27"
)

fun recent(
    mcVersion: String,
    neoforgeVersion: String,
    neoformVersion: String,
    fabricApiVersion: String,
    datapackFormat: Int,
    resourcePackFormat: Int,
    javaVersion: Int = 17
) = mcVersion to mapOf(
    Keys.minecraftVersion to mcVersion,
    Keys.neoforgeVersion to neoforgeVersion,
    Keys.neoformVersion to neoformVersion,
    Keys.fabricApiVersion to fabricApiVersion,
    Keys.resourcePackFormat to resourcePackFormat.toString(),
    Keys.dataPackFormat to datapackFormat.toString(),
    Keys.javaVersion to javaVersion.toString(),
)
