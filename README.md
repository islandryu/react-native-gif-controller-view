# react-native-gif-controller-view



https://github.com/islandryu/react-native-gif-controller-view/assets/65934663/ca939c3d-8404-442d-b205-bd7b910208f7



## Overview

`GifControllerView` is a component designed for advanced control and manipulation of GIF images in React Native applications. This component offers properties to modify GIF presentation styles, animation controls, and color mappings. Moreover, using the reference methods (`GifControllerViewRef`), developers can fetch detailed color statistics and frame-specific data for any GIF.

## Installation

Make sure you've already set up React Native in your project. Then, install the `GifControllerView` component:

```bash
npm install react-native-gif-controller-view
```

## Usage

### Props

#### `source` (`ImageSourcePropType`)

The GIF image source.

#### `style` (`StyleProp<ImageStyle>`)

Styles the `GifControllerView`. This prop accepts all standard React Native `ImageStyle` properties.

#### `colorMappings` (Array of `{ from: string; to: string }`)

An array that defines the color transformation mappings. Each mapping transforms a color `from` a specified value `to` another value.

Example:

```jsx
colorMappings={[
  { from: "#FFFFFF", to: "#000000" },
  // This mapping transforms all white colors in the GIF to black.
]}
```

#### `isAnimating` (`boolean`)

Controls the GIF's animation state. If `true`, the GIF animates. If `false`, it pauses.

#### `isReverse` (`boolean`)

If set to `true`, the GIF animates in reverse. If `false`, it animates normally.

#### `speed` (`number`)

Determines the GIF's playback speed. A value of `1` is the default speed, `2` would be twice as fast, and so on.

### Ref Methods

Using React's `ref`, you can access the following methods:

#### `getAllColorCount(index: number)`

Returns a promise that resolves to an array containing the count of all colors in a specified frame. Each entry in the array includes the `color` and its `count`.

Example:

```jsx
const ref = useRef();

// ... Somewhere in your code:
const colorData = await ref.current?.getAllColorCount(0);
```

#### `seekTo(index: number)`

Navigates to a specified frame index in the GIF.

Example:

```jsx
const ref = useRef();

// ... Somewhere in your code:
ref.current?.seekTo(5); // Jumps to the 6th frame (0-indexed).
```

#### `getFrameData()`

Returns a promise that resolves to an array containing data about each frame. Each entry in the array includes the `delayTime` (time delay before the next frame) and `frameIndex` (index of the frame).

Example:

```jsx
const ref = useRef();

// ... Somewhere in your code:
const frameData = await ref.current?.getFrameData();
```

## Example

```jsx
import GifControllerView from 'react-native-gif-controller-view';

// In your render:
<GifControllerView
  source={require('./path_to_your_gif.gif')}
  style={{ width: 100, height: 100 }}
  colorMappings={[{ from: '#FFFFFF', to: '#000000' }]}
  isAnimating={true}
  isReverse={false}
  speed={1}
  ref={yourRef}
/>;
```

## Contributing

See the [contributing guide](CONTRIBUTING.md) to learn how to contribute to the repository and the development workflow.

## License

MIT

---

Made with [create-react-native-library](https://github.com/callstack/react-native-builder-bob)
