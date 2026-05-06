package com.pranksterlab.core.model

enum class PrankCategory(
    val displayName: String,
    val description: String,
    val safeForRandomDefault: Boolean = true
) {
    FUNNY("Funny", "Silly sounds and goofy noises", true),
    CREEPY("Creepy", "Spooky vibes and subtle noises", true),
    VOICE("Voice", "Various spoken lines and vocalizations", true),
    ANIMAL("Animal", "Critters and creatures", true),
    REACTION("Reaction", "Laughs, boos, and audience sounds", true),
    GLITCH("Glitch", "Digital stutters and technical errors", true),
    OFFICE("Office", "Workplace annoyance and subtle chaos", true),
    HORROR_LITE("Horror Lite", "Mild spooky fun without genuine panic", true),
    SCI_FI("Sci-Fi", "Aliens, spaceships, and future tech", true),
    CHAOS("Chaos", "Loud, cartoonish disruptions", true),
    CARTOON("Cartoon", "Classic slapstick and bouncy effects", true),
    AMBIENCE("Ambience", "Background loops and environmental tracks", true),
    ROBOT("Robot", "Mechanical voices and servos", true),
    MONSTER("Monster", "Goofy growls and groans", true),
    DOOR_KNOCKS("Door Knocks", "From subtle taps to heavy pounding", true),
    FOOTSTEPS("Footsteps", "Walking, running, creeping", true),
    PHONE("Phone", "Fictional rings and pings", true),
    MEME_REACTIONS("Meme Reactions", "Internet culture go-to sounds", true),
    TOY_BOX("Toy Box", "Squeaks, rattles, and plastic drops", true),
    CUSTOM("Custom", "Your own uploaded and trimmed sounds", true)
}
