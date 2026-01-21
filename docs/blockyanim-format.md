# Block Animation Format (.blockyanim)

This document describes the `.blockyanim` file format used for animating block states in Hytale.

## Overview

Blockyanim files define animations for block models, controlling how individual parts of a block move, rotate, scale, and change visibility over time. These are commonly used for:

- Doors opening and closing
- Chests opening
- Fire and light flickering effects
- Mechanical block animations
- Environmental decorations

## File Location

Block animations are stored in:
```
Assets/Common/Blocks/Animations/
```

Organized into subdirectories by category (e.g., `Doors/`, `Containers/`, `Lights/`).

## Frame Rate

Block animations run at **20 frames per second**. All time values in keyframes are specified in frames at this rate.

## File Structure

```json
{
  "formatVersion": 0,
  "duration": 10,
  "holdLastKeyframe": true,
  "nodeAnimations": {
    "NodeName": {
      "position": [...],
      "orientation": [...],
      "shapeStretch": [...],
      "shapeVisible": [...],
      "shapeUvOffset": [...]
    }
  }
}
```

## Top-Level Fields

| Field | Type | Required | Description |
|-------|------|----------|-------------|
| `formatVersion` | integer | Yes | Schema version, currently `0` |
| `duration` | integer | Yes | Total animation length in frames (at 20 FPS) |
| `holdLastKeyframe` | boolean | No | If `true`, animation holds the final keyframe values when complete. Default is `false` |
| `nodeAnimations` | object | Yes | Map of node names to their animation tracks |

## Node Animations

The `nodeAnimations` object maps node names (as defined in the block's `.blockymodel`) to animation data. Each node can have any combination of the five track types.

### Animation Track Types

| Track | Value Type | Description |
|-------|------------|-------------|
| `position` | `[x, y, z]` | Translates the node in 3D space |
| `orientation` | `[x, y, z, w]` | Rotates the node using quaternion values |
| `shapeStretch` | `[x, y, z]` | Scales the node along each axis |
| `shapeVisible` | `boolean` | Shows or hides the node |
| `shapeUvOffset` | `[u, v]` | Offsets texture UV coordinates for scrolling effects |

## Keyframe Structure

Each track contains an array of keyframes. Keyframe structure varies by track type:

### Position/Orientation/ShapeStretch/ShapeUvOffset Keyframes

```json
{
  "time": 0,
  "delta": [0.0, 0.5, 0.0],
  "interpolationType": "Linear"
}
```

| Field | Type | Description |
|-------|------|-------------|
| `time` | integer | Frame number when this keyframe occurs |
| `delta` | array | The value at this keyframe (format depends on track type) |
| `interpolationType` | string | How to interpolate to this keyframe |

### ShapeVisible Keyframes

```json
{
  "time": 5,
  "delta": false
}
```

Visibility keyframes don't use interpolation - they switch instantly.

## Interpolation Types

| Type | Description |
|------|-------------|
| `None` | No interpolation, value snaps instantly |
| `Linear` | Linear interpolation between keyframes |
| `EaseIn` | Starts slow, accelerates |
| `EaseOut` | Starts fast, decelerates |
| `EaseInOut` | Starts slow, speeds up, then slows down |
| `Bezier` | Custom bezier curve interpolation |

## Examples

### Simple Door Animation

A door that rotates open over 10 frames:

```json
{
  "formatVersion": 0,
  "duration": 10,
  "holdLastKeyframe": true,
  "nodeAnimations": {
    "Door": {
      "orientation": [
        {
          "time": 0,
          "delta": [0.0, 0.0, 0.0, 1.0],
          "interpolationType": "EaseOut"
        },
        {
          "time": 10,
          "delta": [0.0, 0.707, 0.0, 0.707],
          "interpolationType": "EaseOut"
        }
      ]
    }
  }
}
```

### Flickering Light

A light that flickers by toggling visibility:

```json
{
  "formatVersion": 0,
  "duration": 20,
  "holdLastKeyframe": false,
  "nodeAnimations": {
    "Flame": {
      "shapeVisible": [
        { "time": 0, "delta": true },
        { "time": 3, "delta": false },
        { "time": 5, "delta": true },
        { "time": 12, "delta": false },
        { "time": 14, "delta": true }
      ]
    }
  }
}
```

### UV Scrolling Effect

Animated texture scrolling for water or conveyor effects:

```json
{
  "formatVersion": 0,
  "duration": 40,
  "holdLastKeyframe": false,
  "nodeAnimations": {
    "Surface": {
      "shapeUvOffset": [
        {
          "time": 0,
          "delta": [0.0, 0.0],
          "interpolationType": "Linear"
        },
        {
          "time": 40,
          "delta": [1.0, 0.0],
          "interpolationType": "Linear"
        }
      ]
    }
  }
}
```

### Chest Opening with Multiple Nodes

A chest with a lid that rotates and hinges that move:

```json
{
  "formatVersion": 0,
  "duration": 8,
  "holdLastKeyframe": true,
  "nodeAnimations": {
    "Lid": {
      "orientation": [
        {
          "time": 0,
          "delta": [0.0, 0.0, 0.0, 1.0],
          "interpolationType": "EaseOut"
        },
        {
          "time": 8,
          "delta": [-0.383, 0.0, 0.0, 0.924],
          "interpolationType": "EaseOut"
        }
      ]
    },
    "Latch": {
      "position": [
        {
          "time": 0,
          "delta": [0.0, 0.0, 0.0],
          "interpolationType": "Linear"
        },
        {
          "time": 4,
          "delta": [0.0, 0.0, -0.0625],
          "interpolationType": "Linear"
        }
      ]
    }
  }
}
```

## Integration with Blocks

Blocks reference animations through their `CustomModelAnimation` property in the block definition:

```json
{
  "Parent": "Template_Block",
  "CustomModelAnimation": "Blocks/Animations/Doors/WoodenDoor_Open"
}
```

The animation path is relative to `Assets/Common/` and omits the `.blockyanim` extension.

## Looping Behavior

- If `holdLastKeyframe` is `false`, the animation loops back to the start
- If `holdLastKeyframe` is `true`, the animation plays once and holds the final values
- Looping animations (fire, water) typically set `holdLastKeyframe: false`
- State transitions (doors, chests) typically set `holdLastKeyframe: true`

## Best Practices

1. **Keep durations short** - Most block animations are 5-20 frames (0.25-1 second)
2. **Use EaseOut for mechanical motion** - Doors and lids feel more natural with deceleration
3. **Match node names exactly** - Node names must match those in the `.blockymodel` file
4. **Consider reverse animations** - Doors need both open and close animations
5. **Test at 20 FPS** - Remember the fixed frame rate when timing animations
