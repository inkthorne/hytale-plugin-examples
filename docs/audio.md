# Audio System

Hytale's audio system is defined through JSON assets in `Server/Audio/`. The system supports multi-layer sound events, hierarchical audio categories for mixing, ambient soundscapes with environmental conditions, and spatial audio effects.

## Quick Navigation

| Section | Directory | Files | Description |
|---------|-----------|-------|-------------|
| [SoundEvents](#soundevents) | `SoundEvents/` | 1,155 | Individual sound definitions with layers |
| [AudioCategories](#audiocategories) | `AudioCategories/` | 92 | Volume/mixing groups with inheritance |
| [AmbienceFX](#ambiencefx) | `AmbienceFX/` | 166 | Ambient soundscapes with conditions |
| [EQ](#eq-equalizer) | `EQ/` | 2 | Equalizer presets |
| [Reverb](#reverb) | `Reverb/` | 22 | Environment reverb settings |
| [ItemSounds](#itemsounds) | `ItemSounds/` | 37 | Inventory drag/drop sounds |
| [SoundSets](#soundsets) | `SoundSets/` | 1 | Named sound event collections |

**Total: 1,413 audio asset files**

---

## SoundEvents

**Location:** `Server/Audio/SoundEvents/`

Sound events are the core audio units. Each defines one or more sound layers, volume/pitch variation, looping behavior, and spatial attenuation.

### Directory Structure

```
SoundEvents/
├── BlockSounds/     - Per-block break/build/walk/land sounds
├── SFX/             - Player, NPC, weapons, UI, effects
│   ├── Animals/
│   ├── Combat/
│   ├── Interactions/
│   ├── NPCs/
│   ├── Player/
│   ├── UI/
│   └── Weapons/
└── Environments/    - Environmental emitters
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Layers` | array | Multiple concurrent sound layers (see below) |
| `Files` | array | Direct file references (shorthand for single layer) |
| `Volume` | float | Base volume in dB (default: 0) |
| `RandomSettings` | object | Pitch/volume variation |
| `Looping` | boolean | Whether sound loops continuously |
| `StartDelay` | float | Delay before playback starts (seconds) |
| `Probability` | float | Chance to play (0.0-1.0) |
| `RoundRobinHistorySize` | int | Prevents repeating same file in sequence |
| `PreventSoundInterruption` | boolean | Don't interrupt if already playing |
| `MaxInstance` | int | Maximum concurrent instances |
| `Parent` | string | Inherit from another sound event |
| `AudioCategory` | string | Mixing category reference |
| `MaxDistance` | float | Distance at which sound is silent |
| `StartAttenuationDistance` | float | Distance at which falloff begins |

### Layer System

Sound events can have multiple concurrent layers, each with independent files and settings:

```json
{
  "Layers": [
    {
      "Files": ["Sounds/SFX/Weapons/Battleaxe/Swing/Battleaxe_Swing_01.ogg"],
      "Volume": -3,
      "RandomSettings": {
        "MinPitch": 0.95,
        "MaxPitch": 1.05
      }
    },
    {
      "Files": ["Sounds/SFX/Combat/Whoosh/Combat_Whoosh_Heavy_01.ogg"],
      "Volume": -6
    }
  ],
  "AudioCategory": "AudioCat_Weapons_Battleaxe"
}
```

### RandomSettings

Add variation to prevent repetitive sounds:

| Property | Type | Description |
|----------|------|-------------|
| `MinPitch` | float | Minimum pitch multiplier (default: 1.0) |
| `MaxPitch` | float | Maximum pitch multiplier (default: 1.0) |
| `MinVolume` | float | Minimum volume offset in dB |
| `MaxVolume` | float | Maximum volume offset in dB |

### Parent Inheritance

Sound events can inherit from presets to share attenuation settings:

```json
{
  "Parent": "SFX_Attn_Large",
  "Files": ["Sounds/SFX/Weapons/Mace/Impact/Mace_Impact_01.ogg"],
  "Volume": -2
}
```

Common attenuation presets:
- `SFX_Attn_Large` - Large radius (explosions, loud impacts)
- `SFX_Attn_Medium` - Medium radius (combat, interactions)
- `SFX_Attn_Small` - Small radius (footsteps, UI)

### Examples

**Block Sound (multi-file with variation):**

```json
{
  "Files": [
    "Sounds/SFX/Materials/Bone/Break/Bone_Break_01.ogg",
    "Sounds/SFX/Materials/Bone/Break/Bone_Break_02.ogg",
    "Sounds/SFX/Materials/Bone/Break/Bone_Break_03.ogg"
  ],
  "Volume": 0,
  "RandomSettings": {
    "MinPitch": 0.9,
    "MaxPitch": 1.1
  },
  "RoundRobinHistorySize": 2,
  "AudioCategory": "AudioCat_BlockDestruction"
}
```

**Weapon Swing (multi-layer):**

```json
{
  "Layers": [
    {
      "Files": [
        "Sounds/SFX/Weapons/Battleaxe/Swing/Battleaxe_Swing_01.ogg",
        "Sounds/SFX/Weapons/Battleaxe/Swing/Battleaxe_Swing_02.ogg"
      ],
      "Volume": -3,
      "RandomSettings": {
        "MinPitch": 0.95,
        "MaxPitch": 1.05
      }
    }
  ],
  "AudioCategory": "AudioCat_Weapons_Battleaxe"
}
```

**Looping Environmental Sound:**

```json
{
  "Files": ["Sounds/SFX/Environments/Water/Waterfall_Loop.ogg"],
  "Volume": -6,
  "Looping": true,
  "MaxDistance": 50,
  "StartAttenuationDistance": 10,
  "AudioCategory": "AudioCat_Environment"
}
```

---

## AudioCategories

**Location:** `Server/Audio/AudioCategories/`

Audio categories define volume mixing groups with hierarchical inheritance. They allow grouping sounds for volume control (e.g., all NPC sounds, all weapon sounds).

### Directory Structure

```
AudioCategories/
├── NPC/         - Per-NPC audio categories
└── Weapons/     - Per-weapon audio categories
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Volume` | float | Volume adjustment in dB (can be negative) |
| `Parent` | string | Parent category for inheritance |

### Hierarchy Example

```json
// AudioCat_NPC.json (parent)
{
  "Volume": 0
}

// AudioCat_NPC_Wolf.json
{
  "Volume": 0,
  "Parent": "AudioCat_NPC"
}
```

### Common Categories

| Category | Purpose |
|----------|---------|
| `AudioCat_Music` | Background music |
| `AudioCat_SFX` | General sound effects |
| `AudioCat_Environment` | Environmental sounds |
| `AudioCat_UI` | User interface sounds |
| `AudioCat_Combat` | Combat sounds |
| `AudioCat_BlockDestruction` | Block breaking sounds |
| `AudioCat_Footsteps` | Footstep sounds |
| `AudioCat_NPC` | NPC vocalizations |
| `AudioCat_Weapons_*` | Per-weapon-type categories |

---

## AmbienceFX

**Location:** `Server/Audio/AmbienceFX/`

Ambient audio defines soundscapes that play based on environmental conditions. Includes ambient beds (continuous background), emitter sounds (periodic triggers), and music.

### Directory Structure

```
AmbienceFX/
├── Ambience/    - Zone-specific ambient soundscapes
│   ├── Zone1/
│   │   ├── Environments/
│   │   │   ├── Forest/
│   │   │   ├── Desert/
│   │   │   └── Cave/
│   │   └── Weather/
│   └── Global/
└── Music/       - Background music tracks
    └── Global/
```

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Conditions` | object | When this ambience plays |
| `AmbientBed` | object | Continuous background sound |
| `Sounds` | array | Triggered emitter sounds |
| `Music` | object | Background music configuration |

### Conditions System

Conditions determine when ambient audio plays:

| Condition | Type | Description |
|-----------|------|-------------|
| `EnvironmentIds` | array | Specific environment IDs |
| `EnvironmentTagPattern` | object | Tag pattern matching (see below) |
| `WeatherTagPattern` | object | Weather condition matching |
| `SunLightLevel` | object | Light level range (Min/Max) |
| `DayTime` | object | Time of day range (Min/Max, 0.0-1.0) |
| `Altitude` | object | Height range (Min/Max) |

### Tag Patterns

Tag patterns use boolean logic to match conditions:

```json
{
  "EnvironmentTagPattern": {
    "And": [
      { "Equals": "Forest" },
      { "Not": { "Equals": "Dense" } }
    ]
  }
}
```

**Operators:**
- `Equals` - Exact tag match
- `And` - All conditions must match
- `Or` - Any condition must match
- `Not` - Inverts the condition

### AmbientBed

Continuous looping background sound:

```json
{
  "AmbientBed": {
    "SoundEventId": "Amb_Forest_Day_Bed",
    "Volume": -12,
    "FadeInTime": 2.0,
    "FadeOutTime": 2.0
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `SoundEventId` | string | Sound event to loop |
| `Volume` | float | Volume in dB |
| `FadeInTime` | float | Fade in duration (seconds) |
| `FadeOutTime` | float | Fade out duration (seconds) |

### Emitter Sounds

Periodic triggered sounds with spatial positioning:

```json
{
  "Sounds": [
    {
      "SoundEventId": "Amb_Forest_Bird_Chirp",
      "MinDelay": 5.0,
      "MaxDelay": 15.0,
      "MinRadius": 10,
      "MaxRadius": 30,
      "Probability": 0.7
    }
  ]
}
```

| Property | Type | Description |
|----------|------|-------------|
| `SoundEventId` | string | Sound event to trigger |
| `MinDelay` | float | Minimum time between triggers |
| `MaxDelay` | float | Maximum time between triggers |
| `MinRadius` | float | Minimum spawn distance from player |
| `MaxRadius` | float | Maximum spawn distance from player |
| `Probability` | float | Chance to play when triggered |

### Music Configuration

Background music track playlists:

```json
{
  "Music": {
    "Tracks": [
      {
        "SoundEventId": "Mus_Zone1_Forest_Day_01",
        "Weight": 1.0
      },
      {
        "SoundEventId": "Mus_Zone1_Forest_Day_02",
        "Weight": 1.0
      }
    ],
    "MinDelay": 60,
    "MaxDelay": 180,
    "FadeInTime": 3.0,
    "FadeOutTime": 3.0
  }
}
```

| Property | Type | Description |
|----------|------|-------------|
| `Tracks` | array | Weighted track list |
| `MinDelay` | float | Minimum silence between tracks |
| `MaxDelay` | float | Maximum silence between tracks |
| `FadeInTime` | float | Track fade in duration |
| `FadeOutTime` | float | Track fade out duration |

### Complete Example

```json
{
  "Conditions": {
    "EnvironmentTagPattern": {
      "And": [
        { "Equals": "Forest" },
        { "Equals": "Zone1" }
      ]
    },
    "DayTime": {
      "Min": 0.25,
      "Max": 0.75
    }
  },
  "AmbientBed": {
    "SoundEventId": "Amb_Zone1_Forest_Day_Bed",
    "Volume": -9,
    "FadeInTime": 2.0,
    "FadeOutTime": 2.0
  },
  "Sounds": [
    {
      "SoundEventId": "Amb_Zone1_Forest_Bird_01",
      "MinDelay": 8.0,
      "MaxDelay": 20.0,
      "MinRadius": 15,
      "MaxRadius": 40,
      "Probability": 0.8
    },
    {
      "SoundEventId": "Amb_Zone1_Forest_Rustle",
      "MinDelay": 10.0,
      "MaxDelay": 30.0,
      "MinRadius": 5,
      "MaxRadius": 20,
      "Probability": 0.5
    }
  ]
}
```

---

## EQ (Equalizer)

**Location:** `Server/Audio/EQ/`

Equalizer presets for audio filtering, typically used for environmental effects like underwater audio.

### Properties

4-band parametric equalizer with Low, LowMid, HighMid, and High bands:

| Property | Type | Description |
|----------|------|-------------|
| `Low` | object | Low frequency band |
| `LowMid` | object | Low-mid frequency band |
| `HighMid` | object | High-mid frequency band |
| `High` | object | High frequency band |

### Band Properties

| Property | Type | Description |
|----------|------|-------------|
| `Gain` | float | Volume adjustment in dB |
| `CutOff` | float | Cutoff frequency (Hz) for Low/High |
| `Center` | float | Center frequency (Hz) for mid bands |
| `Width` | float | Bandwidth (octaves) for mid bands |

### Example (Underwater EQ)

```json
{
  "Low": {
    "Gain": 0,
    "CutOff": 200
  },
  "LowMid": {
    "Gain": -3,
    "Center": 500,
    "Width": 1.5
  },
  "HighMid": {
    "Gain": -9,
    "Center": 2000,
    "Width": 2.0
  },
  "High": {
    "Gain": -18,
    "CutOff": 4000
  }
}
```

---

## Reverb

**Location:** `Server/Audio/Reverb/`

Reverb presets simulate acoustic environments like caves, rooms, and outdoor spaces.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `DecayTime` | float | Reverb tail duration (seconds) |
| `Diffusion` | float | Echo density (0.0-1.0) |
| `Density` | float | Modal density (0.0-1.0) |
| `LowShelfFrequency` | float | Low frequency shelf (Hz) |
| `LowShelfGain` | float | Low frequency adjustment (dB) |
| `HighCut` | float | High frequency rolloff (Hz) |
| `EarlyDelay` | float | Pre-delay time (ms) |
| `LateDelay` | float | Late reflection delay (ms) |
| `Reflections` | float | Early reflection level (dB) |
| `LateReverb` | float | Late reverb level (dB) |
| `HFReference` | float | High frequency reference (Hz) |
| `HFDecayRatio` | float | HF decay relative to mid (0.0-1.0) |

### Example (Cave Reverb)

```json
{
  "DecayTime": 2.5,
  "Diffusion": 0.8,
  "Density": 0.9,
  "LowShelfFrequency": 250,
  "LowShelfGain": 0,
  "HighCut": 8000,
  "EarlyDelay": 20,
  "LateDelay": 40,
  "Reflections": -6,
  "LateReverb": -3,
  "HFReference": 5000,
  "HFDecayRatio": 0.6
}
```

### Common Presets

| Preset | Description |
|--------|-------------|
| `Rev_Cave` | Large cave with long decay |
| `Rev_Cave_Small` | Small cave or tunnel |
| `Rev_Room` | Indoor room |
| `Rev_Hall` | Large hall or chamber |
| `Rev_Outdoor` | Open outdoor space |
| `Rev_Underwater` | Muffled underwater acoustics |

---

## ItemSounds

**Location:** `Server/Audio/ItemSounds/`

Item sounds define drag and drop sounds for inventory interactions. Items reference these via `ItemSoundSetId`.

### Properties

| Property | Type | Description |
|----------|------|-------------|
| `Drag` | string | Sound event when picking up item |
| `Drop` | string | Sound event when placing item |

### Example

```json
{
  "Drag": "SFX_UI_Inventory_Drag_Metal",
  "Drop": "SFX_UI_Inventory_Drop_Metal"
}
```

### Integration with Items

In item definitions (`Server/Item/`):

```json
{
  "Name": "Iron Sword",
  "ItemSoundSetId": "ItemSounds_Metal"
}
```

---

## SoundSets

**Location:** `Server/Audio/SoundSets/`

Sound sets group related sound events under named keys for easy reference by other systems.

### Example

```json
{
  "Attack": "SFX_Sword_Swing",
  "Impact": "SFX_Sword_Impact",
  "Block": "SFX_Sword_Block"
}
```

---

## Integration with Other Systems

### Block Sounds

Blocks reference sound events by material type. The BlockType's material name maps to files in `BlockSounds/`:

```
BlockSounds/
├── Bone/
│   ├── SFX_Bone_Break.json
│   ├── SFX_Bone_Build.json
│   ├── SFX_Bone_Walk.json
│   └── SFX_Bone_Land.json
├── Stone/
│   ├── SFX_Stone_Break.json
│   └── ...
└── Wood/
    └── ...
```

Block types specify their material in their definition, and the audio system automatically loads the corresponding sounds.

### Interactions

Interactions can trigger sounds via `WorldSoundEventId` (spatial, heard by all nearby) or `LocalSoundEventId` (only heard by the acting player):

```json
{
  "Type": "Simple",
  "RunTime": 0.2,
  "Effects": {
    "WorldSoundEventId": "SFX_Sword_Swing",
    "LocalSoundEventId": "SFX_UI_Click"
  }
}
```

See [interactions.md](interactions.md) for full interaction documentation.

### NPC Audio

NPCs reference sound events in their animation definitions. Animation events can trigger sounds at specific keyframes:

```json
{
  "AnimationId": "Attack",
  "Events": [
    {
      "Time": 0.2,
      "Type": "Sound",
      "SoundEventId": "SFX_Wolf_Attack"
    }
  ]
}
```

### Weapon Audio Categories

Weapons define their audio category for mixing control:

```json
{
  "ItemId": "Battleaxe_T1",
  "AudioCategory": "AudioCat_Weapons_Battleaxe"
}
```

The category hierarchy allows adjusting all battleaxe sounds together while still inheriting from the parent weapons category.

---

## File Format Reference

All audio assets use JSON format:

| Asset Type | Location | Purpose |
|------------|----------|---------|
| Sound Event | `SoundEvents/*.json` | Individual sounds |
| Audio Category | `AudioCategories/*.json` | Mixing groups |
| Ambience | `AmbienceFX/Ambience/*.json` | Ambient soundscapes |
| Music | `AmbienceFX/Music/*.json` | Background music |
| EQ Preset | `EQ/*.json` | Equalizer settings |
| Reverb Preset | `Reverb/*.json` | Reverb environments |
| Item Sounds | `ItemSounds/*.json` | Inventory sounds |
| Sound Set | `SoundSets/*.json` | Named sound groups |

Sound files themselves are `.ogg` format located in `Common/Sounds/`.
