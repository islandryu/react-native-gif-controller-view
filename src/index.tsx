import React, { forwardRef, useImperativeHandle } from 'react';
import {
  requireNativeComponent,
  UIManager,
  Platform,
  findNodeHandle,
  NativeModules,
  processColor,
} from 'react-native';
// @ts-expect-error
import resolveAssetSource from 'react-native/Libraries/Image/resolveAssetSource';
import type { GifControllerViewRef, GifControllerViewProps } from './types';
export * from './types';

const LINKING_ERROR =
  "The package 'react-native-gif-controller-view' doesn't seem to be linked. Make sure: \n\n" +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

export const GifImageView = getComponent('GifControllerView');

function getComponent(ComponentName: string) {
  return UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
}

export const GifControllerView = forwardRef<
  GifControllerViewRef,
  GifControllerViewProps
>(({ ...props }, ref) => {
  const imageProps = {
    isAnimating: true,
    isReverse: false,
    speed: 1,
    disableLoop: false,
    ...props,
    source: resolveAssetSource(props.source).uri,
  };
  const componentRef = React.useRef(null);

  useImperativeHandle(ref, () => ({
    getAllColorCount: (index: number) => getAllColorCount(index),
    seekTo: (index: number) => seekTo(index),
    getFrameData: () => getFrameData(),
  }));

  const getAllColorCount = (index: number) => {
    const node = findNodeHandle(componentRef.current);
    return NativeModules.GifControllerViewModule.getAllColorCount(node, index);
  };

  const seekTo = (index: number) => {
    const node = findNodeHandle(componentRef.current);
    return NativeModules.GifControllerViewModule.seekTo(node, index);
  };

  const getFrameData = () => {
    const node = findNodeHandle(componentRef.current);
    return NativeModules.GifControllerViewModule.getFrameData(node);
  };

  const colorMappings = props.colorMappings?.map((item) => {
    return {
      from: processColor(item.from),
      to: processColor(item.to),
    };
  });
  return (
    <GifImageView
      {...imageProps}
      // @ts-expect-error
      ref={componentRef}
      colorMappings={colorMappings}
    />
  );
});
