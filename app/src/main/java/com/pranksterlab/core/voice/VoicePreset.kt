package com.pranksterlab.core.voice

enum class VoiceCategory {
    ROBOT_TECH,
    GOBLIN_GREMLIN,
    SPOOKY,
    CARTOON_SILLY,
    ANNOUNCER,
    CREATURE,
    OFFICE_PRANK,
    SCI_FI,
    DAD_JOKE,
    WEIRD_NARRATOR
}

data class VoicePreset(
    val id: String,
    val displayName: String,
    val category: VoiceCategory,
    val description: String,
    val pitch: Float,
    val speechRate: Float,
    val volume: Float,
    val toneStyle: String,
    val effectStyle: String,
    val intensityLevel: Int,
    val recommendedUse: String,
    val samplePhrase: String,
    val isSafeForRandomMode: Boolean
)

object VoicePresetLibrary {
    val presets: List<VoicePreset> = listOf(
        VoicePreset("glitch_bot", "Glitch Bot", VoiceCategory.ROBOT_TECH, "Choppy neon machine banter", 0.86f, 1.05f, 0.90f, "Digital", "Bitcrush", 3, "Tech parody lines", "Diagnostic complete. Mischief engine online.", true),
        VoicePreset("friendly_android", "Friendly Android", VoiceCategory.ROBOT_TECH, "Helpful companion with smooth tone", 1.02f, 1.00f, 0.95f, "Warm Synth", "Clean", 2, "Friendly reminders", "Hello human, your snack timer has reached destiny.", true),
        VoicePreset("broken_intercom", "Broken Intercom", VoiceCategory.ROBOT_TECH, "Old PA speaker with dropouts", 0.92f, 0.92f, 0.88f, "Compressed", "Intercom", 4, "Office intercom skits", "Attention deck seven... please ignore the mysterious beeping.", true),
        VoicePreset("tiny_terminal", "Tiny Terminal", VoiceCategory.ROBOT_TECH, "Mini processor squeak", 1.35f, 1.18f, 0.82f, "High Synth", "Crisp", 2, "Quick one-liners", "Command accepted: deploy tiny chaos package.", true),
        VoicePreset("retro_service_droid", "Retro Service Droid", VoiceCategory.ROBOT_TECH, "Polite vintage helper robot", 0.96f, 0.90f, 0.90f, "Retro Synth", "Vinyl", 2, "Retro cafeteria bits", "Greetings valued guest, your toast has entered orbit.", true),
        VoicePreset("villain_computer", "Villain Computer", VoiceCategory.ROBOT_TECH, "Dramatic AI monologue mode", 0.78f, 0.88f, 0.96f, "Dark Synth", "Reverb", 5, "Dramatic fake warnings", "You cannot stop me, I have upgraded to sarcastic mode.", false),

        VoicePreset("tiny_goblin", "Tiny Goblin", VoiceCategory.GOBLIN_GREMLIN, "Mischievous cave chatter", 1.40f, 1.10f, 0.90f, "Nasal", "Snappy", 3, "Snack stealing jokes", "I borrowed your chips for scientific goblin research.", true),
        VoicePreset("sneaky_gremlin", "Sneaky Gremlin", VoiceCategory.GOBLIN_GREMLIN, "Whispery chaos gremlin", 1.22f, 1.04f, 0.84f, "Whisper", "Rustle", 3, "Stealth prank intros", "Psst. The copy machine owes me two cookies.", true),
        VoicePreset("basement_gremlin", "Basement Gremlin", VoiceCategory.GOBLIN_GREMLIN, "Dusty under-stairs troublemaker", 1.12f, 0.86f, 0.82f, "Raspy", "Room", 4, "Spooky-funny notes", "I live by the breaker box and demand pizza tribute.", false),
        VoicePreset("snack_goblin", "Snack Goblin", VoiceCategory.GOBLIN_GREMLIN, "Greedy munching prank voice", 1.28f, 1.08f, 0.96f, "Chewy", "Muffle", 2, "Kitchen prank messages", "This pantry is now under goblin snack protection.", true),
        VoicePreset("mischief_imp", "Mischief Imp", VoiceCategory.GOBLIN_GREMLIN, "Tiny imp with confident chaos", 1.30f, 1.20f, 0.90f, "Pointy", "Spark", 4, "Rapid-fire prank delivery", "I replaced your calendar with surprise dance breaks.", true),

        VoicePreset("haunted_hallway", "Haunted Hallway", VoiceCategory.SPOOKY, "Distant corridor ghost echo", 0.74f, 0.78f, 0.80f, "Hollow", "Echo", 4, "Creepy ambiance lines", "Footsteps heard. Courage optional.", false),
        VoicePreset("ghost_host", "Ghost Host", VoiceCategory.SPOOKY, "Paranormal late-night presenter", 0.82f, 0.86f, 0.85f, "Eerie", "Hall", 4, "Campfire style intros", "Welcome back to midnight mysteries and questionable snacks.", false),
        VoicePreset("creepy_doll", "Creepy Doll", VoiceCategory.SPOOKY, "Broken toy recital", 1.28f, 0.75f, 0.90f, "Childlike", "Warp", 5, "Haunted toy bits", "Let's play hide and shriek.", false),
        VoicePreset("shadow_narrator", "Shadow Narrator", VoiceCategory.SPOOKY, "Velvet menace narration", 0.76f, 0.82f, 0.88f, "Low", "Dark Hall", 4, "Slow suspense setup", "Every hallway has a secret and this one giggles.", false),
        VoicePreset("slow_monster", "Slow Monster", VoiceCategory.SPOOKY, "Massive lumbering dialogue", 0.62f, 0.68f, 0.94f, "Heavy", "Sub Echo", 5, "Big monster monologues", "I am not mad, just dramatically hungry.", false),
        VoicePreset("whispering_attic", "Whispering Attic", VoiceCategory.SPOOKY, "Dusty attic hush with flutter", 0.84f, 0.72f, 0.74f, "Breathy", "Tape Flutter", 4, "Ambient haunted clips", "The attic says your socks are missing on purpose.", false),

        VoicePreset("angry_squirrel", "Angry Squirrel", VoiceCategory.CARTOON_SILLY, "Fast annoyed squeaks", 1.45f, 1.22f, 0.87f, "Sharp", "Chatter", 4, "Hyper complaint bits", "Who moved my acorn spreadsheet?", true),
        VoicePreset("dramatic_hamster", "Dramatic Hamster", VoiceCategory.CARTOON_SILLY, "Tiny actor with huge emotion", 1.32f, 0.90f, 0.95f, "Theatrical", "Studio", 3, "Soap-opera jokes", "This wheel... is my destiny!", true),
        VoicePreset("confused_narrator", "Confused Narrator", VoiceCategory.CARTOON_SILLY, "Lost storyteller energy", 1.00f, 0.96f, 0.92f, "Narrative", "Flutter", 2, "Misguided tutorial gags", "Step one was obvious, step two got weird.", true),
        VoicePreset("squeaky_coach", "Squeaky Coach", VoiceCategory.CARTOON_SILLY, "Tiny whistle-coach pressure", 1.36f, 1.24f, 0.98f, "Peppy", "Squeak", 4, "Workout prank lines", "Give me ten giggles and hydrate immediately.", true),
        VoicePreset("overexcited_penguin", "Overexcited Penguin", VoiceCategory.CARTOON_SILLY, "Sliding mascot with too much hype", 1.24f, 1.18f, 0.94f, "Bouncy", "Wobble", 3, "Party invites", "Waddle faster, the dance floor is melting!", true),
        VoicePreset("panic_llama", "Panic Llama", VoiceCategory.CARTOON_SILLY, "Flustered llama commentary", 1.18f, 1.28f, 0.93f, "Breathy", "Quiver", 4, "Chaotic status updates", "Emergency! Someone alphabetized my drama.", true),

        VoicePreset("game_show_host", "Game Show Host", VoiceCategory.ANNOUNCER, "Big prize wheel hype", 1.10f, 1.14f, 1.00f, "Hype", "Sparkle", 4, "Game intros", "Welcome contestants to totally unnecessary trivia!", true),
        VoicePreset("movie_trailer_guy", "Movie Trailer Guy", VoiceCategory.ANNOUNCER, "Epic trailer cadence", 0.72f, 0.84f, 1.00f, "Epic", "Wide Reverb", 5, "Trailer spoof lines", "In a world where snacks disappear, one hero blames the cat.", true),
        VoicePreset("sports_hype_voice", "Sports Hype Voice", VoiceCategory.ANNOUNCER, "Arena-ready excitement", 0.96f, 1.20f, 1.00f, "Punchy", "Crowd", 4, "Friendly competition banter", "Tonight's championship: couch versus motivation!", true),
        VoicePreset("mall_pa_system", "Mall PA System", VoiceCategory.ANNOUNCER, "Softly compressed shopping broadcast", 0.98f, 0.90f, 0.88f, "PA", "Intercom", 2, "Store parody calls", "Cleanup on aisle fun, repeat, aisle fun.", true),
        VoicePreset("arcade_referee", "Arcade Referee", VoiceCategory.ANNOUNCER, "Retro tournament referee", 1.14f, 1.08f, 0.96f, "Arcade", "8-bit", 3, "Retro score calls", "Bonus round begins now. Buttons at the ready.", true),

        VoicePreset("tiny_monster", "Tiny Monster", VoiceCategory.CREATURE, "Small beast with big attitude", 0.90f, 1.06f, 0.92f, "Growly", "Crunch", 3, "Playful monster notes", "Rawr means please pass the salsa.", true),
        VoicePreset("cave_troll_lite", "Cave Troll Lite", VoiceCategory.CREATURE, "Chunky but friendly cave troll", 0.68f, 0.74f, 0.98f, "Rocky", "Cave", 4, "Fantasy prank scenes", "Troll says meeting postponed until snack arrives.", true),
        VoicePreset("alien_visitor", "Alien Visitor", VoiceCategory.CREATURE, "Curious off-world tourist", 1.16f, 0.94f, 0.90f, "Glassy", "Phase", 3, "Alien check-ins", "Greetings Earth unit, your fridge emits jazz.", true),
        VoicePreset("grumpy_creature", "Grumpy Creature", VoiceCategory.CREATURE, "Low grumble with comic timing", 0.72f, 0.82f, 0.95f, "Grumble", "Thump", 4, "Complaint-themed skits", "I woke up dramatic and out of patience.", true),
        VoicePreset("swamp_critter", "Swamp Critter", VoiceCategory.CREATURE, "Bog creature with bubbly texture", 0.82f, 0.88f, 0.89f, "Boggy", "Bubble", 3, "Swamp adventure bits", "Welcome to the swamp, mind the sarcastic frogs.", true),

        VoicePreset("sarcastic_butler", "Sarcastic Butler", VoiceCategory.OFFICE_PRANK, "Polite but razor-dry sarcasm", 0.94f, 0.90f, 0.90f, "Dry", "Room", 2, "Office reminder pranks", "Certainly, I shall schedule your panic for 3 PM.", true),
        VoicePreset("sleepy_announcer", "Sleepy Announcer", VoiceCategory.OFFICE_PRANK, "Late shift public speaker", 0.88f, 0.72f, 0.83f, "Drowsy", "Soft", 2, "End-of-day messages", "Attention team, nap o'clock has been approved.", true),
        VoicePreset("awkward_receptionist", "Awkward Receptionist", VoiceCategory.OFFICE_PRANK, "Nervous front-desk cadence", 1.08f, 0.98f, 0.91f, "Nervous", "Desk Mic", 2, "Reception desk skits", "Hello, your confidence has checked in before you did.", true),
        VoicePreset("spreadsheet_goblin", "Spreadsheet Goblin", VoiceCategory.OFFICE_PRANK, "Formula-loving desk gremlin", 1.12f, 1.12f, 0.92f, "Nerdy", "Clicky", 3, "Data humor clips", "I merged your cells and your destiny.", true),
        VoicePreset("meeting_gremlin", "Meeting Gremlin", VoiceCategory.OFFICE_PRANK, "Agenda sabotage specialist", 1.04f, 1.06f, 0.90f, "Snarky", "Conference", 3, "Meeting reminder jokes", "This meeting could have been a sandwich.", true),

        VoicePreset("space_announcer", "Space Announcer", VoiceCategory.SCI_FI, "Orbital station status broadcast", 1.12f, 0.95f, 1.00f, "Command", "Echo", 3, "Sci-fi broadcasts", "Docking sequence complete. Dance sequence optional.", true),
        VoicePreset("starship_warning_lite", "Starship Warning Lite", VoiceCategory.SCI_FI, "Calm caution system voice", 0.84f, 1.00f, 0.94f, "Alert", "Pulse", 3, "Fake ship status updates", "Notice: coffee levels below mission minimum.", true),
        VoicePreset("robot_elevator", "Robot Elevator", VoiceCategory.SCI_FI, "Smooth futuristic floor guide", 1.04f, 0.92f, 0.90f, "Neutral Synth", "Lift", 2, "Transit announcements", "Now arriving at floor nine and three snacks.", true),
        VoicePreset("moon_base_intern", "Moon Base Intern", VoiceCategory.SCI_FI, "Enthusiastic rookie space aide", 1.20f, 1.00f, 0.88f, "Bright", "Radio", 2, "Light space comedy", "Moon base memo: please label your anti-gravity lunch.", true),
        VoicePreset("cosmic_tour_guide", "Cosmic Tour Guide", VoiceCategory.SCI_FI, "Cheerful galaxy sightseeing host", 1.06f, 0.96f, 0.93f, "Guided", "Starlight", 2, "Tour narration", "On your left, a nebula shaped like a waffle.", true),

        VoicePreset("awkward_dad_joke", "Awkward Dad Joke", VoiceCategory.DAD_JOKE, "Proud joke setup pause", 1.00f, 0.92f, 0.92f, "Cheerful", "Garage", 1, "Classic groaners", "I used to hate facial hair, but it grew on me.", true),
        VoicePreset("bbq_philosopher", "BBQ Philosopher", VoiceCategory.DAD_JOKE, "Grill-side wisdom delivery", 0.90f, 0.86f, 0.95f, "Warm", "Patio", 2, "Cookout announcements", "Life is like barbecue: low and slow wins.", true),
        VoicePreset("garage_narrator", "Garage Narrator", VoiceCategory.DAD_JOKE, "Tool-bench storytelling voice", 0.94f, 0.88f, 0.92f, "Matter-of-fact", "Workbench", 2, "DIY humor", "If it rattles, it's either progress or mystery.", true),
        VoicePreset("lawn_chair_prophet", "Lawn Chair Prophet", VoiceCategory.DAD_JOKE, "Relaxed oracle of backyard truth", 0.92f, 0.82f, 0.90f, "Laid Back", "Summer Air", 2, "Backyard joke sermons", "I predict burgers and mildly terrible puns.", true),

        VoicePreset("dramatic_whisper", "Dramatic Whisper", VoiceCategory.WEIRD_NARRATOR, "Soft narration with huge stakes", 1.00f, 0.78f, 0.80f, "Whispered", "Close Mic", 4, "Overblown secret reveals", "In silence, the toaster made its choice.", true),
        VoicePreset("overly_serious_narrator", "Overly Serious Narrator", VoiceCategory.WEIRD_NARRATOR, "Documentary gravity for trivial events", 0.82f, 0.84f, 0.92f, "Grave", "Cinema", 4, "Epic daily-life narration", "He approached the sink as legends foretold.", true),
        VoicePreset("tiny_documentary_voice", "Tiny Documentary Voice", VoiceCategory.WEIRD_NARRATOR, "Mini nature-doc host with confidence", 1.26f, 0.90f, 0.90f, "Scholarly", "Field", 3, "Mini documentary jokes", "Observe the wild roommate, guarding the last slice.", true),
        VoicePreset("mysterious_instructional_tape", "Mysterious Instructional Tape", VoiceCategory.WEIRD_NARRATOR, "Vintage training tape energy", 0.88f, 0.80f, 0.85f, "Instructional", "Tape", 3, "Retro tutorial parody", "Step one: remain calm. Step two: pretend this is normal.", true)
    )
}
