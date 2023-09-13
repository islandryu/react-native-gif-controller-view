import type { ImageStyle } from 'react-native';
import type { StyleProp } from 'react-native';
import type { ImageSourcePropType } from 'react-native';

export type GifControllerViewProps = {
  source: ImageSourcePropType;
  style?: StyleProp<ImageStyle>;
  colorMappings: { from: string; to: string }[];
  isAnimating?: boolean;
  isReverse?: boolean;
  speed?: number;
  disableLoop?: boolean;
};

export type GifControllerViewRef = {
  getAllColorCount: (index: number) => Promise<
    {
      color: string;
      count: number;
    }[]
  >;
  seekTo: (index: number) => void;
  getFrameData: () => Promise<
    {
      delayTime: number;
      frameIndex: number;
    }[]
  >;
};
