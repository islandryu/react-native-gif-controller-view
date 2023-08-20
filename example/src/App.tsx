import React, { useRef, useState } from 'react';
import { StyleSheet, View, Button } from 'react-native';
import { GifControllerView } from 'react-native-gif-controller-view';
import type { GifControllerViewRef } from 'react-native-gif-controller-view';

function App() {
  const [isAnimating, setIsAnimating] = useState(true);
  const [isReverse, setIsReverse] = useState(false);
  const [speed, setSpeed] = useState(0.5);
  const [colorMappings, setColorMappings] = useState<
    { from: string; to: string }[]
  >([]);
  const controllerViewRef = useRef<GifControllerViewRef>(null);

  const toggleAnimation = () => {
    setIsAnimating(!isAnimating);
  };

  const toggleReverse = () => {
    setIsReverse(!isReverse);
  };

  const increaseSpeed = () => {
    setSpeed((prevSpeed) => prevSpeed + 0.5);
  };

  const changeColor = async () => {
    const colorCount = await controllerViewRef.current?.getAllColorCount(10);
    const mainColor = colorCount?.sort((a, b) => b.count - a.count)[1]?.color;
    if (!mainColor) return;
    setColorMappings([{ from: mainColor, to: '#FF0000' }]);
  };
  return (
    <View style={styles.container}>
      <GifControllerView
        source={{
          uri: 'https://www.easygifanimator.net/images/samples/eglite.gif',
        }}
        style={styles.gif}
        ref={controllerViewRef}
        colorMappings={colorMappings}
        speed={speed}
        isReverse={isReverse}
        isAnimating={isAnimating}
      />
      <View style={styles.buttons}>
        <Button
          title={isAnimating ? 'Stop Animation' : 'Start Animation'}
          onPress={toggleAnimation}
        />
        <Button
          title={isReverse ? 'Normal Mode' : 'Reverse Mode'}
          onPress={toggleReverse}
        />
        <Button title={`Speed: ${speed}`} onPress={increaseSpeed} />
        <Button title="Change Color to Red" onPress={changeColor} />
        <Button
          title="Seek to First Frame"
          onPress={() => controllerViewRef.current?.seekTo(0)}
        />
        <Button
          title="Get Frame Data"
          onPress={async () => {
            const frameData = await controllerViewRef.current?.getFrameData();
            console.log(frameData);
          }}
        />
      </View>
    </View>
  );
}

const styles = StyleSheet.create({
  container: {
    flex: 1,
    alignItems: 'center',
    justifyContent: 'center',
    backgroundColor: '#f5f5f5',
  },
  gif: {
    width: 200,
    height: 200,
    marginBottom: 20,
  },
  buttons: {
    width: '80%',
  },
});

export default App;
