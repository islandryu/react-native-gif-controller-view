import {
  requireNativeComponent,
  UIManager,
  Platform,
  type ViewStyle,
} from 'react-native';

const LINKING_ERROR =
  `The package 'react-native-gif-controller-view' doesn't seem to be linked. Make sure: \n\n` +
  Platform.select({ ios: "- You have run 'pod install'\n", default: '' }) +
  '- You rebuilt the app after installing the package\n' +
  '- You are not using Expo Go\n';

type GifControllerViewProps = {
  color: string;
  style: ViewStyle;
};

const ComponentName = 'GifControllerViewView';

export const GifControllerViewView =
  UIManager.getViewManagerConfig(ComponentName) != null
    ? requireNativeComponent<GifControllerViewProps>(ComponentName)
    : () => {
        throw new Error(LINKING_ERROR);
      };
